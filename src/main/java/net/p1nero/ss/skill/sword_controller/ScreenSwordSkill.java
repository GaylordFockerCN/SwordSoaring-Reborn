package net.p1nero.ss.skill.sword_controller;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.entity.sword.screen_sword.ScreenSwordEntity;
import net.p1nero.ss.util.ItemUtils;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ScreenSwordSkill extends KillAuraSkill {

    private static final UUID EVENT_UUID = UUID.fromString("051a9bb2-7541-11ee-b962-0242ac191981");
    public static SkillDataManager.SkillDataKey<Integer> PROTECT_COUNT;
    private int maxProtectCount;
    private float healCount;

    public ScreenSwordSkill(Builder builder) {
        super(builder);
    }

    @Override
    public void setParams(CompoundTag parameters) {
        super.setParams(parameters);
        maxProtectCount = parameters.getInt("protect_count");
        healCount = parameters.getFloat("heal_count");
    }

    public int getMaxProtectCount() {
        return maxProtectCount;
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(PROTECT_COUNT);
        container.getExecuter().getOriginal().setGlowingTag(false);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, hurtEvent -> {
            if(hurtEvent.getPlayerPatch().getOriginal().level.getEntity(container.getDataManager().getDataValue(SWORD_ENTITY_ID)) instanceof ScreenSwordEntity screenSwordEntity){
                int protectCountLeft = container.getDataManager().getDataValue(PROTECT_COUNT);
                if(protectCountLeft <= 0) {
                    return;
                }
                container.getDataManager().setDataSync(PROTECT_COUNT, protectCountLeft - 1, hurtEvent.getPlayerPatch().getOriginal());
                if((protectCountLeft - 1) % (maxProtectCount / 6) == 0){
                    hurtEvent.getPlayerPatch().playSound(EpicFightSounds.NEUTRALIZE_MOBS.get(), 0.0F, 0.0F);
                    hurtEvent.getPlayerPatch().getOriginal().heal(healCount);
                } else {
                    hurtEvent.getPlayerPatch().playSound(EpicFightSounds.CLASH.get(), 0.0F, 0.0F);
                    EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(hurtEvent.getPlayerPatch().getOriginal().getLevel(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, hurtEvent.getPlayerPatch().getOriginal(), hurtEvent.getDamageSource().getDirectEntity());
                }
                //免疫硬直
                if(hurtEvent.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource){
                    epicFightDamageSource.setImpact(0);
                    epicFightDamageSource.setStunType(StunType.NONE);
                }
                //免疫投掷物
                if(hurtEvent.getDamageSource().isProjectile()){
                    hurtEvent.setAmount(0);
                    hurtEvent.setResult(AttackResult.ResultType.MISSED);
                    hurtEvent.setParried(true);
                    hurtEvent.setCanceled(true);
                } else {
                    //反伤（减伤有bug，setAmount无效，额外写太麻烦了）
                    Entity entity = hurtEvent.getDamageSource().getEntity();
                    if(entity != null){
                        //难道没有直接获取某个武器的伤害的办法吗。。
                        double total = ItemUtils.getItemAttackDamage(hurtEvent.getPlayerPatch().getOriginal(), screenSwordEntity.getItemStack(null));
                        //反击伤害不超过武器最大伤害
                        float counterattackDamage = hurtEvent.getAmount() * 0.5F > total ? (float) total : hurtEvent.getAmount() * 0.5F;
                        hurtEvent.getDamageSource().getEntity().hurt(hurtEvent.getDamageSource(), counterattackDamage);
                    }
                }
            } else {
                container.getDataManager().setDataSync(PROTECT_COUNT, 0, hurtEvent.getPlayerPatch().getOriginal());
                if(container.getExecuter().getOriginal().isCurrentlyGlowing()){
                    container.getExecuter().getOriginal().setGlowingTag(false);
                }
            }
        });
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getOriginal().setGlowingTag(false);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        executer.getSkill(this).getDataManager().setDataSync(PROTECT_COUNT, maxProtectCount, executer.getOriginal());
    }

    @Override
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(this.maxProtectCount);
        list.add(this.healCount);
        return super.getTooltipArgsOfScreen(list);
    }

    @Override
    public boolean shouldDraw(SkillContainer container) {
        return container.getDataManager().getDataValue(PROTECT_COUNT) > 0 || super.shouldDraw(container);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
        poseStack.pushPose();
        poseStack.translate(0.0, (float) gui.getSlidingProgression(), 0.0);
        RenderSystem.setShaderTexture(0, getSkillTexture());
        GuiComponent.blit(poseStack, (int) x, (int) y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        int protectCount = container.getDataManager().getDataValue(PROTECT_COUNT);
        int currentCooldown = container.getDataManager().getDataValue(COOL_DOWN_TIMER);
        int currentLifetime = this.cooldown - currentCooldown;
        if(protectCount > 0 && currentLifetime < this.lifeTime) {
            gui.font.drawShadow(poseStack, container.getDataManager().getDataValue(PROTECT_COUNT).toString(), x + 6.0F, y + 8.0F, 16777215);
        } else {
            gui.font.drawShadow(poseStack, String.format("%.1f", (container.getDataManager().getDataValue(COOL_DOWN_TIMER) / 20.0)), x + 6.0F, y + 8.0F, 16733525);
        }
    }
}