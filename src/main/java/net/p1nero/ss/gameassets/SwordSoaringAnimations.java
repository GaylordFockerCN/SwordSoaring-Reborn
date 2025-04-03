package net.p1nero.ss.gameassets;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.gameassets.animations.*;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;

@Mod.EventBusSubscriber(modid = SwordSoaring.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SwordSoaringAnimations {

    @SubscribeEvent
    public static void registerAnimations(AnimationRegistryEvent event) {
        event.getRegistryMap().put(SwordSoaring.MOD_ID, ()->{
            SwordConvergenceAnimations.buildSwordConvergenceAnim();
            BabylonAnimations.buildBabylonAnim();
            FlyAnimations.buildFlyAnim();
            FlySwordAnimations.buildFlySwordAnim();
            ScreenSwordAnimations.buildScreenSwordAnim();
            VatanseverAnimations.buildVatanseverAnim();
            VatanseverStormAnimations.buildVatanseverStormAnim();
        });
    }
}