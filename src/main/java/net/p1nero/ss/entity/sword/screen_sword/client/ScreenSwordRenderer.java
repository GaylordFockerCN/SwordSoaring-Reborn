package net.p1nero.ss.entity.sword.screen_sword.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.p1nero.ss.entity.client.layer.ReplaceableRenderLayer;
import net.p1nero.ss.entity.client.model.EmptyEntityModel;
import net.p1nero.ss.entity.sword.screen_sword.ScreenSwordEntity;
import net.p1nero.ss.entity.vatansever.client.VatanseverRenderer;
import org.jetbrains.annotations.NotNull;

public class ScreenSwordRenderer extends MobRenderer<ScreenSwordEntity, EmptyEntityModel<ScreenSwordEntity>> {
    public ScreenSwordRenderer(EntityRendererProvider.Context context) {
        super(context, new EmptyEntityModel<>(), 1);
        this.addLayer(new ReplaceableRenderLayer<>(this));
    }

    @Override
    public boolean shouldRender(@NotNull ScreenSwordEntity pLivingEntity, @NotNull Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return pLivingEntity.tickCount > 2;
    }

    @Override
    public void render(@NotNull ScreenSwordEntity pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        this.shadowRadius = 0;
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ScreenSwordEntity swordEntity) {
        return VatanseverRenderer.TEXTURE;
    }
}