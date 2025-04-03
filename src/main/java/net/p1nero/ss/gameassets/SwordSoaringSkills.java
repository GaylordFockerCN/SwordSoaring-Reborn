package net.p1nero.ss.gameassets;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.gameassets.skills.FlyingSkills;
import net.p1nero.ss.gameassets.skills.SwordControllerSkills;
import net.p1nero.ss.gameassets.skills.VatanseverSkills;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = SwordSoaring.MOD_ID)
public class SwordSoaringSkills {

    @SubscribeEvent
    public static void buildSkills(SkillBuildEvent event){
        SwordControllerSkills.buildSwordControllerSkills(event);
        FlyingSkills.buildSwordSoaringSkills(event);
        VatanseverSkills.buildVatanseverSkills(event);
    }

    public static <T extends Skill, B extends Skill.Builder<T>> Skill build(SkillBuildEvent event, Function<B, T> constructor, B builder, String name){
        SkillManager.register(constructor, builder, SwordSoaring.MOD_ID, name);
        return event.build(SwordSoaring.MOD_ID, name);
    }
}