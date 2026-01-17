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
import net.p1nero.ss.entity.sword.fly_sword.FlySwordEntity;
import net.p1nero.ss.entity.sword.fly_sword.FlySwordPatch;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonEntity;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonPatch;
import net.p1nero.ss.entity.sword.screen_sword.ScreenSwordEntity;
import net.p1nero.ss.entity.sword.screen_sword.ScreenSwordPatch;
import net.p1nero.ss.entity.sword.wan.WanEntity;
import net.p1nero.ss.entity.sword.wan.WanPatch;
import net.p1nero.ss.entity.vatansever.VatanseverEntity;
import net.p1nero.ss.entity.vatansever.VatanseverEntityPatch;
import net.p1nero.ss.entity.vatansever_storm.VatanseverStormEntity;
import net.p1nero.ss.entity.vatansever_storm.VatanseverStormEntityPatch;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import yesman.epicfight.api.neoevent.EntityPatchRegistryEvent;

@EventBusSubscriber(modid = SwordSoaringMod.MOD_ID)
public class ModEvents{

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(SwordSoaringEntities.WAN_ENTITY.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
        event.put(SwordSoaringEntities.BABYLON.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
        event.put(SwordSoaringEntities.FLY_SWORD.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
        event.put(SwordSoaringEntities.SCREEN_SWORD.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
        event.put(SwordSoaringEntities.VATANSEVER.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
        event.put(SwordSoaringEntities.VATANSEVER_STORM.get(), AbstractArtifactSpiritEntity.getDefaultAttribute());
    }

    @SubscribeEvent
    public static void setPatch(EntityPatchRegistryEvent event) {
        event.getTypeEntry().put(SwordSoaringEntities.WAN_ENTITY.get(), (entity) -> new WanPatch((WanEntity) entity));
        event.getTypeEntry().put(SwordSoaringEntities.BABYLON.get(), (entity) -> new BabylonPatch((BabylonEntity) entity));
        event.getTypeEntry().put(SwordSoaringEntities.FLY_SWORD.get(), (entity) -> new FlySwordPatch((FlySwordEntity) entity));
        event.getTypeEntry().put(SwordSoaringEntities.SCREEN_SWORD.get(), (entity -> new ScreenSwordPatch((ScreenSwordEntity) entity)));
        event.getTypeEntry().put(SwordSoaringEntities.VATANSEVER.get(), (entity) -> new VatanseverEntityPatch((VatanseverEntity) entity));
        event.getTypeEntry().put(SwordSoaringEntities.VATANSEVER_STORM.get(), (entity) -> new VatanseverStormEntityPatch((VatanseverStormEntity) entity));
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(SwordSoaringArmatures::registerArmatures);
        SwordSoaringMod.runInArmourersWorkshopLoaded(() -> ArmourersWorkshopCompat::registerSwordSoaringItemProvider);
        SwordSoaringConfig.initSwordList();
    }
}