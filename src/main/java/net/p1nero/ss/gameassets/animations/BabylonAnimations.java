package net.p1nero.ss.gameassets.animations;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.p1nero.ss.animation.BabylonMultiPhaseAttackAnimation;
import net.p1nero.ss.client.sound.SwordSoaringSounds;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonArmature;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import net.p1nero.ss.util.AnimationUtils;
import net.p1nero.ss.util.vfx.ParticleVFX;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class BabylonAnimations {

    public static StaticAnimation BABYLON_IDLE;
    public static StaticAnimation BABYLON_SHOOT_START;
    public static StaticAnimation BABYLON_SHOOT_LOOP;
    public static StaticAnimation BABYLON_SUMMON_PLAYER;

    public static void buildBabylonAnim() {
        BabylonArmature babylonArmature = SwordSoaringArmatures.babylonArmature;
        BABYLON_IDLE = new StaticAnimation(true, "babylon/babylon_idle", babylonArmature);
        BABYLON_SHOOT_START = new BabylonMultiPhaseAttackAnimation(0.15F, "babylon/babylon_shoot_start", babylonArmature, AnimationUtils.getPhases(babylonArmature.joints, 1.33F, 3.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.getOriginal().discard(), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F);
        BABYLON_SHOOT_LOOP = new BabylonMultiPhaseAttackAnimation(0.15F, "babylon/babylon_shoot_go", babylonArmature, AnimationUtils.getPhases(babylonArmature.joints, 1.33F, 3.0F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.getOriginal().discard(), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F);
        HumanoidArmature biped = Armatures.BIPED;
        BABYLON_SUMMON_PLAYER = new ActionAnimation(0.15F, 2.4F, "babylon/babylon_shoot_owner", biped)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F)
                .addEvents(AnimationEvent.TimeStampedEvent.create(1.3F, ((livingEntityPatch, staticAnimation, objects) -> {
                    ParticleVFX.createSphereParticles(livingEntityPatch.getOriginal().level, livingEntityPatch.getOriginal().getEyePosition(), ParticleTypes.END_ROD, 5, 0.1, 0.2, 100);
                    livingEntityPatch.playSound(SwordSoaringSounds.VATANSEVER_WHOOSH_BIG.get(), 0.0F, 0.0F);
                }), AnimationEvent.Side.BOTH))
                .newTimePair(0.0F, 2.4F)
                .addStateRemoveOld(EntityState.ATTACK_RESULT, (source -> AttackResult.ResultType.MISSED))
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false);
    }
}