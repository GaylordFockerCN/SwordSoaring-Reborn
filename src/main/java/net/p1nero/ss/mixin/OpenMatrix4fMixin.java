package net.p1nero.ss.mixin;

import com.mojang.math.Quaternion;
import net.p1nero.ss.util.MathUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;

/**
 * 修复缩放导致的旋转bug， 迁移1.20的时候记得删
 */
@Mixin(value = OpenMatrix4f.class)
public class OpenMatrix4fMixin {
    @Inject(method = "toQuaternion(Lyesman/epicfight/api/utils/math/OpenMatrix4f;)Lcom/mojang/math/Quaternion;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void sword_soaring$toQuaternion(OpenMatrix4f matrix, CallbackInfoReturnable<Quaternion> cir){
        OpenMatrix4f newMatrix = new OpenMatrix4f(matrix);
        newMatrix = MathUtils.removeScale(newMatrix);
        float diagonal = newMatrix.m00 + newMatrix.m11 + newMatrix.m22;
        float w;
        float x;
        float y;
        float z;
        float y4;
        if (diagonal > 0.0F) {
            y4 = (float)(Math.sqrt(diagonal + 1.0F) * 2.0);
            w = y4 / 4.0F;
            x = (newMatrix.m21 - newMatrix.m12) / y4;
            y = (newMatrix.m02 - newMatrix.m20) / y4;
            z = (newMatrix.m10 - newMatrix.m01) / y4;
        } else if (newMatrix.m00 > newMatrix.m11 && newMatrix.m00 > newMatrix.m22) {
            y4 = (float)(Math.sqrt(1.0F + newMatrix.m00 - newMatrix.m11 - newMatrix.m22) * 2.0);
            w = (newMatrix.m21 - newMatrix.m12) / y4;
            x = y4 / 4.0F;
            y = (newMatrix.m01 + newMatrix.m10) / y4;
            z = (newMatrix.m02 + newMatrix.m20) / y4;
        } else if (newMatrix.m11 > newMatrix.m22) {
            y4 = (float)(Math.sqrt(1.0F + newMatrix.m11 - newMatrix.m00 - newMatrix.m22) * 2.0);
            w = (newMatrix.m02 - newMatrix.m20) / y4;
            x = (newMatrix.m01 + newMatrix.m10) / y4;
            y = y4 / 4.0F;
            z = (newMatrix.m12 + newMatrix.m21) / y4;
        } else {
            y4 = (float)(Math.sqrt(1.0F + newMatrix.m22 - newMatrix.m00 - newMatrix.m11) * 2.0);
            w = (newMatrix.m10 - newMatrix.m01) / y4;
            x = (newMatrix.m02 + newMatrix.m20) / y4;
            y = (newMatrix.m12 + newMatrix.m21) / y4;
            z = y4 / 4.0F;
        }

        cir.setReturnValue(new Quaternion(x, y, z, w));
    }
}