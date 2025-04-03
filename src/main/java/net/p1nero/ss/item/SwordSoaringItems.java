package net.p1nero.ss.item;

import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.p1nero.ss.SwordSoaring;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.item.WeaponItem;

public class SwordSoaringItems {
    public static final CreativeModeTab SWORD_SOARING_ITEM_TAB = new CreativeModeTab("sword_soaring.items") {
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(VATANSEVER.get());
        }
    };
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SwordSoaring.MOD_ID);
    public static final RegistryObject<Item> VATANSEVER = ITEMS.register("vatansever", () -> new VatanseverItem(Tiers.NETHERITE, 1, 0, (new Item.Properties()).rarity(Rarity.EPIC).tab(SWORD_SOARING_ITEM_TAB)));
}