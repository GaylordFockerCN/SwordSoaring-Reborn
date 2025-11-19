package net.p1nero.ss.mixin;

import net.minecraft.client.CameraType;
import net.minecraftforge.client.event.ViewportEvent;
import net.p1nero.ss.client.SwordSoairngCameraManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.events.engine.RenderEngine;

/**
 * 防止被ef相机干扰
 */
@Mixin(value = RenderEngine.class, remap = false)
public class RenderEngineMixin {

    @Inject(method = "correctCamera", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$correctCamera(ViewportEvent.ComputeCameraAngles event, float partialTicks, CallbackInfo ci){
        if(SwordSoairngCameraManager.isZooming()){
            ci.cancel();
        }
    }

    @Inject(method = "setRangedWeaponThirdPerson", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$setRangedWeaponThirdPerson(ViewportEvent.ComputeCameraAngles event, CameraType pov, double partialTicks, CallbackInfo ci){
        if(SwordSoairngCameraManager.isZooming()){
            ci.cancel();
        }
    }

}
