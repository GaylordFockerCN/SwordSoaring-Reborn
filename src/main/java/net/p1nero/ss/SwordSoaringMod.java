package net.p1nero.ss;

import com.merlin204.avalon.item.IChangeArmatureItem;
import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import com.mojang.logging.LogUtils;
import com.p1nero.invincible.api.skill.ComboType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.p1nero.ss.client.particle.SwordSoaringParticles;
import net.p1nero.ss.client.sound.SwordSoaringSounds;
import net.p1nero.ss.compat.EpicSkillsCompat;
import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.gameassets.*;
import net.p1nero.ss.item.SwordSoaringItems;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod(SwordSoaringMod.MOD_ID)
public class SwordSoaringMod {

    public static final String MOD_ID = "sword_soaring";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SwordSoaringMod(FMLJavaModLoadingContext context) {
        SkillCategories.ENUM_MANAGER.registerEnumCls(SwordSoaringMod.MOD_ID, SwordSoaringSkillCategories.class);
        SkillSlot.ENUM_MANAGER.registerEnumCls(SwordSoaringMod.MOD_ID, SwordSoaringSkillSlots.class);
        CapabilityItem.WeaponCategories.ENUM_MANAGER.registerEnumCls(SwordSoaringMod.MOD_ID, SwordSoaringWeaponCategories.class);
        ComboType.ENUM_MANAGER.registerEnumCls(SwordSoaringMod.MOD_ID, SwordSoaringComboTypes.class);

        if(EpicFightSharedConstants.isPhysicalClient() && ModList.get().isLoaded("epicskills")) {
            EpicSkillsCompat.registerCategorySlotTexture();
        }

        IEventBus bus = context.getModEventBus();
        SwordSoaringDatakeys.DATA_KEYS.register(bus);
        SwordSoaringItems.ITEMS.register(bus);
        SwordSoaringItems.SWORD_SOARING_ITEM_TAB.register(bus);
        SwordSoaringEntities.ENTITIES.register(bus);
        SwordSoaringSounds.SOUND_EVENTS.register(bus);
        SwordSoaringParticles.PARTICLES.register(bus);

        context.registerConfig(ModConfig.Type.COMMON, SwordSoaringConfig.SPEC);
    }

    public static boolean isArmourersWorkshopLoaded() {
        return ModList.get().isLoaded("armourers_workshop");
    }

    /**
     * 判断物品是否属于剑或者被视为剑。
     */
    public static boolean isValidSword(ItemStack sword) {
        if (SwordSoaringConfig.swordItems == null || SwordSoaringConfig.notSwordItems == null) {
            return false;
        }
        if (SwordSoaringConfig.notSwordItems.contains(sword.getItem())) {
            return false;
        }
        return sword.getItem() instanceof SwordItem || SwordSoaringConfig.swordItems.contains(sword.getItem());
    }

    public static void runInArmourersWorkshopLoaded(Supplier<Runnable> handler) {
        if (isArmourersWorkshopLoaded()) {
            handler.get().run();
        }
    }

    public static @NotNull ResourceLocation rl(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}