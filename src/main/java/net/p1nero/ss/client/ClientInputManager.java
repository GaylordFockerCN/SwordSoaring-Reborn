package net.p1nero.ss.client;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.math.Min;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.p1nero.ss.SwordSoaringConfig;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.keymapping.SwordSoaringKeyMappings;
import net.p1nero.ss.gameassets.SwordSoaringSkillCategories;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.mixin.ControlEngineAccessor;
import net.p1nero.ss.network.PacketHandler;
import net.p1nero.ss.network.PacketRelay;
import net.p1nero.ss.network.packet.server.RequestVatanseverSwordBackPacket;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;

@Mod.EventBusSubscriber(modid = SwordSoaringMod.MOD_ID, value = {Dist.CLIENT})
public class ClientInputManager {
    public static long lastPressTime;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        while (SwordSoaringKeyMappings.TAKE_OFF.consumeClick()){
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            long currentTime = System.currentTimeMillis();
            if(localPlayer != null && !localPlayer.onGround() && currentTime - lastPressTime < SwordSoaringConfig.FLY_DELAY.get()) {
                sendSkillPacket(SwordSoaringSkillSlots.SWORD_SOARING, SwordSoaringKeyMappings.TAKE_OFF);
            }
            lastPressTime = System.currentTimeMillis();
        }
        while (SwordSoaringKeyMappings.SWORD_SKILL.consumeClick()){
            sendSkillPacket(SwordSoaringSkillSlots.SWORD_CONTROLLER, SwordSoaringKeyMappings.SWORD_SKILL);
        }
        while (SwordSoaringKeyMappings.SWITCH_MODE.consumeClick()) {
            switchModeKeyPressed();
        }
        while (SwordSoaringKeyMappings.SWORD_BACK.consumeClick()) {
            swordBackKeyPressed();
        }
    }

    public static void swordBackKeyPressed() {
        PacketRelay.sendToServer(PacketHandler.INSTANCE, new RequestVatanseverSwordBackPacket());
    }

    public static void switchModeKeyPressed() {
        LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getEntityPatch(Minecraft.getInstance().player, LocalPlayerPatch.class);
        if (localPlayerPatch != null) {
            SkillContainer skillContainer = localPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_SOARING);
            List<Skill> learnedSkills = localPlayerPatch.getSkillCapability().listAcquiredSkills().filter(skill ->
                    skill.getCategory() == SwordSoaringSkillCategories.SWORD_SOARING).toList();
            if (learnedSkills.isEmpty()) {
                return;
            }
            int index = learnedSkills.indexOf(skillContainer.getSkill());
            int next = (index + 1) % learnedSkills.size();
            Skill nextSkill = learnedSkills.get(next);
            skillContainer.setSkill(nextSkill);
            EpicFightNetworkManager.sendToServer(new CPChangeSkill(SwordSoaringSkillSlots.SWORD_SOARING, -1, nextSkill));
            localPlayerPatch.getOriginal().displayClientMessage(Component.translatable("tips.sword_soaring.style_change").append(nextSkill.getDisplayName()), true);
        }
    }

    public static void sendSkillPacket(SkillSlot slot, KeyMapping key){
        LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getEntityPatch(Minecraft.getInstance().player, LocalPlayerPatch.class);
        if(localPlayerPatch != null){
            if(localPlayerPatch.getPlayerMode() == PlayerPatch.PlayerMode.EPICFIGHT && localPlayerPatch.getSkill(slot) != null && localPlayerPatch.getSkill(slot).sendCastRequest(localPlayerPatch, ClientEngine.getInstance().controlEngine).shouldReserveKey()){
                ControlEngineAccessor controlEngine = (ControlEngineAccessor) ClientEngine.getInstance().controlEngine;
                controlEngine.setReserveCounter(8);
                controlEngine.setReservedOrHoldingSkillSlot(slot);
                controlEngine.setReservedKey(key);
            }
        }
    }

}