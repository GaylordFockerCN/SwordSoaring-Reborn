package net.p1nero.ss.gameassets;

import com.p1nero.invincible.skill.api.ComboType;

import java.util.ArrayList;
import java.util.List;

public enum SwordSoaringComboTypes implements ComboType {
    KEY_SWORD_SKILL,
    KEY_TAKE_OFF;
    final int id;

    SwordSoaringComboTypes() {
        this.subTypes = new ArrayList<>();
        this.id = ComboType.ENUM_MANAGER.assign(this);
    }
    final List<ComboType> subTypes;
    @Override
    public List<ComboType> getSubTypes() {
        return subTypes;
    }

    public int universalOrdinal() {
        return this.id;
    }
}