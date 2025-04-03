package net.p1nero.ss.animation;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.entity.PartEntity;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.entity.vatansever.VatanseverEntityPatch;
import net.p1nero.ss.skill.weapon_passive.VatanseverPassive;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.Comparator;
import java.util.List;

public class VatanseverPlayerShootAnimation extends PlayerScanAnimation{
    public VatanseverPlayerShootAnimation(float convertTime, float scanStartTime, float scanEndTime, String path, Armature armature, @Nullable Collider collider, Joint colliderJoint) {
        super(convertTime, scanStartTime, scanEndTime, path, armature, collider, colliderJoint);
    }

    public VatanseverPlayerShootAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
    }

    public VatanseverPlayerShootAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, path, armature);
    }

    public VatanseverPlayerShootAnimation(float convertTime, String path, Armature armature, Phase... phases) {
        super(convertTime, path, armature, phases);
    }

    @Override
    protected void searchAndSetTarget(LivingEntityPatch<?> entityPatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, Phase phase) {
        entityPatch.getArmature().initializeTransform();
        float prevPoseTime = prevState.attacking() ? prevElapsedTime : phase.preDelay;
        float poseTime = state.attacking() ? elapsedTime : phase.contact;
        List<Entity> list = this.getPhaseByTime(elapsedTime).getCollidingEntities(entityPatch, this, prevPoseTime, poseTime, this.getPlaySpeed(entityPatch));
        if (list.contains(entityPatch.getTarget())) {
            return;
        }
        list.sort(Comparator.comparingDouble((entity) -> entity.distanceTo(entityPatch.getOriginal())));
        for(Entity target : list){
            LivingEntity trueEntity = this.getTrueEntity(target);
            if (trueEntity != null && trueEntity.isAlive() && !entityPatch.isTeammate(trueEntity)) {
                if (target instanceof LivingEntity || target instanceof PartEntity) {
                    if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        if(target instanceof AbstractArtifactSpiritEntity artifactSpiritEntity && serverPlayerPatch.getOriginal().equals(artifactSpiritEntity.getOwner())){
                            continue;
                        }
                        SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
                        VatanseverEntityPatch vatanseverEntityPatch = EpicFightCapabilities.getEntityPatch(serverPlayerPatch.getOriginal().level.getEntity(manager.getDataValue(VatanseverPassive.ARTIFACT_SPIRIT_ENTITY_ID)), VatanseverEntityPatch.class);
                        if(vatanseverEntityPatch != null){
                            vatanseverEntityPatch.setAttakTargetSync(trueEntity);
                            break;
                        }
                    }
                }
            }
        }
    }
}