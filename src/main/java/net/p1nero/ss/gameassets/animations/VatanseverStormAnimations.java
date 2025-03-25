package net.p1nero.ss.gameassets.animations;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.entity.vatansever_storm.VatanseverStormArmature;
import net.p1nero.ss.entity.vatansever_storm.VatanseverStormEntity;
import net.p1nero.ss.entity.vatansever_storm.VatanseverStormEntityPatch;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VatanseverStormAnimations {

    public static StaticAnimation VATANSEVER_STORM_IDLE;
    public static StaticAnimation VATANSEVER_STORM_UP;
    public static StaticAnimation VATANSEVER_STORM_MIDDLE;
    public static StaticAnimation VATANSEVER_STORM_MIDDLE_2;
    public static StaticAnimation VATANSEVER_STORM_DOWN;
    public static StaticAnimation VATANSEVER_STORM_RISE_1;
    public static StaticAnimation VATANSEVER_STORM_RISE_2;
    public static StaticAnimation VATANSEVER_STORM_RISE_3;
    public static StaticAnimation VATANSEVER_STORM_RISE_4;

    public static void buildVatanseverStormAnim() {
        VatanseverStormArmature stormArmature = SwordSoaringArmatures.vatanseverStormArmature;
        VATANSEVER_STORM_IDLE = new StaticAnimation(true, "vatansever_storm/vatansever_storm_idle", stormArmature);
        VATANSEVER_STORM_UP = new ActionAnimation(0.0001F, "vatansever_storm/vatansever_storm_up", stormArmature)
                .addEvents(AnimationEvent.TimePeriodEvent.create(0, 3, (entityPatch, self, params) -> stormVFX(entityPatch), AnimationEvent.Side.CLIENT))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(VATANSEVER_STORM_UP);
                }), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.8F));
        VATANSEVER_STORM_MIDDLE = new ActionAnimation(0.0001F, "vatansever_storm/vatansever_storm_middle", stormArmature)
                .addEvents(AnimationEvent.TimePeriodEvent.create(0, 3, (entityPatch, self, params) -> stormVFX(entityPatch), AnimationEvent.Side.CLIENT))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(VATANSEVER_STORM_MIDDLE);
                }), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.9F));
        VATANSEVER_STORM_MIDDLE_2 = new ActionAnimation(0.0001F, "vatansever_storm/vatansever_storm_middle_2", stormArmature)
                .addEvents(AnimationEvent.TimePeriodEvent.create(0, 3, (entityPatch, self, params) -> {
                            stormVFX(entityPatch);
                            if (entityPatch instanceof VatanseverStormEntityPatch vatanseverStormEntityPatch) {
                                stormDamage(vatanseverStormEntityPatch, 35, 3, 25);
                            }
                        }
                        , AnimationEvent.Side.BOTH))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(VATANSEVER_STORM_MIDDLE_2);
                }), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.9F));
        VATANSEVER_STORM_DOWN = new ActionAnimation(0.0001F, "vatansever_storm/vatansever_storm_down", stormArmature)
                .addEvents(AnimationEvent.TimePeriodEvent.create(0, 3, (entityPatch, self, params) -> stormVFX(entityPatch), AnimationEvent.Side.CLIENT))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(VATANSEVER_STORM_DOWN);
                }), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.8F));
        VATANSEVER_STORM_RISE_1 = new ActionAnimation(0.0001F, "vatansever_storm/vatansever_storm_rise_1", stormArmature)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    LivingEntity entity = livingEntityPatch.getOriginal();
                    entity.discard();
                }), AnimationEvent.Side.SERVER));
        VATANSEVER_STORM_RISE_2 = new ActionAnimation(0.0001F, "vatansever_storm/vatansever_storm_rise_2", stormArmature)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    LivingEntity entity = livingEntityPatch.getOriginal();
                    entity.discard();
                }), AnimationEvent.Side.SERVER));
        VATANSEVER_STORM_RISE_3 = new ActionAnimation(0.0001F, "vatansever_storm/vatansever_storm_rise_3", stormArmature)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    LivingEntity entity = livingEntityPatch.getOriginal();
                    entity.discard();
                }), AnimationEvent.Side.SERVER));
        VATANSEVER_STORM_RISE_4 = new ActionAnimation(0.0001F, "vatansever_storm/vatansever_storm_rise_4", stormArmature)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    LivingEntity entity = livingEntityPatch.getOriginal();
                    entity.discard();
                }), AnimationEvent.Side.SERVER));
    }


    private static void stormParticle(VatanseverStormEntityPatch vatanseverStormEntityPatch, Joint toolJoint, int particleCount) {
        VatanseverStormEntity vatanseverStormEntity = vatanseverStormEntityPatch.getOriginal();
        if (vatanseverStormEntity.getOwner() == null) return;
        Level world = vatanseverStormEntity.level;
        Random random = world.random;
        // 速度范围
        float minSpeed = -0.5f;
        float maxSpeed = 0.5f;
        float rxv = minSpeed + (maxSpeed - minSpeed) * random.nextFloat();
        float ryv = minSpeed + (maxSpeed - minSpeed) * random.nextFloat();
        float rzv = minSpeed + (maxSpeed - minSpeed) * random.nextFloat();
        // 获取变换矩阵
        OpenMatrix4f transformMatrix = vatanseverStormEntityPatch.getArmature()
                .getBindedTransformFor(vatanseverStormEntityPatch.getArmature().getCurrentPose(), toolJoint);
        // 应用旋转
        OpenMatrix4f rotation = new OpenMatrix4f().rotate(
                -(float) Math.toRadians(vatanseverStormEntity.yBodyRot + 180.0F),
                new Vec3f(0.0F, 1.0F, 0.0F)
        );
        OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);

        // 基础位置（包含实体坐标）
        float baseX = transformMatrix.m30 + (float) vatanseverStormEntity.getX();
        float baseY = transformMatrix.m31 + (float) vatanseverStormEntity.getY();
        float baseZ = transformMatrix.m32 + (float) vatanseverStormEntity.getZ();
        // 生成粒子的通用方法
        for (int i = 0; i < 5 * particleCount; i++) {
            // 生成球体内的随机偏移
            float xOffset, yOffset, zOffset;
            do {
                xOffset = random.nextFloat() * 4 - 2;
                yOffset = random.nextFloat() * 4 - 2;
                zOffset = random.nextFloat() * 4 - 2;
            } while (xOffset * xOffset + yOffset * yOffset + zOffset * zOffset > 4.0f);

            // 计算最终位置
            float x = baseX + xOffset;
            float y = baseY + yOffset;
            float z = baseZ + zOffset;

            // 生成主粒子
            world.addParticle(ParticleTypes.SOUL, true, x, y, z, rxv, ryv, rzv);

            // 5%概率生成下落线条
            if (random.nextFloat() < 0.05f) {
                float currentY = y;
                for (int j = 0; j < 15; j++) { // 生成5个下落粒子
                    currentY -= 3f; // 每次下落0.5格
                    world.addParticle(
                            ParticleTypes.SMOKE,
                            true,
                            x + (random.nextFloat() - 0.5f) * 0.2f, // 添加水平随机偏移
                            currentY,
                            z + (random.nextFloat() - 0.5f) * 0.2f,
                            0f, -0.2f, 0f // 向下运动
                    );
                }
            }
        }
        for (int i = 0; i < 2 * particleCount; i++) {
            // 生成球体内的随机偏移
            float xOffset, yOffset, zOffset;
            do {
                xOffset = random.nextFloat() * 4 - 2;
                yOffset = random.nextFloat() * 4 - 2;
                zOffset = random.nextFloat() * 4 - 2;
            } while (xOffset * xOffset + yOffset * yOffset + zOffset * zOffset > 4.0f);

            // 计算最终位置
            float x = baseX + xOffset;
            float y = baseY + yOffset;
            float z = baseZ + zOffset;


            // 5%概率生成下落线条
            if (random.nextFloat() < 0.05f) {
                float currentY = y;
                for (int j = 0; j < 20; j++) {
                    currentY -= 2f; // 每次下落0.5格
                    world.addParticle(ParticleTypes.SMOKE, true, x + (random.nextFloat() - 0.5f) * 0.2f, currentY, z + (random.nextFloat() - 0.5f) * 0.2f, 0f, -0.2f, 0f // 向下运动
                    );
                }
            }
        }
    }

    public static void stormDamage(VatanseverStormEntityPatch vatanseverStormEntityPatch, float attractRadius, float damage, float damageRadius) {
        VatanseverStormEntity storm = vatanseverStormEntityPatch.getOriginal();
        LivingEntity source = storm.getOwner();

        Vec3 Pos = storm.position();
        if (storm.level instanceof ServerLevel level) {

            AABB area = new AABB(
                    Pos.x - attractRadius, Pos.y - attractRadius, Pos.z - attractRadius,
                    Pos.x + attractRadius, Pos.y + attractRadius, Pos.z + attractRadius);

            storm.level.getEntitiesOfClass(Entity.class, area).forEach(entity -> {
                if (entity == storm) return;
                if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) return;

                Vec3 entityPos = entity.position();
                Vec3 delta = Pos.subtract(entityPos);
                double distance = delta.length();

                if (distance > 1.0) {
                    Vec3 direction = delta.normalize();
                    double speed = 0.5;
                    entity.setDeltaMovement(entity.getDeltaMovement().add(direction.scale(speed)));
                } else {
                    Vec3 safePos = Pos.subtract(delta.normalize().scale(1.0));
                    entity.setPos(safePos.x, safePos.y, safePos.z);
                    entity.setDeltaMovement(Vec3.ZERO);
                }
            });
            AABB damageArea = new AABB(Pos.x() - damageRadius, Pos.y() - damageRadius, Pos.z() - damageRadius, Pos.x() + damageRadius, Pos.y() + damageRadius, Pos.z() + damageRadius);
            //来源实体过滤
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, damageArea, entity ->
                    entity.isAlive() && entity.distanceToSqr(Pos) <= damageRadius * damageRadius && !(entity instanceof Player player && player.isCreative()) && entity != source && !(entity instanceof AbstractArtifactSpiritEntity)
            );
            for (LivingEntity entity : new ArrayList<>(entities)) {
                if (entity.invulnerableTime == 0 && source != null) {
                    entity.hurt(DamageSource.indirectMobAttack(source,source), damage);
                    entity.invulnerableTime = 5;
                    if (!entity.level.isClientSide) {
                        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
                    }
                    if (entity.level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SMOKE, entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(), 5, 0.5, 0.25, 0.5, 0.2);
                        serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), EpicFightSounds.BLADE_HIT, SoundSource.HOSTILE, 1.0F, 0.8F + entity.level.random.nextFloat() * 0.4F
                        );
                    }
                }
            }
        }
    }


    public static void stormVFX(LivingEntityPatch<?> entityPatch) {
        int particleCount = 1;
        Level world = entityPatch.getOriginal().level;
        if (entityPatch instanceof VatanseverStormEntityPatch vatanseverStormEntityPatch && world.isClientSide) {
            for (Joint joint : SwordSoaringArmatures.vatanseverStormArmature.rootJoints) {
                stormParticle(vatanseverStormEntityPatch, joint, particleCount);
            }
        }
    }
}
