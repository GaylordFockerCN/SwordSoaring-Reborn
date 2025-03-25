package net.p1nero.ss.entity.sword.fly_sword;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.entity.sword.AbstractSwordEntity;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import net.p1nero.ss.gameassets.animations.FlySwordAnimations;
import net.p1nero.ss.skill.weapon_passive.VatanseverPassive;
import net.p1nero.ss.util.AnimationUtils;
import net.p1nero.ss.util.vfx.ParticleVFX;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.ArrayList;
import java.util.List;

public class FlySwordEntity extends AbstractSwordEntity {
    private int maxTickCount = -1;
    private static final EntityDataAccessor<Boolean> ROTATION_LOCK = SynchedEntityData.defineId(FlySwordEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ANIMATION_END = SynchedEntityData.defineId(FlySwordEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FLYING_BACK = SynchedEntityData.defineId(FlySwordEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> READY_TO_FLY_BACK = SynchedEntityData.defineId(FlySwordEntity.class, EntityDataSerializers.BOOLEAN);
    private LivingEntity target;

    public FlySwordEntity(EntityType<? extends AbstractArtifactSpiritEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FlySwordEntity(LivingEntity owner, int maxTickCount, LivingEntity target) {
        super(SwordSoaringEntities.FLY_SWORD.get(), owner.getMainHandItem().copy(), owner);
        this.maxTickCount = maxTickCount;
        this.target = target;
        if (target != null && target.isAlive()) {
            setPos(target.position());
        }
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(ROTATION_LOCK, true);
        getEntityData().define(FLYING_BACK, false);
        getEntityData().define(READY_TO_FLY_BACK, false);
        getEntityData().define(ANIMATION_END, false);
    }

    public boolean isRotationLock() {
        return getEntityData().get(ROTATION_LOCK);
    }

    public void setRotationLock(boolean rotationLock) {
        getEntityData().set(ROTATION_LOCK, rotationLock);
    }

    public boolean isAnimationEnd() {
        return getEntityData().get(ANIMATION_END);
    }

    public void setAnimationEnd(boolean flying) {
        getEntityData().set(ANIMATION_END, flying);
    }

    public boolean isFlyingBack() {
        return getEntityData().get(FLYING_BACK);
    }

    public void setFlyingBack(boolean flying) {
        getEntityData().set(FLYING_BACK, flying);
    }

    public boolean isReadyToFlyBack() {
        return getEntityData().get(READY_TO_FLY_BACK);
    }

    public void setReadyToFlyBack(boolean flying) {
        getEntityData().set(READY_TO_FLY_BACK, flying);
    }

    public boolean callFlyingBack() {
        if (getPatch() instanceof FlySwordPatch flySwordPatch) {
            if (flySwordPatch.getEntityState().inaction()) {
                return false;
            }
            flySwordPatch.playAnimationSynchronized(FlySwordAnimations.FLY_SWORD_ATK_FLY_BACK, 0.001F);
            if (getOwner() != null) {
                flySwordPatch.rotateTo(getOwner(), 30, true);
                setReadyToFlyBack(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public LivingEntity getTarget() {
        return target;
    }

    /**
     * 最好只用一次
     */
    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    @Override
    protected void moveToOwner(LivingEntity owner) {
        noPhysics = true;
        if (isFlyingBack()) {
            Vec3 vec3 = AnimationUtils.getJointWorldPos(getPatch(), SwordSoaringArmatures.flySwordArmature.body);
            ParticleVFX.createSphereParticles(level, vec3, ParticleTypes.SMOKE, 0.2, 0.01, 0.05, 100);
            flySwordDamage(2, 2.5F);
            if (!level.isClientSide) {
                if (this.position().distanceTo(owner.getEyePosition()) < 1.5) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE, getX(), getY(), getZ(), 300, 0.5, 0.5, 0.5, 0.5);
                    level.playSound(null, getX(), getY(), getZ(), SoundEvents.FIRE_EXTINGUISH, owner.getSoundSource(), 1.0F, 1.0F);
                    addOwnerSwordCount();
                    this.discard();
                    return;
                }
                Vec3 dir = owner.getEyePosition().subtract(this.getEyePosition()).normalize().scale(0.8F);
                setDeltaMovement(dir);//旋转在Patch里操作
            }
        } else {
            if (isReadyToFlyBack()) {
                getPatch().rotateTo(owner, 30, true);
            } else if (isRotationLock()) {
                setYRot(0);
                setYBodyRot(0);
                setYHeadRot(0);
            }
            if (!level.isClientSide) {
                if (target != null && target.isAlive() && !isAnimationEnd()) {
                    this.setPos(target.position());
                }
                if (tickCount == maxTickCount) {
                    addOwnerSwordCount();
                    this.discard();
                }
            }
        }
        noPhysics = false;
    }

    public void addOwnerSwordCount() {
        if (getOwnerPatch() instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
            if (manager.hasData(VatanseverPassive.SWORD_COUNT)) {
                int currentCnt = manager.getDataValue(VatanseverPassive.SWORD_COUNT);
                manager.setDataSync(VatanseverPassive.SWORD_COUNT, Math.min(currentCnt + 1, 6), serverPlayerPatch.getOriginal());
            }
        }
    }

    public void flySwordDamage(float attractRadius, float damageRadius) {
        if (getOwner() == null) {
            return;
        }
        double baseDamage = getOwner().getAttributeValue(Attributes.ATTACK_DAMAGE) * 3;

        Vec3 pos = this.position();
        if (this.level instanceof ServerLevel) {
            AABB damageArea = new AABB(pos.x() - damageRadius, pos.y() - damageRadius, pos.z() - damageRadius,
                    pos.x() + damageRadius, pos.y() + damageRadius, pos.z() + damageRadius);
            //来源实体过滤
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, damageArea, entity ->
                    entity.isAlive() && entity.distanceToSqr(pos) <= damageRadius * damageRadius && !(entity instanceof Player player && (player.isCreative() || player.isSpectator())) && entity != getOwner() && !(entity instanceof AbstractArtifactSpiritEntity));
            for (LivingEntity entity : new ArrayList<>(entities)) {

                if(entity.distanceTo(this) < attractRadius){
                    Vec3 entityPos = entity.position();
                    Vec3 delta = pos.subtract(entityPos);
                    double distance = delta.length();

                    if (distance > 1.0) {
                        Vec3 direction = delta.normalize();
                        double speed = 0.5;
                        entity.setDeltaMovement(entity.getDeltaMovement().add(direction.scale(speed)));
                    } else {
                        Vec3 safePos = pos.subtract(delta.normalize().scale(1.0));
                        entity.setPos(safePos.x, safePos.y, safePos.z);
                        entity.setDeltaMovement(Vec3.ZERO);
                    }
                }

                if (entity.invulnerableTime == 0) {
                    entity.hurt(DamageSource.indirectMobAttack(getOwner(), getOwner()), (float) baseDamage);
                    entity.invulnerableTime = 10;
                    if (!entity.level.isClientSide) {
                        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
                    }
                }
            }
        }
    }

    @Override
    protected Item getOriginalItem() {
        return null;
    }

    @Override
    protected boolean shouldRemoveWhenOwnerLost() {
        return false;
    }
}
