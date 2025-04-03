package net.p1nero.ss.entity.sword.screen_sword.client;

import net.minecraft.client.model.EntityModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.entity.sword.AbstractSwordEntity;
import net.p1nero.ss.entity.sword.client.PatchedReplaceableRenderer;
import net.p1nero.ss.gameassets.SwordSoaringMeshes;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedScreenSwordRenderer<E extends AbstractSwordEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>> extends PatchedReplaceableRenderer<E, T, M, ScreenSwordMesh> {
    @Override
    public ScreenSwordMesh getMesh(T t) {
        return SwordSoaringMeshes.screenSwordMesh;
    }
}