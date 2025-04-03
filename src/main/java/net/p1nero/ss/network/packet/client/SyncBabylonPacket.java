package net.p1nero.ss.network.packet.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p1nero.ss.capability.SSCapabilityProvider;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonEntity;
import net.p1nero.ss.network.packet.BasePacket;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public record SyncBabylonPacket(int id, int size, ArrayList<ItemStack> itemStacks) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(itemStacks.size());
        itemStacks.forEach(buf::writeItem);
    }
    public static SyncBabylonPacket decode(FriendlyByteBuf buf){
        int id = buf.readInt();
        int size = buf.readInt();
        ArrayList<ItemStack> newItemStack = new ArrayList<>();
        for(int i = 0; i < size; i++){
            newItemStack.add(buf.readItem());
        }
        return new SyncBabylonPacket(id, size, newItemStack);
    }

    @Override
    public void execute(@Nullable ServerPlayer player) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null){
            Entity entity = Minecraft.getInstance().level.getEntity(id);
            if(entity instanceof Player localPlayer){
                localPlayer.getCapability(SSCapabilityProvider.SS_PLAYER).ifPresent(ssPlayer -> ssPlayer.updateBabylonItems(itemStacks));
            }
            if(entity instanceof BabylonEntity babylonEntity){
                babylonEntity.updateBabylonItems(itemStacks);
            }
        }
    }
}