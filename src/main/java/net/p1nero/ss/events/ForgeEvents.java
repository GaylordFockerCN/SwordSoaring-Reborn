package net.p1nero.ss.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.p1nero.ss.SwordSoaringMod;

@EventBusSubscriber(modid = SwordSoaringMod.MOD_ID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event){

    }

}