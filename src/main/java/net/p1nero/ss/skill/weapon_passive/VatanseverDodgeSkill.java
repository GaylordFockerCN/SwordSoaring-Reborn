package net.p1nero.ss.skill.weapon_passive;

import com.p1nero.invincible.client.particles.InvincibleParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import yesman.epicfight.api.client.input.InputManager;
import yesman.epicfight.api.client.input.MovementDirection;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;

public class VatanseverDodgeSkill extends DodgeSkill {

    private static final UUID EVENT_UUID = UUID.fromString("23bd5c76-fe77-11ed-be56-0242ac114514");

    public VatanseverDodgeSkill(Builder builder) {
        super(builder);
    }

    public void onInitiate(SkillContainer container) {
        container.getExecutor().getEventListener().addEventListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event) -> {
            SkillContainer weaponInnate = event.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE);
            weaponInnate.getSkill().setStackSynchronize(weaponInnate, weaponInnate.getStack() + 1);
            ServerPlayer serverPlayer = event.getPlayerPatch().getOriginal();
            SkillDataManager manager = event.getPlayerPatch().getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
            if(manager.hasData(SwordSoaringDatakeys.ARTIFACT_SPIRIT_ENTITY_ID.get())) {
                int vatanseverId = manager.getDataValue(SwordSoaringDatakeys.ARTIFACT_SPIRIT_ENTITY_ID.get());
                serverPlayer.serverLevel().sendParticles(InvincibleParticles.TRANSPARENT_AFTER_IMAGE.get(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 1, serverPlayer.getId(), 1, 1, serverPlayer.getId());
                serverPlayer.serverLevel().sendParticles(InvincibleParticles.TRANSPARENT_AFTER_IMAGE.get(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 1, vatanseverId, 1, 1, vatanseverId);
            } else {
                onRemoved(container);
            }
        });
    }

    public void onRemoved(SkillContainer container) {
        container.getExecutor().getEventListener().removeListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID);
    }

    @OnlyIn(Dist.CLIENT)
    public Object getExecutionPacket(SkillContainer container, FriendlyByteBuf args) {
        LocalPlayerPatch executor = container.getClientExecutor();
        Input input = executor.getOriginal().input;
        float pulse = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(executor.getOriginal()), 0.0F, 1.0F);
        input.tick(false, pulse);
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

        CPSkillRequest packet = new CPSkillRequest(container.getSlot());
        packet.getBuffer().writeInt(animation);
        packet.getBuffer().writeFloat((vertic == 0 && horizon != 0) ? yRot : degree);
        return packet;
    }

    @Override
    public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf args) {
        super.executeOnServer(skillContainer, args);
        skillContainer.getExecutor().playSound(EpicFightSounds.ENTITY_MOVE.get(), 1.0F, 1.0F);
    }
}