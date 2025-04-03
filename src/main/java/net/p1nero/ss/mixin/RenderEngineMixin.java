package net.p1nero.ss.mixin;

import net.minecraft.client.CameraType;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.ViewportEvent;
import net.p1nero.ss.client.CameraAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.client.events.engine.RenderEngine;

/**
 * 防止被ef相机干扰
 */
@Mixin(value = RenderEngine.class, remap = false)
public class RenderEngineMixin {
    @Inject(method = "unlockRotation", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$unlockRotation(Entity cameraEntity, CallbackInfo ci){
        if(CameraAnim.isZooming()){
            ci.cancel();
        }
    }

    @Inject(method = "correctCamera", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$correctCamera(ViewportEvent.ComputeCameraAngles event, float partialTicks, CallbackInfo ci){
        if(CameraAnim.isZooming()){
            ci.cancel();
        }
    }

    @Inject(method = "rotateCameraByMouseInput", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$rotateCameraByMouseInput(float dx, float dy, CallbackInfo ci){
        if(CameraAnim.isZooming()){
            ci.cancel();
        }
    }

    @Inject(method = "setRangedWeaponThirdPerson", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$setRangedWeaponThirdPerson(ViewportEvent.ComputeCameraAngles event, CameraType pov, double partialTicks, CallbackInfo ci){
        if(CameraAnim.isZooming()){
            ci.cancel();
        }
    }

    @Inject(method = "isPlayerRotationLocked", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$isPlayerRotationLocked(CallbackInfoReturnable<Boolean> cir){
        if(CameraAnim.isZooming()){
            cir.setReturnValue(false);
        }
    }

}
