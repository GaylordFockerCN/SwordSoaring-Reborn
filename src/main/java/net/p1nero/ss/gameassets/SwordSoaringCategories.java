package net.p1nero.ss.gameassets;

import yesman.epicfight.world.capabilities.item.WeaponCategory;

public enum SwordSoaringCategories implements WeaponCategory {
    ARTIFACT_SPIRIT;
    SwordSoaringCategories(){
        this.id = WeaponCategory.ENUM_MANAGER.assign(this);
    }
    final int id;
    @Override
    public int universalOrdinal() {
        return this.id;
    }
}