package net.p1nero.ss.events;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
import net.p1nero.ss.network.PacketHandler;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;

@Mod.EventBusSubscriber(modid = SwordSoaringMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
        event.getTypeEntry().put(SwordSoaringEntities.WAN_ENTITY.get(), (entity) -> WanPatch::new);
        event.getTypeEntry().put(SwordSoaringEntities.BABYLON.get(), (entity) -> BabylonPatch::new);
        event.getTypeEntry().put(SwordSoaringEntities.FLY_SWORD.get(), (entity) -> FlySwordPatch::new);
        event.getTypeEntry().put(SwordSoaringEntities.SCREEN_SWORD.get(), (entity) -> ScreenSwordPatch::new);
        event.getTypeEntry().put(SwordSoaringEntities.VATANSEVER.get(), (entity) -> VatanseverEntityPatch::new);
        event.getTypeEntry().put(SwordSoaringEntities.VATANSEVER_STORM.get(), (entity) -> VatanseverStormEntityPatch::new);
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.register();

        event.enqueueWork(SwordSoaringArmatures::registerArmatures);

        SwordSoaringMod.runInArmourersWorkshopLoaded(() -> ArmourersWorkshopCompat::registerSwordSoaringItemProvider);

        SwordSoaringConfig.initSwordList();
    }
}