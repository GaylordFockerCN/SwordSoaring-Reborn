package net.p1nero.ss.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;

/**
 * copy from org.joml.Quaternionf
 */
public class MathUtils {

    public static OpenMatrix4f removeScale(OpenMatrix4f src) {
        float xScale = new Vec3f(src.m00, src.m01, src.m02).length();
        float yScale = new Vec3f(src.m10, src.m11, src.m12).length();
        float zScale = new Vec3f(src.m20, src.m21, src.m22).length();

        OpenMatrix4f copy = new OpenMatrix4f(src);
        copy.scale(1 / xScale, 1 / yScale, 1 / zScale);

        return copy;
    }

    public static void mulPoseStack(PoseStack poseStack, OpenMatrix4f pose) {
        OpenMatrix4f transposed = pose.transpose(null);
        yesman.epicfight.api.utils.math.MathUtils.translateStack(poseStack, pose);
        yesman.epicfight.api.utils.math.MathUtils.rotateStack(poseStack, transposed);
        yesman.epicfight.api.utils.math.MathUtils.scaleStack(poseStack, transposed);
    }
    public static Quaternion rotateTo(Vec3 from, Vec3 to){
        return rotateTo(Vec3f.fromDoubleVector(from), Vec3f.fromDoubleVector(to));
    }

    public static Quaternion rotateTo(Vec3f from, Vec3f to){
        return rotateTo(from.x, from.y, from.z, to.x, to.y, to.z);
    }

    public static Quaternion rotateTo(float fromDirX, float fromDirY, float fromDirZ, float toDirX, float toDirY, float toDirZ) {
        Quaternion self = new Quaternion(0, 0, 0, 1);
        return rotateTo(fromDirX, fromDirY, fromDirZ, toDirX, toDirY, toDirZ, self, self);
    }

    public static Quaternion rotateTo(float fromDirX, float fromDirY, float fromDirZ, float toDirX, float toDirY, float toDirZ, Quaternion from, Quaternion dest) {
        float fn = invsqrt(Math.fma(fromDirX, fromDirX, Math.fma(fromDirY, fromDirY, fromDirZ * fromDirZ)));
        float tn = invsqrt(Math.fma(toDirX, toDirX, Math.fma(toDirY, toDirY, toDirZ * toDirZ)));
        float fx = fromDirX * fn;
        float fy = fromDirY * fn;
        float fz = fromDirZ * fn;
        float tx = toDirX * tn;
        float ty = toDirY * tn;
        float tz = toDirZ * tn;
        float dot = fx * tx + fy * ty + fz * tz;
        float x;
        float y;
        float z;
        float r;
        if (dot < -1F) {
            x = fy;
            y = -fx;
            z = 0.0F;
            r = 0.0F;
            if (fy * fy + y * y == 0.0F) {
                x = 0.0F;
                y = fz;
                z = -fy;
                r = 0.0F;
            }
        } else {
            float sd2 = (float) Math.sqrt((1.0F + dot) * 2.0F);
            float isd2 = 1.0F / sd2;
            float cx = fy * tz - fz * ty;
            float cy = fz * tx - fx * tz;
            float cz = fx * ty - fy * tx;
            x = cx * isd2;
            y = cy * isd2;
            z = cz * isd2;
            r = sd2 * 0.5F;
            float n2 = invsqrt(Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, r * r))));
            x *= n2;
            y *= n2;
            z *= n2;
            r *= n2;
        }

        dest.set(Math.fma(from.r, x, Math.fma(from.i, r, Math.fma(from.j, z, -from.k * y))), Math.fma(from.r, y, Math.fma(-from.i, z, Math.fma(from.j, r, from.k * x))), Math.fma(from.r, z, Math.fma(from.i, y, Math.fma(-from.j, x, from.k * r))), Math.fma(from.r, r, Math.fma(-from.i, x, Math.fma(-from.j, y, -from.k * z))));
        return dest;
    }

    public static float invsqrt(float r) {
        return 1.0F / (float)java.lang.Math.sqrt(r);
    }
}