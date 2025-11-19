package net.p1nero.ss.events;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.SwordSoairngCameraManager;
import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.entity.ray.client.RayRenderer;
import net.p1nero.ss.entity.sword.fly_sword.client.FlySwordRenderer;
import net.p1nero.ss.entity.sword.fly_sword.client.PatchedFlySwordRenderer;
import net.p1nero.ss.entity.sword.gate_of_babylon.client.BabylonRenderer;
import net.p1nero.ss.entity.sword.gate_of_babylon.client.PatchedBabylonRenderer;
import net.p1nero.ss.entity.sword.screen_sword.client.PatchedScreenSwordRenderer;
import net.p1nero.ss.entity.sword.screen_sword.client.ScreenSwordRenderer;
import net.p1nero.ss.entity.sword.wan.client.PatchedWanRenderer;
import net.p1nero.ss.entity.sword.wan.client.WanRenderer;
import net.p1nero.ss.entity.vatansever.client.PatchedVatanseverRenderer;
import net.p1nero.ss.entity.vatansever.client.VatanseverRenderer;
import net.p1nero.ss.entity.vatansever_storm.client.PatchedVatanseverStormRenderer;
import net.p1nero.ss.entity.vatansever_storm.client.VatanseverStormRenderer;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;

@Mod.EventBusSubscriber(modid = SwordSoaringMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(SwordSoaringEntities.WAN_ENTITY.get(), WanRenderer::new);
        EntityRenderers.register(SwordSoaringEntities.BABYLON.get(), BabylonRenderer::new);
        EntityRenderers.register(SwordSoaringEntities.FLY_SWORD.get(), FlySwordRenderer::new);
        EntityRenderers.register(SwordSoaringEntities.SCREEN_SWORD.get(), ScreenSwordRenderer::new);
        EntityRenderers.register(SwordSoaringEntities.VATANSEVER.get(), VatanseverRenderer::new);
        EntityRenderers.register(SwordSoaringEntities.VATANSEVER_STORM.get(), VatanseverStormRenderer::new);
        EntityRenderers.register(SwordSoaringEntities.RAY_ENTITY.get(), RayRenderer::new);

        EpicFightCameraAPI.getInstance().addCameraSetupListenerPost(SwordSoaringMod.MOD_ID, SwordSoairngCameraManager::onCameraSetupEnd);
    }

    @SubscribeEvent
    public static void onPatchedRenderer(PatchedRenderersEvent.Add event){
        event.addPatchedEntityRenderer(SwordSoaringEntities.WAN_ENTITY.get(), entityType -> new PatchedWanRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(SwordSoaringEntities.BABYLON.get(), entityType -> new PatchedBabylonRenderer<>(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(SwordSoaringEntities.FLY_SWORD.get(), entityType -> new PatchedFlySwordRenderer<>(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(SwordSoaringEntities.SCREEN_SWORD.get(),entityType -> new PatchedScreenSwordRenderer<>(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(SwordSoaringEntities.VATANSEVER.get(), entityType -> new PatchedVatanseverRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(SwordSoaringEntities.VATANSEVER_STORM.get(), entityType -> new PatchedVatanseverStormRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType));
    }

}