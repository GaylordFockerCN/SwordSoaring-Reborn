package net.p1nero.ss.gameassets;

import com.p1nero.invincible.api.EventPresets;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.api.events.TimeStampedEvent;
import com.p1nero.invincible.conditions.CooldownCondition;
import com.p1nero.invincible.conditions.CustomCondition;
import com.p1nero.invincible.conditions.StackCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.entity.vatansever.VatanseverEntity;
import net.p1nero.ss.gameassets.animations.FlyAnimations;
import net.p1nero.ss.gameassets.animations.ScreenSwordAnimations;
import net.p1nero.ss.gameassets.animations.VatanseverAnimations;
import net.p1nero.ss.item.SwordSoaringItems;
import net.p1nero.ss.skill.sword_controller.*;
import net.p1nero.ss.skill.sword_soaring.SwordSoaringSkill;
import net.p1nero.ss.skill.sword_soaring.SwordSoaringSkillElytra;
import net.p1nero.ss.skill.weapon_innate.VatanseverWeaponInnateSkill;
import net.p1nero.ss.skill.weapon_passive.VatanseverDodgeSkill;
import net.p1nero.ss.skill.weapon_passive.VatanseverPassive;
import yesman.epicfight.data.conditions.entity.TargetInDistance;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SwordSoaringSkills {
    public static final DeferredRegister<Skill> REGISTRY = DeferredRegister.create(EpicFightRegistries.Keys.SKILL, SwordSoaringMod.MOD_ID);

    public static DeferredHolder<Skill, SwordSoaringSkill> SWORD_SOARING_APPRENTICE = SwordSoaringSkills.REGISTRY.register("sword_soaring_apprentice", (key) ->
            SwordSoaringSkill.createSwordSoaringSkill(SwordSoaringSkill::new)
                    .setFlyingAnimations(FlyAnimations.APPRENTICE_INIT, FlyAnimations.APPRENTICE_FLYING, FlyAnimations.APPRENTICE_SPEED_UP).build(key));

    public static DeferredHolder<Skill, SwordSoaringSkill> SWORD_SOARING_EXPERT = SwordSoaringSkills.REGISTRY.register("sword_soaring_expert", (key) ->
            SwordSoaringSkill.createSwordSoaringSkill(SwordSoaringSkill::new)
                    .setFlyingAnimations(FlyAnimations.EXPERT_INIT, FlyAnimations.EXPERT_FLYING, FlyAnimations.EXPERT_SPEED_UP)
                    .build(key));

    public static DeferredHolder<Skill, SwordSoaringSkill> SWORD_SOARING_MASTER = SwordSoaringSkills.REGISTRY.register("sword_soaring_master", (key) ->
            SwordSoaringSkill.createSwordSoaringSkill(SwordSoaringSkill::new)
                    .setFlyingAnimations(FlyAnimations.MASTER_INIT, FlyAnimations.MASTER_FLYING, FlyAnimations.MASTER_SPEED_UP)
                    .build(key));

    public static DeferredHolder<Skill, SwordSoaringSkillElytra> SWORD_SOARING_ELYTRA_APPRENTICE = SwordSoaringSkills.REGISTRY.register("sword_soaring_elytra_apprentice", (key) ->
            SwordSoaringSkill.createSwordSoaringSkill(SwordSoaringSkillElytra::new)
                    .setFlyingAnimations(FlyAnimations.APPRENTICE_INIT, FlyAnimations.APPRENTICE_FLYING, FlyAnimations.APPRENTICE_SPEED_UP)
                    .build(key));

    public static DeferredHolder<Skill, SwordSoaringSkillElytra> SWORD_SOARING_ELYTRA_EXPERT = SwordSoaringSkills.REGISTRY.register("sword_soaring_elytra_expert", (key) ->
            SwordSoaringSkill.createSwordSoaringSkill(SwordSoaringSkillElytra::new)
                    .setFlyingAnimations(FlyAnimations.EXPERT_INIT, FlyAnimations.EXPERT_FLYING, FlyAnimations.EXPERT_SPEED_UP)
                    .build(key));

    public static DeferredHolder<Skill, SwordSoaringSkillElytra> SWORD_SOARING_ELYTRA_MASTER = SwordSoaringSkills.REGISTRY.register("sword_soaring_elytra_master", (key) ->
            SwordSoaringSkill.createSwordSoaringSkill(SwordSoaringSkillElytra::new)
                    .setFlyingAnimations(FlyAnimations.MASTER_INIT, FlyAnimations.MASTER_FLYING, FlyAnimations.MASTER_SPEED_UP)
                    .build(key));

    public static DeferredHolder<Skill, KillAuraSkill> KILL_AURA_1 = REGISTRY.register("kill_aura_1", (key) ->
            KillAuraSkill.createKillAuraBuilder(KillAuraSkill::new)
                    .setCreativeTab(SwordSoaringItems.DEFAULT_TAB.get())
                    .setPlayerSummonAnim(ScreenSwordAnimations.PLAYER_SUMMON_KILL_AURA_1)
                    .setSwordSummonAnim(ScreenSwordAnimations.KILL_AURA_1_SUMMON)
                    .build(key));

    public static DeferredHolder<Skill, KillAuraSkill> KILL_AURA_2 = REGISTRY.register("kill_aura_2", (key) ->
            KillAuraSkill.createKillAuraBuilder(KillAuraSkill::new)
                    .setCreativeTab(SwordSoaringItems.DEFAULT_TAB.get())
                    .setPlayerSummonAnim(ScreenSwordAnimations.PLAYER_SUMMON_KILL_AURA_2)
                    .setSwordSummonAnim(ScreenSwordAnimations.KILL_AURA_2_SUMMON)
                    .build(key));

    public static DeferredHolder<Skill, ScreenSwordSkill> SCREEN_SWORD = REGISTRY.register("screen_sword", (key) ->
            ScreenSwordSkill.createKillAuraBuilder(ScreenSwordSkill::new)
                    .setCreativeTab(SwordSoaringItems.DEFAULT_TAB.get())
                    .setPlayerSummonAnim(ScreenSwordAnimations.PLAYER_SUMMON_SCREEN_SWORD)
                    .setSwordSummonAnim(ScreenSwordAnimations.SCREEN_SWORD_SUMMON)
                    .build(key));

    public static DeferredHolder<Skill, RainSwordSkill> RAIN_SWORD = REGISTRY.register("rain_sword", (key) ->
            RainSwordSkill.createBuilder(RainSwordSkill::new)
                    .setCreativeTab(SwordSoaringItems.DEFAULT_TAB.get())
                    .setCategory(SwordSoaringSkillCategories.SWORD_CONTROLLER)
                    .setResource(Skill.Resource.NONE)
                    .build(key));

    public static DeferredHolder<Skill, GateOfBabylonSkill> GATE_OF_BABYLON = REGISTRY.register("babylon", (key) ->
            GateOfBabylonSkill.createBuilder(GateOfBabylonSkill::new)
                    .setCreativeTab(SwordSoaringItems.DEFAULT_TAB.get())
                    .setCategory(SwordSoaringSkillCategories.SWORD_CONTROLLER)
                    .setResource(Skill.Resource.NONE)
                    .build(key));

    public static DeferredHolder<Skill, WanJianGuiZongSkill> WAN_JIAN_GUI_ZONG = REGISTRY.register("wan_jian_gui_zong", (key) ->
            WanJianGuiZongSkill.createBuilder(WanJianGuiZongSkill::new)
                    .setCreativeTab(SwordSoaringItems.DEFAULT_TAB.get())
                    .setCategory(SwordSoaringSkillCategories.SWORD_CONTROLLER)
                    .setResource(Skill.Resource.NONE)
                    .build(key));
    public static DeferredHolder<Skill, VatanseverWeaponInnateSkill> VATANSEVER_INNATE = REGISTRY.register("vatansever_innate", (key) ->
            ComboBasicAttack.createComboBasicAttack(VatanseverWeaponInnateSkill::new)
                    .setCombo(getRoot())
                    .setShouldDrawGui(true)
                    .build(key));

    public static DeferredHolder<Skill, VatanseverPassive> VATANSEVER_PASSIVE = REGISTRY.register("vatansever_passive", (key) ->
            Skill.createBuilder(VatanseverPassive::new)
                    .setCategory(SkillCategories.WEAPON_PASSIVE)
                    .setResource(Skill.Resource.NONE)
                    .build(key));

    public static DeferredHolder<Skill, VatanseverDodgeSkill> VATANSEVER_DODGE = REGISTRY.register("vatansever_dodge", (key) ->
            VatanseverDodgeSkill.createDodgeBuilder(VatanseverDodgeSkill::new)
                    .setAnimations(
                            VatanseverAnimations.PLAYER_DODGE_F,
                            VatanseverAnimations.PLAYER_DODGE_B,
                            VatanseverAnimations.PLAYER_DODGE_L,
                            VatanseverAnimations.PLAYER_DODGE_R
                    )
                    .setCreativeTab(SwordSoaringItems.DEFAULT_TAB.get())
                    .build(key));

    public static ComboNode getRoot() {
        ComboNode root = ComboNode.create();
        ComboNode a = ComboNode.createNode(VatanseverAnimations.PLAYER_AUTO1).addCondition(checkSwordCount(1, 6)).setCanBeInterrupt(false);
        ComboNode aa_1 = ComboNode.createNode(VatanseverAnimations.PLAYER_AUTO2_1).addCondition(checkSwordCount(2, 6)).setCanBeInterrupt(false);
        ComboNode aa_2 = ComboNode.createNode(VatanseverAnimations.PLAYER_AUTO2_2).addCondition(checkSwordCount(2, 6)).setCanBeInterrupt(false);
        ComboNode aaa = ComboNode.createNode(VatanseverAnimations.PLAYER_AUTO3).addCondition(checkSwordCount(1, 6)).setCanBeInterrupt(false);
        ComboNode aab = ComboNode.createNode(VatanseverAnimations.PLAYER_AUTO3_B).addCondition(checkSwordCount(5, 6)).setCanBeInterrupt(false);
        ComboNode aaaa = ComboNode.createNode(VatanseverAnimations.PLAYER_AUTO4).addCondition(checkSwordCount(6)).setCanBeInterrupt(false);
        ComboNode aaab = ComboNode.createNode(VatanseverAnimations.PLAYER_AUTO4_B).addCondition(checkSwordCount(6)).setCanBeInterrupt(false);
        ComboNode storm = ComboNode.createNode(VatanseverAnimations.PLAYER_STORM_START).addCondition(checkSwordCount(6))
                .setCooldown(1200)
                .addCondition(new StackCondition(1, 7))
                .addTimeEvent(EventPresets.consumeStack(1))
                .addCondition(new CooldownCondition(false))
                .setPriority(10)
                .setCanBeInterrupt(false);
        ComboNode execute = ComboNode.createNode(VatanseverAnimations.PLAYER_EXECUTE)
                .setConvertTime(0.15F)
                .addCondition(new StackCondition(7, 7))
                .addCondition(new CustomCondition() {
                    @Override
                    public boolean predicate(LivingEntityPatch<?> entityPatch) {
                        return entityPatch.getTarget() != null;
                    }
                })
                .addCondition(new TargetInDistance(0, 5))
                .addTimeEvent(new TimeStampedEvent(0.01F, (entityPatch, target, invinciblePlayer) -> {
                    if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                        container.getSkill().setStackSynchronize(container, 0);
                        container.getSkill().setConsumptionSynchronize(container, 0);
                        serverPlayerPatch.getTarget().moveTo(serverPlayerPatch.getOriginal().position());
                        serverPlayerPatch.getTarget().setYRot(serverPlayerPatch.getYRot());
                        serverPlayerPatch.getTarget().setYBodyRot(serverPlayerPatch.getYRot());
                        serverPlayerPatch.getTarget().setYHeadRot(serverPlayerPatch.getYRot());
                        LivingEntityPatch<?> livingEntityPatch = EpicFightCapabilities.getEntityPatch(serverPlayerPatch.getTarget(), LivingEntityPatch.class);
                        if (livingEntityPatch.getArmature() instanceof HumanoidArmature) {
                            livingEntityPatch.playAnimationSynchronized(VatanseverAnimations.PLAYER_BE_EXECUTED, 0.10F);
                        }
                    }
                }));
        ComboNode shootL3 = ComboNode.createNode(VatanseverAnimations.PLAYER_SHOOT_L3).addCondition(checkSwordCount(6)).addCondition(checkIsNotInaction()).setPriority(6).setCanBeInterrupt(false);
        ComboNode shootR3 = ComboNode.createNode(VatanseverAnimations.PLAYER_SHOOT_R3).addCondition(checkSwordCount(5)).addCondition(checkIsNotInaction()).setPriority(5).setCanBeInterrupt(false);
        ComboNode shootL2 = ComboNode.createNode(VatanseverAnimations.PLAYER_SHOOT_L2).addCondition(checkSwordCount(4)).addCondition(checkIsNotInaction()).setPriority(4).setCanBeInterrupt(false);
        ComboNode shootR2 = ComboNode.createNode(VatanseverAnimations.PLAYER_SHOOT_R2).addCondition(checkSwordCount(3)).addCondition(checkIsNotInaction()).setPriority(3).setCanBeInterrupt(false);
        ComboNode shootL1 = ComboNode.createNode(VatanseverAnimations.PLAYER_SHOOT_L1).addCondition(checkSwordCount(2)).addCondition(checkIsNotInaction()).setPriority(2).setCanBeInterrupt(false);
        ComboNode shootR1 = ComboNode.createNode(VatanseverAnimations.PLAYER_SHOOT_R1).addCondition(checkSwordCount(1)).addCondition(checkIsNotInaction()).setPriority(1).setCanBeInterrupt(false);
        ComboNode shoot = ComboNode.create()
                .addConditionNode(shootL1)
                .addConditionNode(shootL2)
                .addConditionNode(shootL3)
                .addConditionNode(shootR1)
                .addConditionNode(shootR2)
                .addConditionNode(shootR3)
                .addConditionNode(storm);
        root.key1(a);
        a.key1(aa_1);
        aa_1.key1(aa_2);
        aa_2.key1(aaa);
        aa_2.key2(aab);
        aaa.key1(aaaa);
        aaa.key2(aaab);
        aaaa.key1(a);
        a.key3(shoot);
        aa_1.key3(shoot);
        aa_2.key3(shoot);
        aaa.key3(shoot);
        aaaa.key3(shoot);
        shoot.key1(a);
        shoot.key3(shoot);
        root.key3(shoot);
        root.key1_4(execute);
        return root;
    }

    public static CustomCondition checkSwordCount(int swordCount) {
        return checkSwordCount(swordCount, swordCount);
    }

    public static CustomCondition checkSwordCount(int min, int max) {
        return new CustomCondition() {
            @Override
            public boolean predicate(LivingEntityPatch<?> entityPatch) {
                if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                    int swordCount = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(SwordSoaringDatakeys.SWORD_COUNT);
                    return min <= swordCount && max >= swordCount;
                }
                return false;
            }

        };
    }

    public static CustomCondition checkIsNotInaction() {
        return new CustomCondition() {
            @Override
            public boolean predicate(LivingEntityPatch<?> entityPatch) {
                if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                    int id = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(SwordSoaringDatakeys.ARTIFACT_SPIRIT_ENTITY_ID);
                    Entity entity = serverPlayerPatch.getOriginal().level().getEntity(id);
                    if (entity instanceof VatanseverEntity vatansever) {
                        return !vatansever.getPatch().getEntityState().movementLocked();
                    }
                }
                return false;
            }

        };
    }

}
