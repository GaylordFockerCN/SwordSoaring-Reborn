package net.p1nero.ss.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.p1nero.ss.item.VatanseverItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$playStepSound(CallbackInfo ci) {
        if((Entity)(Object)this instanceof LivingEntity livingEntity){
            if (livingEntity.getMainHandItem().getItem() instanceof VatanseverItem) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "spawnSprintParticle", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$spawnSprintParticle(CallbackInfo ci) {
        if((Entity)(Object)this instanceof LivingEntity livingEntity){
            if (livingEntity.getMainHandItem().getItem() instanceof VatanseverItem) {
                ci.cancel();
            }
        }
    }
}