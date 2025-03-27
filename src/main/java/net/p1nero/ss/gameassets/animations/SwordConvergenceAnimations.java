package net.p1nero.ss.gameassets.animations;

import com.p1nero.invincible.api.animation.StaticAnimationProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.p1nero.ss.Config;
import net.p1nero.ss.animation.BabylonMultiPhaseAttackAnimation;
import net.p1nero.ss.client.CameraAnim;
import net.p1nero.ss.entity.sword.sword_convergence.SwordConvergenceEntity;
import net.p1nero.ss.entity.sword.sword_convergence.WanArmature;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.skill.sword_controller.WanJianGuiZongSkill;
import net.p1nero.ss.util.AnimationUtils;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.ArrayList;
import java.util.List;

public class SwordConvergenceAnimations {
    public static StaticAnimation WAN1_L;
    public static StaticAnimation WAN2_L;
    public static StaticAnimation WAN3_L;
    public static StaticAnimation WAN4_L;
    public static StaticAnimation WAN_SHOOT_L;

    public static StaticAnimation WAN1_R;
    public static StaticAnimation WAN2_R;
    public static StaticAnimation WAN3_R;
    public static StaticAnimation WAN4_R;
    public static StaticAnimation WAN_SHOOT_R;

    public static StaticAnimation WAN1_PLAYER;
    public static StaticAnimation WAN2_PLAYER;
    public static StaticAnimation WAN3_PLAYER;


    public static AnimationEvent.TimeStampedEvent summonAndPlay(float time, StaticAnimationProvider animationToPlay) {
        return AnimationEvent.TimeStampedEvent.create(time, (livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch.getOriginal() instanceof SwordConvergenceEntity swordConvergenceEntity) {
                SwordConvergenceEntity newSwords = new SwordConvergenceEntity(swordConvergenceEntity.getOwner());
                newSwords.setAnimationToPlay(animationToPlay.get());
                newSwords.initBabylonItems(swordConvergenceEntity.getValidBabylonItems(), false);
                swordConvergenceEntity.level.addFreshEntity(newSwords);
            }
        }, AnimationEvent.Side.SERVER);
    }

    public static AnimationEvent nextPlay(StaticAnimationProvider animation) {
        return AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(animation.get()), AnimationEvent.Side.SERVER);
    }

    public static final AnimationEvent DISCARD_SELF = AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.getOriginal().discard(), AnimationEvent.Side.SERVER);

    @OnlyIn(Dist.CLIENT)
    public static List<TrailInfo> getWanTrails(){
        if(!Config.ENABLE_TRAIL.get()){
            return List.of();
        }
        List<TrailInfo> wanTrails = new ArrayList<>();
        for(Joint joint : SwordSoaringArmatures.wanArmature.joints){
            wanTrails.add(TrailInfo.builder()
                    .r(1.0F).b(1.0F).g(1.0F)
                    .startPos(new Vec3(0.1, 0, 0))
                    .endPos(new Vec3(-0.1, 0, 0))
                    .time(0, 3)
                    .lifetime(10)
                    .interpolations(6)
                    .joint(joint.getName())
                    .itemSkinHand(InteractionHand.MAIN_HAND)
                    .texture("epicfight:textures/particle/swing_trail.png")
                    .type((SimpleParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(Config.TRAIL_PARTICLE_TYPE.get())))
                    .create());
        }
        return wanTrails;
    }

    public static void buildSwordConvergenceAnim() {
        HumanoidArmature biped = Armatures.BIPED;
        WAN1_PLAYER = new ActionAnimation(0.15F, "wan/wan_owner_1", biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch.isLogicalClient()){
                                if(livingEntityPatch.getOriginal() == Minecraft.getInstance().player){
                                    CameraAnim.zoomIn(new Vec3f(0, -3, -6), 450);
                                }
                            }
                        }, AnimationEvent.Side.CLIENT),
                        AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                serverPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_CONTROLLER).getDataManager().setDataSync(WanJianGuiZongSkill.IS_CHARGING, true, serverPlayerPatch.getOriginal());
                            }
                        }, AnimationEvent.Side.SERVER)
                )
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, nextPlay(() -> WAN2_PLAYER))
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F));
        WAN2_PLAYER = new ActionAnimation(0.0001F, "wan/wan_owner_2", biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch.getOriginal() == Minecraft.getInstance().player){
                        CameraAnim.zoomIn(new Vec3f(0, -3, -6), 450);
                    }
                }, AnimationEvent.Side.CLIENT))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        SkillDataManager manager = serverPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_CONTROLLER).getDataManager();
                        if (manager.hasData(WanJianGuiZongSkill.IS_PRESSING) && manager.getDataValue(WanJianGuiZongSkill.IS_PRESSING)) {
                            serverPlayerPatch.reserveAnimation(WAN2_PLAYER);
                        } else {
                            serverPlayerPatch.reserveAnimation(WAN3_PLAYER);
                        }
                    }
                }, AnimationEvent.Side.SERVER))
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false);
        WAN3_PLAYER = new ActionAnimation(0.0001F, "wan/wan_owner_3", biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch.getOriginal() == Minecraft.getInstance().player){
                                CameraAnim.zoomIn(new Vec3f(0, -3, -6), 200);
                            }
                        }, AnimationEvent.Side.CLIENT),
                        AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                serverPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_CONTROLLER).getDataManager().setDataSync(WanJianGuiZongSkill.IS_CHARGING, false, serverPlayerPatch.getOriginal());
                            }
                        }, AnimationEvent.Side.SERVER)
                )
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F));

        WanArmature wanArmature = SwordSoaringArmatures.wanArmature;
        WAN1_L = new BabylonMultiPhaseAttackAnimation(0.15F, "wan/wan_l_1", wanArmature, AnimationUtils.getPhases(wanArmature.wanJoints, 0, 2.667F))
                .addEvents(summonAndPlay(2.30F, () -> WAN2_L))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F));

        WAN2_L = new BabylonMultiPhaseAttackAnimation(0.0001F, "wan/wan_l_2", wanArmature, AnimationUtils.getPhases(wanArmature.wanJoints, 0, 2.667F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch.getOriginal() instanceof SwordConvergenceEntity swordConvergenceEntity) {
                        if (swordConvergenceEntity.isOwnerKeyPressing()) {
                            livingEntityPatch.reserveAnimation(WAN2_L);
                        } else {
                            livingEntityPatch.reserveAnimation(WAN3_L);
                        }
                    }
                }, AnimationEvent.Side.SERVER));
        WAN3_L = new BabylonMultiPhaseAttackAnimation(0.0001F, "wan/wan_l_3", wanArmature, AnimationUtils.getPhases(wanArmature.wanJoints, 0, 2.667F))
                .addEvents(summonAndPlay(1.13F, () -> WAN4_L))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);

        WAN4_L = new BabylonMultiPhaseAttackAnimation(0.15F, "wan/wan_l_4", wanArmature, AnimationUtils.getPhases(wanArmature.wanJoints, 0, 2.667F))
                .addEvents(summonAndPlay(1.13F, () -> WAN_SHOOT_L))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);

        WAN_SHOOT_L = new BabylonMultiPhaseAttackAnimation(0.0001F, "wan/wan_shoot_l", wanArmature, AnimationUtils.getPhases(wanArmature.wanJoints, 0, 2.667F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);

        WAN1_R = new BabylonMultiPhaseAttackAnimation(0.15F, "wan/wan_r_1", wanArmature, AnimationUtils.getPhases(wanArmature.wanJoints, 0, 2.667F))
                .addEvents( summonAndPlay(2.30F, () -> WAN2_R))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F));

        WAN2_R = new BabylonMultiPhaseAttackAnimation(0.0001F, "wan/wan_r_2", wanArmature, AnimationUtils.getPhases(wanArmature.wanJoints, 0, 2.667F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch.getOriginal() instanceof SwordConvergenceEntity swordConvergenceEntity) {
                        if (swordConvergenceEntity.isOwnerKeyPressing()) {
                            livingEntityPatch.reserveAnimation(WAN2_R);
                        } else {
                            livingEntityPatch.reserveAnimation(WAN3_R);
                        }
                    }
                }, AnimationEvent.Side.SERVER));

        WAN3_R = new BabylonMultiPhaseAttackAnimation(0.0001F, "wan/wan_r_3", wanArmature, AnimationUtils.getPhases(wanArmature.wanJoints, 0, 2.667F))
                .addEvents(summonAndPlay(1.13F, () -> WAN4_R))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);

        WAN4_R = new BabylonMultiPhaseAttackAnimation(0.15F, "wan/wan_r_4", wanArmature, AnimationUtils.getPhases(wanArmature.wanJoints, 0, 2.667F))
                .addEvents(summonAndPlay(1.13F, () -> WAN_SHOOT_R))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);

        WAN_SHOOT_R = new BabylonMultiPhaseAttackAnimation(0.0001F, "wan/wan_shoot_r", wanArmature, AnimationUtils.getPhases(wanArmature.wanJoints, 0, 2.667F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);

        if(FMLEnvironment.dist == Dist.CLIENT){
            WAN1_L.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            WAN2_L.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            WAN3_L.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            WAN4_L.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            WAN1_R.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            WAN2_R.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            WAN3_R.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            WAN4_R.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            WAN_SHOOT_R.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
        }

    }
}
