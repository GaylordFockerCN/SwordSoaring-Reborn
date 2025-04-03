package net.p1nero.ss.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.p1nero.ss.SwordSoaring;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.client.gui.HealthBarIndicator;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractArtifactSpiritEntity extends PathfinderMob implements OwnableEntity {
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(AbstractArtifactSpiritEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public AbstractArtifactSpiritEntity(EntityType<? extends AbstractArtifactSpiritEntity> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * 取消血条渲染
     * {@link HealthBarIndicator#shouldDraw(LivingEntity, LivingEntityPatch, LocalPlayerPatch)}
     */
    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_OWNER_UUID, Optional.empty());
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(pUuid));
    }

    public void tame(LivingEntity livingEntity) {
        this.setOwnerUUID(livingEntity.getUUID());
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();
            if(uuid != null){
                Player player = this.level.getPlayerByUUID(uuid);
                if(player == null){
                    if(this.level instanceof ServerLevel serverLevel){
                        return serverLevel.getEntity(uuid) instanceof LivingEntity livingEntity ? livingEntity : null;
                    }
                } else {
                    return player;
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            SwordSoaring.LOGGER.error("error in get artifact spirit's owner", e);
            return null;
        }
    }

    public LivingEntityPatch<?> getOwnerPatch(){
        return EpicFightCapabilities.getEntityPatch(getOwner(), LivingEntityPatch.class);
    }

    public LivingEntityPatch<?> getPatch(){
        return EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
    }

    public <T extends EntityPatch<?>> T getPatch(Class<T> type){
        return EpicFightCapabilities.getEntityPatch(this, type);
    }

    @Override
    public void tick() {
        super.tick();
        fallDistance = 0;
        LivingEntity owner = getOwner();
        if (owner != null && owner.isAlive()) {
            moveToOwner(owner);
            if(getOriginalItem() == null){
                return;
            }
            if (!level.isClientSide && !owner.getMainHandItem().is(getOriginalItem()) && shouldRemoveWhenOwnerLost()) {
                discard();
            }
        } else if(!level.isClientSide && shouldRemoveWhenOwnerLost()){
            discard();
        }
    }

    protected void moveToOwner(LivingEntity owner){
        setYRot(owner.yBodyRot);
        setYBodyRot(owner.yBodyRot);
        setYHeadRot(owner.yBodyRot);
        setPos(owner.position());
    }

    protected boolean shouldRemoveWhenOwnerLost(){
        return true;
    }

    /**
     * null 表示什么物品都可以
     */
    @Nullable
    protected abstract Item getOriginalItem();

    public static AttributeSupplier getDefaultAttribute() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 19.9F)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(EpicFightAttributes.MAX_STRIKES.get(), 10.0F)
                .build();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float p_21017_) {
        return false;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    public boolean canSpawnSprintParticle() {
        return false;
    }
}