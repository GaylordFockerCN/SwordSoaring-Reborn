package net.p1nero.ss.network.packet.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.p1nero.ss.capability.SSCapabilityProvider;
import net.p1nero.ss.entity.sword.fly_sword.FlySwordEntity;
import net.p1nero.ss.network.packet.BasePacket;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public record RequestVatanseverSwordBackPacket() implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
    }
    public static RequestVatanseverSwordBackPacket decode(FriendlyByteBuf buf){
        return new RequestVatanseverSwordBackPacket();
    }

    @Override
    public void execute(@Nullable ServerPlayer player) {
        if(player != null){
            player.getCapability(SSCapabilityProvider.SS_PLAYER).ifPresent(ssPlayer -> {
                Iterator<FlySwordEntity> iterator = ssPlayer.getVatanseverShootEntities().iterator();
                while (iterator.hasNext()){
                    FlySwordEntity flySwordEntity = iterator.next();
                    if(flySwordEntity != null && flySwordEntity.isAlive()){
                        if(flySwordEntity.callFlyingBack()){
                            iterator.remove();
                            break;
                        }
                    } else {
                        iterator.remove();
                    }
                }
            });
        }
    }
}