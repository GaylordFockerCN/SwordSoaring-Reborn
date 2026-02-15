package net.p1nero.ss.events;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.SwordSoaringCameraManager;

@EventBusSubscriber(modid = SwordSoaringMod.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        SwordSoaringCameraManager.tick();
    }

}
