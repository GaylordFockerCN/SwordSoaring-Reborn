package net.p1nero.ss.gameassets;

import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

public enum SwordSoaringSkillSlots implements SkillSlot {
    SWORD_CONTROLLER(SwordSoaringSkillCategories.SWORD_CONTROLLER),
    SWORD_SOARING(SwordSoaringSkillCategories.SWORD_SOARING);
    final SkillCategory category;
    final int id;

    SwordSoaringSkillSlots(SwordSoaringSkillCategories category){
        this.category = category;
        this.id = SkillSlot.ENUM_MANAGER.assign(this);
    }


    @Override
    public SkillCategory category() {
        return category;
    }

    @Override
    public int universalOrdinal() {
        return id;
    }
}