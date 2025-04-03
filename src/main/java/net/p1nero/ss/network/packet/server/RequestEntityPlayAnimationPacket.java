package net.p1nero.ss.network.packet.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.p1nero.ss.network.packet.BasePacket;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record RequestEntityPlayAnimationPacket(int entityId, int namespaceId, int animationId, float modifyTime) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(namespaceId);
        buf.writeInt(animationId);
        buf.writeFloat(modifyTime);
    }
    public static RequestEntityPlayAnimationPacket decode(FriendlyByteBuf buf){
        return new RequestEntityPlayAnimationPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat());
    }

    @Override
    public void execute(@Nullable ServerPlayer player) {
        if(player != null){
            Entity entity = player.level.getEntity(entityId);
            LivingEntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
            if(entityPatch != null){
                entityPatch.playAnimationSynchronized(EpicFightMod.getInstance().animationManager.findAnimationById(namespaceId, animationId), modifyTime);
            }
        }
    }
}