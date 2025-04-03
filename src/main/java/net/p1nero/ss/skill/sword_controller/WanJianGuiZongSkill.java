package net.p1nero.ss.skill.sword_controller;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.Config;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.capability.SSCapabilityProvider;
import net.p1nero.ss.client.keymapping.SwordSoaringKeyMappings;
import net.p1nero.ss.client.sound.WanSoundInstance;
import net.p1nero.ss.entity.sword.fly_sword.FlySwordEntity;
import net.p1nero.ss.entity.sword.sword_convergence.SwordConvergenceEntity;
import net.p1nero.ss.gameassets.animations.FlySwordAnimations;
import net.p1nero.ss.gameassets.animations.SwordConvergenceAnimations;
import net.p1nero.ss.util.ItemUtils;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class WanJianGuiZongSkill extends Skill {
    public static SkillDataManager.SkillDataKey<Integer> COOLDOWN_TIMER;
    public static SkillDataManager.SkillDataKey<Boolean> IS_PRESSING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    public static SkillDataManager.SkillDataKey<Boolean> IS_CHARGING;
    private static final UUID EVENT_UUID = UUID.fromString("d2d810cc-f30f-11ed-a05b-0242ac114581");
    private static int cooldown;

    public WanJianGuiZongSkill(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void setParams(CompoundTag parameters) {
        super.setParams(parameters);
        cooldown = parameters.getInt("cooldown");
    }

    public static int getMaxCooldown() {
        return cooldown;
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(COOLDOWN_TIMER);
        container.getDataManager().registerData(IS_PRESSING);
        container.getDataManager().registerData(IS_CHARGING);
        //蓄力禁移动
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event -> {
            if (event.getPlayerPatch().isBattleMode() && SwordSoaringKeyMappings.SWORD_SKILL.isDown()) {
                Input input = event.getMovementInput();
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
                input.down = false;
                input.up = false;
                input.left = false;
                input.right = false;
                input.jumping = false;
                input.shiftKeyDown = false;
                LocalPlayer clientPlayer = event.getPlayerPatch().getOriginal();
                clientPlayer.setSprinting(false);
                clientPlayer.sprintTriggerTime = -1;
                Minecraft mc = Minecraft.getInstance();
                ClientEngine.getInstance().controllEngine.setKeyBind(mc.options.keySprint, false);
            }
        }));
    }

    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return (executer.getSkill(this).getDataManager().getDataValue(COOLDOWN_TIMER) <= 0 || executer.getOriginal().isCreative()) && executer.getOriginal().isOnGround() && SwordSoaring.isValidSword(executer.getOriginal().getMainHandItem());
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        executer.getSkill(this).getDataManager().setDataSync(COOLDOWN_TIMER, cooldown, executer.getOriginal());
        executer.playAnimationSynchronized(SwordConvergenceAnimations.WAN1_PLAYER, 0.15F);
        ArrayList<ItemStack> list = ItemUtils.calculateValidBabylonItems(executer.getOriginal(), false, (SwordSoaring::isValidSword));
        executer.getOriginal().getCapability(SSCapabilityProvider.SS_PLAYER).ifPresent(ssPlayer -> ssPlayer.setWanSwordList(list));
        ArrayList<ItemStack> firstHalf;
        ArrayList<ItemStack> secondHalf;
        if(list.size() <= 1){
            firstHalf = secondHalf = list;
        } else {
            firstHalf = new ArrayList<>(list.subList(0, list.size() / 2));
            secondHalf = new ArrayList<>(list.subList(list.size() / 2, list.size()));
        }
        SwordConvergenceEntity leftOne = new SwordConvergenceEntity(executer.getOriginal());
        leftOne.setAnimationToPlay(SwordConvergenceAnimations.WAN1_L);
        leftOne.initBabylonItems(firstHalf, true);
        executer.getOriginal().level.addFreshEntity(leftOne);
        SwordConvergenceEntity rightOne = new SwordConvergenceEntity(executer.getOriginal());
        rightOne.setAnimationToPlay(SwordConvergenceAnimations.WAN1_R);
        rightOne.initBabylonItems(secondHalf, true);
        executer.getOriginal().level.addFreshEntity(rightOne);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void executeOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
        Minecraft.getInstance().getSoundManager().play(new WanSoundInstance(executer));
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        if(container.getExecuter().isLogicalClient()){
            boolean isKeyDown = SwordSoaringKeyMappings.SWORD_SKILL.isDown();
            if(isKeyDown != container.getDataManager().getDataValue(IS_PRESSING)){
                container.getDataManager().setDataSync(IS_PRESSING, isKeyDown, ((LocalPlayer) container.getExecuter().getOriginal()));
            }
        }
        int currentCooldown = container.getDataManager().getDataValue(COOLDOWN_TIMER);
        if (currentCooldown > 0 && !container.getExecuter().isLogicalClient()) {
            container.getDataManager().setDataSync(COOLDOWN_TIMER, currentCooldown - 1, ((ServerPlayer) container.getExecuter().getOriginal()));
        }
        if(!container.getExecuter().isLogicalClient() && cooldown - currentCooldown <= 128){
            for(int i = 0; i < Config.SWORD_EFFECT_PER_TICK.get(); i++){
                FlySwordEntity flySwordEntity = new FlySwordEntity(container.getExecuter().getOriginal(), 200, container.getExecuter().getOriginal());
                flySwordEntity.setAnimationToPlay(FlySwordAnimations.WAN_ANIMATIONS.get(currentCooldown % FlySwordAnimations.WAN_ANIMATIONS.size()));
                flySwordEntity.setRotationLock(false);
                float randomRot = (new Random().nextFloat() * 360);
                flySwordEntity.setYRot(randomRot);
                flySwordEntity.setYBodyRot(randomRot);
                flySwordEntity.setYHeadRot(randomRot);
                container.getExecuter().getOriginal().getCapability(SSCapabilityProvider.SS_PLAYER).ifPresent(ssPlayer -> flySwordEntity.setItemStack(ssPlayer.getWanSwordList().get(currentCooldown % ssPlayer.getWanSwordList().size())));
                container.getExecuter().getOriginal().level.addFreshEntity(flySwordEntity);
            }
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDraw(SkillContainer container) {
        return container.getDataManager().getDataValue(COOLDOWN_TIMER) > 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
        poseStack.pushPose();
        poseStack.translate(0.0, (float) gui.getSlidingProgression(), 0.0);
        RenderSystem.setShaderTexture(0, getSkillTexture());
        GuiComponent.blit(poseStack, (int) x, (int) y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        gui.font.drawShadow(poseStack, String.format("%.1f", (container.getDataManager().getDataValue(COOLDOWN_TIMER) / 20.0)), x + 6.0F, y + 8.0F, 16733525);
    }

    @Override
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(cooldown / 20.0);
        return list;
    }
}