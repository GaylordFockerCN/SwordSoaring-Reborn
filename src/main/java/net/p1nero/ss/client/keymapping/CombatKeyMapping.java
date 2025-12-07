package net.p1nero.ss.client.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.client.ClientEngine;

public class CombatKeyMapping extends KeyMapping {
    public CombatKeyMapping(String pName, int pKeyCode, String pCategory) {
        super(pName, pKeyCode, pCategory);
    }

    public CombatKeyMapping(String pName, InputConstants.Type pType, int pKeyCode, String pCategory) {
        super(pName, pType, pKeyCode, pCategory);
    }

    public CombatKeyMapping(String description, IKeyConflictContext keyConflictContext, InputConstants.Type inputType, int keyCode, String category) {
        super(description, keyConflictContext, inputType, keyCode, category);
    }

    public CombatKeyMapping(String description, IKeyConflictContext keyConflictContext, InputConstants.Key keyCode, String category) {
        super(description, keyConflictContext, keyCode, category);
    }

    public CombatKeyMapping(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, InputConstants.Type inputType, int keyCode, String category) {
        super(description, keyConflictContext, keyModifier, inputType, keyCode, category);
    }

    public CombatKeyMapping(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, InputConstants.Key keyCode, String category) {
        super(description, keyConflictContext, keyModifier, keyCode, category);
    }

    public boolean isActiveAndMatches(InputConstants.@NotNull Key keyCode) {
        return super.isActiveAndMatches(keyCode) && ClientEngine.getInstance().isEpicFightMode();
    }

}
