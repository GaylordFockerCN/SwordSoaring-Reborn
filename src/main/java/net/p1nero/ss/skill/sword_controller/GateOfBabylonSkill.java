package net.p1nero.ss.skill.sword_controller;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
import net.p1nero.ss.client.SwordSoairngCameraManager;
import net.p1nero.ss.client.keymapping.SwordSoaringKeyMappings;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonEntity;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.animations.BabylonAnimations;
import net.p1nero.ss.item.VatanseverItem;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.ArrayList;
import java.util.List;

public class GateOfBabylonSkill extends Skill {
    private int cooldown, interval;
    private int count = -1;
    private int perShootCount;
    private Vec3 startPos;
    private float startYRot;
    public GateOfBabylonSkill(SkillBuilder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void setParams(CompoundTag parameters) {
        super.setParams(parameters);
        cooldown = parameters.getInt("cooldown");
        interval = parameters.getInt("interval");
        cooldown += (cooldown % interval);
    }

    @Override
    public boolean canExecute(SkillContainer container) {
        PlayerPatch<?> executer = container.getExecutor();
        if(executer.getOriginal().getMainHandItem().getItem() instanceof VatanseverItem){
            return false;
        }
        return (container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER.get()) <= 0 || executer.getOriginal().isCreative()) && !executer.getOriginal().getMainHandItem().isEmpty() && executer.getOriginal().onGround();
    }

    @Override
    public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
        super.executeOnServer(container, args);
        perShootCount = SwordSoaringArmatures.BABYLON_ARMATURE.get().joints.size();//这个时候joints才初始化了= =byd新版本
        ServerPlayerPatch executor = container.getServerExecutor();
        container.getDataManager().setDataSync(SwordSoaringDatakeys.COOLDOWN_TIMER.get(), cooldown);
        executor.playAnimationSynchronized(BabylonAnimations.BABYLON_SUMMON_PLAYER, 0.15F);
        executor.getOriginal().getCapability(SSCapabilityProvider.SS_PLAYER).ifPresent(ssPlayer -> {
            int size = ssPlayer.initBabylonItems(executor.getOriginal());
            count = size / (perShootCount + 1) + 1;
            container.getDataManager().setDataSync(SwordSoaringDatakeys.CAMERA_TIMER.get(), count * interval);
        });
        startPos = executor.getOriginal().position();
        startYRot = executor.getOriginal().getYRot();
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        int currentCooldown = container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER.get());
        if (currentCooldown > 0) {
            container.getDataManager().setData(SwordSoaringDatakeys.COOLDOWN_TIMER.get(), currentCooldown - 1);
        }
        int currentCameraTimer = container.getDataManager().getDataValue(SwordSoaringDatakeys.CAMERA_TIMER.get());
        if (currentCameraTimer > 0) {
            container.getDataManager().setData(SwordSoaringDatakeys.CAMERA_TIMER.get(), currentCameraTimer - 1);
        }
        if(!container.getExecutor().isLogicalClient()){
            ServerPlayer serverPlayer = ((ServerPlayer) container.getExecutor().getOriginal());
            SSPlayer ssPlayer = serverPlayer.getCapability(SSCapabilityProvider.SS_PLAYER).orElse(new SSPlayer());
            if((currentCooldown == this.cooldown - 1 || currentCooldown % interval == 0) && count > 0){
                BabylonEntity babylonEntity = new BabylonEntity(container.getExecutor().getOriginal(), startPos, startYRot);
                ArrayList<ItemStack> itemStacks = new ArrayList<>();
                for(int i = perShootCount * (count-1); i < count * perShootCount && i < ssPlayer.getValidBabylonItems().size(); i++){
                    itemStacks.add(ssPlayer.getValidBabylonItems().get(i));
                }
                babylonEntity.initBabylonItems(itemStacks, true);
                babylonEntity.setAnimationToPlay(currentCooldown == this.cooldown - 1 ? BabylonAnimations.BABYLON_SHOOT_START : BabylonAnimations.BABYLON_SHOOT_LOOP);
                serverPlayer.level().addFreshEntity(babylonEntity);
                count--;
                container.getExecutor().playSound(SoundEvents.PORTAL_AMBIENT, 3.0F, 0.0F, 0.0F);
            }
        } else {
            if(currentCameraTimer > 0){
                if(container.getExecutor().getOriginal().equals(Minecraft.getInstance().player)){
                    SwordSoairngCameraManager.zoomIn(new Vec3f(0, -3 ,-6), 100);
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDraw(SkillContainer container) {
        return container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER.get()) > 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0.0F, (float)gui.getSlidingProgression(), 0.0F);
        guiGraphics.blit(getSkillTexture(), (int) x, (int) y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        guiGraphics.drawString(gui.getFont(), String.format("%.1f", (container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER.get()) / 20.0)), x + 6.0F, y + 8.0F, 16777215, true);
        poseStack.popPose();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(cooldown / 20.0);
        list.add(SwordSoaringKeyMappings.SWORD_SKILL.getTranslatedKeyMessage());
        return list;
    }

}
