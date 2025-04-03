package net.p1nero.ss.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonEntity;
import net.p1nero.ss.entity.sword.gate_of_babylon.BabylonPatch;
import net.p1nero.ss.entity.sword.sword_convergence.SwordConvergenceEntity;
import net.p1nero.ss.entity.vatansever.VatanseverArmature;
import net.p1nero.ss.entity.vatansever.VatanseverEntityPatch;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.client.particle.TrailParticle;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = TrailParticle.class)
public class TrailParticleMixin {
    @Shadow(remap = false) @Final private LivingEntityPatch<?> entitypatch;

    @Shadow(remap = false) @Final private Joint joint;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$render(VertexConsumer vertexConsumer, Camera camera, float partialTick, CallbackInfo ci){
        if(this.entitypatch instanceof VatanseverEntityPatch vatanseverEntityPatch && vatanseverEntityPatch.getArmature() instanceof VatanseverArmature vatanseverArmature){
            for(Joint joint : vatanseverArmature.getInvalidJoints(vatanseverEntityPatch)){
                if(joint.getId() == this.joint.getId()){
                    ci.cancel();
                }
            }
        }
        if(this.entitypatch.getOriginal() instanceof BabylonEntity babylonEntity){
            if(!babylonEntity.hasJoint(joint)){
                ci.cancel();
            }
        }
        if(this.entitypatch.getOriginal() instanceof SwordConvergenceEntity swordConvergenceEntity){
            if(!swordConvergenceEntity.hasJoint(joint)){
                ci.cancel();
            }
        }
    }
}