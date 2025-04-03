package net.p1nero.ss.network.packet.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonEntity;
import net.p1nero.ss.network.PacketHandler;
import net.p1nero.ss.network.PacketRelay;
import net.p1nero.ss.network.packet.BasePacket;
import net.p1nero.ss.network.packet.client.SyncBabylonPacket;
import org.jetbrains.annotations.Nullable;

public record RequestBabylonSyncPacket(int entityId) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }
    public static RequestBabylonSyncPacket decode(FriendlyByteBuf buf){
        return new RequestBabylonSyncPacket(buf.readInt());
    }

    @Override
    public void execute(@Nullable ServerPlayer player) {
        if(player != null){
            Entity entity = player.level.getEntity(entityId);
            if(entity instanceof BabylonEntity babylonEntity){
                PacketRelay.sendToAll(PacketHandler.INSTANCE, new SyncBabylonPacket(babylonEntity.getId(), babylonEntity.getValidBabylonItems().size(), babylonEntity.getValidBabylonItems()));
            }
        }
    }
}