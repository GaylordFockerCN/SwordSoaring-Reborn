package net.p1nero.ss.events;

import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.skill.sword_soaring.SwordSoaringSkill;
import net.p1nero.ss.skill.weapon_passive.VatanseverPassive;

@Mod.EventBusSubscriber(modid = SwordSoaring.MOD_ID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event){
        SwordSoaringSkill.onLivingEquipmentChange(event);
        VatanseverPassive.onLivingEquipmentChange(event);
    }
}