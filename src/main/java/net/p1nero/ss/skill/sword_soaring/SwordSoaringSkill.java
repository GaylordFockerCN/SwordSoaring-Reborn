package net.p1nero.ss.skill.sword_soaring;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.keymapping.SwordSoaringKeyMappings;
import net.p1nero.ss.client.sound.SwordFlyingSoundInstance;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.SwordSoaringSkillCategories;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.gameassets.SwordSoaringSkills;
import net.p1nero.ss.item.SwordSoaringItems;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.event.EntityEventListener;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.entity.TakeDamageEvent;
import yesman.epicfight.api.event.types.player.SkillCastEvent;
import yesman.epicfight.api.utils.side.ClientOnly;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SwordSoaringSkill extends Skill {
    protected int cooldown;
    protected double speed;
    protected final AnimationManager.AnimationAccessor<? extends StaticAnimation> init, flying, acceleration;
    protected final Supplier<Skill> priorSkill;
    
    public static Builder createSwordSoaringSkill(Function<Builder, SwordSoaringSkill> constructor) {
        return new Builder(constructor).setCreativeTab(SwordSoaringItems.DEFAULT_TAB.get()).setCategory(SwordSoaringSkillCategories.SWORD_SOARING).setResource(Resource.NONE);
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
    public void loadDatapackParameters(CompoundTag parameters) {
        super.loadDatapackParameters(parameters);
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
        return !dataManager.getDataValue(SwordSoaringDatakeys.FLYING) && (dataManager.getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER) <= 0 || executer.getOriginal().isCreative()) && SwordSoaringMod.isValidSword(executer.getOriginal().getMainHandItem()) && executer.hasStamina(consumption + 0.1F);
    }

    @Override
    public void executeOnServer(SkillContainer container, CompoundTag args) {
        super.executeOnServer(container, args);
        ServerPlayerPatch executor = container.getServerExecutor();
        SkillDataManager dataManager = executor.getSkill(SwordSoaringSkillSlots.SWORD_SOARING).getDataManager();
        Vec3 view = executor.getOriginal().getViewVector(1.0F);
        executor.getOriginal().push(view.x, 2, view.z);
//        executor.playAnimationSynchronized(init, 0.15F);
        dataManager.setDataSync(SwordSoaringDatakeys.FLYING, true);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void executeOnClient(SkillContainer container, CompoundTag args) {
        LocalPlayerPatch executer = container.getClientExecutor();
        Minecraft.getInstance().getSoundManager().play(new SwordFlyingSoundInstance(executer));
    }

    @OnlyIn(Dist.CLIENT)
    public void onMovementInput(MovementInputUpdateEvent event, SkillContainer container) {
        if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING)) {
            Input input = event.getInput();
            input.forwardImpulse = 0.0F;
            input.leftImpulse = 0.0F;
            input.down = false;
            input.up = false;
            input.left = false;
            input.right = false;
            input.jumping = false;
            input.shiftKeyDown = false;
            LocalPlayer clientPlayer = container.getClientExecutor().getOriginal();
            clientPlayer.setSprinting(false);
            clientPlayer.sprintTriggerTime = -1;
            ControlEngine.setSprintingKeyStateNotDown();
        }
    }

    public void onHurtEventPost(TakeDamageEvent.Post event, SkillContainer container) {
        if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING)) {
            stopFlying(container, container.getServerExecutor().getOriginal());
        }
    }

    public void onFallEvent(LivingFallEvent fallEvent, SkillContainer container) {
        if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING)) {
            if (!container.getExecutor().isLogicalClient()) {
                stopFlying(container, container.getServerExecutor().getOriginal());
                container.getServerExecutor().updateMotion(false);
            }
            fallEvent.setDamageMultiplier(0);
            fallEvent.setCanceled(true);
        }
    }

    public void onSkillCast(SkillCastEvent event, SkillContainer container) {
        if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING)) {
            if(event.getSkillContainer().getSlot() == SkillSlots.WEAPON_INNATE) {
                event.cancel();
            } else if(!container.getExecutor().isLogicalClient()) {
                stopFlying(container, container.getServerExecutor().getOriginal());
            }
        }
    }

    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event, SkillContainer container){
        if(container.getSkill() instanceof SwordSoaringSkill skill && event.getSlot() == EquipmentSlot.MAINHAND){
            if(container.getDataManager().hasData(SwordSoaringDatakeys.FLYING) && container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING)){
                skill.stopFlying(container, container.getServerExecutor().getOriginal());
            }
        }
    }

    @Override
    public void onInitiate(SkillContainer container, EntityEventListener eventListener) {
        super.onInitiate(container, eventListener);

        Collection<?> capabilitySkill = container.getExecutor().getPlayerSkills().listAcquiredSkills().filter(skill ->
                skill.getCategory() == SwordSoaringSkillCategories.SWORD_SOARING).toList();
        if(capabilitySkill.contains(SwordSoaringSkills.SWORD_SOARING_MASTER) || capabilitySkill.contains(SwordSoaringSkills.SWORD_SOARING_ELYTRA_MASTER)){
            cooldown = 0;
            consumption = 0;
        }

        eventListener.registerEvent(EpicFightEventHooks.Entity.TAKE_DAMAGE_POST, event -> {
            onHurtEventPost(event, container);
        }, this);

        eventListener.registerEvent(EpicFightEventHooks.Player.CAST_SKILL, event -> {
            onSkillCast(event, container);
        }, this);
        NeoForge.EVENT_BUS.<LivingEquipmentChangeEvent>addListener(livingEquipmentChangeEvent -> {
            onLivingEquipmentChange(livingEquipmentChangeEvent, container);
        });
        NeoForge.EVENT_BUS.<LivingFallEvent>addListener(livingFallEvent -> {
            onFallEvent(livingFallEvent, container);
        });
    }

    @ClientOnly
    @Override
    @OnlyIn(Dist.CLIENT)
    public void onInitiateClient(SkillContainer container) {
        super.onInitiateClient(container);
        NeoForge.EVENT_BUS.<MovementInputUpdateEvent>addListener(inputUpdateEvent -> {
            onMovementInput(inputUpdateEvent, container);
        });
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        int currentCooldown = container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER);
        if (!container.getExecutor().isLogicalClient() && currentCooldown > 0) {
            container.getDataManager().setDataSync(SwordSoaringDatakeys.COOLDOWN_TIMER, currentCooldown - 1);
        }
        flyingTick(container);
    }

    public void flyingTick(SkillContainer container){
        if (container.getExecutor().isLogicalClient()) {
            if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING) && container.getExecutor().hasStamina(consumption + 0.1F) && SwordSoaringMod.isValidSword(container.getExecutor().getOriginal().getMainHandItem())) {
                LocalPlayer localPlayer = ((LocalPlayer) container.getExecutor().getOriginal());
                Vec3 view = localPlayer.getViewVector(1.0F).normalize();
                Vec3 accelerationSpeed = view.scale(speed);

                Vec3 normalSpeed = accelerationSpeed.scale(0.33F);
                boolean accelerating = SwordSoaringKeyMappings.ACCELERATION.isDown();
                if (accelerating != container.getDataManager().getDataValue(SwordSoaringDatakeys.SPEED_UP)) {
                    container.getDataManager().setDataSync(SwordSoaringDatakeys.SPEED_UP, accelerating);
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
            if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING)) {
                container.getExecutor().resetActionTick();
                if (container.getDataManager().getDataValue(SwordSoaringDatakeys.SPEED_UP)) {
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
        container.getDataManager().setDataSync(SwordSoaringDatakeys.FLYING, false);
        container.getDataManager().setDataSync(SwordSoaringDatakeys.SPEED_UP, false);
        container.getDataManager().setDataSync(SwordSoaringDatakeys.COOLDOWN_TIMER, cooldown);
        

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDraw(SkillContainer container) {
        return container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER) > 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
        guiGraphics.blit(getSkillTexture(), (int) x, (int) y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        guiGraphics.drawString(gui.getFont(), String.format("%.1f", (container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER) / 20.0)), x + 6.0F, y + 8.0F, 16777215, true);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(SwordSoaringKeyMappings.TAKE_OFF.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.BOLD));
        list.add(SwordSoaringKeyMappings.ACCELERATION.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.BOLD));
        return list;
    }

    public static class Builder extends SkillBuilder<Builder> {
        protected AnimationManager.AnimationAccessor<? extends StaticAnimation> init;
        protected AnimationManager.AnimationAccessor<? extends StaticAnimation> flying;
        protected AnimationManager.AnimationAccessor<? extends StaticAnimation> acceleration;
        @Nullable
        protected Supplier<Skill> priorSkill;

        public Builder(Function<Builder, ? extends SwordSoaringSkill> constructor) {
            super(constructor);
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
