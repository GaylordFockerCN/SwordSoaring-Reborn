package net.p1nero.ss;

import com.mojang.logging.LogUtils;
import com.p1nero.invincible.api.combo.ComboType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.p1nero.ss.capability.SwordSoaringAttachments;
import net.p1nero.ss.client.sound.SwordSoaringSounds;
import net.p1nero.ss.compat.EpicSkillsCompat;
import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.gameassets.*;
import net.p1nero.ss.item.SwordSoaringItems;
import net.p1nero.ss.network.packet.client.SyncBabylonPacket;
import net.p1nero.ss.network.packet.server.RequestBabylonSyncPacket;
import net.p1nero.ss.network.packet.server.RequestEntityPlayAnimationPacket;
import net.p1nero.ss.network.packet.server.RequestVatanseverSwordBackPacket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import yesman.epicfight.api.animation.LivingMotion;
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

    public SwordSoaringMod(net.neoforged.bus.api.IEventBus bus, ModContainer modContainer) {
        SkillCategories.ENUM_MANAGER.registerEnumCls(SwordSoaringMod.MOD_ID, SwordSoaringSkillCategories.class);
        SkillSlot.ENUM_MANAGER.registerEnumCls(SwordSoaringMod.MOD_ID, SwordSoaringSkillSlots.class);
        LivingMotion.ENUM_MANAGER.registerEnumCls(SwordSoaringMod.MOD_ID, SwordSoaringLivingMotions.class);
        CapabilityItem.WeaponCategories.ENUM_MANAGER.registerEnumCls(SwordSoaringMod.MOD_ID, SwordSoaringWeaponCategories.class);
        ComboType.ENUM_MANAGER.registerEnumCls(SwordSoaringMod.MOD_ID, SwordSoaringComboTypes.class);

        if(EpicFightSharedConstants.isPhysicalClient() && ModList.get().isLoaded("epicskills")) {
            EpicSkillsCompat.registerCategorySlotTexture();
        }

        SwordSoaringSkills.REGISTRY.register(bus);
        SwordSoaringAttachments.ATTACHMENT_TYPES.register(bus);
        SwordSoaringDatakeys.DATA_KEYS.register(bus);
        SwordSoaringItems.ITEMS.register(bus);
        SwordSoaringItems.CREATIVE_TABS.register(bus);
        SwordSoaringEntities.ENTITIES.register(bus);
        SwordSoaringSounds.SOUND_EVENTS.register(bus);
        bus.addListener(this::registerPackets);

        modContainer.registerConfig(ModConfig.Type.COMMON, SwordSoaringConfig.SPEC);
    }

    public void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MOD_ID).versioned("1.0.0").optional();

        // CLIENTBOUND
        registrar.playToClient(SyncBabylonPacket.TYPE, SyncBabylonPacket.STREAM_CODEC, SyncBabylonPacket::execute);
        // SERVERBOUND
        registrar.playToServer(RequestBabylonSyncPacket.TYPE, RequestBabylonSyncPacket.STREAM_CODEC, RequestBabylonSyncPacket::execute);
        registrar.playToServer(RequestEntityPlayAnimationPacket.TYPE, RequestEntityPlayAnimationPacket.STREAM_CODEC, RequestEntityPlayAnimationPacket::execute);
        registrar.playToServer(RequestVatanseverSwordBackPacket.TYPE, RequestVatanseverSwordBackPacket.STREAM_CODEC, RequestVatanseverSwordBackPacket::execute);

    }

    public static boolean isArmourersWorkshopLoaded() {
        return ModList.get().isLoaded("armourers_workshop");
    }

    /**
     * 判断物品是否属于剑或者被视为剑。
     * 无法监听事件，干脆直接在这里初始化剑物品表。
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
