package net.p1nero.ss.gameassets.skills;

import com.p1nero.invincible.conditions.CooldownCondition;
import com.p1nero.invincible.conditions.CustomCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import com.p1nero.invincible.skill.api.ComboNode;
import net.p1nero.ss.gameassets.SwordSoaringComboTypes;
import net.p1nero.ss.gameassets.SwordSoaringSkills;
import net.p1nero.ss.gameassets.animations.VatanseverAnimations;
import net.p1nero.ss.skill.weapon_innate.VatanseverWeaponInnateSkill;
import net.p1nero.ss.skill.weapon_passive.VatanseverPassive;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class VatanseverSkills {
    public static Skill VATANSEVER_INNATE;
    public static Skill VATANSEVER_PASSIVE;

    public static void buildVatanseverSkills(SkillBuildEvent event) {
        ComboNode root = ComboNode.create();
        ComboNode a = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_AUTO1).addCondition(checkSwordCount(1, 6)).setCanBeInterrupt(false);
        ComboNode aa = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_AUTO2).addCondition(checkSwordCount(2, 6)).setCanBeInterrupt(false);
        ComboNode aaa = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_AUTO3).addCondition(checkSwordCount(1, 6)).setCanBeInterrupt(false);
        ComboNode aab = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_AUTO3_B).addCondition(checkSwordCount(5, 6)).setCanBeInterrupt(false);
        ComboNode aaaa = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_AUTO4).addCondition(checkSwordCount(6)).setCanBeInterrupt(false);
        ComboNode aaab = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_AUTO4_B).addCondition(checkSwordCount(6)).setCanBeInterrupt(false);
        ComboNode storm = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_STORM_START).addCondition(checkSwordCount(6))
                .setCooldown(800)
                .addCondition(new CooldownCondition(false)).setCanBeInterrupt(false);
        ComboNode shootL3 = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_SHOOT_L3).addCondition(checkSwordCount(6)).setPriority(6).setCanBeInterrupt(false);
        ComboNode shootR3 = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_SHOOT_R3).addCondition(checkSwordCount(5)).setPriority(5).setCanBeInterrupt(false);
        ComboNode shootL2 = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_SHOOT_L2).addCondition(checkSwordCount(4)).setPriority(4).setCanBeInterrupt(false);
        ComboNode shootR2 = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_SHOOT_R2).addCondition(checkSwordCount(3)).setPriority(3).setCanBeInterrupt(false);
        ComboNode shootL1 = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_SHOOT_L1).addCondition(checkSwordCount(2)).setPriority(2).setCanBeInterrupt(false);
        ComboNode shootR1 = ComboNode.createNode(() -> VatanseverAnimations.PLAYER_SHOOT_R1).addCondition(checkSwordCount(1)).setPriority(1).setCanBeInterrupt(false);
        ComboNode shoot = ComboNode.create().addConditionAnimation(shootL1)
                .addConditionAnimation(shootL2)
                .addConditionAnimation(shootL3)
                .addConditionAnimation(shootR1)
                .addConditionAnimation(shootR2)
                .addConditionAnimation(shootR3);
        root.key1(a);
        a.key1(aa);
        aa.key1(aaa);
        aa.key2(aab);
        aaa.key1(aaaa);
        aaa.key2(aaab);
        aaaa.key1(a);
        root.key3(shoot);
        a.key3(shoot);
        aa.key3(shoot);
        aaa.key3(shoot);
        aaaa.key3(shoot);
        shoot.key1(a);
        shoot.key3(shoot);
        root.addChild(SwordSoaringComboTypes.KEY_SWORD_SKILL, storm);
        VATANSEVER_INNATE = SwordSoaringSkills.build(event, VatanseverWeaponInnateSkill::new, ComboBasicAttack.createComboBasicAttack().setCombo(root).setShouldDrawGui(true), "vatansever_innate");
        VATANSEVER_PASSIVE = SwordSoaringSkills.build(event, VatanseverPassive::new, Skill.createBuilder().setCategory(SkillCategories.WEAPON_PASSIVE).setResource(Skill.Resource.NONE), "vatansever_passive");
    }

    public static CustomCondition checkSwordCount(int swordCount) {
        return checkSwordCount(swordCount, swordCount);
    }

    public static CustomCondition checkSwordCount(int min, int max) {
        return new CustomCondition() {
            @Override
            public boolean predicate(LivingEntityPatch<?> entityPatch) {
                if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                    int swordCount = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(VatanseverPassive.SWORD_COUNT);
                    return min <= swordCount && max >= swordCount;
                }
                return false;
            }

        };
    }
}