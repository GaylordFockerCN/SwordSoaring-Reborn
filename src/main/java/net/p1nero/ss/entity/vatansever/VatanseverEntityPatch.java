package net.p1nero.ss.entity.vatansever;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.animations.VatanseverAnimations;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;

public class VatanseverEntityPatch extends AbstractArtifactSpiritPatch<VatanseverEntity> {

    public VatanseverEntityPatch(VatanseverEntity entity) {
        super(entity);
    }

    /**
     * 刚加入时服务端调用playSync客户端会来不及播，只能手动修
     */
    @Override
    public void onJoinWorld(VatanseverEntity entity, Level level, boolean worldgenSpawn) {
        super.onJoinWorld(entity, level, worldgenSpawn);
        if(this.isLogicalClient()){
            this.getClientAnimator().playAnimation(VatanseverAnimations.VATANSEVER_INIT, 0.0F);
        }
    }

    @Override
    protected void initAnimator(Animator animator) {
        super.initAnimator(animator);
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
            if (manager.hasData(SwordSoaringDatakeys.SWORD_COUNT)) {
                return manager.getDataValue(SwordSoaringDatakeys.SWORD_COUNT);
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
