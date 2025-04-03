package net.p1nero.ss.gameassets;

import yesman.epicfight.skill.SkillCategory;

public enum SwordSoaringSkillCategories implements SkillCategory
{
    SWORD_CONTROLLER(true, true, true),
    SWORD_SOARING(true, true, true);

    final boolean save;
    final boolean sync;
    final boolean modifiable;
    final int id;

    SwordSoaringSkillCategories(boolean ShouldSave, boolean ShouldSync, boolean Modifiable){
        this.modifiable = Modifiable;
        this.save = ShouldSave;
        this.sync = ShouldSync;
        this.id = SkillCategory.ENUM_MANAGER.assign(this);
    }

    @Override
    public boolean shouldSave()
    {
        return this.save;
    }

    @Override
    public boolean shouldSynchronize()
    {
        return this.sync;
    }

    @Override
    public boolean learnable()
    {
        return this.modifiable;
    }
    @Override
    public int universalOrdinal()
    {
        return this.id;
    }
}