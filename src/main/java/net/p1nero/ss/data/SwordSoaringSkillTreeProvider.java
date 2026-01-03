package net.p1nero.ss.data;

import com.yesman.epicskills.common.data.SkillTreeProvider;
import java.util.function.Consumer;
import net.minecraft.data.PackOutput;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.gameassets.SwordSoaringSkills;
import yesman.epicfight.api.utils.math.Vec2i;

public class SwordSoaringSkillTreeProvider extends SkillTreeProvider {
    public SwordSoaringSkillTreeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    protected void buildSkillTreePages(Consumer<SkillTreeProvider.SkillTreePageBuilder> writer) {
        writer.accept(
                newPage(SwordSoaringMod.MOD_ID, "sword_soaring_skills")
                        .menuBarColor(37, 27, 18)
                        .newNode(SwordSoaringSkills.SWORD_SOARING_APPRENTICE)
                            .position(20, 30)
                            .abilityPointsRequirement(1)
                        .done()
                        .newNode(SwordSoaringSkills.SWORD_SOARING_EXPERT)
                            .addParent(SwordSoaringSkills.SWORD_SOARING_APPRENTICE)
                            .position(20, 130)
                            .abilityPointsRequirement(5)
                        .done()
                        .newNode(SwordSoaringSkills.SWORD_SOARING_MASTER)
                            .addParent(SwordSoaringSkills.SWORD_SOARING_EXPERT)
                            .position(20, 230)
                            .abilityPointsRequirement(10)
                        .done()

                        .newNode(SwordSoaringSkills.SWORD_SOARING_ELYTRA_APPRENTICE)
                            .position(120, 30)
                            .abilityPointsRequirement(1)
                        .done()
                        .newNode(SwordSoaringSkills.SWORD_SOARING_ELYTRA_EXPERT)
                            .addParent(SwordSoaringSkills.SWORD_SOARING_ELYTRA_APPRENTICE)
                            .position(120, 130)
                            .abilityPointsRequirement(5)
                        .done()
                        .newNode(SwordSoaringSkills.SWORD_SOARING_ELYTRA_MASTER)
                            .addParent(SwordSoaringSkills.SWORD_SOARING_ELYTRA_EXPERT)
                            .position(120, 230)
                            .abilityPointsRequirement(10)
                        .done()

                        .newNode(SwordSoaringSkills.KILL_AURA_1)
                            .position(220, 30)
                            .abilityPointsRequirement(5)
                        .done()
                        .newNode(SwordSoaringSkills.KILL_AURA_2)
                            .position(320, 30)
                            .abilityPointsRequirement(5)
                        .done()
                        .newNode(SwordSoaringSkills.WAN_JIAN_GUI_ZONG)
                            .addParent(SwordSoaringSkills.KILL_AURA_1, new Vec2i(220, 80))
                            .addParent(SwordSoaringSkills.KILL_AURA_2, new Vec2i(320, 80))
                            .position(270, 130)
                            .abilityPointsRequirement(20)
                        .done()


                        .newNode(SwordSoaringSkills.SCREEN_SWORD)
                        .position(220, 230)
                        .abilityPointsRequirement(5)
                        .done()
                        .newNode(SwordSoaringSkills.RAIN_SWORD)
                        .position(320, 230)
                        .abilityPointsRequirement(5)
                        .done()
                        .newNode(SwordSoaringSkills.GATE_OF_BABYLON)
                        .addParent(SwordSoaringSkills.SCREEN_SWORD, new Vec2i(220, 280))
                        .addParent(SwordSoaringSkills.RAIN_SWORD, new Vec2i(320, 280))
                        .position(270, 330)
                        .abilityPointsRequirement(20)
                        .done()
        );
    }
}
