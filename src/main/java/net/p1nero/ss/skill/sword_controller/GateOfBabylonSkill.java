package net.p1nero.ss.skill.sword_controller;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.capability.SSCapabilityProvider;
import net.p1nero.ss.capability.SSPlayer;
import net.p1nero.ss.client.CameraAnim;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonEntity;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import net.p1nero.ss.gameassets.animations.BabylonAnimations;
import net.p1nero.ss.item.SwordSoaringItems;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.ArrayList;
import java.util.List;

public class GateOfBabylonSkill extends Skill {
    public static SkillDataManager.SkillDataKey<Integer> COOLDOWN_TIMER;
    public static SkillDataManager.SkillDataKey<Integer> CAMERA_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    private int cooldown, interval;
    private int count = -1;
    private final int perShootCount;
    private Vec3 startPos;
    private float startYRot;
    public GateOfBabylonSkill(Builder<? extends Skill> builder) {
        super(builder);
        perShootCount = SwordSoaringArmatures.babylonArmature.joints.size();
    }

    @Override
    public void setParams(CompoundTag parameters) {
        super.setParams(parameters);
        cooldown = parameters.getInt("cooldown");
        interval = parameters.getInt("interval");
        cooldown += (cooldown % interval);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(COOLDOWN_TIMER);
        container.getDataManager().registerData(CAMERA_TIMER);
    }

    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return (executer.getSkill(this).getDataManager().getDataValue(COOLDOWN_TIMER) <= 0 || executer.getOriginal().isCreative()) && !executer.getOriginal().getMainHandItem().isEmpty() && executer.getOriginal().isOnGround() && !executer.getOriginal().getMainHandItem().is(SwordSoaringItems.VATANSEVER.get());
    }
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        executer.getSkill(this).getDataManager().setDataSync(COOLDOWN_TIMER, cooldown, executer.getOriginal());
        executer.playAnimationSynchronized(BabylonAnimations.BABYLON_SUMMON_PLAYER, 0.15F);
        executer.getOriginal().getCapability(SSCapabilityProvider.SS_PLAYER).ifPresent(ssPlayer -> {
            int size = ssPlayer.initBabylonItems(executer.getOriginal());
            count = size / (perShootCount + 1) + 1;
            executer.getSkill(this).getDataManager().setDataSync(CAMERA_TIMER, count * interval, executer.getOriginal());
        });
        startPos = executer.getOriginal().position();
        startYRot = executer.getOriginal().getYRot();
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        int currentCooldown = container.getDataManager().getDataValue(COOLDOWN_TIMER);
        if (currentCooldown > 0) {
            container.getDataManager().setData(COOLDOWN_TIMER, currentCooldown - 1);
        }
        int currentCameraTimer = container.getDataManager().getDataValue(CAMERA_TIMER);
        if (currentCameraTimer > 0) {
            container.getDataManager().setData(CAMERA_TIMER, currentCameraTimer - 1);
        }
        if(!container.getExecuter().isLogicalClient()){
            ServerPlayer serverPlayer = ((ServerPlayer) container.getExecuter().getOriginal());
            SSPlayer ssPlayer = serverPlayer.getCapability(SSCapabilityProvider.SS_PLAYER).orElse(new SSPlayer());
            if((currentCooldown == this.cooldown - 1 || currentCooldown % interval == 0) && count > 0){
                BabylonEntity babylonEntity = new BabylonEntity(container.getExecuter().getOriginal(), startPos, startYRot);
                ArrayList<ItemStack> itemStacks = new ArrayList<>();
                for(int i = perShootCount * (count-1); i < count * perShootCount && i < ssPlayer.getValidBabylonItems().size(); i++){
                    itemStacks.add(ssPlayer.getValidBabylonItems().get(i));
                }
                babylonEntity.initBabylonItems(itemStacks, true);
                babylonEntity.setAnimationToPlay(currentCooldown == this.cooldown - 1 ? BabylonAnimations.BABYLON_SHOOT_START : BabylonAnimations.BABYLON_SHOOT_LOOP);
                serverPlayer.level.addFreshEntity(babylonEntity);
                count--;
                container.getExecuter().playSound(SoundEvents.PORTAL_AMBIENT, 3.0F, 0.0F, 0.0F);
            }
        } else {
            if(currentCameraTimer > 0){
                if(container.getExecuter().getOriginal().equals(Minecraft.getInstance().player)){
                    CameraAnim.zoomIn(new Vec3f(0, -3 ,-6), 200);
                }
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