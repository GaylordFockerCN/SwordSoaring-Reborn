package net.p1nero.ss.events;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.entity.sword.fly_sword.client.FlySwordRenderer;
import net.p1nero.ss.entity.sword.fly_sword.client.PatchedFlySwordRenderer;
import net.p1nero.ss.entity.sword.gate_of_babylon.client.BabylonRenderer;
import net.p1nero.ss.entity.sword.gate_of_babylon.client.PatchedBabylonRenderer;
import net.p1nero.ss.entity.sword.screen_sword.client.PatchedScreenSwordRenderer;
import net.p1nero.ss.entity.sword.screen_sword.client.ScreenSwordRenderer;
import net.p1nero.ss.entity.sword.sword_convergence.client.PatchedSwordConvergenceRenderer;
import net.p1nero.ss.entity.sword.sword_convergence.client.SwordConvergenceRenderer;
import net.p1nero.ss.entity.vatansever.client.PatchedVatanseverRenderer;
import net.p1nero.ss.entity.vatansever.client.VatanseverRenderer;
import net.p1nero.ss.entity.vatansever_storm.client.PatchedVatanseverStormRenderer;
import net.p1nero.ss.entity.vatansever_storm.client.VatanseverStormRenderer;
import net.p1nero.ss.gameassets.SwordSoaringSkillCategories;
import net.p1nero.ss.item.SwordSoaringItems;
import net.p1nero.ss.item.client.RenderVatansever;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.item.EpicFightItems;
import yesman.epicfight.world.item.SkillBookItem;

@Mod.EventBusSubscriber(modid = SwordSoaring.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(SwordSoaringEntities.SWORD_CONVERGENCE_ENTITY.get(), SwordConvergenceRenderer::new);
        EntityRenderers.register(SwordSoaringEntities.BABYLON.get(), BabylonRenderer::new);
        EntityRenderers.register(SwordSoaringEntities.FLY_SWORD.get(), FlySwordRenderer::new);
        EntityRenderers.register(SwordSoaringEntities.SCREEN_SWORD.get(), ScreenSwordRenderer::new);
        EntityRenderers.register(SwordSoaringEntities.VATANSEVER.get(), VatanseverRenderer::new);
        EntityRenderers.register(SwordSoaringEntities.VATANSEVER_STORM.get(), VatanseverStormRenderer::new);

        ItemProperties.register(EpicFightItems.SKILLBOOK.get(), new ResourceLocation(SwordSoaring.MOD_ID,"skill"), (pStack, pLevel, pEntity, pSeed) -> {
            Skill skill = SkillBookItem.getContainSkill(pStack);

            if (skill != null) {
                if (skill.getCategory() == SkillCategories.GUARD) {
                    return 1;
                } else if (skill.getCategory() == SkillCategories.PASSIVE) {
                    return 2;
                } else if (skill.getCategory() == SkillCategories.DODGE) {
                    return 3;
                } else if (skill.getCategory() == SkillCategories.IDENTITY) {
                    return 4;
                } else if (skill.getCategory() == SkillCategories.MOVER) {
                    return 5;
                } else if (skill.getCategory() == SwordSoaringSkillCategories.SWORD_SOARING) {
                    return 6;
                } else if (skill.getCategory() == SwordSoaringSkillCategories.SWORD_CONTROLLER) {
                    return 7;
                }
            }

            return 0;
        });
    }

    @SubscribeEvent
    public static void onRenderItem(final PatchedRenderersEvent.Add event) {
        event.addItemRenderer(SwordSoaringItems.VATANSEVER.get(), new RenderVatansever());
    }

    @SubscribeEvent
    public static void onPatchedRenderer(PatchedRenderersEvent.Add event){
        event.addPatchedEntityRenderer(SwordSoaringEntities.SWORD_CONVERGENCE_ENTITY.get(), PatchedSwordConvergenceRenderer::new);
        event.addPatchedEntityRenderer(SwordSoaringEntities.BABYLON.get(), PatchedBabylonRenderer::new);
        event.addPatchedEntityRenderer(SwordSoaringEntities.FLY_SWORD.get(), PatchedFlySwordRenderer::new);
        event.addPatchedEntityRenderer(SwordSoaringEntities.SCREEN_SWORD.get(), PatchedScreenSwordRenderer::new);
        event.addPatchedEntityRenderer(SwordSoaringEntities.VATANSEVER.get(), PatchedVatanseverRenderer::new);
        event.addPatchedEntityRenderer(SwordSoaringEntities.VATANSEVER_STORM.get(), PatchedVatanseverStormRenderer::new);
    }
}