package net.p1nero.ss.gameassets.animations;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforgespi.Environment;
import net.p1nero.ss.SwordSoaringConfig;
import net.p1nero.ss.animation.BabylonMultiPhaseAttackAnimation;
import net.p1nero.ss.client.SwordSoaringCameraManager;
import net.p1nero.ss.entity.sword.wan.WanEntity;
import net.p1nero.ss.entity.sword.wan.WanArmature;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.utils.AnimationUtils;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class WanAnimations {
    public static AnimationManager.AnimationAccessor<AttackAnimation> WAN1_L;
    public static AnimationManager.AnimationAccessor<AttackAnimation> WAN2_L;
    public static AnimationManager.AnimationAccessor<AttackAnimation> WAN3_L;
    public static AnimationManager.AnimationAccessor<AttackAnimation> WAN4_L;
    public static AnimationManager.AnimationAccessor<AttackAnimation> WAN_SHOOT_L;

    public static AnimationManager.AnimationAccessor<AttackAnimation> WAN1_R;
    public static AnimationManager.AnimationAccessor<AttackAnimation> WAN2_R;
    public static AnimationManager.AnimationAccessor<AttackAnimation> WAN3_R;
    public static AnimationManager.AnimationAccessor<AttackAnimation> WAN4_R;
    public static AnimationManager.AnimationAccessor<AttackAnimation> WAN_SHOOT_R;

    public static AnimationManager.AnimationAccessor<ActionAnimation> WAN1_PLAYER;
    public static AnimationManager.AnimationAccessor<ActionAnimation> WAN2_PLAYER;
    public static AnimationManager.AnimationAccessor<ActionAnimation> WAN3_PLAYER;


    public static AnimationEvent.InTimeEvent summonAndPlay(float time, AnimationManager.AnimationAccessor<? extends StaticAnimation> animationToPlay) {
        return AnimationEvent.InTimeEvent.create(time, (livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch.getOriginal() instanceof WanEntity wanEntity && wanEntity.getOwner() != null) {
                WanEntity newSwords = new WanEntity(wanEntity.getOwner());
                newSwords.setAnimationToPlay(animationToPlay);
                newSwords.initBabylonItems(wanEntity.getValidBabylonItems(), false);
                wanEntity.level().addFreshEntity(newSwords);
            }
        }, AnimationEvent.Side.SERVER);
    }

    public static AnimationEvent nextPlay(AnimationManager.AnimationAccessor<? extends StaticAnimation> animation) {
        return AnimationEvent.SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(animation), AnimationEvent.Side.SERVER);
    }

    public static final AnimationEvent DISCARD_SELF = AnimationEvent.SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.getOriginal().discard(), AnimationEvent.Side.SERVER);

    @OnlyIn(Dist.CLIENT)
    public static List<TrailInfo> getWanTrails(){
        int updateInterVal =  SwordSoaringConfig.WAN_TRAIL_UPDATE_TICK.get();
        if(updateInterVal <= 0){
            return List.of();
        }
        List<TrailInfo> wanTrails = new ArrayList<>();
        for(Joint joint : SwordSoaringArmatures.WAN_ARMATURE.get().joints){
            wanTrails.add(TrailInfo.builder()
                    .r(1.0F).b(1.0F).g(1.0F)
                    .startPos(new Vec3(0.1, 0, 0))
                    .endPos(new Vec3(-0.1, 0, 0))
                    .time(0, 3)
                    .lifetime(10)
                    .interpolations(6)
                    .updateInterval(updateInterVal)
                    .joint(joint.getName())
                    .itemSkinHand(InteractionHand.MAIN_HAND)
                    .texture("epicfight:textures/particle/swing_trail.png")
                    .type((SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(SwordSoaringConfig.TRAIL_PARTICLE_TYPE.get())))
                    .create());
        }
        return wanTrails;
    }

    public static void buildWanAnim(AnimationManager.AnimationBuilder builder) {
        Armatures.ArmatureAccessor<HumanoidArmature> biped = Armatures.BIPED;
        WAN1_PLAYER = builder.nextAccessor("wan/wan_owner_1", accessor -> new ActionAnimation(0.15F, accessor, biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch.isLogicalClient()){
                                if(livingEntityPatch.getOriginal() == Minecraft.getInstance().player){
                                    SwordSoaringCameraManager.zoomIn(new Vec3f(0, -3, -6), 200);
                                }
                            }
                        }, AnimationEvent.Side.CLIENT),
                        AnimationEvent.SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                serverPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_CONTROLLER).getDataManager().setDataSync(SwordSoaringDatakeys.IS_CHARGING, true);
                            }
                        }, AnimationEvent.Side.SERVER)
                )
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, nextPlay(WAN2_PLAYER))
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 0.5F)));
        WAN2_PLAYER = builder.nextAccessor("wan/wan_owner_2", accessor -> new ActionAnimation(0.0001F, accessor, biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch.getOriginal() == Minecraft.getInstance().player){
                        SwordSoaringCameraManager.zoomIn(new Vec3f(0, -3, -6), 200);
                    }
                }, AnimationEvent.Side.CLIENT))
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        SkillDataManager manager = serverPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_CONTROLLER).getDataManager();
                        if (manager.hasData(SwordSoaringDatakeys.IS_PRESSING) && manager.getDataValue(SwordSoaringDatakeys.IS_PRESSING)) {
                            serverPlayerPatch.reserveAnimation(WAN2_PLAYER);
                        } else {
                            serverPlayerPatch.reserveAnimation(WAN3_PLAYER);
                        }
                    }
                }, AnimationEvent.Side.SERVER))
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false));
        WAN3_PLAYER = builder.nextAccessor("wan/wan_owner_3", accessor ->  new ActionAnimation(0.0001F, accessor, biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch.getOriginal() == Minecraft.getInstance().player){
                                SwordSoaringCameraManager.zoomIn(new Vec3f(0, -3, -6), 100);
                            }
                        }, AnimationEvent.Side.CLIENT),
                        AnimationEvent.SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                serverPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_CONTROLLER).getDataManager().setDataSync(SwordSoaringDatakeys.IS_CHARGING, false);
                            }
                        }, AnimationEvent.Side.SERVER)
                )
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F)));

        Armatures.ArmatureAccessor<WanArmature> wanArmature = SwordSoaringArmatures.WAN_ARMATURE;

        WAN1_L = builder.nextAccessor("wan/wan_l_1", accessor -> {
            BabylonMultiPhaseAttackAnimation animation = new BabylonMultiPhaseAttackAnimation(0.15F, accessor, wanArmature, AnimationUtils.getPhases(wanArmature.get().wanJoints, 0, 2.667F))
                    .addEvents(summonAndPlay(2.30F, WAN2_L))
                    .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 0.5F));
            if(Environment.get().getDist() == Dist.CLIENT){
                animation.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            }
            return animation;
        });
        WAN2_L = builder.nextAccessor("wan/wan_l_2", accessor ->  {
            BabylonMultiPhaseAttackAnimation animation = new BabylonMultiPhaseAttackAnimation(0.0001F, accessor, wanArmature, AnimationUtils.getPhases(wanArmature.get().wanJoints, 0, 2.667F))
                    .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                        if (livingEntityPatch.getOriginal() instanceof WanEntity wanEntity) {
                            if (wanEntity.isOwnerKeyPressing()) {
                                livingEntityPatch.reserveAnimation(WAN2_L);
                            } else {
                                livingEntityPatch.reserveAnimation(WAN3_L);
                            }
                        }
                    }, AnimationEvent.Side.SERVER));
            if(Environment.get().getDist() == Dist.CLIENT){
                animation.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            }
            return animation;
        });
        WAN3_L = builder.nextAccessor( "wan/wan_l_3", accessor ->  {
            BabylonMultiPhaseAttackAnimation animation = new BabylonMultiPhaseAttackAnimation(0.0001F, accessor, wanArmature, AnimationUtils.getPhases(wanArmature.get().wanJoints, 0, 2.667F))
                    .addEvents(summonAndPlay(1.13F, WAN4_L))
                    .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);
            if(Environment.get().getDist() == Dist.CLIENT){
                animation.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            }
            return animation;
        });
        WAN4_L = builder.nextAccessor("wan/wan_l_4", accessor -> {
            BabylonMultiPhaseAttackAnimation animation = new BabylonMultiPhaseAttackAnimation(0.15F, accessor, wanArmature, AnimationUtils.getPhases(wanArmature.get().wanJoints, 0, 2.667F))
                    .addEvents(summonAndPlay(1.13F, WAN_SHOOT_L))
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);
            if(Environment.get().getDist() == Dist.CLIENT){
                animation.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            }
            return animation;
        });
        WAN_SHOOT_L = builder.nextAccessor("wan/wan_shoot_l", accessor ->  {
            BabylonMultiPhaseAttackAnimation animation = new BabylonMultiPhaseAttackAnimation(0.0001F, accessor, wanArmature, AnimationUtils.getPhases(wanArmature.get().wanJoints, 0, 2.667F))
                    .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);
            if(Environment.get().getDist() == Dist.CLIENT){
                animation.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            }
            return animation;
        });

        WAN1_R = builder.nextAccessor("wan/wan_r_1", accessor -> {
            BabylonMultiPhaseAttackAnimation animation = new BabylonMultiPhaseAttackAnimation(0.15F, accessor, wanArmature, AnimationUtils.getPhases(wanArmature.get().wanJoints, 0, 2.667F))
                    .addEvents( summonAndPlay(2.30F, WAN2_R))
                    .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 0.5F));
            if(Environment.get().getDist() == Dist.CLIENT){
                animation.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            }
            return animation;
        });
        WAN2_R = builder.nextAccessor("wan/wan_r_2", accessor ->  {
            BabylonMultiPhaseAttackAnimation animation = new BabylonMultiPhaseAttackAnimation(0.0001F, accessor, wanArmature, AnimationUtils.getPhases(wanArmature.get().wanJoints, 0, 2.667F))
                    .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                        if (livingEntityPatch.getOriginal() instanceof WanEntity wanEntity) {
                            if (wanEntity.isOwnerKeyPressing()) {
                                livingEntityPatch.reserveAnimation(WAN2_R);
                            } else {
                                livingEntityPatch.reserveAnimation(WAN3_R);
                            }
                        }
                    }, AnimationEvent.Side.SERVER));
            if(Environment.get().getDist() == Dist.CLIENT){
                animation.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            }
            return animation;
        });
        WAN3_R = builder.nextAccessor("wan/wan_r_3", accessor -> {
            BabylonMultiPhaseAttackAnimation animation = new BabylonMultiPhaseAttackAnimation(0.0001F, accessor, wanArmature, AnimationUtils.getPhases(wanArmature.get().wanJoints, 0, 2.667F))
                    .addEvents(summonAndPlay(1.13F, WAN4_R))
                    .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);
            if(Environment.get().getDist() == Dist.CLIENT){
                animation.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            }
            return animation;
        });
        WAN4_R = builder.nextAccessor("wan/wan_r_4", accessor ->  {
            BabylonMultiPhaseAttackAnimation animation = new BabylonMultiPhaseAttackAnimation(0.15F, accessor, wanArmature, AnimationUtils.getPhases(wanArmature.get().wanJoints, 0, 2.667F))
                    .addEvents(summonAndPlay(1.13F, WAN_SHOOT_R))
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);
            if(Environment.get().getDist() == Dist.CLIENT){
                animation.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            }
            return animation;
        });
        WAN_SHOOT_R = builder.nextAccessor("wan/wan_shoot_r", accessor -> {
            BabylonMultiPhaseAttackAnimation animation = new BabylonMultiPhaseAttackAnimation(0.0001F, accessor, wanArmature, AnimationUtils.getPhases(wanArmature.get().wanJoints, 0, 2.667F))
                    .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, DISCARD_SELF);
            if(Environment.get().getDist() == Dist.CLIENT){
                animation.addProperty(ClientAnimationProperties.TRAIL_EFFECT, getWanTrails());
            }
            return animation;
        });

    }
}
