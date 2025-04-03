package net.p1nero.ss.gameassets.skills;

import net.p1nero.ss.gameassets.SwordSoaringSkillCategories;
import net.p1nero.ss.gameassets.SwordSoaringSkills;
import net.p1nero.ss.gameassets.animations.ScreenSwordAnimations;
import net.p1nero.ss.item.SwordSoaringItems;
import net.p1nero.ss.skill.sword_controller.*;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;

public class SwordControllerSkills {
    public static Skill KILL_AURA_1;
    public static Skill KILL_AURA_2;
    public static Skill SCREEN_SWORD;
    public static Skill RAIN_SWORD;
    public static Skill GATE_OF_BABYLON;
    public static Skill WAN_JIAN_GUI_ZONG;

    public static void buildSwordControllerSkills(SkillBuildEvent event) {
        KILL_AURA_1 = SwordSoaringSkills.build(event, KillAuraSkill::new, KillAuraSkill.createKillAuraBuilder().setCreativeTab(SwordSoaringItems.SWORD_SOARING_ITEM_TAB)
                .setPlayerSummonAnim(() -> ScreenSwordAnimations.PLAYER_SUMMON_KILL_AURA_1).setSwordSummonAnim(() -> ScreenSwordAnimations.KILL_AURA_1_SUMMON), "kill_aura_1");
        KILL_AURA_2 = SwordSoaringSkills.build(event, KillAuraSkill::new, KillAuraSkill.createKillAuraBuilder().setCreativeTab(SwordSoaringItems.SWORD_SOARING_ITEM_TAB)
                .setPlayerSummonAnim(() -> ScreenSwordAnimations.PLAYER_SUMMON_KILL_AURA_2).setSwordSummonAnim(() -> ScreenSwordAnimations.KILL_AURA_2_SUMMON), "kill_aura_2");
        SCREEN_SWORD = SwordSoaringSkills.build(event, ScreenSwordSkill::new, ScreenSwordSkill.createKillAuraBuilder().setCreativeTab(SwordSoaringItems.SWORD_SOARING_ITEM_TAB)
                .setPlayerSummonAnim(() -> ScreenSwordAnimations.PLAYER_SUMMON_SCREEN_SWORD).setSwordSummonAnim(() -> ScreenSwordAnimations.SCREEN_SWORD_SUMMON), "screen_sword");
        RAIN_SWORD = SwordSoaringSkills.build(event, RainSwordSkill::new, RainSwordSkill.createBuilder().setCategory(SwordSoaringSkillCategories.SWORD_CONTROLLER).setCreativeTab(SwordSoaringItems.SWORD_SOARING_ITEM_TAB).setResource(Skill.Resource.NONE), "rain_sword");
        GATE_OF_BABYLON = SwordSoaringSkills.build(event, GateOfBabylonSkill::new, GateOfBabylonSkill.createBuilder().setCategory(SwordSoaringSkillCategories.SWORD_CONTROLLER).setCreativeTab(SwordSoaringItems.SWORD_SOARING_ITEM_TAB).setResource(Skill.Resource.NONE), "babylon");
        WAN_JIAN_GUI_ZONG = SwordSoaringSkills.build(event, WanJianGuiZongSkill::new, WanJianGuiZongSkill.createBuilder().setCategory(SwordSoaringSkillCategories.SWORD_CONTROLLER).setCreativeTab(SwordSoaringItems.SWORD_SOARING_ITEM_TAB).setResource(Skill.Resource.NONE), "wan_jian_gui_zong");
    }
}