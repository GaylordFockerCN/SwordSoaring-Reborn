package net.p1nero.ss.entity.sword.fly_sword;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.gameassets.animations.FlySwordAnimations;
import net.p1nero.ss.item.VatanseverItem;
import net.p1nero.ss.network.PacketHandler;
import net.p1nero.ss.network.PacketRelay;
import net.p1nero.ss.network.packet.server.RequestEntityPlayAnimationPacket;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;

public class FlySwordPatch extends AbstractArtifactSpiritPatch<FlySwordEntity> {
    private boolean played;
    public List<StaticAnimation> list = List.of(FlySwordAnimations.FLY_SWORD_ATK_4_1, FlySwordAnimations.FLY_SWORD_ATK_4_2, FlySwordAnimations.FLY_SWORD_ATK_4_3, FlySwordAnimations.FLY_SWORD_ATK_4_4, FlySwordAnimations.FLY_SWORD_ATK_3);

    /**
     * 播放初始动画
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    protected void clientTick(LivingEvent.LivingTickEvent event) {
        super.clientTick(event);
        if (!played) {
            if (this.isLogicalClient() && this.getOwnerPatch() != null) {
                if(!this.getOwnerPatch().getOriginal().equals(Minecraft.getInstance().player)){
                    return;
                }
                StaticAnimation toPlay = getInitAnimation(this.getOwnerPatch());
                PacketRelay.sendToServer(PacketHandler.INSTANCE, new RequestEntityPlayAnimationPacket(this.getOriginal().getId(), toPlay.getNamespaceId(), toPlay.getId(), 0.0001F));
                played = true;
            }
        }
    }

    public StaticAnimation getInitAnimation(PlayerPatch<?> ownerPatch){
        StaticAnimation toPlay = this.getOriginal().getAnimationToPlay();
        if (toPlay != null) {
            return toPlay;
        } else {
            return list.get(getOriginal().getRandom().nextInt(list.size()));
        }
    }

    @Override
    public void initAnimator(ClientAnimator animator) {
        animator.addLivingAnimation(LivingMotions.IDLE, FlySwordAnimations.FLY_SWORD_ATK_IDLE);
        animator.addLivingAnimation(LivingMotions.FLY, FlySwordAnimations.FLY_SWORD_ATK_FLY);
        animator.setCurrentMotionsAsDefault();
    }

    @Override
    public Collider getColliderMatching(InteractionHand hand) {
        return EpicFightCapabilities.getItemStackCapability(getOriginal().getItemStack(this)).getWeaponCollider();
    }

    @Override
    public void updateMotion(boolean considerInaction) {
        if (getOriginal().isFlyingBack()) {
            this.currentLivingMotion = LivingMotions.FLY;
            this.currentCompositeMotion = LivingMotions.FLY;
        } else {
            keepIdleMotion(considerInaction);
        }
    }

    @Override
    public OpenMatrix4f getModelMatrix(float partialTicks) {
        if (this.getOriginal().isFlyingBack() && getOwnerPatch() != null) {
            Vec3 dir = getOwnerPatch().getOriginal().getEyePosition(partialTicks).subtract(this.getOriginal().getPosition(partialTicks)).normalize();
            float xRot = (float) MathUtils.getXRotOfVector(dir);
            float yRot = (float) MathUtils.getYRotOfVector(dir);
            return MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, xRot, xRot, yRot, yRot, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        return super.getModelMatrix(partialTicks);
    }
}