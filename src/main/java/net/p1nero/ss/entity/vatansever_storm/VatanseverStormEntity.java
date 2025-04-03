package net.p1nero.ss.entity.vatansever_storm;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.item.SwordSoaringItems;

public class VatanseverStormEntity extends AbstractArtifactSpiritEntity {
    public static final int MAX_LIFE_TIME = 460;
    public VatanseverStormEntity(EntityType<? extends VatanseverStormEntity> entityType, Level level) {
        super(entityType, level);
    }

    public VatanseverStormEntity(Level level, Player owner, Vec3 pos) {
        super(SwordSoaringEntities.VATANSEVER_STORM.get(), level);
        tame(owner);
        setPos(pos);
        moveToOwner(owner);
        setNoAi(true);
        setNoGravity(true);
        noPhysics = true;
    }

    @Override
    protected void moveToOwner(LivingEntity owner) {
        setYRot(0);
        setYBodyRot(0);
        setYHeadRot(0);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide){
            if(tickCount > MAX_LIFE_TIME) {
                this.discard();
            }
        }
    }

    @Override
    protected boolean shouldRemoveWhenOwnerLost() {
        return false;
    }

    @Override
    protected Item getOriginalItem() {
        return SwordSoaringItems.VATANSEVER.get();
    }
}