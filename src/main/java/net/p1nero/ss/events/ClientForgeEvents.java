package net.p1nero.ss.events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.SwordSoairngCameraManager;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.SwordSoaringLivingMotions;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.gameassets.animations.FlyAnimations;
import net.p1nero.ss.gameassets.skills.FlyingSkills;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.client.forgeevent.UpdatePlayerMotionEvent;
import yesman.epicfight.api.forgeevent.InitAnimatorEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.skill.SkillContainer;

@Mod.EventBusSubscriber(modid = SwordSoaringMod.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void swordsoaring$onCustomLayer(UpdatePlayerMotionEvent event) {
        AbstractClientPlayerPatch<?> clientPlayerPatch = event.getPlayerPatch();
        SkillContainer container = clientPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_SOARING);
        if(!container.hasSkill()) {
            return;
        }
        if(container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get())) {
            if(container.hasSkill(FlyingSkills.SWORD_SOARING_APPRENTICE) || container.hasSkill(FlyingSkills.SWORD_SOARING_ELYTRA_APPRENTICE)) {
                event.setMotion(SwordSoaringLivingMotions.SWORD_SOARING_BASIC);
            }
            if(container.hasSkill(FlyingSkills.SWORD_SOARING_EXPERT) || container.hasSkill(FlyingSkills.SWORD_SOARING_ELYTRA_EXPERT) ) {
                event.setMotion(SwordSoaringLivingMotions.SWORD_SOARING_EXPERT);
            }
            if(container.hasSkill(FlyingSkills.SWORD_SOARING_MASTER) || container.hasSkill(FlyingSkills.SWORD_SOARING_ELYTRA_MASTER)) {
                event.setMotion(SwordSoaringLivingMotions.SWORD_SOARING_MASTER);
            }
        }

        if(container.getDataManager().getDataValue(SwordSoaringDatakeys.SPEED_UP.get())) {
            if(container.hasSkill(FlyingSkills.SWORD_SOARING_APPRENTICE) || container.hasSkill(FlyingSkills.SWORD_SOARING_ELYTRA_APPRENTICE)) {
                event.setMotion(SwordSoaringLivingMotions.SPEED_UP_BASIC);
            }
            if(container.hasSkill(FlyingSkills.SWORD_SOARING_EXPERT) || container.hasSkill(FlyingSkills.SWORD_SOARING_ELYTRA_EXPERT) ) {
                event.setMotion(SwordSoaringLivingMotions.SPEED_UP_EXPERT);
            }
            if(container.hasSkill(FlyingSkills.SWORD_SOARING_MASTER) || container.hasSkill(FlyingSkills.SWORD_SOARING_ELYTRA_MASTER)) {
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
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            return;
        }
        SwordSoairngCameraManager.tick();
    }
}
