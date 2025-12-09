package net.p1nero.ss.events;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.SwordSoaringCameraManager;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.SwordSoaringLivingMotions;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.gameassets.SwordSoaringSkills;
import net.p1nero.ss.gameassets.animations.FlyAnimations;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.client.neoevent.UpdatePlayerMotionEvent;
import yesman.epicfight.api.neoevent.InitAnimatorEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.skill.SkillContainer;

@EventBusSubscriber(modid = SwordSoaringMod.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void swordsoaring$onCompositeLayer(UpdatePlayerMotionEvent.CompositeLayer event) {
        handleLayer(event);
    }

    @SubscribeEvent
    public static void swordsoaring$onBaseLayer(UpdatePlayerMotionEvent.BaseLayer event) {
        handleLayer(event);
    }

    public static void handleLayer(UpdatePlayerMotionEvent event) {
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

    @SubscribeEvent
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

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        SwordSoaringCameraManager.tick();
    }

}
