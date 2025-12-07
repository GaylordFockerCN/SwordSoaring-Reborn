package net.p1nero.ss.compat.controlify;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.ControlifyBindApi;
import dev.isxander.controlify.api.bind.InputBindingSupplier;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.entrypoint.InitContext;
import dev.isxander.controlify.api.entrypoint.PreInitContext;
import dev.isxander.controlify.bindings.BindContext;
import dev.isxander.controlify.bindings.RadialIcons;
import dev.isxander.controlify.utils.render.Blit;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.keymapping.SwordSoaringKeyMappings;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class ControlifyCompat implements ControlifyEntrypoint {
    private static InputBindingSupplier swordSkill;
    private static InputBindingSupplier takeOff;
    private static InputBindingSupplier toggleSoaringMode;
    private static InputBindingSupplier acceleration;
    private static InputBindingSupplier swordBack;

    @Override
    public void onControllersDiscovered(ControlifyApi controlify) {

    }

    @Override
    public void onControlifyInit(InitContext context) {

    }

    @Override
    public void onControlifyPreInit(PreInitContext context) {
        final ControlifyBindApi registrar = ControlifyBindApi.get();
        registerCustomRadialIcons();
        registrar.registerBindContext(IN_GAME_EPIC_FIGHT_CONTEXT);
        registerInputBindings(registrar);
    }

    private enum SwordSoaringRadialIcons {
        SCREEN_SWORD(SwordSoaringMod.rl("textures/gui/skills/sword_controller/screen_sword.png")),
        VATANSEVER(SwordSoaringMod.rl("textures/item/vatansever.png")),
        SWORD_SOARING_APPRENTICE(SwordSoaringMod.rl("textures/gui/skills/sword_soaring/sword_soaring_apprentice.png")),
        ;

        private final @NotNull ResourceLocation id;

        SwordSoaringRadialIcons(@NotNull ResourceLocation id) {
            this.id = id;
        }

        public @NotNull ResourceLocation getId() {
            return id;
        }
    }

    private static void registerCustomRadialIcons() {
        for (SwordSoaringRadialIcons icon : SwordSoaringRadialIcons.values()) {
            final ResourceLocation location = icon.getId();
            RadialIcons.registerIcon(location, (graphics, x, y, tickDelta) -> {
                graphics.pose().pushPose();
                graphics.pose().translate(x, y, 0);
                graphics.pose().scale(0.5f, 0.5f, 1f);
                Blit.blitTex(graphics, location, 0, 0, 0, 0, 32, 32, 32, 32);
                graphics.pose().popPose();
            });
        }
    }

    private static void registerInputBindings(ControlifyBindApi registrar) {
        swordSkill = registrar.registerBinding(
                builder -> builder.id(SwordSoaringMod.rl("sword_skill"))
                        .category(ComponentConstants.COMMON_CATEGORY)
                        .allowedContexts(IN_GAME_EPIC_FIGHT_CONTEXT)
                        .name(ComponentConstants.SWORD_SKILL)
                        .description(ComponentConstants.SWORD_SKILL_DESCRIPTION)
                        .addKeyCorrelation(SwordSoaringKeyMappings.SWORD_SKILL)
                        .keyEmulation(SwordSoaringKeyMappings.SWORD_SKILL)
                        .radialCandidate(SwordSoaringRadialIcons.SCREEN_SWORD.getId())
        );
        takeOff = registrar.registerBinding(
                builder -> builder.id(SwordSoaringMod.rl("take_off"))
                        .category(ComponentConstants.COMMON_CATEGORY)
                        .allowedContexts(IN_GAME_EPIC_FIGHT_CONTEXT)
                        .name(ComponentConstants.TAKE_OFF)
                        .description(ComponentConstants.TAKE_OFF_DESCRIPTION)
                        .addKeyCorrelation(SwordSoaringKeyMappings.TAKE_OFF)
                        .keyEmulation(SwordSoaringKeyMappings.TAKE_OFF)
        );
        toggleSoaringMode = registrar.registerBinding(
                builder -> builder.id(SwordSoaringMod.rl("switch_mode"))
                        .category(ComponentConstants.COMMON_CATEGORY)
                        .allowedContexts(IN_GAME_EPIC_FIGHT_CONTEXT)
                        .name(ComponentConstants.SWITCH_MODE)
                        .description(ComponentConstants.SWITCH_MODE_DESCRIPTION)
                        .addKeyCorrelation(SwordSoaringKeyMappings.SWITCH_MODE)
                        .keyEmulation(SwordSoaringKeyMappings.SWITCH_MODE)
                        .radialCandidate(SwordSoaringRadialIcons.SWORD_SOARING_APPRENTICE.getId())
        );
        acceleration = registrar.registerBinding(
                builder -> builder.id(SwordSoaringMod.rl("acceleration"))
                        .category(ComponentConstants.COMMON_CATEGORY)
                        .allowedContexts(IN_GAME_EPIC_FIGHT_CONTEXT)
                        .name(ComponentConstants.ACCELERATION)
                        .description(ComponentConstants.ACCELERATION_DESCRIPTION)
                        .addKeyCorrelation(SwordSoaringKeyMappings.ACCELERATION)
                        .keyEmulation(SwordSoaringKeyMappings.ACCELERATION)
        );
        swordBack = registrar.registerBinding(
                builder -> builder.id(SwordSoaringMod.rl("sword_back"))
                        .category(ComponentConstants.COMMON_CATEGORY)
                        .allowedContexts(IN_GAME_EPIC_FIGHT_CONTEXT)
                        .name(ComponentConstants.SWORD_BACK)
                        .description(ComponentConstants.SWORD_BACK_DESCRIPTION)
                        .addKeyCorrelation(SwordSoaringKeyMappings.SWORD_BACK)
                        .keyEmulation(SwordSoaringKeyMappings.SWORD_BACK)
                        .radialCandidate(SwordSoaringRadialIcons.VATANSEVER.getId())
        );
    }

    private static final BindContext IN_GAME_EPIC_FIGHT_CONTEXT = new BindContext(
            SwordSoaringMod.rl("epicfight_combat"),
            mc -> {
                final boolean isInGame = mc.screen == null && mc.level != null && mc.player != null;
                final LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getEntityPatch(mc.player, LocalPlayerPatch.class);
                if (localPlayerPatch == null) {
                    return false;
                }
                return isInGame && localPlayerPatch.isEpicFightMode();
            }
    );

    private static class ComponentConstants {
        private static final Component COMMON_CATEGORY = Component.translatable("key.sword_soaring.common");

        // Names
        private static final Component SWORD_SKILL = Component.translatable("key.sword_soaring.sword_skill");
        private static final Component SWORD_BACK = Component.translatable("key.sword_soaring.sword_back");
        private static final Component TAKE_OFF = Component.translatable("key.sword_soaring.take_off");
        private static final Component ACCELERATION = Component.translatable("key.sword_soaring.acceleration");
        private static final Component SWITCH_MODE = Component.translatable("key.sword_soaring.switch_mode");

        // Descriptions
        private static final Component SWORD_SKILL_DESCRIPTION = Component.translatable("key.sword_soaring.sword_skill.description");
        private static final Component SWORD_BACK_DESCRIPTION = Component.translatable("key.sword_soaring.sword_back.description");
        private static final Component TAKE_OFF_DESCRIPTION = Component.translatable("key.sword_soaring.take_off.description");
        private static final Component ACCELERATION_DESCRIPTION = Component.translatable("key.sword_soaring.acceleration.description");
        private static final Component SWITCH_MODE_DESCRIPTION = Component.translatable("key.sword_soaring.switch_mode.description");
    }
}
