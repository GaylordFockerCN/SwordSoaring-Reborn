package net.p1nero.ss.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.p1nero.ss.entity.client.model.EmptyEntityModel;
import org.jetbrains.annotations.NotNull;

public class ReplaceableRenderLayer<T extends LivingEntity> extends RenderLayer<T, EmptyEntityModel<T>> {

    public ReplaceableRenderLayer(RenderLayerParent<T, EmptyEntityModel<T>> pRenderer) {
        super(pRenderer);
    }

    /**
     * 什么都不做，假图层，实际渲染在 {@link PatchedReplaceableLayer} 里
     */
    @Override
    public void render(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {}
}