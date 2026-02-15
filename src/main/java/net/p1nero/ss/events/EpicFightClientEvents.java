package net.p1nero.ss.events;

import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.entity.sword.fly_sword.client.PatchedFlySwordRenderer;
import net.p1nero.ss.entity.sword.gate_of_babylon.client.PatchedBabylonRenderer;
import net.p1nero.ss.entity.sword.screen_sword.client.PatchedScreenSwordRenderer;
import net.p1nero.ss.entity.sword.wan.client.PatchedWanRenderer;
import net.p1nero.ss.entity.vatansever.client.PatchedVatanseverRenderer;
import net.p1nero.ss.entity.vatansever_storm.client.PatchedVatanseverStormRenderer;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.SwordSoaringLivingMotions;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.gameassets.SwordSoaringSkills;
import net.p1nero.ss.gameassets.animations.FlyAnimations;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.client.event.types.entity.ModifyPlayerLivingMotionEvent;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;
import yesman.epicfight.api.event.types.animation.InitAnimatorEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.skill.SkillContainer;

public class EpicFightClientEvents {

    public static void swordsoaring$onCompositeLayer(ModifyPlayerLivingMotionEvent.CompositeLayer event) {
        handleLayer(event);
    }

    public static void swordsoaring$onBaseLayer(ModifyPlayerLivingMotionEvent.BaseLayer event) {
        handleLayer(event);
    }

    public static void handleLayer(ModifyPlayerLivingMotionEvent event) {
        AbstractClientPlayerPatch<?> clientPlayerPatch = event.getPlayerPatch();
        SkillContainer container = clientPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_SOARING);
        if(!container.hasSkill()) {
            return;
        }
        if(container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING)) {
            if(container.hasSkill(SwordSoaringSkills.SWORD_SOARING_APPRENTICE.get()) || container.hasSkill(SwordSoaringSkills.SWORD_SOARING_ELYTRA_APPRENTICE.get())) {
                event.setMotion(SwordSoaringLivingMotions.SWORD_SOARING_BASIC);
            }
            if(container.hasSkill(SwordSoaringSkills.SWORD_SOARING_EXPERT.get()) || container.hasSkill(SwordSoaringSkills.SWORD_SOARING_ELYTRA_EXPERT.get()) ) {
                event.setMotion(SwordSoaringLivingMotions.SWORD_SOARING_EXPERT);
            }
            if(container.hasSkill(SwordSoaringSkills.SWORD_SOARING_MASTER.get()) || container.hasSkill(SwordSoaringSkills.SWORD_SOARING_ELYTRA_MASTER.get())) {
                event.setMotion(SwordSoaringLivingMotions.SWORD_SOARING_MASTER);
            }
        }

        if(container.getDataManager().getDataValue(SwordSoaringDatakeys.SPEED_UP)) {
            if(container.hasSkill(SwordSoaringSkills.SWORD_SOARING_APPRENTICE.get()) || container.hasSkill(SwordSoaringSkills.SWORD_SOARING_ELYTRA_APPRENTICE.get())) {
                event.setMotion(SwordSoaringLivingMotions.SPEED_UP_BASIC);
            }
            if(container.hasSkill(SwordSoaringSkills.SWORD_SOARING_EXPERT.get()) || container.hasSkill(SwordSoaringSkills.SWORD_SOARING_ELYTRA_EXPERT.get())) {
                event.setMotion(SwordSoaringLivingMotions.SPEED_UP_EXPERT);
            }
            if(container.hasSkill(SwordSoaringSkills.SWORD_SOARING_MASTER.get()) || container.hasSkill(SwordSoaringSkills.SWORD_SOARING_ELYTRA_MASTER.get())) {
                event.setMotion(SwordSoaringLivingMotions.SPEED_UP_MASTER);
            }
        }
    }

    public static void swordsoaring$onAnimatorInit(InitAnimatorEvent event) {
        Animator animator = event.getAnimator();

        if(!animator.getLivingAnimations().containsKey(SwordSoaringLivingMotions.SWORD_SOARING_BASIC)) {
            animator.addLivingAnimation(SwordSoaringLivingMotions.SWORD_SOARING_BASIC, FlyAnimations.APPRENTICE_FLYING);
        }

        if(!animator.getLivingAnimations().containsKey(SwordSoaringLivingMotions.SWORD_SOARING_EXPERT)) {
            animator.addLivingAnimation(SwordSoaringLivingMotions.SWORD_SOARING_EXPERT, FlyAnimations.EXPERT_FLYING);
        }

        if(!animator.getLivingAnimations().containsKey(SwordSoaringLivingMotions.SWORD_SOARING_MASTER)) {
            animator.addLivingAnimation(SwordSoaringLivingMotions.SWORD_SOARING_MASTER, FlyAnimations.MASTER_FLYING);
        }

        if(!animator.getLivingAnimations().containsKey(SwordSoaringLivingMotions.SPEED_UP_BASIC)) {
            animator.addLivingAnimation(SwordSoaringLivingMotions.SPEED_UP_BASIC, FlyAnimations.APPRENTICE_SPEED_UP);
        }

        if(!animator.getLivingAnimations().containsKey(SwordSoaringLivingMotions.SPEED_UP_EXPERT)) {
            animator.addLivingAnimation(SwordSoaringLivingMotions.SPEED_UP_EXPERT, FlyAnimations.EXPERT_SPEED_UP);
        }

        if(!animator.getLivingAnimations().containsKey(SwordSoaringLivingMotions.SPEED_UP_MASTER)) {
            animator.addLivingAnimation(SwordSoaringLivingMotions.SPEED_UP_MASTER, FlyAnimations.MASTER_SPEED_UP);
        }
    }

    public static void registerPatchedRenderer(RegisterPatchedRenderersEvent.AddEntity event){
        event.addPatchedEntityRenderer(SwordSoaringEntities.WAN_ENTITY.get(), entityType -> new PatchedWanRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(SwordSoaringEntities.BABYLON.get(), entityType -> new PatchedBabylonRenderer<>(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(SwordSoaringEntities.FLY_SWORD.get(), entityType -> new PatchedFlySwordRenderer<>(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(SwordSoaringEntities.SCREEN_SWORD.get(),entityType -> new PatchedScreenSwordRenderer<>(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(SwordSoaringEntities.VATANSEVER.get(), entityType -> new PatchedVatanseverRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(SwordSoaringEntities.VATANSEVER_STORM.get(), entityType -> new PatchedVatanseverStormRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
    }

}
