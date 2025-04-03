package net.p1nero.ss.skill.sword_controller;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.entity.sword.fly_sword.FlySwordEntity;
import net.p1nero.ss.gameassets.animations.ScreenSwordAnimations;
import net.p1nero.ss.util.vfx.ParticleVFX;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

public class RainSwordSkill extends Skill {
    private static final UUID EVENT_UUID = UUID.fromString("051a9bb2-1145-14ee-b962-0242ac191981");
    public static SkillDataManager.SkillDataKey<Integer> DELAY_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    public static SkillDataManager.SkillDataKey<Integer> COOLDOWN_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    public static SkillDataManager.SkillDataKey<Boolean> PLAY_BIG_DIPPER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    private int lifeTime, minCount, maxCount, interval, cooldown;

    public RainSwordSkill(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void setParams(CompoundTag parameters) {
        super.setParams(parameters);
        interval = parameters.getInt("interval");
        lifeTime = parameters.getInt("life_time");
        minCount = parameters.getInt("min_count");
        maxCount = parameters.getInt("max_count");
        cooldown = parameters.getInt("cooldown");
        cooldown += lifeTime;
        if (minCount > maxCount) {
            throw new IllegalArgumentException("max count can not be less than min count!");
        }
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(DELAY_TIMER);
        container.getDataManager().registerData(COOLDOWN_TIMER);
        container.getDataManager().registerData(PLAY_BIG_DIPPER);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, EVENT_UUID, basicAttackEvent -> {
            LivingEntity target = basicAttackEvent.getPlayerPatch().getTarget();
            int currentLifeTime = cooldown - container.getDataManager().getDataValue(COOLDOWN_TIMER);
            if (currentLifeTime < this.lifeTime && target != null) {
                int count = basicAttackEvent.getPlayerPatch().getOriginal().getRandom().nextInt(minCount, maxCount);
                container.getDataManager().setDataSync(DELAY_TIMER, count * interval, basicAttackEvent.getPlayerPatch().getOriginal());
            }
        });
        //造成伤害就画一次
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, skillExecuteEvent -> {
            container.getDataManager().setDataSync(PLAY_BIG_DIPPER, true, skillExecuteEvent.getPlayerPatch().getOriginal());
        });
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
    }

    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return executer.getOriginal().isOnGround() && SwordSoaring.isValidSword(executer.getValidItemInHand(InteractionHand.MAIN_HAND)) && (executer.getSkill(this).getDataManager().getDataValue(COOLDOWN_TIMER) <= 0 || executer.getOriginal().isCreative());
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        executer.getSkill(this).getDataManager().setDataSync(COOLDOWN_TIMER, cooldown, executer.getOriginal());
        executer.playAnimationSynchronized(ScreenSwordAnimations.PLAYER_SUMMON_RAIN_SWORD, 0.15F);
        executer.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 0.0F, 0.0F);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        int currentCooldown = container.getDataManager().getDataValue(COOLDOWN_TIMER);
        if (currentCooldown > 0) {
            container.getDataManager().setData(COOLDOWN_TIMER, currentCooldown - 1);
        }
        int currentLifetime = this.cooldown - currentCooldown;
        if (currentLifetime < this.lifeTime && container.getExecuter().isLogicalClient()) {
            Player player = container.getExecuter().getOriginal();
            if (currentLifetime <= 10) {
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.END_ROD, player.level, player.getEyePosition(), -1, 0.8F, player.getYRot(), 0, -0.1F, 0);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.WAX_OFF, player.level, player.position().add(0, 0.3, 0), 0.1F, 0.8F, player.getYRot(), 0, 0.0F, 0);
            } else if (currentLifetime % 60 == 0) {
                boolean b = (currentLifetime % 120 == 0);
                container.getExecuter().playSound(b ? SoundEvents.AMETHYST_CLUSTER_STEP : SoundEvents.AMETHYST_BLOCK_STEP, 2.5F, -0.5F, 0.5F);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.END_ROD, player.level, player.position().add(0, 0.3, 0), -1, 1.5F, currentLifetime, 0, 0.05F, 0);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.END_ROD, player.level, player.position().add(0, 0.3, 0), -1, 1.5F, currentLifetime, 0, 0, 0);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.END_ROD, player.level, player.position().add(0, 0.3, 0), -1, 1.5F, currentLifetime, 0, -0.05F, 0);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.WAX_ON, player.level, player.position().add(0, 0.3, 0), b ? -1 : 0.1F, 1.5F, currentLifetime, 0, 0, 0);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.WAX_OFF, player.level, player.position().add(0, 0.3, 0), b ? 0.1F : -1, 1.5F, currentLifetime, 0, 0, 0);
            }
        }
        int delayTimer = container.getDataManager().getDataValue(DELAY_TIMER);
        if (delayTimer > 0) {
            if (!container.getExecuter().isLogicalClient() && delayTimer % interval == 0) {
                LivingEntity target = container.getExecuter().getTarget();
                if (target != null) {
                    FlySwordEntity flySwordEntity = new FlySwordEntity(container.getExecuter().getOriginal(), -114, target);
                    flySwordEntity.setRotationLock(false);
                    float yRot = (delayTimer * 1.0F / interval) / maxCount * 360.0F;
                    flySwordEntity.setYRot(yRot);
                    flySwordEntity.setYBodyRot(yRot);
                    flySwordEntity.setYHeadRot(yRot);
                    target.level.addFreshEntity(flySwordEntity);
                }
            }
            container.getDataManager().setData(DELAY_TIMER, delayTimer - 1);
        }

        if(container.getDataManager().getDataValue(PLAY_BIG_DIPPER) && container.getExecuter().isLogicalClient()){
            if(currentLifetime > this.lifeTime){
                return;
            }
            Player player = container.getExecuter().getOriginal();
            boolean b = player.getRandom().nextBoolean();
            ParticleVFX.createBigDipperXYParticle(ParticleTypes.END_ROD, player.level, player.getEyePosition().add(0, 1, 0), -1, 0.8F, player.getYRot(), currentLifetime, 0, 0, 0);
            ParticleVFX.createBigDipperXYParticle(ParticleTypes.WAX_ON, player.level, player.getEyePosition().add(0, 1, 0), b ? -1 : 0.1F, 0.8F, player.getYRot(), currentLifetime, 0, 0, 0);
            ParticleVFX.createBigDipperXYParticle(ParticleTypes.WAX_OFF, player.level, player.getEyePosition().add(0, 1, 0), b ? 0.1F : -1, 0.8F, player.getYRot(), currentLifetime, 0, 0.00F, 0);
            container.getDataManager().setDataSync(PLAY_BIG_DIPPER, false, ((LocalPlayer) player));
        }

    }

    @Override
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(minCount);
        list.add(maxCount);
        list.add(lifeTime / 20.0);
        list.add(cooldown / 20.0);
        return list;
    }

    @Override
    public boolean shouldDraw(SkillContainer container) {
        return container.getDataManager().getDataValue(COOLDOWN_TIMER) > 0;
    }

    @Override
    public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
        poseStack.pushPose();
        poseStack.translate(0.0, (float) gui.getSlidingProgression(), 0.0);
        RenderSystem.setShaderTexture(0, getSkillTexture());
        GuiComponent.blit(poseStack, (int) x, (int) y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        int currentCooldown = container.getDataManager().getDataValue(COOLDOWN_TIMER);
        int currentLifetime = this.cooldown - currentCooldown;
        if (currentLifetime > this.lifeTime) {
            gui.font.drawShadow(poseStack, String.format("%.1f", currentCooldown / 20.0), x + 6.0F, y + 8.0F, 16733525);
        } else {
            gui.font.drawShadow(poseStack, String.format("%.1f", (this.lifeTime - currentLifetime) / 20.0), x + 6.0F, y + 8.0F, 16777215);
        }
    }
}