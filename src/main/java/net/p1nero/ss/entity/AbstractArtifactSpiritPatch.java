package net.p1nero.ss.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

public abstract class AbstractArtifactSpiritPatch<T extends AbstractArtifactSpiritEntity> extends MobPatch<T> {

    public AbstractArtifactSpiritPatch() {
        super();
    }

    public AbstractArtifactSpiritPatch(Faction faction) {
        super(faction);
    }

    @Nullable
    private PlayerPatch<?> ownerPatch;

    @Override
    public void updateMotion(boolean considerInaction) {
        if(this.state.inaction() && considerInaction){
            this.currentLivingMotion = LivingMotions.IDLE;
            this.currentCompositeMotion = LivingMotions.IDLE;
        } else {
            syncMotionToOwner(considerInaction);
        }
    }

    public void syncMotionToOwner(boolean considerInaction){
        if (this.original.getOwner() instanceof Player player) {
            PlayerPatch<?> patch = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
            this.currentLivingMotion = patch.currentLivingMotion;
            this.currentCompositeMotion = patch.currentCompositeMotion;
        } else {
            this.currentLivingMotion = LivingMotions.IDLE;
            this.currentCompositeMotion = LivingMotions.IDLE;
        }
    }

    public void keepIdleMotion(boolean considerInaction){
        this.currentLivingMotion = LivingMotions.IDLE;
        this.currentCompositeMotion = LivingMotions.IDLE;
    }

    @Override
    public StaticAnimation getHitAnimation(StunType stunType) {
        return null;
    }

    @Nullable
    public PlayerPatch<?> getOwnerPatch() {
        if (ownerPatch != null) {
            return ownerPatch;
        }
        if (getOriginal().getOwner() != null) {
            ownerPatch = EpicFightCapabilities.getEntityPatch(getOriginal().getOwner(), PlayerPatch.class);
            return ownerPatch;
        }
        return null;
    }

    /**
     * 视为主人攻击，并触发事件
     */
    @Override
    public AttackResult attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand) {
        if (getOwnerPatch() != null && shouldUseOwnerAttack()) {
            AttackResult result = getOwnerPatch().attack(damageSource, target, hand);
            if(result.resultType.dealtDamage() && getOwnerPatch() instanceof ServerPlayerPatch serverPlayerPatch){
                serverPlayerPatch.getEventListener().triggerEvents(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, new DealtDamageEvent(serverPlayerPatch, target instanceof LivingEntity livingEntity ? livingEntity : null, damageSource, result.damage));
            }
            return result;
        }
        return super.attack(damageSource, target, hand);
    }

    public boolean shouldUseOwnerAttack(){
        return true;
    }

    @Nullable
    @Override
    public EpicFightDamageSource getEpicFightDamageSource() {
        if (getOwnerPatch() != null) {
            return getOwnerPatch().getEpicFightDamageSource();
        }
        return super.getEpicFightDamageSource();
    }

    @Override
    public EpicFightDamageSource getDamageSource(StaticAnimation animation, InteractionHand hand) {
        if (getOwnerPatch() != null) {
            return getOwnerPatch().getDamageSource(animation, hand);
        }
        return super.getDamageSource(animation, hand);
    }

    @Override
    public SoundEvent getSwingSound(InteractionHand hand) {
        if (getOwnerPatch() == null) {
            return super.getSwingSound(hand);
        }
        return getOwnerPatch().getSwingSound(hand);
    }

    @Override
    public SoundEvent getWeaponHitSound(InteractionHand hand) {
        if (getOwnerPatch() == null) {
            return super.getWeaponHitSound(hand);
        }
        return getOwnerPatch().getWeaponHitSound(hand);
    }

    /**
     * 主人不同则杀
     */
    @Override
    public boolean isTeammate(Entity entityIn) {
        if(entityIn.equals(this.getOriginal().getOwner())){
            return true;
        }
        if(entityIn instanceof AbstractArtifactSpiritEntity artifactSpiritEntity && getOwnerPatch() != null){
            return getOwnerPatch().getOriginal().equals(artifactSpiritEntity.getOwner());
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean flashTargetIndicator(LocalPlayerPatch playerPatch) {
        return false;
    }

}
