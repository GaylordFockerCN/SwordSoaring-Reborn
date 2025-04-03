package net.p1nero.ss.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class ItemUtils {
    public static double getItemAttackDamage(LivingEntity livingEntity, ItemStack itemStack){
        if(itemStack.isEmpty()){
            return 0;
        }
        AtomicReference<Double> totalDamage;
        if(livingEntity != null){
            totalDamage = new AtomicReference<>(livingEntity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE));
        } else {
            totalDamage = new AtomicReference<>(0.0);
        }
        itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).forEach(attributeModifier -> {
            if(attributeModifier.getOperation().equals(AttributeModifier.Operation.ADDITION)){
                totalDamage.updateAndGet(v -> v + attributeModifier.getAmount());
            }
        });
        return totalDamage.get();
    }

    public static ArrayList<ItemStack> calculateValidBabylonItems(ServerPlayer player, boolean shouldDelete) {
        return calculateValidBabylonItems(player, shouldDelete, (itemStack -> true));
    }

    /**
     * 获取玩家身上以及末影箱的所有物品
     */
    public static ArrayList<ItemStack> calculateValidBabylonItems(ServerPlayer player, boolean shouldDelete, Predicate<ItemStack> predicate) {
        ArrayList<ItemStack> validBabylonItems = new ArrayList<>();
        player.getInventory().items.forEach(itemStack -> {

            //包括背包，潜影贝等
            boolean isItemHandler = itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent();
            if (isItemHandler) {
                itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
                    for (int i = 0; i < iItemHandler.getSlots(); i++) {
                        ItemStack inSideItem = iItemHandler.getStackInSlot(i);
                        if (!inSideItem.isEmpty() && predicate.test(inSideItem)) {
                            validBabylonItems.add(inSideItem.copy());
                            if(shouldDelete){
                                inSideItem.setCount(0);
                            }
                        }
                    }
                });
            } else {
                if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                    validBabylonItems.add(itemStack.copy());
                    if(shouldDelete){
                        itemStack.setCount(0);
                    }
                }
            }
        });
        ArrayList<ItemStack> enderChestStacks = new ArrayList<>();
        PlayerEnderChestContainer enderChestContainer = player.getEnderChestInventory();
        for (int i = 0; i < enderChestContainer.getContainerSize(); i++) {
            ItemStack itemStack = enderChestContainer.getItem(i);
            if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                enderChestStacks.add(itemStack.copy());
                if(shouldDelete){
                    itemStack.setCount(0);
                }
            }
        }
        validBabylonItems.addAll(enderChestStacks);
        return validBabylonItems;
    }
}