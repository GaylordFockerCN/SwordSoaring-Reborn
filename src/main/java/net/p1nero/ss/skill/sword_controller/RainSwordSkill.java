package net.p1nero.ss.skill.sword_controller;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.keymapping.SwordSoaringKeyMappings;
import net.p1nero.ss.entity.sword.fly_sword.FlySwordEntity;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.animations.ScreenSwordAnimations;
import net.p1nero.ss.utils.vfx.ParticleVFX;
import yesman.epicfight.api.event.EntityEventListener;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.entity.DealDamageEvent;
import yesman.epicfight.api.event.types.player.SkillCastEvent;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.List;

public class RainSwordSkill extends Skill {
    private int lifeTime, minCount, maxCount, interval, cooldown;

    public RainSwordSkill(SkillBuilder<?> builder) {
        super(builder);
    }

    @Override
    public void loadDatapackParameters(CompoundTag parameters) {
        super.loadDatapackParameters(parameters);
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

    public void onSkillCast(SkillCastEvent event, SkillContainer container) {
        if(event.getSkillContainer().getSlot() == SkillSlots.WEAPON_INNATE) {
            LivingEntity target = container.getServerExecutor().getTarget();
            int currentLifeTime = cooldown - container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER);
            if (currentLifeTime < this.lifeTime && target != null) {
                int count = container.getServerExecutor().getOriginal().getRandom().nextInt(minCount, maxCount);
                container.getDataManager().setDataSync(SwordSoaringDatakeys.DELAY_TIMER, count * interval);
            }
        }
    }

    public void onDealDamage(DealDamageEvent.Post event, SkillContainer container) {
        //造成伤害就画一次
        container.getDataManager().setDataSync(SwordSoaringDatakeys.PLAY_BIG_DIPPER, true);
    }

    @Override
    public boolean canExecute(SkillContainer container) {
        PlayerPatch<?> executor = container.getExecutor();
        return executor.getOriginal().onGround() && SwordSoaringMod.isValidSword(executor.getValidItemInHand(InteractionHand.MAIN_HAND)) && (container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER) <= 0 || executor.getOriginal().isCreative());
    }

    @Override
    public void onInitiate(SkillContainer container, EntityEventListener eventListener) {
        super.onInitiate(container, eventListener);

        eventListener.registerEvent(EpicFightEventHooks.Entity.DELIVER_DAMAGE_POST, event -> {
            onDealDamage(event, container);
        }, this);


        eventListener.registerEvent(EpicFightEventHooks.Player.CAST_SKILL, event -> {
            onSkillCast(event, container);
        }, this);
    }

    @Override
    public void executeOnServer(SkillContainer container, CompoundTag args) {
        super.executeOnServer(container, args);
        ServerPlayerPatch executor = container.getServerExecutor();
        container.getDataManager().setDataSync(SwordSoaringDatakeys.COOLDOWN_TIMER, cooldown);
        executor.playAnimationSynchronized(ScreenSwordAnimations.PLAYER_SUMMON_RAIN_SWORD, 0.15F);
        executor.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 0.0F, 0.0F);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        int currentCooldown = container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER);
        if (currentCooldown > 0) {
            container.getDataManager().setData(SwordSoaringDatakeys.COOLDOWN_TIMER, currentCooldown - 1);
        }
        int currentLifetime = this.cooldown - currentCooldown;
        if (currentLifetime < this.lifeTime && container.getExecutor().isLogicalClient()) {
            Player player = container.getExecutor().getOriginal();
            if (currentLifetime <= 10) {
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.END_ROD, player.level(), player.getEyePosition(), -1, 0.8F, player.getYRot(), 0, -0.1F, 0);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.WAX_OFF, player.level(), player.position().add(0, 0.3, 0), 0.1F, 0.8F, player.getYRot(), 0, 0.0F, 0);
            } else if (currentLifetime % 60 == 0) {
                boolean b = (currentLifetime % 120 == 0);
                container.getExecutor().playSound(b ? SoundEvents.AMETHYST_CLUSTER_STEP : SoundEvents.AMETHYST_BLOCK_STEP, 2.5F, -0.5F, 0.5F);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.END_ROD, player.level(), player.position().add(0, 0.3, 0), -1, 1.5F, currentLifetime, 0, 0.05F, 0);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.END_ROD, player.level(), player.position().add(0, 0.3, 0), -1, 1.5F, currentLifetime, 0, 0, 0);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.END_ROD, player.level(), player.position().add(0, 0.3, 0), -1, 1.5F, currentLifetime, 0, -0.05F, 0);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.WAX_ON, player.level(), player.position().add(0, 0.3, 0), b ? -1 : 0.1F, 1.5F, currentLifetime, 0, 0, 0);
                ParticleVFX.createBigDipperXZParticle(ParticleTypes.WAX_OFF, player.level(), player.position().add(0, 0.3, 0), b ? 0.1F : -1, 1.5F, currentLifetime, 0, 0, 0);
            }
        }
        int delayTimer = container.getDataManager().getDataValue(SwordSoaringDatakeys.DELAY_TIMER);
        if (delayTimer > 0) {
            if (!container.getExecutor().isLogicalClient() && delayTimer % interval == 0) {
                LivingEntity target = container.getExecutor().getTarget();
                if (target != null) {
                    FlySwordEntity flySwordEntity = new FlySwordEntity(container.getExecutor().getOriginal(), -114, target);
                    flySwordEntity.setRotationLock(false);
                    float yRot = (delayTimer * 1.0F / interval) / maxCount * 360.0F;
                    flySwordEntity.setYRot(yRot);
                    flySwordEntity.setYBodyRot(yRot);
                    flySwordEntity.setYHeadRot(yRot);
                    target.level().addFreshEntity(flySwordEntity);
                }
            }
            container.getDataManager().setData(SwordSoaringDatakeys.DELAY_TIMER, delayTimer - 1);
        }

        if(container.getDataManager().getDataValue(SwordSoaringDatakeys.PLAY_BIG_DIPPER) && container.getExecutor().isLogicalClient()){
            if(currentLifetime > this.lifeTime){
                return;
            }
            Player player = container.getExecutor().getOriginal();
            boolean b = player.getRandom().nextBoolean();
            ParticleVFX.createBigDipperXYParticle(ParticleTypes.END_ROD, player.level(), player.getEyePosition().add(0, 1, 0), -1, 0.8F, player.getYRot(), currentLifetime, 0, 0, 0);
            ParticleVFX.createBigDipperXYParticle(ParticleTypes.WAX_ON, player.level(), player.getEyePosition().add(0, 1, 0), b ? -1 : 0.1F, 0.8F, player.getYRot(), currentLifetime, 0, 0, 0);
            ParticleVFX.createBigDipperXYParticle(ParticleTypes.WAX_OFF, player.level(), player.getEyePosition().add(0, 1, 0), b ? 0.1F : -1, 0.8F, player.getYRot(), currentLifetime, 0, 0.00F, 0);
            container.getDataManager().setDataSync(SwordSoaringDatakeys.PLAY_BIG_DIPPER, false);
        }

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(minCount);
        list.add(maxCount);
        list.add(lifeTime / 20.0);
        list.add(cooldown / 20.0);
        list.add(SwordSoaringKeyMappings.SWORD_SKILL.getTranslatedKeyMessage());
        return list;
    }

    @Override
    public boolean shouldDraw(SkillContainer container) {
        return container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER) > 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
        guiGraphics.blit(getSkillTexture(), (int) x, (int) y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        int currentCooldown = container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER);
        int currentLifetime = this.cooldown - currentCooldown;
        if (currentLifetime > this.lifeTime) {
            guiGraphics.drawString(gui.getFont(), String.format("%.1f", currentCooldown / 20.0), x + 6.0F, y + 8.0F, 16777215, true);
        } else {
            guiGraphics.drawString(gui.getFont(), String.format("%.1f", (this.lifeTime - currentLifetime) / 20.0), x + 6.0F, y + 8.0F, 16777215, true);
        }
    }

}
