package net.p1nero.ss.entity.sword.fly_sword;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.gameassets.animations.FlySwordAnimations;
import net.p1nero.ss.network.packet.server.RequestEntityPlayAnimationPacket;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;

public class FlySwordPatch extends AbstractArtifactSpiritPatch<FlySwordEntity> {
    private boolean played;
    public List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> list = List.of(FlySwordAnimations.FLY_SWORD_ATK_4_1, FlySwordAnimations.FLY_SWORD_ATK_4_2, FlySwordAnimations.FLY_SWORD_ATK_4_3, FlySwordAnimations.FLY_SWORD_ATK_4_4, FlySwordAnimations.FLY_SWORD_ATK_3);

    public FlySwordPatch(FlySwordEntity entity) {
        super(entity);
    }

    /**
     * 播放初始动画
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void postTickClient() {
        super.postTickClient();
        if (!played) {
            if (this.isLogicalClient() && this.getOwnerPatch() != null) {
                if(!this.getOwnerPatch().getOriginal().equals(Minecraft.getInstance().player)){
                    return;
                }
                AnimationManager.AnimationAccessor<? extends StaticAnimation> toPlay = getInitAnimation(this.getOwnerPatch());
                PacketDistributor.sendToServer(new RequestEntityPlayAnimationPacket(this.getOriginal().getId(), toPlay.id(), 0.0001F));
                played = true;
            }
        }
    }

    public AnimationManager.AnimationAccessor<? extends StaticAnimation> getInitAnimation(PlayerPatch<?> ownerPatch){
        AnimationManager.AnimationAccessor<? extends StaticAnimation> toPlay = this.getOriginal().getAnimationToPlay();
        if (toPlay != null) {
            return toPlay;
        } else {
            return list.get(getOriginal().getRandom().nextInt(list.size()));
        }
    }

    @Override
    protected void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, FlySwordAnimations.FLY_SWORD_ATK_IDLE);
        animator.addLivingAnimation(LivingMotions.FLY, FlySwordAnimations.FLY_SWORD_ATK_FLY);
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
