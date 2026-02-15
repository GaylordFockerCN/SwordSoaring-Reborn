package net.p1nero.ss.entity.sword.screen_sword;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.gameassets.animations.ScreenSwordAnimations;
import net.p1nero.ss.network.packet.server.RequestEntityPlayAnimationPacket;
import net.p1nero.ss.skill.sword_controller.KillAuraSkill;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class ScreenSwordPatch extends AbstractArtifactSpiritPatch<ScreenSwordEntity> {
    private boolean played;

    public ScreenSwordPatch(ScreenSwordEntity entity) {
        super(entity);
    }

    /**
     * Join World的时候主人还没初始化，只能换这里操作
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void preTickClient() {
        super.preTickClient();
        if(!played){
            if(this.isLogicalClient() && this.getOwnerPatch() != null){
                if(!this.getOwnerPatch().getOriginal().equals(Minecraft.getInstance().player)){
                    return;
                }
                SkillContainer container = this.getOwnerPatch().getSkill(SwordSoaringSkillSlots.SWORD_CONTROLLER);
                if(container.getSkill() instanceof KillAuraSkill killAuraSkill){
                    AnimationManager.AnimationAccessor<? extends StaticAnimation> toPlay = killAuraSkill.getSwordSummonAnim();
                    PacketDistributor.sendToServer(new RequestEntityPlayAnimationPacket(this.getOriginal().getId(), toPlay.id(), 0.0001F));
                    played = true;
                }
            }
        }
    }

    @Override
    protected void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, ScreenSwordAnimations.SCREEN_SWORD_IDLE);
    }

    @Override
    public Collider getColliderMatching(InteractionHand hand) {
        return EpicFightCapabilities.getItemStackCapability(getOriginal().getItemStack(this)).getWeaponCollider();
    }


    @Override
    public void updateMotion(boolean considerInaction) {
        keepIdleMotion(considerInaction);
    }
}
