package net.p1nero.ss.skill.sword_soaring;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.invincible.api.animation.StaticAnimationProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.client.keymapping.SwordSoaringKeyMappings;
import net.p1nero.ss.client.sound.SwordFlyingSoundInstance;
import net.p1nero.ss.gameassets.SwordSoaringSkillCategories;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.gameassets.skills.FlyingSkills;
import net.p1nero.ss.item.SwordSoaringItems;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public class SwordSoaringSkill extends Skill {

    private static final UUID EVENT_UUID = UUID.fromString("051a9bb2-7541-11ee-b962-0242ac114514");
    protected int cooldown;
    protected double speed;
    protected final StaticAnimationProvider init, flying, acceleration;
    protected final Supplier<Skill> priorSkill;
    public static final SkillDataManager.SkillDataKey<Integer> COOL_DOWN_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    public static final SkillDataManager.SkillDataKey<Boolean> FLYING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    public static final SkillDataManager.SkillDataKey<Boolean> ACCELERATING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);

    public static Builder createSwordSoaringSkill() {
        return new Builder().setCreativeTab(SwordSoaringItems.SWORD_SOARING_ITEM_TAB).setCategory(SwordSoaringSkillCategories.SWORD_SOARING).setResource(Resource.NONE);
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
    public boolean canExecute(PlayerPatch<?> executer) {
        if(executer.getOriginal().getMainHandItem().is(SwordSoaringItems.VATANSEVER.get()) || executer.getOriginal().isUnderWater()){
            return false;
        }
        SkillDataManager dataManager = executer.getSkill(SwordSoaringSkillSlots.SWORD_SOARING).getDataManager();
        return !dataManager.getDataValue(FLYING) && (dataManager.getDataValue(COOL_DOWN_TIMER) <= 0 || executer.getOriginal().isCreative()) && SwordSoaring.isValidSword(executer.getOriginal().getMainHandItem()) && executer.hasStamina(consumption + 0.1F);
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        SkillDataManager dataManager = executer.getSkill(SwordSoaringSkillSlots.SWORD_SOARING).getDataManager();
        Vec3 view = executer.getOriginal().getViewVector(1.0F);
        executer.getOriginal().push(view.x, 2, view.z);
        executer.playAnimationSynchronized(init.get(), 0.15F);
        dataManager.setDataSync(FLYING, true, executer.getOriginal());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void executeOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
        Minecraft.getInstance().getSoundManager().play(new SwordFlyingSoundInstance(executer));
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(COOL_DOWN_TIMER);
        container.getDataManager().registerData(FLYING);
        container.getDataManager().registerData(ACCELERATING);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, event -> {
            if (container.getDataManager().getDataValue(FLYING)) {
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
        });
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_POST, EVENT_UUID, hurtEvent -> {
            if (container.getDataManager().getDataValue(FLYING)) {
                stopFlying(container, hurtEvent.getPlayerPatch().getOriginal());
            }
        });
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.FALL_EVENT, EVENT_UUID, fallEvent -> {
            if (container.getDataManager().getDataValue(FLYING)) {
                if (!fallEvent.getPlayerPatch().isLogicalClient()) {
                    stopFlying(container, ((ServerPlayer) fallEvent.getPlayerPatch().getOriginal()));
                }
                fallEvent.getForgeEvent().setDamageMultiplier(0);
                fallEvent.getForgeEvent().setCanceled(true);
                fallEvent.getPlayerPatch().updateMotion(false);
            }
        });
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, EVENT_UUID, basicAttackEvent -> {
            if (container.getDataManager().getDataValue(FLYING)) {
                basicAttackEvent.setCanceled(true);
            }
        });

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID, skillExecuteEvent -> {
            if (container.getDataManager().getDataValue(FLYING) && !skillExecuteEvent.getPlayerPatch().isLogicalClient()) {
                stopFlying(container, ((ServerPlayer) skillExecuteEvent.getPlayerPatch().getOriginal()));
            }
        });

        //成为大师后，初级和高级飞行将不消耗耐力
        Collection<?> capabilitySkill = container.getExecuter().getSkillCapability().getLearnedSkills(SwordSoaringSkillCategories.SWORD_SOARING);
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
                if(container.getDataManager().hasData(FLYING) && container.getDataManager().getDataValue(FLYING)){
                    skill.stopFlying(container, serverPlayer);
                }
            }
        }
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        PlayerEventListener listener = container.getExecuter().getEventListener();
        listener.removeListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.HURT_EVENT_POST, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.FALL_EVENT, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        int currentCooldown = container.getDataManager().getDataValue(COOL_DOWN_TIMER);
        if (!container.getExecuter().isLogicalClient() && currentCooldown > 0) {
            container.getDataManager().setDataSync(COOL_DOWN_TIMER, currentCooldown - 1, ((ServerPlayer) container.getExecuter().getOriginal()));
        }
        flyingTick(container);
    }

    public void flyingTick(SkillContainer container){
        if (container.getExecuter().isLogicalClient()) {
            if (container.getDataManager().getDataValue(FLYING) && container.getExecuter().hasStamina(consumption + 0.1F) && SwordSoaring.isValidSword(container.getExecuter().getOriginal().getMainHandItem())) {
                LocalPlayer localPlayer = ((LocalPlayer) container.getExecuter().getOriginal());
                Vec3 accelerationSpeed = localPlayer.getViewVector(1.0F).normalize().scale(speed);
                Vec3 normalSpeed = accelerationSpeed.scale(0.33F);
                boolean accelerating = SwordSoaringKeyMappings.ACCELERATION.isDown();
                if (accelerating != container.getDataManager().getDataValue(ACCELERATING)) {
                    if (accelerating) {
                        container.getExecuter().playAnimationSynchronized(acceleration.get(), 0.0F);
                    } else {
                        container.getExecuter().playAnimationSynchronized(flying.get(), 0.0F);
                    }
                    container.getDataManager().setDataSync(ACCELERATING, accelerating, localPlayer);
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
            if (container.getDataManager().getDataValue(FLYING)) {
                container.getExecuter().resetActionTick();
                if (container.getDataManager().getDataValue(ACCELERATING)) {
                    container.getExecuter().consumeStamina(consumption);
                } else if (SwordSoaring.isValidSword(container.getExecuter().getOriginal().getMainHandItem())) {
                    container.getExecuter().consumeStamina(consumption * 0.33F);
                }
                if(container.getExecuter().getOriginal().isUnderWater() || !container.getExecuter().hasStamina(consumption + 0.1F) || !SwordSoaring.isValidSword(container.getExecuter().getOriginal().getMainHandItem())){
                    stopFlying(container, ((ServerPlayer) container.getExecuter().getOriginal()));
                }
            }
        }
    }
    
    public void stopFlying(SkillContainer container, ServerPlayer serverPlayer){
        container.getDataManager().setDataSync(FLYING, false, serverPlayer);
        container.getDataManager().setDataSync(ACCELERATING, false, serverPlayer);
        container.getDataManager().setDataSync(COOL_DOWN_TIMER, cooldown, serverPlayer);

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDraw(SkillContainer container) {
        return container.getDataManager().getDataValue(COOL_DOWN_TIMER) > 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
        poseStack.pushPose();
        poseStack.translate(0.0, (float) gui.getSlidingProgression(), 0.0);
        RenderSystem.setShaderTexture(0, getSkillTexture());
        GuiComponent.blit(poseStack, (int) x, (int) y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        gui.font.drawShadow(poseStack, String.format("%.1f", (container.getDataManager().getDataValue(COOL_DOWN_TIMER) / 20.0)), x + 6.0F, y + 8.0F, 16777215);
    }

    public static class Builder extends Skill.Builder<SwordSoaringSkill> {
        protected StaticAnimationProvider init;
        protected StaticAnimationProvider flying;
        protected StaticAnimationProvider acceleration;
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

        public Builder setFlyingAnimations(StaticAnimationProvider init, StaticAnimationProvider flying, StaticAnimationProvider acceleration) {
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