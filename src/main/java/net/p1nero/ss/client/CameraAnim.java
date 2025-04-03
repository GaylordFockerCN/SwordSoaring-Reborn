package net.p1nero.ss.client;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.p1nero.ss.SwordSoaring;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.ClientEngine;

/**
 * 抄ef原版的调视角，改了个方向，注意要取消动画的turning lock才不会被打断
 */
@Mod.EventBusSubscriber(modid = SwordSoaring.MOD_ID, value = Dist.CLIENT)
public class CameraAnim {
    public static final Vec3f DEFAULT_AIMING_CORRECTION = new Vec3f(1.5F, 0.0F, 1.25F);
    private static Vec3f aimingCorrection = DEFAULT_AIMING_CORRECTION;
    private static final int ZOOM_MAX_COUNT = 60;
    private static boolean aiming;
    private static int zoomOutTimer = 0;
    private static int zoomCount;

    public static boolean isZooming() {
        return zoomOutTimer > 0;
    }

    public static void zoomIn(Vec3f aimingCorrection, int timer) {
        aiming = true;
        zoomCount = zoomCount == 0 ? 1 : zoomCount;
        zoomOutTimer = timer;
        CameraAnim.aimingCorrection = aimingCorrection;
    }
    public static void zoomIn(Vec3f aimingCorrection) {
        aiming = true;
        zoomCount = zoomCount == 0 ? 1 : zoomCount;
        zoomOutTimer = 0;
        CameraAnim.aimingCorrection = aimingCorrection;
    }

    public static void zoomOut(){
        aiming = false;
        zoomOutTimer = 0;
    }

    public static void zoomOut(int timer) {
        zoomOutTimer = timer;
    }

    /**
     * 实现过渡
     */
    @SubscribeEvent
    public static void cameraSetupEvent(ViewportEvent.ComputeCameraAngles event) {
        if (zoomCount > 0) {
            setCameraAnimThirdPerson(event, Minecraft.getInstance().options.getCameraType(), event.getPartialTick());
            if(!Minecraft.getInstance().isPaused()) {
                zoomCount = aiming ? zoomCount + 1 : zoomCount - 1;
                zoomCount = Math.min(ZOOM_MAX_COUNT, zoomCount);
                zoomOutTimer--;
                if(zoomOutTimer < 0){
                    aiming = false;
                }
            }
        }
    }

    private static void setCameraAnimThirdPerson(ViewportEvent.ComputeCameraAngles event, CameraType pov, double partialTicks) {
        if (ClientEngine.getInstance().getPlayerPatch() == null || Minecraft.getInstance().level == null) {
            return;
        }

        Camera camera = event.getCamera();
        Entity entity = Minecraft.getInstance().getCameraEntity();
        if(entity == null){
            return;
        }
        Vec3 vector = camera.getPosition();
        double totalX = vector.x();
        double totalY = vector.y();
        double totalZ = vector.z();

        if (pov == CameraType.THIRD_PERSON_BACK) {
            double posX = vector.x();
            double posY = vector.y();
            double posZ = vector.z();
            double entityPosX = entity.xOld + (entity.getX() - entity.xOld) * partialTicks;
            double entityPosY = entity.yOld + (entity.getY() - entity.yOld) * partialTicks + entity.getEyeHeight();
            double entityPosZ = entity.zOld + (entity.getZ() - entity.zOld) * partialTicks;
            float intpol = (float) zoomCount / (float) ZOOM_MAX_COUNT;
            Vec3f interpolatedCorrection = new Vec3f(aimingCorrection.x * intpol, aimingCorrection.y * intpol, aimingCorrection.z * intpol);
            OpenMatrix4f rotationMatrix = ClientEngine.getInstance().getPlayerPatch().getMatrix((float)partialTicks);
            Vec3f rotateVec = OpenMatrix4f.transform3v(rotationMatrix, interpolatedCorrection, null);
            double d3 = Math.sqrt((rotateVec.x * rotateVec.x) + (rotateVec.y * rotateVec.y) + (rotateVec.z * rotateVec.z));
            double smallest = d3;
            double d00 = posX + rotateVec.x;
            double d11 = posY - rotateVec.y;
            double d22 = posZ + rotateVec.z;

            for (int i = 0; i < 8; ++i) {
                float f = (float) ((i & 1) * 2 - 1);
                float f1 = (float) ((i >> 1 & 1) * 2 - 1);
                float f2 = (float) ((i >> 2 & 1) * 2 - 1);
                f = f * 0.1F;
                f1 = f1 * 0.1F;
                f2 = f2 * 0.1F;
                HitResult raytraceresult = Minecraft.getInstance().level.clip(new ClipContext(new Vec3(entityPosX + f, entityPosY + f1, entityPosZ + f2), new Vec3(d00 + f + f2, d11 + f1, d22 + f2), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

                double d7 = raytraceresult.getLocation().distanceTo(new Vec3(entityPosX, entityPosY, entityPosZ));
                if (d7 < smallest) {
                    smallest = d7;
                }
            }

            float dist = d3 == 0 ? 0 : (float) (smallest / d3);
            totalX += rotateVec.x * dist;
            totalY -= rotateVec.y * dist;
            totalZ += rotateVec.z * dist;
        }

        BlockPos cameraPos= new BlockPos(totalX, totalY, totalZ);
        //防止视角卡墙里
        if(Minecraft.getInstance().level.getBlockState(cameraPos).is(Blocks.AIR)){
            camera.setPosition(totalX, totalY, totalZ);
        }
    }
}