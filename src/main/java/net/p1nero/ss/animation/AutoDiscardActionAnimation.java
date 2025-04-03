package net.p1nero.ss.animation;

import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * 借碰撞箱
 */
public class AutoDiscardActionAnimation extends ActionAnimation {

    public AutoDiscardActionAnimation(float convertTime, String path, Armature armature) {
        super(convertTime, path, armature);
    }

    public AutoDiscardActionAnimation(float convertTime, float postDelay, String path, Armature armature) {
        super(convertTime, postDelay, path, armature);
    }

    @Override
    public void end(LivingEntityPatch<?> entityPatch, DynamicAnimation nextAnimation, boolean isEnd) {
        super.end(entityPatch, nextAnimation, isEnd);
        if(!entityPatch.isLogicalClient()){
            entityPatch.getOriginal().discard();
        }
    }
}