package net.p1nero.ss.entity.sword.sword_convergence.client;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.entity.client.layer.ReplaceableRenderLayer;
import net.p1nero.ss.entity.client.model.EmptyEntityModel;
import org.jetbrains.annotations.NotNull;

public class SwordConvergenceRenderer extends MobRenderer<AbstractArtifactSpiritEntity, EmptyEntityModel<AbstractArtifactSpiritEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(SwordSoaring.MOD_ID, "textures/entity/vatansever_swordgroup.png");
    public SwordConvergenceRenderer(EntityRendererProvider.Context context) {
        super(context, new EmptyEntityModel<>(), 1);
        this.addLayer(new ReplaceableRenderLayer<>(this));
    }

    @Override
    public boolean shouldRender(@NotNull AbstractArtifactSpiritEntity pLivingEntity, @NotNull Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return pLivingEntity.tickCount > 2;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull AbstractArtifactSpiritEntity vatanseverStormEntity) {
        return TEXTURE;
    }
}