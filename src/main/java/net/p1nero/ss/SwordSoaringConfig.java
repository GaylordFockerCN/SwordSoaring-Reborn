package net.p1nero.ss;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = SwordSoaringMod.MOD_ID)
public class SwordSoaringConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEMS_CAN_FLY;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEMS_CAN_NOT_FLY;
    public static final ModConfigSpec.IntValue FLY_DELAY;
    public static final ModConfigSpec.BooleanValue ITEMS_BLOOM;
    public static final ModConfigSpec.BooleanValue REMOVE_ITEM;
    public static final ModConfigSpec.IntValue SWORD_EFFECT_PER_TICK;
    public static final ModConfigSpec.IntValue WAN_TRAIL_UPDATE_TICK;
    public static final ModConfigSpec.ConfigValue<String> TRAIL_PARTICLE_TYPE;


    static final ModConfigSpec SPEC;

    static {
        ITEMS_CAN_FLY = BUILDER
                .comment("A list of items considered as sword.", "被视为剑的物品")
                .defineListAllowEmpty(List.of("items_considered_as_sword"), List.of(), SwordSoaringConfig::validateItemName);
        ITEMS_CAN_NOT_FLY = BUILDER
                .comment("A list of items not considered as sword.", "不被视为剑的物品")
                .defineListAllowEmpty(List.of("items_not_considered_as_sword"), List.of("sword_soaring:vatansever"), SwordSoaringConfig::validateItemName);
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

    private static ModConfigSpec.BooleanValue createBool(String key, boolean defaultValue, String... comment) {
        return BUILDER
                .comment(comment)
                .translation("config." + SwordSoaringMod.MOD_ID + "." + key)
                .define(key, defaultValue);
    }

    private static ModConfigSpec.DoubleValue createDouble(String key, double defaultValue, String... comment) {
        return BUILDER
                .comment(comment)
                .translation("config." + SwordSoaringMod.MOD_ID + "." + key)
                .defineInRange(key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    private static ModConfigSpec.IntValue createInt(String key, int defaultValue, String... comment) {
        return BUILDER
                .comment(comment)
                .translation("config." + SwordSoaringMod.MOD_ID + "." + key)
                .defineInRange(key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Reloading event) {
        initSwordList();
    }

    public static void initSwordList() {
        SwordSoaringConfig.swordItems = SwordSoaringConfig.ITEMS_CAN_FLY.get().stream()
                .map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName)))
                .collect(Collectors.toSet());
        SwordSoaringConfig.notSwordItems = SwordSoaringConfig.ITEMS_CAN_NOT_FLY.get().stream()
                .map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName)))
                .collect(Collectors.toSet());

//        if(ModList.get().isLoaded("epic_fight_avalon")) {
//            ForgeRegistries.ITEMS.getValues().stream()
//                    .filter(item -> item instanceof IAvalonAnimationItem || item instanceof IChangeArmatureItem)
//                    .forEach(item -> {
//                        SwordSoaringConfig.notSwordItems.add(item);
//                    });
//        }
    }

}
