package net.p1nero.ss.entity.sword.gate_of_babylon.client;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.p1nero.ss.entity.client.layer.ReplaceableRenderLayer;
import net.p1nero.ss.entity.client.model.EmptyEntityModel;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonEntity;
import net.p1nero.ss.entity.vatansever.client.VatanseverRenderer;
import org.jetbrains.annotations.NotNull;

public class BabylonRenderer extends MobRenderer<BabylonEntity, EmptyEntityModel<BabylonEntity>> {
    public BabylonRenderer(EntityRendererProvider.Context context) {
        super(context, new EmptyEntityModel<>(), 1);
        this.addLayer(new ReplaceableRenderLayer<>(this));
    }

    @Override
    public boolean shouldRender(@NotNull BabylonEntity pLivingEntity, @NotNull Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return pLivingEntity.tickCount > 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BabylonEntity swordEntity) {
        return VatanseverRenderer.TEXTURE;
    }
}