package net.p1nero.ss.entity.sword.gate_of_babylon.client;

import net.minecraft.client.model.EntityModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.entity.client.layer.ReplaceableRenderLayer;
import net.p1nero.ss.entity.sword.client.PatchedReplaceableRenderer;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonEntity;
import net.p1nero.ss.entity.sword.gate_of_babylon.client.layer.PatchedBabylonRandomReplaceableLayer;
import net.p1nero.ss.gameassets.SwordSoaringMeshes;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@SuppressWarnings({"unchecked", "rawtypes"})
@OnlyIn(Dist.CLIENT)
public class PatchedBabylonRenderer<E extends BabylonEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>> extends PatchedReplaceableRenderer<E, T, M, BabylonMesh> {

    @Override
    protected void addReplaceablePatchedLayer() {
        this.addPatchedLayer(ReplaceableRenderLayer.class, new PatchedBabylonRandomReplaceableLayer());
    }

    @Override
    public BabylonMesh getMesh(T babylonPatch) {
        return SwordSoaringMeshes.babylonMesh;
    }
}