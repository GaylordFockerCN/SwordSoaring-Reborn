package net.p1nero.ss.entity.sword;

import net.minecraft.world.item.ItemStack;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public interface IPatchedItemSupplier {
    ItemStack getItemStack(LivingEntityPatch<?> livingEntityPatch);
}