package net.p1nero.ss;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = SwordSoaring.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.BooleanValue ENABLE_LOOT_TABLE;
    public static final ForgeConfigSpec.BooleanValue ARACHNOPHOBIA_MODE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEMS_CAN_FLY;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEMS_CAN_NOT_FLY;
    public static final ForgeConfigSpec.BooleanValue ITEMS_BLOOM;
    public static final ForgeConfigSpec.BooleanValue REMOVE_ITEM;
    public static final ForgeConfigSpec.IntValue SWORD_EFFECT_PER_TICK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_TRAIL;
    public static final ForgeConfigSpec.ConfigValue<String> TRAIL_PARTICLE_TYPE;

    static final ForgeConfigSpec SPEC;

    static {
        ENABLE_LOOT_TABLE = createBool("enable_loot_table", true, "If true, you will get the skill book when defeat the boss.", "若为true，击败boss将可获取技能书。否则你将自己添加技能书获取方式。");
        ARACHNOPHOBIA_MODE = createBool("arachnophobia_mode", false, "Arachnophobia mode, if true, the boss will have no legs.", "蜘蛛恐惧症模式：true时boss将不会有腿（1.20boss开发中）");
        BUILDER.push("Sword Properties剑配置");
        ITEMS_CAN_FLY = BUILDER
                .comment("A list of items considered as sword.", "被视为剑的物品")
                .defineListAllowEmpty(List.of("items considered as sword"), List::of, Config::validateItemName);
        ITEMS_CAN_NOT_FLY = BUILDER
                .comment("A list of items not considered as sword.", "不被视为剑的物品")
                .defineListAllowEmpty(List.of("items not considered as sword."), () -> List.of("sword_soaring:vatansever"), Config::validateItemName);
        BUILDER.pop();
        BUILDER.push("Gate of Babylon Skill王之财宝");
        ITEMS_BLOOM = createBool("items_bloom", false, "should the item shot glowing", "发射的物品是否发光");
        REMOVE_ITEM = createBool("remove_item", false, "BE CAREFUL TO CHANGE!! remove the item in backpack and spawn when shot", "【慎重启用！】发射物品时是否删除背包内的物品，并在射出去后掉落。若有遗失概不负责。");
        BUILDER.pop();
        BUILDER.push("Sword Convergence万剑归宗");
        SWORD_EFFECT_PER_TICK = createInt("sword_effect_per_tick", 4, "", "每秒聚集的剑数（仅特效）");
        ENABLE_TRAIL = createBool("enable_trail", true, "should wan shows the trails", "是否启用刀光特效");
        TRAIL_PARTICLE_TYPE = BUILDER.comment("default trail particle type", "默认刀光类型").define("trail_particle_type", "epicfight:swing_trail");
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static Set<Item> swordItems = new HashSet<>();
    public static Set<Item> notSwordItems = new HashSet<>();

    private static ForgeConfigSpec.BooleanValue createBool(String key, boolean defaultValue, String... comment) {
        return BUILDER
                .comment(comment)
                .translation("config." + SwordSoaring.MOD_ID + "." + key)
                .define(key, defaultValue);
    }

    private static ForgeConfigSpec.DoubleValue createDouble(String key, double defaultValue, String... comment) {
        return BUILDER
                .comment(comment)
                .translation("config." + SwordSoaring.MOD_ID + "." + key)
                .defineInRange(key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    private static ForgeConfigSpec.IntValue createInt(String key, int defaultValue, String... comment) {
        return BUILDER
                .comment(comment)
                .translation("config." + SwordSoaring.MOD_ID + "." + key)
                .defineInRange(key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

}
