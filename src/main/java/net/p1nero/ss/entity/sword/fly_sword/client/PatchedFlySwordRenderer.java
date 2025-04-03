package net.p1nero.ss.entity.sword.fly_sword.client;

import net.minecraft.client.model.EntityModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.entity.sword.client.PatchedReplaceableRenderer;
import net.p1nero.ss.entity.sword.fly_sword.FlySwordEntity;
import net.p1nero.ss.gameassets.SwordSoaringMeshes;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedFlySwordRenderer<E extends FlySwordEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>> extends PatchedReplaceableRenderer<E, T, M, FlySwordMesh> {
    @Override
    public FlySwordMesh getMesh(T t) {
        return SwordSoaringMeshes.flySwordMesh;
    }
}