package net.p1nero.ss;

import com.merlin204.avalon.item.IChangeArmatureItem;
import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = SwordSoaringMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SwordSoaringConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEMS_CAN_FLY;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEMS_CAN_NOT_FLY;
    public static final ForgeConfigSpec.IntValue FLY_DELAY;
    public static final ForgeConfigSpec.BooleanValue ITEMS_BLOOM;
    public static final ForgeConfigSpec.BooleanValue REMOVE_ITEM;
    public static final ForgeConfigSpec.IntValue SWORD_EFFECT_PER_TICK;
    public static final ForgeConfigSpec.IntValue WAN_TRAIL_UPDATE_TICK;
    public static final ForgeConfigSpec.ConfigValue<String> TRAIL_PARTICLE_TYPE;


    static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("Sword Properties 剑配置");
        ITEMS_CAN_FLY = BUILDER
                .comment("A list of items considered as sword.", "被视为剑的物品")
                .defineListAllowEmpty(List.of("items_considered_as_sword"), List::of, SwordSoaringConfig::validateItemName);
        ITEMS_CAN_NOT_FLY = BUILDER
                .comment("A list of items not considered as sword.", "不被视为剑的物品")
                .defineListAllowEmpty(List.of("items_not_considered_as_sword"), () -> List.of("sword_soaring:vatansever"), SwordSoaringConfig::validateItemName);
        BUILDER.push("Sword Soaring 御剑凌虚");
        FLY_DELAY = createInt("fly_delay", 200, "time mills between double click of starting flying", "起飞的双击间隔的毫秒数");
        BUILDER.pop();
        BUILDER.push("Gate of Babylon Skill 王之财宝");
        ITEMS_BLOOM = createBool("items_bloom", false, "should the item shot glowing", "发射的物品是否发光");
        REMOVE_ITEM = createBool("remove_item", false, "BE CAREFUL TO CHANGE!! remove the item in backpack and spawn when shot", "【慎重启用！】发射物品时是否删除背包内的物品，并在射出去后掉落。若有遗失概不负责。");
        BUILDER.pop();
        BUILDER.push("Sword Convergence 万剑归宗");
        SWORD_EFFECT_PER_TICK = createInt("sword_effect_per_tick", 4, "additional swords per tick", "每秒聚集的剑数（仅特效）");
        WAN_TRAIL_UPDATE_TICK = createInt("wan_trail_update_tick", 0, "trail effect update interval", "刀光刷新频率，0为关闭");
        TRAIL_PARTICLE_TYPE = BUILDER.comment("default trail particle type", "默认刀光类型").define("trail_particle_type", "epicfight:swing_trail");
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static Set<Item> swordItems = null;
    public static Set<Item> notSwordItems = null;

    private static ForgeConfigSpec.BooleanValue createBool(String key, boolean defaultValue, String... comment) {
        return BUILDER
                .comment(comment)
                .translation("config." + SwordSoaringMod.MOD_ID + "." + key)
                .define(key, defaultValue);
    }

    private static ForgeConfigSpec.DoubleValue createDouble(String key, double defaultValue, String... comment) {
        return BUILDER
                .comment(comment)
                .translation("config." + SwordSoaringMod.MOD_ID + "." + key)
                .defineInRange(key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    private static ForgeConfigSpec.IntValue createInt(String key, int defaultValue, String... comment) {
        return BUILDER
                .comment(comment)
                .translation("config." + SwordSoaringMod.MOD_ID + "." + key)
                .defineInRange(key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Reloading event) {
        initSwordList();
    }

    public static void initSwordList() {
        SwordSoaringConfig.swordItems = SwordSoaringConfig.ITEMS_CAN_FLY.get().stream()
                .map(itemName -> ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemName)))
                .collect(Collectors.toSet());
        SwordSoaringConfig.notSwordItems = SwordSoaringConfig.ITEMS_CAN_NOT_FLY.get().stream()
                .map(itemName -> ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemName)))
                .collect(Collectors.toSet());

        if(ModList.get().isLoaded("epic_fight_avalon")) {
            ForgeRegistries.ITEMS.getValues().stream()
                    .filter(item -> item instanceof IAvalonAnimationItem || item instanceof IChangeArmatureItem)
                    .forEach(item -> {
                        SwordSoaringConfig.notSwordItems.add(item);
                    });
        }
    }

}
