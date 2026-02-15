package net.p1nero.ss.skill.sword_controller;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.p1nero.ss.entity.sword.screen_sword.ScreenSwordEntity;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.utils.ItemUtils;
import yesman.epicfight.api.event.EntityEventListener;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.entity.TakeDamageEvent;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

import java.util.List;

public class ScreenSwordSkill extends KillAuraSkill {

    private int maxProtectCount;
    private float healCount;

    public ScreenSwordSkill(Builder builder) {
        super(builder);
    }

    @Override
    public void loadDatapackParameters(CompoundTag parameters) {
        super.loadDatapackParameters(parameters);
        maxProtectCount = parameters.getInt("protect_count");
        healCount = parameters.getFloat("heal_count");
    }

    public int getMaxProtectCount() {
        return maxProtectCount;
    }


    public void onHurtEventPost(TakeDamageEvent.Income hurtEvent, SkillContainer container) {
        if(container.getExecutor().getOriginal().level().getEntity(container.getDataManager().getDataValue(SwordSoaringDatakeys.SWORD_ENTITY_ID)) instanceof ScreenSwordEntity screenSwordEntity){
            int protectCountLeft = container.getDataManager().getDataValue(SwordSoaringDatakeys.PROTECT_COUNT);
            if(protectCountLeft <= 0) {
                return;
            }
            container.getDataManager().setDataSync(SwordSoaringDatakeys.PROTECT_COUNT, protectCountLeft - 1);
            if((protectCountLeft - 1) % (maxProtectCount / 6) == 0){
                container.getExecutor().playSound(EpicFightSounds.NEUTRALIZE_MOBS.get(), 0.0F, 0.0F);
                container.getExecutor().getOriginal().heal(healCount);
            } else {
                container.getExecutor().playSound(EpicFightSounds.CLASH.get(), 0.0F, 0.0F);
                if(!container.getExecutor().isLogicalClient()) {
                    EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(container.getServerExecutor().getOriginal().serverLevel(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, container.getExecutor().getOriginal(), hurtEvent.getDamageSource().getDirectEntity());
                }
            }
            //免疫硬直
            if(hurtEvent.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource){
                epicFightDamageSource.setBaseImpact(0);
                epicFightDamageSource.setStunType(StunType.NONE);
            }
            //免疫远程
            if(!hurtEvent.getDamageSource().isDirect()){
                hurtEvent.setResult(AttackResult.ResultType.MISSED);
                hurtEvent.setParried(true);
            } else {
                //反伤（减伤有bug，setAmount无效，额外写太麻烦了）
                Entity entity = hurtEvent.getDamageSource().getEntity();
                if(entity != null){
                    //难道没有直接获取某个武器的伤害的办法吗。。
                    double total = ItemUtils.getItemAttackDamage(container.getExecutor().getOriginal(), screenSwordEntity.getItemStack(null));
                    //反击伤害不超过武器最大伤害
                    float counterattackDamage = hurtEvent.getDamage() * 0.5F > total ? (float) total : hurtEvent.getDamage() * 0.5F;
                    hurtEvent.getDamageSource().getEntity().hurt(hurtEvent.getDamageSource(), counterattackDamage);
                }
            }
        } else {
            container.getDataManager().setDataSync(SwordSoaringDatakeys.PROTECT_COUNT, 0);
            if(container.getExecutor().getOriginal().isCurrentlyGlowing()){
                container.getExecutor().getOriginal().setGlowingTag(false);
            }
        }
    }

    @Override
    public void onInitiate(SkillContainer container, EntityEventListener eventListener) {
        super.onInitiate(container, eventListener);
        eventListener.registerEvent(EpicFightEventHooks.Entity.TAKE_DAMAGE_INCOME, event -> {
            onHurtEventPost(event, container);
        }, this);
    }

    @Override
    public void executeOnServer(SkillContainer container, CompoundTag args) {
        super.executeOnServer(container, args);
        ServerPlayerPatch executer = container.getServerExecutor();
        container.getDataManager().setDataSync(SwordSoaringDatakeys.PROTECT_COUNT, maxProtectCount);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(this.maxProtectCount);
        list.add(this.healCount);
        return super.getTooltipArgsOfScreen(list);
    }

    @Override
    public boolean shouldDraw(SkillContainer container) {
        return container.getDataManager().getDataValue(SwordSoaringDatakeys.PROTECT_COUNT) > 0 || super.shouldDraw(container);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
        guiGraphics.blit(getSkillTexture(), (int) x, (int) y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);

        int protectCount = container.getDataManager().getDataValue(SwordSoaringDatakeys.PROTECT_COUNT);
        int currentCooldown = container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER);
        int currentLifetime = this.cooldown - currentCooldown;
        if(protectCount > 0 && currentLifetime < this.lifeTime) {
            guiGraphics.drawString(gui.getFont(), container.getDataManager().getDataValue(SwordSoaringDatakeys.PROTECT_COUNT).toString(), x + 6.0F, y + 8.0F, 16777215, true);
        } else {
            guiGraphics.drawString(gui.getFont(), String.format("%.1f", (container.getDataManager().getDataValue(SwordSoaringDatakeys.COOLDOWN_TIMER) / 20.0)), x + 6.0F, y + 8.0F, 16777215, true);
        }
    }

}
