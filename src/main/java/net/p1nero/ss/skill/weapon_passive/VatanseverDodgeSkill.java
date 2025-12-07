package net.p1nero.ss.skill.weapon_passive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import yesman.epicfight.api.client.input.InputManager;
import yesman.epicfight.api.client.input.MovementDirection;
import yesman.epicfight.api.neoevent.playerpatch.DodgeSuccessEvent;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillEvent;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.dodge.DodgeSkill;

public class VatanseverDodgeSkill extends DodgeSkill {

    public VatanseverDodgeSkill(DodgeSkill.Builder<?> builder) {
        super(builder);
    }

    @SkillEvent(side = SkillEvent.Side.SERVER)
    public void onDodgeSuccess(DodgeSuccessEvent event, SkillContainer container) {
        SkillContainer weaponInnate = event.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE);
        weaponInnate.getSkill().setStackSynchronize(weaponInnate, weaponInnate.getStack() + 1);
        ServerPlayer serverPlayer = event.getPlayerPatch().getOriginal();
        SkillDataManager manager = event.getPlayerPatch().getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
        if(manager.hasData(SwordSoaringDatakeys.ARTIFACT_SPIRIT_ENTITY_ID)) {
            int vatanseverId = manager.getDataValue(SwordSoaringDatakeys.ARTIFACT_SPIRIT_ENTITY_ID);
            serverPlayer.serverLevel().sendParticles(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 1, serverPlayer.getId(), 1, 1, serverPlayer.getId());
            serverPlayer.serverLevel().sendParticles(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 1, vatanseverId, 1, 1, vatanseverId);
        } else {
            onRemoved(container);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void gatherArguments(SkillContainer container, ControlEngine controlEngine, CompoundTag arguments) {
        LocalPlayerPatch executor = container.getClientExecutor();
        Input input = executor.getOriginal().input;
        float sneakingSpeed = (float) executor.getOriginal().getAttributeValue(Attributes.SNEAKING_SPEED);
        input.tick(false, sneakingSpeed);
        MovementDirection movementDirection = MovementDirection.fromInputState(InputManager.getInputState(input));
        int vertic = movementDirection.vertical();
        int horizon = movementDirection.horizontal();
        float yRot = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
        float degree = (float)(-(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon)) + yRot;
        int animation;
        if (vertic == 0) {
            if (horizon == 0) {
                animation = 0;
            } else {
                animation = horizon >= 0 ? 2 : 3;
            }
        } else {
            animation = vertic >= 0 ? 0 : 1;
        }
        arguments.putInt("direction", animation);
        arguments.putFloat("yRot", (vertic == 0 && horizon != 0) ? yRot : degree);
    }

    @Override
    public void executeOnServer(SkillContainer skillContainer, CompoundTag args) {
        super.executeOnServer(skillContainer, args);
        skillContainer.getExecutor().playSound(EpicFightSounds.ENTITY_MOVE.get(), 1.0F, 1.0F);
    }

}
