package net.p1nero.ss.skill.weapon_passive;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.p1nero.ss.capability.SSCapabilityProvider;
import net.p1nero.ss.capability.SSPlayer;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.entity.vatansever.VatanseverEntity;
import net.p1nero.ss.entity.vatansever.VatanseverEntityPatch;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.gameassets.animations.VatanseverAnimations;
import net.p1nero.ss.skill.sword_soaring.SwordSoaringSkill;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;

public class VatanseverPassive extends ArtifactSpiritPassiveSkill{
    private static final UUID EVENT_UUID = UUID.fromString("d1d114cc-f30f-11ed-a05b-0242ac114514");
    public static SkillDataManager.SkillDataKey<Integer> SWORD_COUNT;

    public VatanseverPassive(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(SWORD_COUNT);
        container.getDataManager().setData(SWORD_COUNT, 6);
        summonVatansever(container);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID, skillExecuteEvent -> {
            if(!skillExecuteEvent.getPlayerPatch().isLogicalClient() && !(skillExecuteEvent.getPlayerPatch().getOriginal().level.getEntity(getArtifactSpiritId(container)) instanceof VatanseverEntity)){
                if(!summonVatansever(container)){
                    container.getDataManager().setDataSync(SWORD_COUNT, 0, ((ServerPlayer) container.getExecuter().getOriginal()));
                }
            }
        });
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SET_TARGET_EVENT, EVENT_UUID, setTargetEvent -> {
            if(setTargetEvent.getTarget() instanceof VatanseverEntity){
                setTargetEvent.getPlayerPatch().setAttackTarget(null);
            }
        });
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.TARGET_INDICATOR_ALERT_CHECK_EVENT, EVENT_UUID, indicatorCheckEvent -> {
            if(indicatorCheckEvent.getTarget() instanceof VatanseverEntityPatch){
                indicatorCheckEvent.setCanceled(true);
            }
        });
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, hurtEvent -> {
            Player player = hurtEvent.getPlayerPatch().getOriginal();
            if(player.isFallFlying()){
                double power = player.getDeltaMovement().length();
                if(power > 1){
                    LevelUtil.circleSlamFracture(player, player.level, player.position().add(0, -1, 0), power * 2);
                }
                hurtEvent.setResult(AttackResult.ResultType.MISSED);
                hurtEvent.setAmount(0);
                hurtEvent.setCanceled(true);
            }
        });
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.FALL_EVENT, EVENT_UUID, fallEvent -> {
            Player player = fallEvent.getPlayerPatch().getOriginal();
            double power = player.getDeltaMovement().length();
            if(power > 1){
                LevelUtil.circleSlamFracture(player, player.level, player.position().add(0, -1, 0), power * 2);
            }
            fallEvent.getForgeEvent().setCanceled(true);
            player.stopFallFlying();
        });
    }

    public boolean summonVatansever(SkillContainer container){
        if(!container.getExecuter().isLogicalClient() && container.getDataManager().getDataValue(ARTIFACT_SPIRIT_ENTITY_ID) == 0){
            VatanseverEntity vatanseverEntity = new VatanseverEntity(container.getExecuter().getOriginal().level, container.getExecuter().getOriginal());
            boolean success = container.getExecuter().getOriginal().level.addFreshEntity(vatanseverEntity);
            container.getExecuter().playAnimationSynchronized(VatanseverAnimations.PLAYER_INIT, 0.15F);
            container.getDataManager().setDataSync(ARTIFACT_SPIRIT_ENTITY_ID, vatanseverEntity.getId(), ((ServerPlayer) container.getExecuter().getOriginal()));
            container.getDataManager().setDataSync(SWORD_COUNT, 6, ((ServerPlayer) container.getExecuter().getOriginal()));
            return success;
        }
        return false;
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        int id = getArtifactSpiritId(container);
        if(id != 0 && container.getExecuter().getOriginal().level.getEntity(id) instanceof VatanseverEntity abstractArtifactSpiritEntity){
            if(abstractArtifactSpiritEntity.isAlive()){
                abstractArtifactSpiritEntity.discard();
            }
        }
        container.getExecuter().getOriginal().getCapability(SSCapabilityProvider.SS_PLAYER).ifPresent(SSPlayer::clearVatanseverShootEntities);
        container.getDataManager().setData(ARTIFACT_SPIRIT_ENTITY_ID, 0);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.TARGET_INDICATOR_ALERT_CHECK_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.SET_TARGET_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.FALL_EVENT, EVENT_UUID);
    }

    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event){
        if(event.getEntity() instanceof ServerPlayer serverPlayer && serverPlayer.isAlive()){
            ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);
            SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
            if(manager.hasData(ARTIFACT_SPIRIT_ENTITY_ID)){
                VatanseverEntityPatch vatanseverEntityPatch = EpicFightCapabilities.getEntityPatch(serverPlayer.level.getEntity(manager.getDataValue(ARTIFACT_SPIRIT_ENTITY_ID)), VatanseverEntityPatch.class);
                if(vatanseverEntityPatch != null && vatanseverEntityPatch.getEntityState().inaction()){
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }
}