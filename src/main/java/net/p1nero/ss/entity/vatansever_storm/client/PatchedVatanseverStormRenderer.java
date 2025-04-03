package net.p1nero.ss.entity.vatansever_storm.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.entity.client.model.EmptyEntityModel;
import net.p1nero.ss.entity.vatansever_storm.VatanseverStormEntity;
import net.p1nero.ss.entity.vatansever_storm.VatanseverStormEntityPatch;
import net.p1nero.ss.gameassets.SwordSoaringMeshes;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.forgeevent.PrepareModelEvent;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class PatchedVatanseverStormRenderer extends PatchedLivingEntityRenderer<VatanseverStormEntity, VatanseverStormEntityPatch, EmptyEntityModel<VatanseverStormEntity>, VatanseverStormMesh> {
    public static final int FADE_TIME = 100;
    @Override
    public void render(VatanseverStormEntity entityIn, VatanseverStormEntityPatch entitypatch, LivingEntityRenderer<VatanseverStormEntity, EmptyEntityModel<VatanseverStormEntity>> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
        try {
            RenderNameTagEvent renderNameTagEvent = new RenderNameTagEvent(entityIn, entityIn.getDisplayName(), renderer, poseStack, buffer, packedLight, partialTicks);
            MinecraftForge.EVENT_BUS.post(renderNameTagEvent);
            if (((Boolean)shouldShowName.invoke(renderer, entityIn) || renderNameTagEvent.getResult() == Event.Result.ALLOW) && renderNameTagEvent.getResult() != Event.Result.DENY) {
                renderNameTag.invoke(renderer, entityIn, renderNameTagEvent.getContent(), poseStack, buffer, packedLight);
            }
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException exception) {
            SwordSoaring.LOGGER.error("error rendering VatanseverStorm", exception);
        }
        float alpha = 1.0F;
        int dif = VatanseverStormEntity.MAX_LIFE_TIME - entityIn.tickCount;
        if(dif < FADE_TIME){
            alpha = dif * 1.0F / FADE_TIME;
        }
        Minecraft mc = Minecraft.getInstance();
        boolean isVisible = this.isVisible(renderer, entityIn);
        boolean isVisibleToPlayer = !isVisible && !entityIn.isInvisibleTo(Objects.requireNonNull(mc.player));
        boolean isGlowing = mc.shouldEntityAppearGlowing(entityIn);
        RenderType renderType = this.getRenderType(entityIn, entitypatch, renderer, isVisible, isVisibleToPlayer, isGlowing);
        Armature armature = entitypatch.getArmature();
        poseStack.pushPose();
        this.mulPoseStack(poseStack, armature, entityIn, entitypatch, partialTicks);
        OpenMatrix4f[] poseMatrices = this.getPoseMatrices(entitypatch, armature, partialTicks);
        if (renderType != null) {
            this.prepareVanillaModel(entityIn, renderer.getModel(), renderer, partialTicks);
            VatanseverStormMesh mesh = this.getMesh(entitypatch);
            this.prepareModel(mesh, entityIn, entitypatch);
            PrepareModelEvent prepareModelEvent = new PrepareModelEvent(this, mesh, entitypatch, buffer, poseStack, packedLight, partialTicks);
            if (!MinecraftForge.EVENT_BUS.post(prepareModelEvent)) {
                VertexConsumer builder = buffer.getBuffer(renderType);
                mesh.drawModelWithPose(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, alpha, this.getOverlayCoord(entityIn, entitypatch, partialTicks), armature, poseMatrices);
            }
        }

        if (!entityIn.isSpectator()) {
            this.renderLayer(renderer, entitypatch, entityIn, poseMatrices, buffer, poseStack, packedLight, partialTicks);
        }

        if (renderType != null && Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            for (Layer layer : entitypatch.getClientAnimator().getAllLayers()) {
                AnimationPlayer animPlayer = layer.animationPlayer;
                float playTime = animPlayer.getPrevElapsedTime() + (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()) * partialTicks;
                animPlayer.getAnimation().renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
            }
        }

        poseStack.popPose();
    }

    @Override
    public VatanseverStormMesh getMesh(VatanseverStormEntityPatch vatanseverStormEntityPatch) {
        return SwordSoaringMeshes.vatanseverStormMesh;
    }

//    /**
//     * 做渐隐
//     */
//    @Override
//    public RenderType getRenderType(VatanseverStormEntity entityIn, VatanseverStormEntityPatch entitypatch, LivingEntityRenderer<VatanseverStormEntity, EmptyEntityModel<VatanseverStormEntity>> renderer, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
//        return RenderType.entityTranslucent(renderer.getTextureLocation(entityIn));
//    }
}