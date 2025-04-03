package net.p1nero.ss.gameassets;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.entity.sword.fly_sword.FlySwordArmature;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonArmature;
import net.p1nero.ss.entity.sword.screen_sword.ScreenSwordArmature;
import net.p1nero.ss.entity.sword.sword_convergence.WanArmature;
import net.p1nero.ss.entity.vatansever.VatanseverArmature;
import net.p1nero.ss.entity.vatansever_storm.VatanseverStormArmature;
import yesman.epicfight.api.forgeevent.ModelBuildEvent;
import yesman.epicfight.gameasset.Armatures;

@Mod.EventBusSubscriber(modid = SwordSoaring.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SwordSoaringArmatures {
    public static WanArmature wanArmature;
    public static BabylonArmature babylonArmature;
    public static FlySwordArmature flySwordArmature;
    public static ScreenSwordArmature screenSwordArmature;
    public static VatanseverArmature vatanseverArmature;
    public static VatanseverStormArmature vatanseverStormArmature;

    @SubscribeEvent
    public static void build(ModelBuildEvent.ArmatureBuild event) {
        wanArmature = event.get(SwordSoaring.MOD_ID, "entity/wan", WanArmature::new);
        babylonArmature = event.get(SwordSoaring.MOD_ID, "entity/babylon", BabylonArmature::new);
        flySwordArmature = event.get(SwordSoaring.MOD_ID, "entity/fly_sword", FlySwordArmature::new);
        screenSwordArmature = event.get(SwordSoaring.MOD_ID, "entity/screen_sword", ScreenSwordArmature::new);
        vatanseverArmature = event.get(SwordSoaring.MOD_ID, "entity/vatansever", VatanseverArmature::new);
        vatanseverStormArmature = event.get(SwordSoaring.MOD_ID, "entity/vatansever_swordgroup", VatanseverStormArmature::new);
        Armatures.registerEntityTypeArmature(SwordSoaringEntities.SWORD_CONVERGENCE_ENTITY.get(), wanArmature);
        Armatures.registerEntityTypeArmature(SwordSoaringEntities.BABYLON.get(), babylonArmature);
        Armatures.registerEntityTypeArmature(SwordSoaringEntities.FLY_SWORD.get(), flySwordArmature);
        Armatures.registerEntityTypeArmature(SwordSoaringEntities.SCREEN_SWORD.get(), screenSwordArmature);
        Armatures.registerEntityTypeArmature(SwordSoaringEntities.VATANSEVER.get(), vatanseverArmature);
        Armatures.registerEntityTypeArmature(SwordSoaringEntities.VATANSEVER_STORM.get(), vatanseverStormArmature);
    }
}