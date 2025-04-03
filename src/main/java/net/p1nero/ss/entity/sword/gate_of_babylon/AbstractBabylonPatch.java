package net.p1nero.ss.entity.sword.gate_of_babylon;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.gameassets.SwordSoaringColliders;
import net.p1nero.ss.network.PacketHandler;
import net.p1nero.ss.network.PacketRelay;
import net.p1nero.ss.network.packet.server.RequestEntityPlayAnimationPacket;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;

public abstract class AbstractBabylonPatch<T extends BabylonEntity> extends AbstractArtifactSpiritPatch<T> {

    protected boolean played;

    protected float baseDamage;

    /**
     * Join World的时候主人还没初始化，只能换这里操作
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    protected void clientTick(LivingEvent.LivingTickEvent event) {
        super.clientTick(event);
        if(!played){
            if(this.isLogicalClient() && this.getOwnerPatch() != null){
                //排除其他玩家干扰
                if(!this.getOwnerPatch().getOriginal().equals(Minecraft.getInstance().player)){
                    return;
                }
                StaticAnimation toPlay = getOriginal().getAnimationToPlay();
                if(toPlay != null){
                    PacketRelay.sendToServer(PacketHandler.INSTANCE, new RequestEntityPlayAnimationPacket(this.getOriginal().getId(), toPlay.getNamespaceId(), toPlay.getId(), 0.0001F));
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