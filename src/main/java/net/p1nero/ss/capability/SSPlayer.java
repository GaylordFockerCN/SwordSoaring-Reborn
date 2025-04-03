package net.p1nero.ss.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.p1nero.ss.Config;
import net.p1nero.ss.entity.sword.fly_sword.FlySwordEntity;
import net.p1nero.ss.network.PacketHandler;
import net.p1nero.ss.network.PacketRelay;
import net.p1nero.ss.network.packet.client.SyncBabylonPacket;
import net.p1nero.ss.util.ItemUtils;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.types.AttackAnimation;

import java.util.*;

/**
 * 记录飞行和技能使用的状态，被坑了，这玩意儿也分服务端和客户端...
 */
public class SSPlayer {
    private final Map<AttackAnimation.Phase, List<Entity>> phaseListMap = new HashMap<>();

    public Map<AttackAnimation.Phase, List<Entity>> getPhaseListMap() {
        return phaseListMap;
    }

    public List<Entity> getCurrentlyHurtEntities(AttackAnimation.Phase phase){
        List<Entity> toReturn = phaseListMap.get(phase);
        if(toReturn == null){
            List<Entity> newList = new ArrayList<>();
            phaseListMap.put(phase, newList);
            return newList;
        }
        return toReturn;
    }

    public void clearMap(){
        phaseListMap.clear();
    }

    private final ArrayList<FlySwordEntity> vatanseverShootEntities = new ArrayList<>();

    public ArrayList<FlySwordEntity> getVatanseverShootEntities() {
        return vatanseverShootEntities;
    }

    public void addVatanseverShootEntity(@NotNull FlySwordEntity flySwordEntity){
        vatanseverShootEntities.add(flySwordEntity);
    }

    public void clearVatanseverShootEntities(){
        Iterator<FlySwordEntity> iterator = vatanseverShootEntities.iterator();
        while (iterator.hasNext()){
            FlySwordEntity flySwordEntity = iterator.next();
            if(flySwordEntity != null && flySwordEntity.isAlive()){
                flySwordEntity.discard();
            }
            iterator.remove();
        }
    }

    private ArrayList<ItemStack> validBabylonItems = new ArrayList<>();

    /**
     * 初始化王财列表，并返回物品数
     */
    public int initBabylonItems(ServerPlayer player){
        validBabylonItems = ItemUtils.calculateValidBabylonItems(player, Config.REMOVE_ITEM.get());
        PacketRelay.sendToPlayer(PacketHandler.INSTANCE, new SyncBabylonPacket(player.getId(), validBabylonItems.size(), validBabylonItems), player);
        return validBabylonItems.size();
    }

    public void updateBabylonItems(ArrayList<ItemStack> newItems){
        validBabylonItems = newItems;
    }

    public ArrayList<ItemStack> getValidBabylonItems() {
        return validBabylonItems;
    }

    private ArrayList<ItemStack> wanSwordList;

    public void setWanSwordList(ArrayList<ItemStack> wanSwordList) {
        this.wanSwordList = wanSwordList;
    }

    public ArrayList<ItemStack> getWanSwordList() {
        return wanSwordList;
    }

    public void saveNBTData(CompoundTag tag){
    }

    public void loadNBTData(CompoundTag tag){
    }

    public void copyFrom(SSPlayer old){
    }
}