package net.p1nero.ss.entity.sword.sword_convergence;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.entity.ReplaceableArmature;
import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonEntity;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.gameassets.animations.SwordConvergenceAnimations;
import net.p1nero.ss.skill.sword_controller.WanJianGuiZongSkill;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.List;

public class SwordConvergenceEntity extends BabylonEntity {
    private static final EntityDataAccessor<Integer> SEED = SynchedEntityData.defineId(SwordConvergenceEntity.class, EntityDataSerializers.INT);//双端打乱顺序需要同步

    public SwordConvergenceEntity(EntityType<? extends AbstractArtifactSpiritEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SwordConvergenceEntity(LivingEntity owner) {
        super(SwordSoaringEntities.SWORD_CONVERGENCE_ENTITY.get(), owner, owner.position(), owner.getYRot());
        if (!level.isClientSide) {
            getEntityData().set(SEED, random.nextInt());
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(SEED, 0);
    }

    public long getSeed() {
        return getEntityData().get(SEED);
    }

    @Override
    public boolean shouldGroundSlam() {
        return false;
    }

    public boolean isOwnerCharging(){
        if(getOwnerPatch() instanceof ServerPlayerPatch serverPlayerPatch){
            DynamicAnimation currentOwnerAnim = serverPlayerPatch.getAnimator().getPlayerFor(null).getAnimation();
            return currentOwnerAnim.equals(SwordConvergenceAnimations.WAN1_PLAYER) || currentOwnerAnim.equals(SwordConvergenceAnimations.WAN2_PLAYER);
        }
        return false;
    }

    public boolean isOwnerKeyPressing(){
        if(getOwnerPatch() instanceof ServerPlayerPatch serverPlayerPatch){
            SkillDataManager manager = serverPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_CONTROLLER).getDataManager();
            return manager.hasData(WanJianGuiZongSkill.IS_PRESSING) && manager.getDataValue(WanJianGuiZongSkill.IS_PRESSING);
        }
        return false;
    }

    @Override
    protected void moveToOwner(LivingEntity owner) {
        if(isOwnerKeyPressing() || isOwnerCharging()){
            setYRot(owner.getYRot());
            setYBodyRot(owner.getYRot());
            setYHeadRot(owner.getYRot());
        }
    }

    public ReplaceableArmature getArmature(){
        return SwordSoaringArmatures.wanArmature;
    }

    @Override
    public boolean hasJoint(Joint joint) {
        List<Joint> joints = getArmature().joints;
        if(tickCount > joints.size()) {
            return true;
        }
        for(int i = 0; i < tickCount; i++){
            if(joints.get(i).getId() == joint.getId()){
                return true;
            }
        }
        return false;
    }
}