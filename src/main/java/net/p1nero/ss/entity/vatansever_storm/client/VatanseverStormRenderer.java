package net.p1nero.ss.entity.vatansever_storm.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.entity.client.model.EmptyEntityModel;
import org.jetbrains.annotations.NotNull;

public class VatanseverStormRenderer extends MobRenderer<AbstractArtifactSpiritEntity, EmptyEntityModel<AbstractArtifactSpiritEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(SwordSoaring.MOD_ID, "textures/entity/vatansever_swordgroup.png");
    public VatanseverStormRenderer(EntityRendererProvider.Context context) {
        super(context, new EmptyEntityModel<>(), 1);
    }

    @Override
    public boolean shouldRender(@NotNull AbstractArtifactSpiritEntity pLivingEntity, @NotNull Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }

    @Override
    public void render(@NotNull AbstractArtifactSpiritEntity pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        this.shadowRadius = 0;
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull AbstractArtifactSpiritEntity vatanseverStormEntity) {
        return TEXTURE;
    }
}