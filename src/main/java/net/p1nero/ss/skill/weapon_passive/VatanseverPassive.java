package net.p1nero.ss.skill.weapon_passive;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.p1nero.ss.capability.SwordSoaringAttachments;
import net.p1nero.ss.capability.SSPlayer;
import net.p1nero.ss.entity.vatansever.VatanseverEntity;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.SwordSoaringSkills;
import net.p1nero.ss.gameassets.animations.VatanseverAnimations;
import net.p1nero.ss.item.VatanseverItem;
import yesman.epicfight.api.event.EntityEventListener;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.entity.TakeDamageEvent;
import yesman.epicfight.api.event.types.player.SetTargetEvent;
import yesman.epicfight.api.event.types.player.SkillCastEvent;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.*;

public class VatanseverPassive extends ArtifactSpiritPassiveSkill{

    public VatanseverPassive(SkillBuilder<?> builder) {
        super(builder);
    }

    @Override
    public boolean canExecute(SkillContainer container) {
        return super.canExecute(container) && container.getExecutor().getOriginal().getMainHandItem().getItem() instanceof VatanseverItem;
    }

    public void onSkillCast(SkillCastEvent skillCastEvent, SkillContainer container) {
        if(!(skillCastEvent.getPlayerPatch().getOriginal().level().getEntity(getArtifactSpiritId(container)) instanceof VatanseverEntity)){
            if(!summonVatansever(container)){
                container.getDataManager().setDataSync(SwordSoaringDatakeys.SWORD_COUNT, 0);
            }
        }
    }

    public void onTargetSet(SetTargetEvent setTargetEvent, SkillContainer container) {
        if(setTargetEvent.getTarget() instanceof VatanseverEntity){
            setTargetEvent.getPlayerPatch().setAttackTarget(null);
        }
    }


    public void onHurtEventPre(TakeDamageEvent.Pre event, SkillContainer container) {
        Player player = container.getExecutor().getOriginal();
        if(player.isFallFlying()){
            double power = player.getDeltaMovement().length();
            if(power > 1){
                LevelUtil.circleSlamFracture(player, player.level(), player.position().add(0, -1, 0), power * 2);
            }
            event.attachValueModifier(ValueModifier.setter(0));
        }
    }

    public void onFallEvent(LivingFallEvent fallEvent, SkillContainer container) {
        Player player = container.getServerExecutor().getOriginal();
        double power = player.getDeltaMovement().length();
        if(power > 1){
            LevelUtil.circleSlamFracture(player, player.level(), player.position().add(0, -1, 0), power * 2);
        }
        fallEvent.setCanceled(true);
        player.stopFallFlying();
    }

    @Override
    public void onInitiate(SkillContainer container, EntityEventListener eventListener) {
        super.onInitiate(container, eventListener);
        Skill lastDodge = container.getExecutor().getSkill(SkillSlots.DODGE).getSkill();
        container.getExecutor().getOriginal().getData(SwordSoaringAttachments.SS_PLAYER).setLastDodgeSkill(lastDodge == SwordSoaringSkills.VATANSEVER_DODGE.get() ? null : lastDodge);
        container.getExecutor().getSkill(SkillSlots.DODGE).setSkill(SwordSoaringSkills.VATANSEVER_DODGE.get());
        container.getDataManager().setData(SwordSoaringDatakeys.SWORD_COUNT, 6);
        summonVatansever(container);
        eventListener.registerEvent(EpicFightEventHooks.Player.CAST_SKILL, event -> {
            onSkillCast(event, container);
        }, this);
        eventListener.registerEvent(EpicFightEventHooks.Player.SET_TARGET, event -> {
            onTargetSet(event, container);
        }, this);
        eventListener.registerEvent(EpicFightEventHooks.Entity.TAKE_DAMAGE_PRE, event -> {
            onHurtEventPre(event, container);
        }, this);
        NeoForge.EVENT_BUS.<LivingFallEvent>addListener(livingFallEvent -> {
            onFallEvent(livingFallEvent, container);
        });
    }

    public boolean summonVatansever(SkillContainer container){
        if(!container.getExecutor().isLogicalClient() && container.getDataManager().getDataValue(SwordSoaringDatakeys.ARTIFACT_SPIRIT_ENTITY_ID) == 0){
            VatanseverEntity vatanseverEntity = new VatanseverEntity(container.getExecutor().getOriginal().level(), container.getExecutor().getOriginal());
            boolean success = container.getExecutor().getOriginal().level().addFreshEntity(vatanseverEntity);
            container.getExecutor().playAnimationSynchronized(VatanseverAnimations.PLAYER_INIT, 0.15F);
            container.getDataManager().setDataSync(SwordSoaringDatakeys.ARTIFACT_SPIRIT_ENTITY_ID, vatanseverEntity.getId());
            container.getDataManager().setDataSync(SwordSoaringDatakeys.SWORD_COUNT, 6);
            return success;
        }
        return false;
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);

        SSPlayer ssPlayer = container.getExecutor().getOriginal().getData(SwordSoaringAttachments.SS_PLAYER);
        container.getExecutor().getSkill(SkillSlots.DODGE).setSkill(ssPlayer.getLastDodgeSkill());

        int id = getArtifactSpiritId(container);
        if(id != 0 && container.getExecutor().getOriginal().level().getEntity(id) instanceof VatanseverEntity abstractArtifactSpiritEntity){
            if(abstractArtifactSpiritEntity.isAlive()){
                abstractArtifactSpiritEntity.discard();
            }
        }
        ssPlayer.clearVatanseverShootEntities();
        container.getDataManager().setData(SwordSoaringDatakeys.ARTIFACT_SPIRIT_ENTITY_ID, 0);

    }

}
