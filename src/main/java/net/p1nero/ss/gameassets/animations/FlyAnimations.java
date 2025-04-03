package net.p1nero.ss.gameassets.animations;

import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class FlyAnimations {
    public static StaticAnimation APPRENTICE_INIT;
    public static StaticAnimation APPRENTICE_FLYING;
    public static StaticAnimation APPRENTICE_ACCELERATION;
    public static StaticAnimation EXPERT_INIT;
    public static StaticAnimation EXPERT_FLYING;
    public static StaticAnimation EXPERT_ACCELERATION;
    public static StaticAnimation MASTER_INIT;
    public static StaticAnimation MASTER_FLYING;
    public static StaticAnimation MASTER_ACCELERATION;

    public static void buildFlyAnim() {
        HumanoidArmature biped = Armatures.BIPED;

        APPRENTICE_INIT = new ActionAnimation(0.15F, "biped/fly_anim/fly_on_sword_apprentice_initiation", biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(APPRENTICE_FLYING)), AnimationEvent.Side.SERVER));
        APPRENTICE_FLYING = new StaticAnimation(true, "biped/fly_anim/fly_on_sword_apprentice_flying", biped);
        APPRENTICE_ACCELERATION = new StaticAnimation(true, "biped/fly_anim/fly_on_sword_apprentice_acceleration", biped);

        EXPERT_INIT = new ActionAnimation(0.15F, "biped/fly_anim/fly_on_sword_expert_initiation", biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(EXPERT_FLYING)), AnimationEvent.Side.SERVER));
        EXPERT_FLYING = new StaticAnimation(true, "biped/fly_anim/fly_on_sword_expert_flying", biped);
        EXPERT_ACCELERATION = new StaticAnimation(true, "biped/fly_anim/fly_on_sword_expert_acceleration", biped);

        MASTER_INIT = new ActionAnimation(0.15F, "biped/fly_anim/fly_on_sword_master_initiation", biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(MASTER_FLYING)), AnimationEvent.Side.SERVER));
        MASTER_FLYING = new StaticAnimation(true, "biped/fly_anim/fly_on_sword_master_flying", biped);
        MASTER_ACCELERATION = new StaticAnimation(true, "biped/fly_anim/fly_on_sword_master_acceleration", biped);

    }
}