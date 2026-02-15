package net.p1nero.ss.events;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.SwordSoaringCameraManager;
import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.entity.sword.fly_sword.client.FlySwordRenderer;
import net.p1nero.ss.entity.sword.gate_of_babylon.client.BabylonRenderer;
import net.p1nero.ss.entity.sword.screen_sword.client.ScreenSwordRenderer;
import net.p1nero.ss.entity.sword.wan.client.WanRenderer;
import net.p1nero.ss.entity.vatansever.client.VatanseverRenderer;
import net.p1nero.ss.entity.vatansever_storm.client.VatanseverStormRenderer;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;

@EventBusSubscriber(modid = SwordSoaringMod.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(()->{
            EntityRenderers.register(SwordSoaringEntities.WAN_ENTITY.get(), WanRenderer::new);
            EntityRenderers.register(SwordSoaringEntities.BABYLON.get(), BabylonRenderer::new);
            EntityRenderers.register(SwordSoaringEntities.FLY_SWORD.get(), FlySwordRenderer::new);
            EntityRenderers.register(SwordSoaringEntities.SCREEN_SWORD.get(), ScreenSwordRenderer::new);
            EntityRenderers.register(SwordSoaringEntities.VATANSEVER.get(), VatanseverRenderer::new);
            EntityRenderers.register(SwordSoaringEntities.VATANSEVER_STORM.get(), VatanseverStormRenderer::new);
            EpicFightClientEventHooks.Camera.BUILD_TRANSFORM_POST.registerEvent(SwordSoaringCameraManager::onEpicFightCameraSetupEnd);
            EpicFightClientEventHooks.Entity.MODIFY_PLAYER_LIVING_MOTION_BASE.registerEvent(EpicFightClientEvents::swordsoaring$onBaseLayer);
            EpicFightClientEventHooks.Entity.MODIFY_PLAYER_LIVING_MOTION_COMPOSITE.registerEvent(EpicFightClientEvents::swordsoaring$onCompositeLayer);
            EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(EpicFightClientEvents::registerPatchedRenderer);
        });

    }

}