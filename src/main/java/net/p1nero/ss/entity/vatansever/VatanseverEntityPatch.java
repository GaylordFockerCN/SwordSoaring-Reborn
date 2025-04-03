package net.p1nero.ss.entity.vatansever;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.gameassets.animations.VatanseverAnimations;
import net.p1nero.ss.skill.weapon_passive.VatanseverPassive;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;

public class VatanseverEntityPatch extends AbstractArtifactSpiritPatch<VatanseverEntity> {

    /**
     * 刚加入时服务端调用playSync客户端会来不及播，不知为何WitherClone可以我不行。只能手动修
     */
    @Override
    public void onJoinWorld(VatanseverEntity entityIn, EntityJoinLevelEvent event) {
        super.onJoinWorld(entityIn, event);
        if(this.isLogicalClient()){
            this.getClientAnimator().playAnimation(VatanseverAnimations.VATANSEVER_INIT, 0.0F);
        }
    }

    @Override
    public void initAnimator(ClientAnimator animator) {
        animator.addLivingAnimation(LivingMotions.IDLE, VatanseverAnimations.VATANSEVER_IDLE);
        animator.addLivingAnimation(LivingMotions.WALK, VatanseverAnimations.VATANSEVER_WALK);
        animator.addLivingAnimation(LivingMotions.RUN, VatanseverAnimations.VATANSEVER_RUN);
        animator.addLivingAnimation(LivingMotions.CHASE, VatanseverAnimations.VATANSEVER_RUN);
        animator.addLivingAnimation(LivingMotions.JUMP, VatanseverAnimations.VATANSEVER_FALL);
        animator.addLivingAnimation(LivingMotions.FALL, VatanseverAnimations.VATANSEVER_FALL);
        animator.addLivingAnimation(LivingMotions.FLOAT, VatanseverAnimations.VATANSEVER_FLOAT);
        animator.addLivingAnimation(LivingMotions.SWIM, VatanseverAnimations.VATANSEVER_SWIM);
        animator.addLivingAnimation(LivingMotions.DEATH, VatanseverAnimations.VATANSEVER_DEATH);
        animator.addLivingAnimation(LivingMotions.FLY, VatanseverAnimations.VATANSEVER_FLY);
        animator.addLivingAnimation(LivingMotions.KNEEL, VatanseverAnimations.VATANSEVER_SNEAK);
        animator.addLivingAnimation(LivingMotions.SNEAK, VatanseverAnimations.VATANSEVER_SNEAK);
        animator.setCurrentMotionsAsDefault();
    }

    public boolean isOwnerFallFlying(){
        if(getOwnerPatch() != null){
            return getOwnerPatch().getOriginal().isFallFlying();
        }
        return false;
    }

    public int getLeftSwordCount(){
        if (getOwnerPatch() != null) {
            SkillDataManager manager = getOwnerPatch().getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
            if (manager.hasData(VatanseverPassive.SWORD_COUNT)) {
                return manager.getDataValue(VatanseverPassive.SWORD_COUNT);
            }
        }
        return 6;
    }

    /**
     * 同步旋转（byd卡顿也同步上了）
     */
    @Override
    public OpenMatrix4f getModelMatrix(float partialTicks) {
        if(getOwnerPatch() != null && isOwnerFallFlying() && isLogicalClient()){
            return getOwnerPatch().getModelMatrix(partialTicks);
        }
        return super.getModelMatrix(partialTicks);
    }

}
