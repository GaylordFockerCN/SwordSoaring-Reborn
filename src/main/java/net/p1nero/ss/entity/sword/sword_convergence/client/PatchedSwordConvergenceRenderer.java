package net.p1nero.ss.entity.sword.sword_convergence.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.entity.client.layer.ReplaceableRenderLayer;
import net.p1nero.ss.entity.client.model.EmptyEntityModel;
import net.p1nero.ss.entity.sword.client.PatchedReplaceableRenderer;
import net.p1nero.ss.entity.sword.sword_convergence.SwordConvergenceEntity;
import net.p1nero.ss.entity.sword.sword_convergence.SwordConvergencePatch;
import net.p1nero.ss.entity.sword.sword_convergence.client.layer.PatchedSwordConvergenceRandomReplaceableLayer;
import net.p1nero.ss.entity.vatansever_storm.client.VatanseverStormMesh;
import net.p1nero.ss.gameassets.SwordSoaringMeshes;

@OnlyIn(Dist.CLIENT)
public class PatchedSwordConvergenceRenderer extends PatchedReplaceableRenderer<SwordConvergenceEntity, SwordConvergencePatch, EmptyEntityModel<SwordConvergenceEntity>, VatanseverStormMesh> {

    @Override
    protected void addReplaceablePatchedLayer() {
        this.addPatchedLayer(ReplaceableRenderLayer.class, new PatchedSwordConvergenceRandomReplaceableLayer<>());
    }

    @Override
    public VatanseverStormMesh getMesh(SwordConvergencePatch vatanseverStormEntityPatch) {
        return SwordSoaringMeshes.vatanseverStormMesh;
    }
}