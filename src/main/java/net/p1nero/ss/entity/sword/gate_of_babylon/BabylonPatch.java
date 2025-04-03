package net.p1nero.ss.entity.sword.gate_of_babylon;

import net.p1nero.ss.gameassets.animations.BabylonAnimations;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.ClientAnimator;

public class BabylonPatch extends AbstractBabylonPatch<BabylonEntity> {
    @Override
    public void initAnimator(ClientAnimator animator) {
        animator.addLivingAnimation(LivingMotions.IDLE, BabylonAnimations.BABYLON_IDLE);
        animator.setCurrentMotionsAsDefault();
    }
}