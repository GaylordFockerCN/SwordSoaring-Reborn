package net.p1nero.ss.entity.sword.gate_of_babylon;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.gameassets.SwordSoaringColliders;
import net.p1nero.ss.network.packet.server.RequestEntityPlayAnimationPacket;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;

public abstract class AbstractBabylonPatch<T extends BabylonEntity> extends AbstractArtifactSpiritPatch<T> {

    protected boolean played;

    protected float baseDamage;

    public AbstractBabylonPatch(T entity) {
        super(entity);
    }

    /**
     * Join World的时候主人还没初始化，只能换这里操作
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void postTickClient() {
        super.postTickClient();
        if(!played){
            if(this.isLogicalClient() && this.getOwnerPatch() != null){
                //排除其他玩家干扰
                if(!this.getOwnerPatch().getOriginal().equals(Minecraft.getInstance().player)){
                    return;
                }

                AnimationManager.AnimationAccessor<? extends StaticAnimation> toPlay = getOriginal().getAnimationToPlay();
                if(toPlay != null){
                    PacketDistributor.sendToServer(new RequestEntityPlayAnimationPacket(this.getOriginal().getId(), toPlay.id(), 0.0001F));
                    played = true;
                }
            }
        }
    }

    /**
     * 懒得根据Joint去查碰撞箱了，太麻烦
     */
    @Override
    public Collider getColliderMatching(InteractionHand hand) {
        return SwordSoaringColliders.FLY_SWORD_COMMON;
    }

    @Override
    public boolean shouldUseOwnerAttack() {
        return false;
    }

    public void setModifiedBaseDamage(float modifiedBaseDamage) {
        this.baseDamage = modifiedBaseDamage;
    }

    @Override
    public float getModifiedBaseDamage(float baseDamage) {
        return this.baseDamage;
    }

    @Override
    public void updateMotion(boolean considerInaction) {
        keepIdleMotion(considerInaction);
    }
}
