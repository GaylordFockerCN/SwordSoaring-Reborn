package net.p1nero.ss.animation;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;
import net.p1nero.ss.capability.SSCapabilityProvider;
import net.p1nero.ss.capability.SSPlayer;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.entity.sword.AbstractSwordEntity;
import net.p1nero.ss.gameassets.animations.ScreenSwordAnimations;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.*;

public class ArtifactSpiritMultiPhaseAttackAnimation extends AttackAnimation {
    public ArtifactSpiritMultiPhaseAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
    }

    public ArtifactSpiritMultiPhaseAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, path, armature);
    }

    public ArtifactSpiritMultiPhaseAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
        super(convertTime, path, armature, phases);
    }

    @Override
    public void begin(LivingEntityPatch<?> entityPatch) {
        super.begin(entityPatch);
        if(entityPatch instanceof AbstractArtifactSpiritPatch<?> artifactSpiritPatch && artifactSpiritPatch.getOwnerPatch() != null){
            artifactSpiritPatch.getOwnerPatch().getOriginal().getCapability(SSCapabilityProvider.SS_PLAYER).ifPresent(SSPlayer::clearMap);
        }
    }

    /**
     * 检查phase是否合法
     */
    public boolean isPhaseValid(LivingEntityPatch<?> entityPatch, Phase phase){
        return true;
    }

    /**
     * 全部进行判断
     */
    @Override
    protected void attackTick(LivingEntityPatch<?> entityPatch) {
        AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(this);
        float elapsedTime = player.getElapsedTime();
        float prevElapsedTime = player.getPrevElapsedTime();
        EntityState state = this.getState(entityPatch, elapsedTime);
        EntityState prevState = this.getState(entityPatch, prevElapsedTime);
        for(Phase phase : phases){
            if(!isPhaseValid(entityPatch, phase)){
                continue;
            }
            if(phase instanceof MultiAttackPhase multiAttackPhase){
                if (prevState.attacking() || state.attacking() || prevState.getLevel() < 2 && state.getLevel() > 2) {
                    this.hurtCollidingEntities(entityPatch, prevElapsedTime, elapsedTime, prevState, state, multiAttackPhase);
                }
            }
        }
    }

    /**
     * 自己在Capability里实现根据phase判断是否攻击过，不同phase独立判断
     */
    protected void hurtCollidingEntities(LivingEntityPatch<?> entityPatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, MultiAttackPhase phase) {
        entityPatch.getArmature().initializeTransform();
        float prevPoseTime = prevState.attacking() ? prevElapsedTime : phase.preDelay;
        float poseTime = state.attacking() ? elapsedTime : phase.contact;
        List<Entity> list = phase.getCollidingEntities(entityPatch, this, prevPoseTime, poseTime, this.getPlaySpeed(entityPatch));
        if (!list.isEmpty()) {
            HitEntityList hitEntities = new HitEntityList(entityPatch, list, phase.getProperty(AnimationProperty.AttackPhaseProperty.HIT_PRIORITY).orElse(HitEntityList.Priority.DISTANCE));

            if(entityPatch instanceof AbstractArtifactSpiritPatch<?> artifactSpiritPatch && artifactSpiritPatch.getOwnerPatch() != null){
                SSPlayer ssPlayer = artifactSpiritPatch.getOwnerPatch().getOriginal().getCapability(SSCapabilityProvider.SS_PLAYER).orElse(new SSPlayer());
                while (hitEntities.next()) {
                    Entity hit = hitEntities.getEntity();
                    LivingEntity trueEntity = this.getTrueEntity(hit);
                    if (trueEntity != null && trueEntity.isAlive() && !ssPlayer.getCurrentlyHurtEntities(phase).contains(trueEntity) && !trueEntity.is(artifactSpiritPatch.getOwnerPatch().getOriginal()) && !trueEntity.is(artifactSpiritPatch.getOriginal())) {
                        if (hit instanceof LivingEntity || hit instanceof PartEntity) {
                            EpicFightDamageSource source = this.getEpicFightDamageSource(entityPatch, hit, phase);
                            int prevInvulTime = hit.invulnerableTime;
                            hit.invulnerableTime = 0;

                            AttackResult attackResult = entityPatch.attack(source, hit, phase.hand);
                            hit.invulnerableTime = prevInvulTime;

                            if (attackResult.resultType.dealtDamage()) {
                                artifactSpiritPatch.getOwnerPatch().getEventListener().triggerEvents(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, new DealtDamageEvent(((ServerPlayerPatch) artifactSpiritPatch.getOwnerPatch()), trueEntity, source, attackResult.damage));
                                if(this.equals(ScreenSwordAnimations.KILL_AURA_2)){
                                    trueEntity.setSecondsOnFire(5);
                                }
                                hit.level.playSound(null, hit.getX(), hit.getY(), hit.getZ(), this.getHitSound(entityPatch, phase), hit.getSoundSource(), 1.0F, 1.0F);
                                this.spawnHitParticle((ServerLevel)hit.getLevel(), entityPatch, hit, phase);
                            }

                            ssPlayer.getCurrentlyHurtEntities(phase).add(trueEntity);

                            if (attackResult.resultType.shouldCount()) {
                                entityPatch.getCurrenltyHurtEntities().add(trueEntity);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 全部渲染
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingEntityPatch<?> entityPatch, float playbackTime, float partialTicks) {
        AnimationPlayer animPlayer = entityPatch.getAnimator().getPlayerFor(this);
        float prevElapsedTime = animPlayer.getPrevElapsedTime();
        float elapsedTime = animPlayer.getElapsedTime();
        for(Phase phase : phases){
            if(!isPhaseValid(entityPatch, phase)){
                continue;
            }
            Pair<Joint, Collider> colliderInfo;
            Collider collider;
            boolean flag = false;
            ArrayList<Pair<Joint, Collider>> newColliders = new ArrayList<>();
            for(Iterator<Pair<Joint, Collider>> iterator = phase.colliders.iterator(); iterator.hasNext(); collider.draw(poseStack, buffer, entityPatch, this, colliderInfo.getFirst(), prevElapsedTime, elapsedTime, partialTicks, this.getPlaySpeed(entityPatch))) {
                colliderInfo = iterator.next();
                collider = colliderInfo.getSecond();
                if(entityPatch.getOriginal() instanceof AbstractSwordEntity swordEntity){
                    ItemStack stack = swordEntity.getItemStack(entityPatch);
                    Collider newCollider = EpicFightCapabilities.getItemStackCapability(stack).getWeaponCollider();
                    if (!newCollider.equals(colliderInfo.getSecond())){
                        flag = true;
                        collider = newCollider;
                    }
                    newColliders.add(new Pair<>(colliderInfo.getFirst(), collider));
                }
            }
            if(flag){
                phase.colliders = newColliders;
            }
        }
    }

    /**
     * 获取武器对应碰撞箱
     */
    public static class MultiAttackPhase extends AttackAnimation.Phase{
        public MultiAttackPhase(float start, float antic, float contact, float recovery, float end, Joint joint, Collider collider) {
            super(start, antic, contact, recovery, end, joint, collider);
        }

        public MultiAttackPhase(float start, float antic, float contact, float recovery, float end, InteractionHand hand, Joint joint, Collider collider) {
            super(start, antic, contact, recovery, end, hand, joint, collider);
        }

        public MultiAttackPhase(float start, float antic, float preDelay, float contact, float recovery, float end, Joint joint, Collider collider) {
            super(start, antic, preDelay, contact, recovery, end, joint, collider);
        }

        public MultiAttackPhase(float start, float antic, float preDelay, float contact, float recovery, float end, InteractionHand hand, Joint joint, Collider collider) {
            super(start, antic, preDelay, contact, recovery, end, hand, joint, collider);
        }

        public MultiAttackPhase(InteractionHand hand, Joint joint, Collider collider) {
            super(hand, joint, collider);
        }

        public MultiAttackPhase(float start, float antic, float preDelay, float contact, float recovery, float end, boolean noStateBind, InteractionHand hand, Joint joint, Collider collider) {
            super(start, antic, preDelay, contact, recovery, end, noStateBind, hand, joint, collider);
        }

        public MultiAttackPhase(float start, float antic, float preDelay, float contact, float recovery, float end, boolean noStateBind, InteractionHand hand, List<Pair<Joint, Collider>> colliders) {
            super(start, antic, preDelay, contact, recovery, end, noStateBind, hand, colliders);
        }

        @Override
        public List<Entity> getCollidingEntities(LivingEntityPatch<?> entityPatch, AttackAnimation animation, float prevElapsedTime, float elapsedTime, float attackSpeed) {
            if(entityPatch.getOriginal() instanceof AbstractSwordEntity swordEntity){
                List<Entity> entities = Lists.newArrayList();
                Pair<Joint, Collider> colliderInfo;
                Collider collider;
                boolean flag = false;
                ArrayList<Pair<Joint, Collider>> newColliders = new ArrayList<>();
                for(Iterator<Pair<Joint, Collider>> iterator = this.colliders.iterator(); iterator.hasNext(); entities.addAll(collider.updateAndSelectCollideEntity(entityPatch, animation, prevElapsedTime, elapsedTime, colliderInfo.getFirst(), attackSpeed))) {
                    colliderInfo = iterator.next();
                    collider = colliderInfo.getSecond();
                    ItemStack stack = swordEntity.getItemStack(entityPatch);
                    Collider newCollider = EpicFightCapabilities.getItemStackCapability(stack).getWeaponCollider();
                    if(!newCollider.equals(colliderInfo.getSecond())){
                        flag = true;
                        collider = newCollider;
                        newColliders.add(new Pair<>(colliderInfo.getFirst(), collider));
                    }
                }
                if(flag){
                    colliders = newColliders;
                }
                return entities;
            }
            return super.getCollidingEntities(entityPatch, animation, prevElapsedTime, elapsedTime, attackSpeed);
        }
    }
}