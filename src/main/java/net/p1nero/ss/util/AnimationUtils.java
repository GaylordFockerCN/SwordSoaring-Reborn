package net.p1nero.ss.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.p1nero.ss.animation.ArtifactSpiritMultiPhaseAttackAnimation;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

import java.util.ArrayList;
import java.util.List;

public class AnimationUtils {

    public static Vec3 getJointWorldPos(LivingEntityPatch<?> entityPatch, Joint joint) {
        Animator animator = entityPatch.getAnimator();
        Pose pose = animator.getPlayerFor(null).getCurrentPose(entityPatch, 0.5F);
        Vec3 pos = entityPatch.getOriginal().position();
        OpenMatrix4f modelTf = OpenMatrix4f.createTranslation((float) pos.x, (float) pos.y, (float) pos.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(entityPatch.getModelMatrix(1)));
        OpenMatrix4f JointTf = new OpenMatrix4f(entityPatch.getArmature().getBindedTransformFor(pose, joint)).mulFront(modelTf);

        return OpenMatrix4f.transform(JointTf, Vec3.ZERO);
    }

    public static ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase[] getPhases(List<Joint> joints, float maxTime){
        ArrayList<ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase> multiAttackPhases = new ArrayList<>();
        for(Joint joint : joints){
            multiAttackPhases.add(new ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase(0.01F, 0.01F, maxTime, maxTime, Float.MAX_VALUE, joint, null));
        }
        return multiAttackPhases.toArray(new ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase[0]);
    }

    public static ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase[] getPhases(List<Joint> joints, float startTime, float endTime){
        ArrayList<ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase> multiAttackPhases = new ArrayList<>();
        for(Joint joint : joints){
            multiAttackPhases.add(new ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase(startTime, startTime, endTime, endTime, Float.MAX_VALUE, joint, null));
        }
        return multiAttackPhases.toArray(new ArtifactSpiritMultiPhaseAttackAnimation.MultiAttackPhase[0]);
    }



    public static void groundSplit(LivingEntityPatch<?> entityPatch, double viewOffset, double xOffset, double yOffset, double zOffset, float damage, float radius, int particleCount) {
        groundSplit(entityPatch, viewOffset, xOffset, yOffset, zOffset, damage, radius, particleCount, StunType.LONG);
    }

    public static void groundSplit(LivingEntityPatch<?> entityPatch, double viewOffset, double xOffset, double yOffset, double zOffset, float damage, float radius, int particleCount, StunType stunType) {
        LivingEntity entity = entityPatch.getOriginal();
        Vec3 pos = entity.position();
        Vec3 dir = entity.getViewVector(1).normalize().scale(viewOffset);
        Vec3 target = pos.add(dir.x + xOffset, -1 + yOffset, dir.z + zOffset);
        Vec3 damagetarget = pos.add(dir.x + xOffset, yOffset, dir.z + zOffset);
        if (entity.level instanceof ServerLevel level) {
            LevelUtil.circleSlamFracture(entity, level, target, radius, false);
            dealAreaDamage(level, damagetarget, entity, damage, radius, stunType);
        } else {
            createRandomSmokeLine(entity.level, target, particleCount);
        }
    }

    public static void dealAreaDamage(ServerLevel level, Vec3 center, LivingEntity source, float damage, float radius, StunType stunType) {
        if (radius <= 0) return;
        AABB area = new AABB(center.x() - radius, center.y() - radius, center.z() - radius, center.x() + radius, center.y() + radius, center.z() + radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, entity ->
                entity.isAlive() && entity.distanceToSqr(center) <= radius * radius
                        && !(entity instanceof Player player && player.isCreative())
                        && entity != source
                        && !(entity instanceof AbstractArtifactSpiritEntity)
                        && !(source instanceof AbstractArtifactSpiritEntity artifactSpiritEntity && entity.equals(artifactSpiritEntity.getOwner())));
        //伤害源换主人
        LivingEntity trueSource = source instanceof OwnableEntity ownableEntity ? ((LivingEntity) ownableEntity.getOwner()) : source;
        for (LivingEntity entity : new ArrayList<>(entities)) {
            if (entity.invulnerableTime >= 0 && trueSource != null) {
                LivingEntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(source, LivingEntityPatch.class);
                if(entityPatch != null){
                    DamageSource original = DamageSource.mobAttack(trueSource);
                    entity.invulnerableTime = 0;
                    entity.hurt((DamageSource) EpicFightDamageSource.commonEntityDamageSource(original.getMsgId(), source, Animations.DUMMY_ANIMATION).setStunType(stunType), damage);
                }
                entity.invulnerableTime = 0;
                entity.hurt(DamageSource.indirectMagic(trueSource, trueSource), damage);
            }
        }
    }



    public static void attractEntities(LivingEntityPatch<?> entityPatch, float attractRadius, float damage, float damageRadius) {
        LivingEntity source = entityPatch.getOriginal();
        Vec3 sourcePos = source.position();
        if (source.level instanceof ServerLevel level) {

            AABB area = new AABB(sourcePos.x - attractRadius, sourcePos.y - attractRadius, sourcePos.z - attractRadius,
                    sourcePos.x + attractRadius, sourcePos.y + attractRadius, sourcePos.z + attractRadius);

            source.level.getEntitiesOfClass(Entity.class, area).forEach(entity -> {
                if (entity == source) return;
                if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) return;

                Vec3 entityPos = entity.position();
                Vec3 delta = sourcePos.subtract(entityPos);
                double distance = delta.length();

                if (distance > 1.0) {
                    Vec3 direction = delta.normalize();
                    double speed = 0.3;
                    entity.setDeltaMovement(entity.getDeltaMovement().add(direction.scale(speed)));
                } else {
                    Vec3 safePos = sourcePos.subtract(delta.normalize().scale(1.0));
                    entity.setPos(safePos.x, safePos.y, safePos.z);
                    entity.setDeltaMovement(Vec3.ZERO);
                }
                if (entity.distanceTo(source) < damageRadius) {
                    entity.hurt(DamageSource.mobAttack(source), damage);
                }
            });
        } else {
            createRandomSmokeLine(source.level, sourcePos, 10);
        }
    }

    private static final double MIN_SPEED1 = 0.1;
    private static final double MAX_SPEED1 = 0.5;

    public static void createRandomSmokeLine(Level level, Vec3 center, int particleCount) {
        RandomSource random1 = level.random;

        for (int i = 0; i < particleCount; i++) {
            double t = (double) i / (particleCount - 1);
            double distance = t * 5.0;

            double angle = random1.nextDouble() * 2 * Math.PI;
            double pitch = random1.nextDouble() * Math.PI - Math.PI / 2;

            double offsetX = Math.cos(angle) * Math.cos(pitch);
            double offsetY = Math.sin(pitch);
            double offsetZ = Math.sin(angle) * Math.cos(pitch);

            double x = center.x() + offsetX * distance;
            double y = center.y() + offsetY * distance;
            double z = center.z() + offsetZ * distance;
            double speed = MIN_SPEED1 + random1.nextDouble() * (MAX_SPEED1 - MIN_SPEED1);
            level.addParticle(ParticleTypes.SMOKE, x, y, z, offsetX * speed, offsetY * speed, offsetZ * speed);
        }
    }
}