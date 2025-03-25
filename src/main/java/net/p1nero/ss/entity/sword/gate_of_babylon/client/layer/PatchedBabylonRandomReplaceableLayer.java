package net.p1nero.ss.entity.sword.gate_of_babylon.client.layer;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.entity.ReplaceableArmature;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonEntity;
import net.p1nero.ss.util.MathUtils;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PatchedBabylonRandomReplaceableLayer<E extends BabylonEntity, T extends AbstractArtifactSpiritPatch<E>, M extends EntityModel<E>, AM extends AnimatedMesh> extends PatchedLayer<E, T, M, RenderLayer<E, M>, AM> {
    public PatchedBabylonRandomReplaceableLayer() {
        super(null);
    }
    public static final int FADE_TIME = 20, LIFE_TIME = 130;
    public static final ResourceLocation LIGHT_TEXTURE = new ResourceLocation(SwordSoaring.MOD_ID, "textures/entity/light_new.png");
    public static final ResourceLocation PORTAL_TEXTURE = new ResourceLocation(SwordSoaring.MOD_ID, "textures/entity/portal_new.png");

    @Override
    protected void renderLayer(T entityPatch, E entity, RenderLayer<E, M> vanillaLayer, PoseStack postStack, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
        if (entityPatch.getArmature() instanceof ReplaceableArmature armature) {
            renderItemInJoint(entity, entityPatch, armature, poses, buffer, postStack, LightTexture.FULL_BRIGHT);
        }
    }

    /**
     * 根据王之宝库随机替换Joint的位置渲染
     */
    public static void renderItemInJoint(BabylonEntity entity, AbstractArtifactSpiritPatch<?> artifactSpiritPatch, ReplaceableArmature armature, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
        if (entity.getOwner() != null) {
            List<ItemStack> babylons = entity.getValidBabylonItems();
            if (babylons.isEmpty()) {
                return;
            }
            List<Joint> jointList = armature.getJoints(artifactSpiritPatch);
            for (int i = 0; i < jointList.size() && i < babylons.size(); i++) {
                Joint joint = jointList.get(i);
                ItemStack itemStack = babylons.get(i);//有多少射多少，穷鬼莫玩
                if (itemStack != null) {
                    OpenMatrix4f jointTransform = poses[joint.getId()];
                    if (entity.getStartTransform(joint.getId()) == null && jointTransform.toScaleVector().length() > 0) {
                        entity.bindStartTransform(joint.getId(), MathUtils.removeScale(jointTransform));
                    }

                    //画传送门
                    poseStack.pushPose();

                    OpenMatrix4f startMatrix = entity.getStartTransform(joint.getId());
                    MathUtils.mulPoseStack(poseStack, startMatrix == null ? jointTransform : startMatrix);
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
                    float alpha = 1.0F;
                    int currentTickCount = entity.tickCount - 15;
                    if(currentTickCount < FADE_TIME){
                        alpha = currentTickCount * 1.0F / FADE_TIME;
                    }
                    if(currentTickCount > LIFE_TIME - FADE_TIME){
                        alpha = (LIFE_TIME - currentTickCount) * 1.0F / FADE_TIME;
                    }
                    final float outerAlpha = alpha;
                    float frame = currentTickCount * 1.0F % 20;
                    //画核心圈圈


                    poseStack.pushPose();
                    poseStack.scale(outerAlpha, outerAlpha, outerAlpha);
                    poseStack.scale(0.7F, 0.7F, 0.7F);
                    renderPortal(poseStack, 0.5F, 1.0F, 1.0F, 1.0F, LIGHT_TEXTURE, buffer,frame);
                    renderPortal(poseStack, 1, 1.0F, 1.0F, 1.0F, PORTAL_TEXTURE, buffer,frame);
                    poseStack.popPose();

                    poseStack.popPose();

                    //画在Joint上
                    poseStack.pushPose();
                    MathUtils.mulPoseStack(poseStack, jointTransform);
                    ItemTransforms.TransformType transformType = ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
                    Minecraft.getInstance().getItemInHandRenderer().renderItem(artifactSpiritPatch.getOriginal(), itemStack, transformType, false, poseStack, buffer, packedLight);
                    poseStack.popPose();
                }
            }
        }
    }

    /**
     * 曦月哥的恩情还不完\ToT/  \ToT/  \ToT/  \ToT/
     */
    public static void renderPortal(PoseStack poseStack, float alpha, float r, float g, float b, ResourceLocation portalTexture, MultiBufferSource buffer,float frame) {
        poseStack.pushPose();
        Matrix4f pMatrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(portalTexture, false));
        consumer.vertex(pMatrix, -1, -1, 0).color(r, g, b, alpha).uv(0, 0.05F*frame-0.05F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 0,1).endVertex();
        consumer.vertex(pMatrix, 1, -1, 0).color(r, g, b, alpha).uv(1, 0.05F*frame-0.05F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 0,1).endVertex();
        consumer.vertex(pMatrix, 1, 1, 0).color(r, g, b, alpha).uv(1, 0.05F*frame).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(0, 0,1).endVertex();
        consumer.vertex(pMatrix, -1, 1, 0).color(r, g, b, alpha).uv(0, 0.05F*frame).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal( 0, 0,1).endVertex();

        poseStack.popPose();
    }

}