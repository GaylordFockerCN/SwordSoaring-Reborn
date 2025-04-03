package net.p1nero.ss.entity.sword.sword_convergence;

import net.p1nero.ss.entity.sword.gate_of_babylon.AbstractBabylonPatch;
import net.p1nero.ss.gameassets.animations.VatanseverStormAnimations;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.ClientAnimator;

public class SwordConvergencePatch extends AbstractBabylonPatch<SwordConvergenceEntity> {

    @Override
    public void initAnimator(ClientAnimator animator) {
        animator.addLivingAnimation(LivingMotions.IDLE, VatanseverStormAnimations.VATANSEVER_STORM_IDLE);
        animator.setCurrentMotionsAsDefault();
    }

    @Override
    public boolean shouldUseOwnerAttack() {
        return true;
    }
}