package net.p1nero.ss.entity.sword.screen_sword;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.gameassets.animations.ScreenSwordAnimations;
import net.p1nero.ss.network.PacketHandler;
import net.p1nero.ss.network.PacketRelay;
import net.p1nero.ss.network.packet.server.RequestEntityPlayAnimationPacket;
import net.p1nero.ss.skill.sword_controller.KillAuraSkill;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class ScreenSwordPatch extends AbstractArtifactSpiritPatch<ScreenSwordEntity> {
    private boolean played;

    /**
     * Join World的时候主人还没初始化，只能换这里操作
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    protected void clientTick(LivingEvent.LivingTickEvent event) {
        super.clientTick(event);
        if(!played){
            if(this.isLogicalClient() && this.getOwnerPatch() != null){
                if(!this.getOwnerPatch().getOriginal().equals(Minecraft.getInstance().player)){
                    return;
                }
                SkillContainer container = this.getOwnerPatch().getSkill(SwordSoaringSkillSlots.SWORD_CONTROLLER);
                if(container.getSkill() instanceof KillAuraSkill killAuraSkill){
                    StaticAnimation toPlay = killAuraSkill.getSwordSummonAnim().get();
                    PacketRelay.sendToServer(PacketHandler.INSTANCE, new RequestEntityPlayAnimationPacket(this.getOriginal().getId(), toPlay.getNamespaceId(), toPlay.getId(), 0.0001F));
                    played = true;
                }
            }
        }
    }

    @Override
    public void initAnimator(ClientAnimator animator) {
        animator.addLivingAnimation(LivingMotions.IDLE, ScreenSwordAnimations.SCREEN_SWORD_IDLE);
        animator.setCurrentMotionsAsDefault();
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