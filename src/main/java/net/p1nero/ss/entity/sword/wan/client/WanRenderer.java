package net.p1nero.ss.entity.sword.wan.client;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.entity.client.layer.ReplaceableRenderLayer;
import net.p1nero.ss.entity.client.model.EmptyEntityModel;
import org.jetbrains.annotations.NotNull;

public class WanRenderer extends MobRenderer<AbstractArtifactSpiritEntity, EmptyEntityModel<AbstractArtifactSpiritEntity>> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SwordSoaringMod.MOD_ID, "textures/entity/vatansever_swordgroup.png");
    public WanRenderer(EntityRendererProvider.Context context) {
        super(context, new EmptyEntityModel<>(), 0);
        this.addLayer(new ReplaceableRenderLayer<>(this));
    }

    @Override
    public boolean shouldRender(@NotNull AbstractArtifactSpiritEntity pLivingEntity, @NotNull Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return pLivingEntity.tickCount > 5;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull AbstractArtifactSpiritEntity vatanseverStormEntity) {
        return TEXTURE;
    }
}
