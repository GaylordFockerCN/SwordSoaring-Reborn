package net.p1nero.ss.gameassets.animations;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.p1nero.ss.animation.*;
import net.p1nero.ss.capability.SSCapabilityProvider;
import net.p1nero.ss.client.sound.SwordSoaringSounds;
import net.p1nero.ss.entity.sword.fly_sword.FlySwordEntity;
import net.p1nero.ss.entity.vatansever.VatanseverArmature;
import net.p1nero.ss.entity.vatansever.VatanseverEntity;
import net.p1nero.ss.entity.vatansever.VatanseverEntityPatch;
import net.p1nero.ss.entity.vatansever_storm.VatanseverStormEntity;
import net.p1nero.ss.entity.vatansever_storm.VatanseverStormEntityPatch;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import net.p1nero.ss.gameassets.SwordSoaringColliders;
import net.p1nero.ss.skill.weapon_passive.ArtifactSpiritPassiveSkill;
import net.p1nero.ss.skill.weapon_passive.VatanseverPassive;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.p1nero.ss.util.AnimationUtils.*;

public class VatanseverAnimations {
    public static StaticAnimation PLAYER_AUTO1;
    public static StaticAnimation PLAYER_AUTO2;
    public static StaticAnimation PLAYER_AUTO3;
    public static StaticAnimation PLAYER_AUTO3_B;
    public static StaticAnimation PLAYER_AUTO4;
    public static StaticAnimation PLAYER_AUTO4_B;
    public static StaticAnimation PLAYER_INIT;
    public static StaticAnimation PLAYER_FLY_BEGIN;
    public static StaticAnimation PLAYER_STORM_START;
    public static StaticAnimation PLAYER_SHOOT_L1;
    public static StaticAnimation PLAYER_SHOOT_L2;
    public static StaticAnimation PLAYER_SHOOT_L3;
    public static StaticAnimation PLAYER_SHOOT_R1;
    public static StaticAnimation PLAYER_SHOOT_R2;
    public static StaticAnimation PLAYER_SHOOT_R3;

    public static StaticAnimation VATANSEVER_IDLE;
    public static StaticAnimation VATANSEVER_WALK;
    public static StaticAnimation VATANSEVER_WALK_F;
    public static StaticAnimation VATANSEVER_WALK_F_STOP;
    public static StaticAnimation VATANSEVER_WALK_B;
    public static StaticAnimation VATANSEVER_WALK_B_STOP;
    public static StaticAnimation VATANSEVER_RUN;
    public static StaticAnimation VATANSEVER_RUN_STOP;
    public static StaticAnimation VATANSEVER_DEATH;
    public static StaticAnimation VATANSEVER_FALL;
    public static StaticAnimation VATANSEVER_FLOAT;
    public static StaticAnimation VATANSEVER_FLY;
    public static StaticAnimation VATANSEVER_FLY_STOP;
    public static StaticAnimation VATANSEVER_SNEAK;
    public static StaticAnimation VATANSEVER_SNEAK_STOP;
    public static StaticAnimation VATANSEVER_SWIM;
    public static StaticAnimation VATANSEVER_AUTO1;
    public static StaticAnimation VATANSEVER_AUTO2;
    public static StaticAnimation VATANSEVER_AUTO3;
    public static StaticAnimation VATANSEVER_AUTO3_B;
    public static StaticAnimation VATANSEVER_AUTO4;
    public static StaticAnimation VATANSEVER_AUTO4_B;
    public static StaticAnimation VATANSEVER_INIT;
    public static StaticAnimation VATANSEVER_FLY_BEGIN;
    public static StaticAnimation VATANSEVER_STORM_START;
    public static StaticAnimation VATANSEVER_SHOOT_L1;
    public static StaticAnimation VATANSEVER_SHOOT_L2;
    public static StaticAnimation VATANSEVER_SHOOT_L3;
    public static StaticAnimation VATANSEVER_SHOOT_R1;
    public static StaticAnimation VATANSEVER_SHOOT_R2;
    public static StaticAnimation VATANSEVER_SHOOT_R3;

    public static void buildVatanseverAnim() {
        VatanseverArmature vatanseverArmature = SwordSoaringArmatures.vatanseverArmature;
        List<Pair<Joint, Collider>> left = List.of(Pair.of(vatanseverArmature.L1, SwordSoaringColliders.VATANSEVER),
                Pair.of(vatanseverArmature.L2, SwordSoaringColliders.VATANSEVER),
                Pair.of(vatanseverArmature.L3, SwordSoaringColliders.VATANSEVER));
        List<Pair<Joint, Collider>> right = List.of(Pair.of(vatanseverArmature.R1, SwordSoaringColliders.VATANSEVER),
                Pair.of(vatanseverArmature.R2, SwordSoaringColliders.VATANSEVER),
                Pair.of(vatanseverArmature.R3, SwordSoaringColliders.VATANSEVER));
        ArrayList<Pair<Joint, Collider>> all = new ArrayList<>();
        all.addAll(left);
        all.addAll(right);

        VATANSEVER_IDLE = new StaticAnimation(true, "biped/vatansever/living/vatansever_idle", vatanseverArmature);
        VATANSEVER_WALK_F = new StaticAnimation(true, "biped/vatansever/living/vatansever_walk", vatanseverArmature);
        VATANSEVER_WALK_F_STOP = new StaticAnimation(true, "biped/vatansever/living/vatansever_walk_stop", vatanseverArmature);
        VATANSEVER_WALK_B = new StaticAnimation(true, "biped/vatansever/living/vatansever_walk_b", vatanseverArmature);
        VATANSEVER_WALK_B_STOP = new StaticAnimation(true, "biped/vatansever/living/vatansever_walk_b_stop", vatanseverArmature);
        VATANSEVER_WALK = new SelectiveAnimation((entityPatch) -> {
            if (entityPatch instanceof VatanseverEntityPatch vatanseverEntityPatch && vatanseverEntityPatch.getOwnerPatch() != null) {
                Vec3 view = vatanseverEntityPatch.getOwnerPatch().getOriginal().getViewVector(1.0F);
                Vec3 move = vatanseverEntityPatch.getOwnerPatch().getOriginal().getDeltaMovement();
                double dot = view.dot(move);
                return dot < 0.0 ? 1 : 0;
            }
            return 0;
        }, VATANSEVER_WALK_F, VATANSEVER_WALK_B);
        VATANSEVER_RUN = new StaticAnimation(true, "biped/vatansever/living/vatansever_run", vatanseverArmature);
        VATANSEVER_RUN_STOP = new StaticAnimation(true, "biped/vatansever/living/vatansever_run_stop", vatanseverArmature);
        VATANSEVER_SWIM = new StaticAnimation(true, "biped/vatansever/living/vatansever_swim", vatanseverArmature);
        VATANSEVER_FALL = new StaticAnimation(true, "biped/vatansever/living/vatansever_fall", vatanseverArmature);
        VATANSEVER_DEATH = new StaticAnimation(true, "biped/vatansever/living/vatansever_death", vatanseverArmature);
        VATANSEVER_FLOAT = new StaticAnimation(true, "biped/vatansever/living/vatansever_float", vatanseverArmature);
        VATANSEVER_FLY = new StaticAnimation(true, "biped/vatansever/living/vatansever_fly", vatanseverArmature)
                .addEvents(AnimationEvent.TimePeriodEvent.create(0, 3, (entityPatch, self, params) -> flyVFX(entityPatch), AnimationEvent.Side.CLIENT));
        VATANSEVER_FLY_STOP = new StaticAnimation(true, "biped/vatansever/living/vatansever_fly_stop", vatanseverArmature);
        VATANSEVER_SNEAK = new StaticAnimation(true, "biped/vatansever/living/vatansever_sneak", vatanseverArmature);
        VATANSEVER_SNEAK_STOP = new StaticAnimation(true, "biped/vatansever/living/vatansever_sneak_stop", vatanseverArmature);
        VATANSEVER_AUTO1 = new VatanseverAttackAnimation(0.01F, "biped/vatansever/vatansever_auto1", vatanseverArmature,
                new ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase(0.0F, 0.7F, 1.1F, 1.1F, Float.MAX_VALUE, vatanseverArmature.R1, SwordSoaringColliders.VATANSEVER)
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                new ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase(0.0F, 0.7F, 1.1F, 1.1F, Float.MAX_VALUE, vatanseverArmature.R2, SwordSoaringColliders.VATANSEVER)
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                new ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase(0.0F, 0.7F, 1.1F, 1.1F, Float.MAX_VALUE, vatanseverArmature.R3, SwordSoaringColliders.VATANSEVER)
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)));
        VATANSEVER_AUTO2 = new VatanseverAttackAnimation(0.01F, "biped/vatansever/vatansever_auto2", vatanseverArmature,
                new ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase(0.0F, 0.7F, 1.0F, 1.60F, Float.MAX_VALUE, vatanseverArmature.L1, SwordSoaringColliders.VATANSEVER)
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                new ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase(0.0F, 0.7F, 1.0F, 1.60F, Float.MAX_VALUE, vatanseverArmature.L2, SwordSoaringColliders.VATANSEVER)
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                new ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase(0.0F, 0.7F, 1.0F, 1.60F, Float.MAX_VALUE, vatanseverArmature.L3, SwordSoaringColliders.VATANSEVER)
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)));
        VATANSEVER_AUTO3 = new AttackAnimation(0.01F, "biped/vatansever/vatansever_auto3", vatanseverArmature,
                new AttackAnimation.Phase(0.0F, 0.01F, 0.01F, 0.01F, 0.01F, Float.MAX_VALUE, false, InteractionHand.MAIN_HAND, right)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.NO_SOUND)
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)));

        VATANSEVER_AUTO3_B = new AttackAnimation(0.01F, "biped/vatansever/vatansever_auto3_b", vatanseverArmature,
                new AttackAnimation.Phase(0.0F, 1.75F, 1.75F, 4.0F, 4.0F, Float.MAX_VALUE, false, InteractionHand.MAIN_HAND, right)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, SwordSoaringSounds.VATANSEVER_WHOOSH_BIG.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)));
        VATANSEVER_AUTO4 = new AttackAnimation(0.01F, "biped/vatansever/vatansever_auto4", vatanseverArmature,
                new AttackAnimation.Phase(0.0F, 1.33F, 1.33F, 1.43F, 4.0F, Float.MAX_VALUE, false, InteractionHand.MAIN_HAND, all)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, SwordSoaringSounds.VATANSEVER_WHOOSH_BIG.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)));
        VATANSEVER_AUTO4_B = new AttackAnimation(0.01F, "biped/vatansever/vatansever_auto4_b", vatanseverArmature,
                new AttackAnimation.Phase(0.0F, 1.33F, 1.33F, 1.43F, 4.0F, Float.MAX_VALUE, false, InteractionHand.MAIN_HAND, all)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.NO_SOUND)
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)));
        VATANSEVER_STORM_START = new ActionAnimation(0.15F, "biped/vatansever/skill/vatansever_storm_start", vatanseverArmature);
        VATANSEVER_INIT = new ActionAnimation(0.15F, "biped/vatansever/vatansever_init", vatanseverArmature)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.3F, (livingEntityPatch, staticAnimation, objects) ->
                        livingEntityPatch.playSound(EpicFightSounds.ENTITY_MOVE, 0.0F, 0.0F), AnimationEvent.Side.CLIENT));
        VATANSEVER_FLY_BEGIN = new ActionAnimation(0.15F, "biped/vatansever/vatansever_fly_begin", vatanseverArmature)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addEvents(AnimationEvent.TimePeriodEvent.create(0, 1.2F, (entityPatch, self, params) -> {
                    flyVFX(entityPatch);
                    entityPatch.playSound(SoundEvents.FIRE_AMBIENT, 0.0F, 0.0F);
                }, AnimationEvent.Side.CLIENT))
                .addEvents(AnimationEvent.TimeStampedEvent.create(2.2F, (entityPatch, self, params) -> entityPatch.playSound(EpicFightSounds.ENTITY_MOVE, 0.0F, 0.0F), AnimationEvent.Side.CLIENT));

        VATANSEVER_SHOOT_L3 = new ActionAnimation(0.15F, "biped/vatansever/skill/vatansever_shoot_l3", vatanseverArmature)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, summonFlySwordInTarget())
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, staticAnimation, objects) -> shootVFX(livingEntityPatch, vatanseverArmature.L3)), AnimationEvent.Side.BOTH));
        VATANSEVER_SHOOT_R3 = new ActionAnimation(0.15F, "biped/vatansever/skill/vatansever_shoot_r3", vatanseverArmature)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, summonFlySwordInTarget())
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, staticAnimation, objects) -> shootVFX(livingEntityPatch, vatanseverArmature.R2)), AnimationEvent.Side.BOTH));
        VATANSEVER_SHOOT_L2 = new ActionAnimation(0.15F, "biped/vatansever/skill/vatansever_shoot_l2", vatanseverArmature)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, summonFlySwordInTarget())
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, staticAnimation, objects) -> shootVFX(livingEntityPatch, vatanseverArmature.L2)), AnimationEvent.Side.BOTH));
        VATANSEVER_SHOOT_R2 = new ActionAnimation(0.15F, "biped/vatansever/skill/vatansever_shoot_r2", vatanseverArmature)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, summonFlySwordInTarget())
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, staticAnimation, objects) -> shootVFX(livingEntityPatch, vatanseverArmature.R2)), AnimationEvent.Side.BOTH));
        VATANSEVER_SHOOT_L1 = new ActionAnimation(0.15F, "biped/vatansever/skill/vatansever_shoot_l1", vatanseverArmature)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, summonFlySwordInTarget())
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, staticAnimation, objects) -> shootVFX(livingEntityPatch, vatanseverArmature.L1)), AnimationEvent.Side.BOTH));
        VATANSEVER_SHOOT_R1 = new ActionAnimation(0.15F, "biped/vatansever/skill/vatansever_shoot_r1", vatanseverArmature)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, summonFlySwordInTarget())
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, staticAnimation, objects) -> shootVFX(livingEntityPatch, vatanseverArmature.R1)), AnimationEvent.Side.BOTH));

        HumanoidArmature biped = Armatures.BIPED;
        PLAYER_AUTO1 = new LinkArtifactSpiritAnimation(0.15F, 1.1F, "biped/vatansever/vatansever_auto1_owner", biped, VATANSEVER_AUTO1)
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(0.7F, ((livingEntityPatch, staticAnimation, objects) -> {
                            swingSounds(livingEntityPatch);
                        }), AnimationEvent.Side.BOTH));
        PLAYER_AUTO2 = new LinkArtifactSpiritAnimation(0.15F, 1.9F, "biped/vatansever/vatansever_auto2_owner", biped, VATANSEVER_AUTO2)
                .newTimePair(1.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(0.7F, ((livingEntityPatch, staticAnimation, objects) -> {
                            swingSounds(livingEntityPatch);
                        }), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(1.56F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if (SwordCountis(livingEntityPatch, 6)) {
                                groundSplit(livingEntityPatch, 3, 0, 0, 0, getTotalAttackDamage(livingEntityPatch) * 6, 1.1F, 200);
                            }
                        }), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(1.65F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if (SwordCountis(livingEntityPatch, 4)) {
                                groundSplit(livingEntityPatch, 3.8, 0, 0, 0, getTotalAttackDamage(livingEntityPatch) * 6, 1.1F, 200);
                            }
                        }), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(1.74F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if (SwordCountis(livingEntityPatch, 2)) {
                                groundSplit(livingEntityPatch, 5, 0, 0, 0, getTotalAttackDamage(livingEntityPatch) * 6, 1.1F, 200);
                            }
                        }), AnimationEvent.Side.BOTH))
                .addEvents(AnimationEvent.TimePeriodEvent.create(1.0F, 1.9F, push(2.8F), AnimationEvent.Side.BOTH));
        PLAYER_AUTO3 = new LinkArtifactSpiritAnimation(0.15F, 2.25F, "biped/vatansever/vatansever_auto3_owner", biped, VATANSEVER_AUTO3)
                .newTimePair(0.0F, 3.0F)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(1F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if (SwordCountis(livingEntityPatch, 5)) {
                                groundSplit(livingEntityPatch, 3, 0, 0, 0, getTotalAttackDamage(livingEntityPatch) * 6, 1.1F, 200);
                            }
                        }), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(1.09F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if (SwordCountis(livingEntityPatch, 3)) {
                                groundSplit(livingEntityPatch, 3.8, 0, 0, 0, getTotalAttackDamage(livingEntityPatch) * 6, 1.1F, 200);
                            }
                        }), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(1.18F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if (SwordCountis(livingEntityPatch, 1)) {
                                groundSplit(livingEntityPatch, 5, 0, 0, 0, getTotalAttackDamage(livingEntityPatch) * 6, 1.1F, 200);
                            }
                        }), AnimationEvent.Side.BOTH))
                .addEvents(AnimationEvent.TimePeriodEvent.create(0.0F, 2.25F, push(2.8F), AnimationEvent.Side.BOTH));
        PLAYER_AUTO3_B = new LinkArtifactSpiritAnimation(0.15F, 4.0F, "biped/vatansever/vatansever_auto3_b_owner", biped, VATANSEVER_AUTO3_B)
                .newTimePair(0.0F, 3.0F)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                .addEvents(AnimationEvent.TimeStampedEvent.create(2.0F, ((livingEntityPatch, staticAnimation, objects) -> {
                    int n = 16;
                    for (int i = 0; i < n; ++i) {
                        groundSplit(livingEntityPatch, 5 + i * 3, 0, 0, 0, getTotalAttackDamage(livingEntityPatch) * 10, 4, 100);
                    }
                }), AnimationEvent.Side.BOTH));
        ;
        PLAYER_AUTO4 = new LinkArtifactSpiritAnimation(0.15F, 4F, "biped/vatansever/vatansever_auto4_owner", biped, VATANSEVER_AUTO4)
                .newTimePair(1.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                .addEvents(AnimationEvent.TimeStampedEvent.create(1.38F, ((livingEntityPatch, staticAnimation, objects) -> {
                    groundSplit(livingEntityPatch, 4.2, 0, 0, 0, getTotalAttackDamage(livingEntityPatch) * 20, 6, 1000, StunType.KNOCKDOWN);
                }), AnimationEvent.Side.BOTH));
        PLAYER_AUTO4_B = new LinkArtifactSpiritAnimation(0.15F, 4F, "biped/vatansever/vatansever_auto4_b_owner", biped, VATANSEVER_AUTO4_B)
                .newTimePair(1.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                .addEvents(AnimationEvent.TimePeriodEvent.create(0.9F, 2.5F, (entityPatch, self, params) ->
                        {
                            swingSounds(entityPatch);
                            attractEntities(entityPatch, 15, getTotalAttackDamage(entityPatch), 3);
                        },
                        AnimationEvent.Side.BOTH))
                .addEvents(AnimationEvent.TimeStampedEvent.create(2.5F, ((livingEntityPatch, staticAnimation, objects) -> {
                    groundSplit(livingEntityPatch, 0, 0, 0, 0, getTotalAttackDamage(livingEntityPatch) * 3, 4, 1000);
                }), AnimationEvent.Side.BOTH));
        PLAYER_INIT = new LinkArtifactSpiritAnimation(0.15F, 0, "biped/vatansever/vatansever_init", biped, VATANSEVER_INIT);
        PLAYER_FLY_BEGIN = new LinkArtifactSpiritAnimation(0.15F, "biped/vatansever/vatansever_fly_begin", biped, VATANSEVER_FLY_BEGIN)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch.getOriginal() instanceof ServerPlayer serverPlayer) {
                        serverPlayer.startFallFlying();
                    }
                }, AnimationEvent.Side.SERVER));
        PLAYER_STORM_START = new LinkArtifactSpiritAnimation(0.15F, "biped/vatansever/skill/vatansever_storm_start", biped, VATANSEVER_STORM_START)
                .addEvents(AnimationEvent.TimeStampedEvent.create(1.0F, ((livingEntityPatch, staticAnimation, objects) -> {
                            groundSplit(livingEntityPatch, 0, 0, 0, 0, 0, 3, 2000);
                            createStorm(livingEntityPatch, 0, 0, 0, VatanseverStormAnimations.VATANSEVER_STORM_RISE_1);
                            createStorm(livingEntityPatch, 0, 0, 0, VatanseverStormAnimations.VATANSEVER_STORM_RISE_2);
                            createStorm(livingEntityPatch, 0, 0, 0, VatanseverStormAnimations.VATANSEVER_STORM_RISE_3);
                            createStorm(livingEntityPatch, 0, 0, 0, VatanseverStormAnimations.VATANSEVER_STORM_RISE_4);
                            Entity entity = livingEntityPatch.getOriginal();
                            if (entity.level instanceof ServerLevel serverLevel) {
                                serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SwordSoaringSounds.VATANSEVER_STORM.get(), SoundSource.HOSTILE, 1.0F, 1.0F
                                );
                            }
                        }), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(2.2F, ((livingEntityPatch, staticAnimation, objects) -> {
                            createStorm(livingEntityPatch, 0, 0, 0, VatanseverStormAnimations.VATANSEVER_STORM_MIDDLE_2);
                            createStorm(livingEntityPatch, 0, 0, 0, VatanseverStormAnimations.VATANSEVER_STORM_MIDDLE);
                            createStorm(livingEntityPatch, 0, 0, 0, VatanseverStormAnimations.VATANSEVER_STORM_UP);
                            createStorm(livingEntityPatch, 0, 0, 0, VatanseverStormAnimations.VATANSEVER_STORM_DOWN);
                        }), AnimationEvent.Side.BOTH));

        PLAYER_SHOOT_L3 = new VatanseverPlayerShootAnimation(0.15F, 0.0F, Float.MAX_VALUE, "biped/vatansever/skill/vatansever_shoot_l3", vatanseverArmature,
                SwordSoaringColliders.SCAN_SCALE, vatanseverArmature.rootJoint).setArtifactSpiritAnimation(VATANSEVER_SHOOT_L3)
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false);
        PLAYER_SHOOT_R3 = new VatanseverPlayerShootAnimation(0.15F, 0.0F, Float.MAX_VALUE, "biped/vatansever/skill/vatansever_shoot_r3", vatanseverArmature,
                SwordSoaringColliders.SCAN_SCALE, vatanseverArmature.rootJoint).setArtifactSpiritAnimation(VATANSEVER_SHOOT_R3)
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false);
        PLAYER_SHOOT_L2 = new VatanseverPlayerShootAnimation(0.15F, 0.0F, Float.MAX_VALUE, "biped/vatansever/skill/vatansever_shoot_l2", vatanseverArmature,
                SwordSoaringColliders.SCAN_SCALE, vatanseverArmature.rootJoint).setArtifactSpiritAnimation(VATANSEVER_SHOOT_L2)
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false);
        PLAYER_SHOOT_R2 = new VatanseverPlayerShootAnimation(0.15F, 0.0F, Float.MAX_VALUE, "biped/vatansever/skill/vatansever_shoot_r2", vatanseverArmature,
                SwordSoaringColliders.SCAN_SCALE, vatanseverArmature.rootJoint).setArtifactSpiritAnimation(VATANSEVER_SHOOT_R2)
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false);
        PLAYER_SHOOT_L1 = new VatanseverPlayerShootAnimation(0.15F, 0.0F, Float.MAX_VALUE, "biped/vatansever/skill/vatansever_shoot_l1", vatanseverArmature,
                SwordSoaringColliders.SCAN_SCALE, vatanseverArmature.rootJoint).setArtifactSpiritAnimation(VATANSEVER_SHOOT_L1)
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false);
        PLAYER_SHOOT_R1 = new VatanseverPlayerShootAnimation(0.15F, 0.0F, Float.MAX_VALUE, "biped/vatansever/skill/vatansever_shoot_r1", vatanseverArmature,
                SwordSoaringColliders.SCAN_SCALE, vatanseverArmature.rootJoint).setArtifactSpiritAnimation(VATANSEVER_SHOOT_R1)
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false);
    }

    public static AnimationEvent.AnimationEventConsumer push(float radius){
        return ((livingEntityPatch, staticAnimation, objects) -> {
            LivingEntity original = livingEntityPatch.getOriginal();
            Level level = original.level;
            Vec3 view = original.getViewVector(1.0F);
            Vec3 dir = new Vec3(view.x, 0, view.z).normalize();
            Vec3 targetPos = original.position().add(dir.scale(radius));
            AABB aabb = original.getBoundingBox().inflate(radius);
            level.getEntities(original, aabb, entity -> !livingEntityPatch.isTeammate(entity) && !(entity instanceof OwnableEntity ownableEntity && original.equals(ownableEntity.getOwner()))).forEach(e -> e.moveTo(targetPos));
        });
    }

    public static AnimationEvent summonFlySwordInTarget() {
        return AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof VatanseverEntityPatch vatanseverEntityPatch && vatanseverEntityPatch.getOwnerPatch() instanceof ServerPlayerPatch serverPlayerPatch) {
                if (vatanseverEntityPatch.getTarget() != null) {
                    vatanseverEntityPatch.getOwnerPatch().getOriginal().getCapability(SSCapabilityProvider.SS_PLAYER).ifPresent(ssPlayer -> {
                        //确保没有多余的剑
                        if (ssPlayer.getVatanseverShootEntities().size() == 6 - vatanseverEntityPatch.getLeftSwordCount()) {
                            FlySwordEntity flySwordEntity = new FlySwordEntity(vatanseverEntityPatch.getOwnerPatch().getOriginal(), 500, vatanseverEntityPatch.getTarget());
                            flySwordEntity.setAnimationToPlay(vatanseverEntityPatch.getOriginal().getRandom().nextBoolean() ? FlySwordAnimations.FLY_SWORD_ATK_1 : FlySwordAnimations.FLY_SWORD_ATK_2);
                            if (vatanseverEntityPatch.getOriginal().level.addFreshEntity(flySwordEntity)) {
                                ssPlayer.addVatanseverShootEntity(flySwordEntity);
                                SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
                                if (manager.hasData(VatanseverPassive.SWORD_COUNT)) {
                                    manager.setDataSync(VatanseverPassive.SWORD_COUNT, vatanseverEntityPatch.getLeftSwordCount() - 1, serverPlayerPatch.getOriginal());
                                }
                            }
                        } else {
                            //否则重置状态
                            Iterator<?> iterator = ssPlayer.getVatanseverShootEntities().iterator();
                            while (iterator.hasNext()) {
                                iterator.remove();
                            }
                            SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
                            if (manager.hasData(VatanseverPassive.SWORD_COUNT)) {
                                manager.setDataSync(VatanseverPassive.SWORD_COUNT, 6, serverPlayerPatch.getOriginal());
                            }
                        }
                    });
                }
            }
        }, AnimationEvent.Side.SERVER);
    }

    private static void swingSounds(LivingEntityPatch<?> livingEntityPatch) {
        Entity entity = livingEntityPatch.getOriginal();
        if (entity.level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SwordSoaringSounds.VATANSEVER_WHOOSH.get(), SoundSource.HOSTILE, 1.0F, 1.0F
            );
        }
    }

    private static void shootVFX(LivingEntityPatch<?> livingEntityPatch, Joint toolJoint) {
        LivingEntity entity = livingEntityPatch.getOriginal();
        OpenMatrix4f transformMatrix = livingEntityPatch.getArmature().getBindedTransformFor(livingEntityPatch.getArmature().getCurrentPose(), toolJoint);
        transformMatrix.translate(new Vec3f(0.0F, 0.0F, 0.0F));
        OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entity.yBodyRotO + 180.0F), new Vec3f(0.0F, 1.0F, 0.0F));
        OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);
        Vec3 pos = new Vec3(transformMatrix.m30 + (float) entity.getX(), transformMatrix.m31 + (float) entity.getY(), transformMatrix.m32 + (float) entity.getZ());
        if (entity.level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SwordSoaringSounds.VATANSEVER_WHOOSH_BIG.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        } else {
            createRandomSmokeLine(entity.level, pos, 50);
        }
    }

    public static void createStorm(LivingEntityPatch<?> entityPatch, double xOffset, double yOffset, double zOffset, StaticAnimation staticAnimation) {
        if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
            Vec3 pos = new Vec3(serverPlayer.getX() + xOffset, serverPlayer.getY() + yOffset, serverPlayer.getZ() + zOffset);
            VatanseverStormEntity stormEntity = new VatanseverStormEntity(serverPlayer.level, serverPlayer, pos);
            serverPlayer.level.addFreshEntity(stormEntity);
            stormEntity.setYRot(serverPlayer.getYRot());
            EpicFightCapabilities.getEntityPatch(stormEntity, VatanseverStormEntityPatch.class).playAnimationSynchronized(staticAnimation, 0.05F);
        }

    }

    private static void jet(VatanseverEntityPatch vatanseverEntityPatch, Joint toolJoint, int particleCount) {
        VatanseverEntity vatanseverEntity = vatanseverEntityPatch.getOriginal();
        if (vatanseverEntity.getOwner() == null) {
            return;
        }
        Level world = vatanseverEntity.level;
        // 获取骨骼变换矩阵
        OpenMatrix4f transformMatrix = vatanseverEntityPatch.getArmature().getBindedTransformFor(vatanseverEntityPatch.getArmature().getCurrentPose(), toolJoint);

        // 初始变换（位置偏移和基础旋转）
        transformMatrix.translate(new Vec3f(0.0F, 0.0F, 0.0F));
        OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(vatanseverEntityPatch.getOriginal().yBodyRot + 180.0F), new Vec3f(0.0F, 1.0F, 0.0F));
        OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);

        // 生成粒子
        for (int i = 0; i < 5 * particleCount; i++) {
            world.addParticle(ParticleTypes.CLOUD, transformMatrix.m30 + (float) vatanseverEntity.getX(), transformMatrix.m31 + (float) vatanseverEntity.getY(), transformMatrix.m32 + (float) vatanseverEntity.getZ(), 0, 0, 0);
        }
        for (int i = 0; i < 3 * particleCount; i++) {
            world.addParticle(
                    ParticleTypes.END_ROD, transformMatrix.m30 + (float) vatanseverEntity.getX(), transformMatrix.m31 + (float) vatanseverEntity.getY(), transformMatrix.m32 + (float) vatanseverEntity.getZ(), 0, 0, 0
            );
        }
    }

    public static void flyVFX(LivingEntityPatch<?> entityPatch) {
        int particleCount = 1;
        if (entityPatch instanceof VatanseverEntityPatch vatanseverEntityPatch) {
            for (Joint joint : SwordSoaringArmatures.vatanseverArmature.joints) {
                jet(vatanseverEntityPatch, joint, particleCount);
            }
        }
    }

    public static float getTotalAttackDamage(LivingEntityPatch<?> entityPatch) {
        LivingEntity owner = entityPatch.getOriginal();
        double baseDamage = owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        return (float) baseDamage;
    }

    public static VatanseverEntityPatch getVatanseverPatch(LivingEntityPatch<?> ownerPatch) {
        if (ownerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            if (serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE) != null) {
                SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
                if (manager.hasData(ArtifactSpiritPassiveSkill.ARTIFACT_SPIRIT_ENTITY_ID)) {
                    Entity entity = serverPlayerPatch.getOriginal().level.getEntity(manager.getDataValue(ArtifactSpiritPassiveSkill.ARTIFACT_SPIRIT_ENTITY_ID));
                    if (entity != null) {
                        return EpicFightCapabilities.getEntityPatch(entity, VatanseverEntityPatch.class);
                    }
                }
            }
        }
        return null;
    }

    public static boolean SwordCountis(LivingEntityPatch<?> ownerPatch, float count) {
        if (getVatanseverPatch(ownerPatch) == null) {
            return false;
        } else {
            VatanseverEntityPatch vatanseverEntityPatch = getVatanseverPatch(ownerPatch);
            return vatanseverEntityPatch.getLeftSwordCount() >= count;
        }
    }
}
