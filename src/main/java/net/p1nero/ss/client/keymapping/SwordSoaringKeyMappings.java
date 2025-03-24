package net.p1nero.ss.client.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import com.p1nero.invincible.client.events.InputManager;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.p1nero.ss.gameassets.SwordSoaringComboTypes;
import org.lwjgl.glfw.GLFW;
import yesman.epicfight.client.input.CombatKeyMapping;

@Mod.EventBusSubscriber(value = {Dist.CLIENT},bus = Mod.EventBusSubscriber.Bus.MOD)
public class SwordSoaringKeyMappings {
    public static final KeyMapping TAKE_OFF = new CombatKeyMapping("key.sword_soaring.take_off", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_SPACE, "key.sword_soaring.common");
    public static final KeyMapping SWITCH_MODE = new CombatKeyMapping("key.sword_soaring.switch_mode", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_TAB, "key.sword_soaring.common");
    public static final KeyMapping ACCELERATION = new CombatKeyMapping("key.sword_soaring.acceleration", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, "key.sword_soaring.common");
    public static final KeyMapping SWORD_SKILL = new CombatKeyMapping("key.sword_soaring.sword_skill", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2, "key.sword_soaring.common");
    public static final KeyMapping SWORD_BACK = new CombatKeyMapping("key.sword_soaring.sword_back", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_5, "key.sword_soaring.common");

    @SubscribeEvent
    public static void registerKeys(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(TAKE_OFF);
        ClientRegistry.registerKeyBinding(SWITCH_MODE);
        ClientRegistry.registerKeyBinding(ACCELERATION);
        ClientRegistry.registerKeyBinding(SWORD_SKILL);
        ClientRegistry.registerKeyBinding(SWORD_BACK);

        InputManager.register(SwordSoaringComboTypes.KEY_SWORD_SKILL, SWORD_SKILL);
        InputManager.register(SwordSoaringComboTypes.KEY_TAKE_OFF, TAKE_OFF);
    }

}
