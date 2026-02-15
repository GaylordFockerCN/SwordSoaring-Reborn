package net.p1nero.ss.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.p1nero.ss.SwordSoaringConfig;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.compat.ArmourersWorkshopCompat;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.entity.sword.fly_sword.FlySwordPatch;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonPatch;
import net.p1nero.ss.entity.sword.screen_sword.ScreenSwordPatch;
import net.p1nero.ss.entity.sword.wan.WanPatch;
import net.p1nero.ss.entity.vatansever.VatanseverEntityPatch;
import net.p1nero.ss.entity.vatansever_storm.VatanseverStormEntityPatch;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import net.p1nero.ss.gameassets.SwordSoaringWeaponCapabilityPreset;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;

@EventBusSubscriber(modid = SwordSoaringMod.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(SwordSoaringEntities.WAN_ENTITY.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
        event.put(SwordSoaringEntities.BABYLON.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
        event.put(SwordSoaringEntities.FLY_SWORD.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
        event.put(SwordSoaringEntities.SCREEN_SWORD.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
        event.put(SwordSoaringEntities.VATANSEVER.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
        event.put(SwordSoaringEntities.VATANSEVER_STORM.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
    }

    public static void registerPatch(EntityPatchRegistryEvent event) {
        event.registerEntityPatch(SwordSoaringEntities.WAN_ENTITY.get(), WanPatch::new);
        event.registerEntityPatch(SwordSoaringEntities.BABYLON.get(), BabylonPatch::new);
        event.registerEntityPatch(SwordSoaringEntities.FLY_SWORD.get(), FlySwordPatch::new);
        event.registerEntityPatch(SwordSoaringEntities.SCREEN_SWORD.get(), (ScreenSwordPatch::new));
        event.registerEntityPatch(SwordSoaringEntities.VATANSEVER.get(), VatanseverEntityPatch::new);
        event.registerEntityPatch(SwordSoaringEntities.VATANSEVER_STORM.get(), VatanseverStormEntityPatch::new);
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SwordSoaringArmatures.registerArmatures();
            SwordSoaringMod.runInArmourersWorkshopLoaded(() -> ArmourersWorkshopCompat::registerSwordSoaringItemProvider);
            SwordSoaringConfig.initSwordList();
            EpicFightEventHooks.Registry.WEAPON_CAPABILITY_PRESET.registerEvent(SwordSoaringWeaponCapabilityPreset::register);
            EpicFightEventHooks.Registry.ENTITY_PATCH.registerEvent(ModEvents::registerPatch);
            EpicFightEventHooks.Animation.INIT_ANIMATOR.registerEvent(EpicFightClientEvents::swordsoaring$onAnimatorInit);
        });
    }
}