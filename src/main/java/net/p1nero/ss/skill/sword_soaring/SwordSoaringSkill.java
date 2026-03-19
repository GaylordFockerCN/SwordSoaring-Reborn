package net.p1nero.ss.skill.sword_soaring;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.keymapping.SwordSoaringKeyMappings;
import net.p1nero.ss.client.sound.SwordFlyingSoundInstance;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.SwordSoaringSkillCategories;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.gameassets.skills.FlyingSkills;
import net.p1nero.ss.item.SwordSoaringItems;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class SwordSoaringSkill extends Skill {

    private static final UUID EVENT_UUID = UUID.fromString("051a9bb2-7541-11ee-b962-0242ac114514");
    protected int cooldown;
    protected double speed;
    protected final AnimationManager.AnimationAccessor<? extends StaticAnimation> init, flying, acceleration;
    protected final Supplier<Skill> priorSkill;

    public static Builder createSwordSoaringSkill() {
        return new Builder().setCreativeTab(SwordSoaringItems.DEFAULT_TAB.get()).setCategory(SwordSoaringSkillCategories.SWORD_SOARING).setResource(Resource.NONE);
    }

    @Override
    public Skill getPriorSkill() {
        return priorSkill == null ? null : priorSkill.get();
    }

    public SwordSoaringSkill(Builder builder) {
        super(builder);
        init = builder.init;
        flying = builder.flying;
        acceleration = builder.acceleration;
        priorSkill = builder.priorSkill;
    }

    @Override
    public void setParams(CompoundTag parameters) {
        super.setParams(parameters);
        cooldown = parameters.getInt("cooldown");
        speed = parameters.getDouble("speed");
    }

    @Override
    public boolean isExecutableState(PlayerPatch<?> executor) {
        return !executor.getOriginal().isSpectator() && !executor.getOriginal().onGround();
    }

    @Override
    public boolean canExecute(SkillContainer container) {
        PlayerPatch<?> executer = container.getExecutor();
        if(executer.getOriginal().getMainHandItem().is(SwordSoaringItems.VATANSEVER.get()) || executer.getOriginal().isUnderWater()){
            return false;
        }
        SkillDataManager dataManager = executer.getSkill(SwordSoaringSkillSlots.SWORD_SOARING).getDataManager();
        return !dataManager.getDataValue(SwordSoaringDatakeys.FLYING.get()) && (dataManager.getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER.get()) <= 0 || executer.getOriginal().isCreative()) && SwordSoaringMod.isValidSword(executer.getOriginal().getMainHandItem()) && executer.hasStamina(consumption + 0.1F);
    }

    @Override
    public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
        super.executeOnServer(container, args);
        ServerPlayerPatch executor = container.getServerExecutor();
        SkillDataManager dataManager = executor.getSkill(SwordSoaringSkillSlots.SWORD_SOARING).getDataManager();
        Vec3 view = executor.getOriginal().getViewVector(1.0F);
        executor.getOriginal().push(view.x, 2, view.z);
//        executor.playAnimationSynchronized(init, 0.15F);
        dataManager.setDataSync(SwordSoaringDatakeys.FLYING.get(), true);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void executeOnClient(SkillContainer container, FriendlyByteBuf args) {
        LocalPlayerPatch executer = container.getClientExecutor();
        Minecraft.getInstance().getSoundManager().play(new SwordFlyingSoundInstance(executer));
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getExecutor().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, event -> {
            if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get())) {
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
                ControlEngine.setSprintingKeyStateNotDown();
            }
        });
        container.getExecutor().getEventListener().addEventListener(PlayerEventListener.EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, hurtEvent -> {
            if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get())) {
                stopFlying(container, hurtEvent.getPlayerPatch().getOriginal());
            }
        });
        container.getExecutor().getEventListener().addEventListener(PlayerEventListener.EventType.FALL_EVENT, EVENT_UUID, fallEvent -> {
            if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get())) {
                if (!fallEvent.getPlayerPatch().isLogicalClient()) {
                    stopFlying(container, ((ServerPlayer) fallEvent.getPlayerPatch().getOriginal()));
                }
                fallEvent.getForgeEvent().setDamageMultiplier(0);
                fallEvent.getForgeEvent().setCanceled(true);
                fallEvent.getPlayerPatch().updateMotion(false);
            }
        });
        container.getExecutor().getEventListener().addEventListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, EVENT_UUID, basicAttackEvent -> {
            if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get())) {
                basicAttackEvent.setCanceled(true);
            }
        });

        container.getExecutor().getEventListener().addEventListener(PlayerEventListener.EventType.SKILL_CAST_EVENT, EVENT_UUID, skillExecuteEvent -> {
            if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get()) && !skillExecuteEvent.getPlayerPatch().isLogicalClient()) {
                stopFlying(container, ((ServerPlayer) skillExecuteEvent.getPlayerPatch().getOriginal()));
            }
        });

        //成为大师后，初级和高级飞行将不消耗耐力
        Collection<?> capabilitySkill = container.getExecutor().getSkillCapability().listAcquiredSkills().filter(skill ->
                skill.getCategory() == SwordSoaringSkillCategories.SWORD_SOARING).toList();
        if(capabilitySkill.contains(FlyingSkills.SWORD_SOARING_MASTER) || capabilitySkill.contains(FlyingSkills.SWORD_SOARING_ELYTRA_MASTER)){
            cooldown = 0;
            consumption = 0;
        }
    }

    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event){
        if(event.getEntity() instanceof ServerPlayer serverPlayer && serverPlayer.isAlive()){
            ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);
            SkillContainer container = serverPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_SOARING);
            if(container.getSkill() instanceof SwordSoaringSkill skill && event.getSlot() == EquipmentSlot.MAINHAND){
                if(container.getDataManager().hasData(SwordSoaringDatakeys.FLYING.get()) && container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get())){
                    skill.stopFlying(container, serverPlayer);
                }
            }
        }
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        PlayerEventListener listener = container.getExecutor().getEventListener();
        listener.removeListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.FALL_EVENT, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.SKILL_CAST_EVENT, EVENT_UUID);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        int currentCooldown = container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER.get());
        if (!container.getExecutor().isLogicalClient() && currentCooldown > 0) {
            container.getDataManager().setDataSync(SwordSoaringDatakeys.COOLDOWN_TIMER.get(), currentCooldown - 1);
        }
        flyingTick(container);
    }

    public void flyingTick(SkillContainer container){
        if (container.getExecutor().isLogicalClient()) {
            if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get()) && container.getExecutor().hasStamina(consumption + 0.1F) && SwordSoaringMod.isValidSword(container.getExecutor().getOriginal().getMainHandItem())) {
                LocalPlayer localPlayer = ((LocalPlayer) container.getExecutor().getOriginal());
                Vec3 view = localPlayer.getViewVector(1.0F).normalize();
                if(EpicFightCameraAPI.getInstance().isTPSMode()) {
                    view = MathUtils.getVectorForRotation(EpicFightCameraAPI.getInstance().getCameraXRot(), EpicFightCameraAPI.getInstance().getCameraYRot());
                    localPlayer.setYRot(EpicFightCameraAPI.getInstance().getCameraYRot());
                    localPlayer.yRotO = EpicFightCameraAPI.getInstance().getCameraYRotO();
                }
                Vec3 accelerationSpeed = view.scale(speed);
                Vec3 normalSpeed = accelerationSpeed.scale(0.33F);
                boolean accelerating = SwordSoaringKeyMappings.ACCELERATION.isDown();
                if (accelerating != container.getDataManager().getDataValue(SwordSoaringDatakeys.SPEED_UP.get())) {
                    container.getDataManager().setDataSync(SwordSoaringDatakeys.SPEED_UP.get(), accelerating);
                }
                //移速控制
                Vec3 currentDeltaMovement = localPlayer.getDeltaMovement();
                if (accelerating) {
                    double currentLength = currentDeltaMovement.length();
                    double speedLength = accelerationSpeed.length();
                    if (currentLength < speedLength) {
                        Vec3 interpolate = accelerationSpeed.scale(currentLength / speedLength + (speedLength - currentLength) * 0.1);
                        localPlayer.setDeltaMovement(interpolate.x, interpolate.y, interpolate.z);
                    } else {
                        localPlayer.setDeltaMovement(accelerationSpeed.x, accelerationSpeed.y, accelerationSpeed.z);
                    }
                } else {
                    double currentLength = currentDeltaMovement.length();
                    double normalLength = normalSpeed.length();
                    if (currentLength > normalLength) {
                        Vec3 interpolate = normalSpeed.scale(normalLength / currentLength - (currentLength - normalLength) * 0.1);
                        localPlayer.setDeltaMovement(interpolate.x, interpolate.y, interpolate.z);
                    } else {
                        localPlayer.setDeltaMovement(normalSpeed.x, normalSpeed.y, normalSpeed.z);
                    }
                }
            }
        } else {
            if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get())) {
                container.getExecutor().resetActionTick();
                if (container.getDataManager().getDataValue(SwordSoaringDatakeys.SPEED_UP.get())) {
                    container.getExecutor().setStamina(container.getExecutor().getStamina() - consumption);
                } else if (SwordSoaringMod.isValidSword(container.getExecutor().getOriginal().getMainHandItem())) {
                    container.getExecutor().setStamina(container.getExecutor().getStamina() - consumption * 0.33F);
                }
                if(container.getExecutor().getOriginal().isUnderWater() || !container.getExecutor().hasStamina(consumption + 0.1F) || !SwordSoaringMod.isValidSword(container.getExecutor().getOriginal().getMainHandItem())){
                    stopFlying(container, ((ServerPlayer) container.getExecutor().getOriginal()));
                }
            }
        }
    }

    public void stopFlying(SkillContainer container, ServerPlayer serverPlayer){
        container.getDataManager().setDataSync(SwordSoaringDatakeys.FLYING.get(), false);
        container.getDataManager().setDataSync(SwordSoaringDatakeys.SPEED_UP.get(), false);
        container.getDataManager().setDataSync(SwordSoaringDatakeys.COOLDOWN_TIMER.get(), cooldown);
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
        list.add(SwordSoaringKeyMappings.TAKE_OFF.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.BOLD));
        list.add(SwordSoaringKeyMappings.ACCELERATION.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.BOLD));
        return list;
    }

    public static class Builder extends SkillBuilder<SwordSoaringSkill> {
        protected AnimationManager.AnimationAccessor<? extends StaticAnimation> init;
        protected AnimationManager.AnimationAccessor<? extends StaticAnimation> flying;
        protected AnimationManager.AnimationAccessor<? extends StaticAnimation> acceleration;
        @Nullable
        protected Supplier<Skill> priorSkill;

        public Builder() {
        }

        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public Builder setActivateType(ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public Builder setResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

        public Builder setFlyingAnimations(AnimationManager.AnimationAccessor<? extends StaticAnimation> init, AnimationManager.AnimationAccessor<? extends StaticAnimation> flying, AnimationManager.AnimationAccessor<? extends StaticAnimation> acceleration) {
            this.init = init;
            this.flying = flying;
            this.acceleration = acceleration;
            return this;
        }

        public Builder setPriorSkill(@Nullable Supplier<Skill> priorSkill) {
            this.priorSkill = priorSkill;
            return this;
        }

    }

}