package net.p1nero.ss.compat;

import moe.plushie.armourers_workshop.api.common.IItemStackProvider;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.ItemStackProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.p1nero.ss.entity.sword.AbstractSwordEntity;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonEntity;

import java.util.List;

public class ArmourersWorkshopCompat {
    public static void copyArmourers(Entity from, Entity to){
        SkinWardrobe ownerSkinWardrobe = SkinWardrobe.of(from);
        SkinWardrobe newWardrobe = SkinWardrobe.of(to);
        if(ownerSkinWardrobe != null && newWardrobe != null){
            CompoundTag tag = new CompoundTag();
            ownerSkinWardrobe.serialize(AbstractDataSerializer.wrap(tag, to));
            newWardrobe.deserialize(AbstractDataSerializer.wrap(tag, to));
            newWardrobe.broadcast();
        }
    }

    public static void registerSwordSoaringItemProvider(){
        ItemStackProvider.getInstance().register(new IItemStackProvider(){
            @Override
            public Iterable<ItemStack> getArmorSlots(Entity entity) {
                return null;
            }

            @Override
            public Iterable<ItemStack> getHandSlots(Entity entity) {
                if(entity instanceof BabylonEntity babylonEntity){
                    return babylonEntity.getValidBabylonItems();
                }

                if(entity instanceof AbstractSwordEntity abstractSwordEntity){
                    return List.of(abstractSwordEntity.getItemStack(null));
                }

                return null;
            }
        });
    }
}