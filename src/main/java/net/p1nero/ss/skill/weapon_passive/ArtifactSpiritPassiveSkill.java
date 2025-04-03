package net.p1nero.ss.skill.weapon_passive;

import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;

public abstract class ArtifactSpiritPassiveSkill extends Skill {
    public static SkillDataManager.SkillDataKey<Integer> ARTIFACT_SPIRIT_ENTITY_ID = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);

    public ArtifactSpiritPassiveSkill(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(ARTIFACT_SPIRIT_ENTITY_ID);
    }

    public int getArtifactSpiritId(SkillContainer container){
        if(container.getDataManager().hasData(ARTIFACT_SPIRIT_ENTITY_ID)){
            return container.getDataManager().getDataValue(ARTIFACT_SPIRIT_ENTITY_ID);
        }
        return 0;
    }
}