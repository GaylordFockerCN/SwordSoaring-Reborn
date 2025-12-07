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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.api.client.event.types.BuildCameraTransform;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillDataManager;

/**
 * 向当前相机的位置的偏移
 */
@EventBusSubscriber(modid = SwordSoaringMod.MOD_ID)
public class SwordSoairngCameraManager {
    public static final Vec3f DEFAULT_AIMING_CORRECTION = new Vec3f(1.5F, 0.0F, 1.25F);
    private static final double DEFAULT_CAMERA_ZOOM = 6;
    private static Vec3f aimingCorrection = DEFAULT_AIMING_CORRECTION;
    private static final int MAX_ZOOM_TICK = 60;
    private static boolean zooming;
    private static int zoomOutTimer = 0;
    private static int zoomTick;

    public static boolean isZooming() {
        return zoomTick <= 0;
    }

    public static boolean shouldAlignPlayerLookToCamera(LocalPlayerPatch localPlayerPatch) {
        SkillDataManager manager = localPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_SOARING).getDataManager();
        return isZooming() || (manager.hasData(SwordSoaringDatakeys.FLYING) && manager.getDataValue(SwordSoaringDatakeys.FLYING));
    }

    public static void zoomIn(Vec3f aimingCorrection, int timer) {
        zooming = true;
        zoomTick = zoomTick == 0 ? 1 : zoomTick;
        zoomOutTimer = timer;
        SwordSoairngCameraManager.aimingCorrection = aimingCorrection;
        EpicFightCameraAPI.getInstance().setCouplingState(true);
    }

    public static void zoomIn(Vec3f aimingCorrection) {
        zoomIn(aimingCorrection, 0);
    }

    public static void zoomOut() {
        zooming = false;
        zoomOutTimer = -1;
        EpicFightCameraAPI.getInstance().setCouplingState(false);
    }

    public static void zoomOut(int timer) {
        zoomOutTimer = timer;
    }

    public static void tick() {
        if (zoomTick > 0) {
            if (!Minecraft.getInstance().isPaused()) {
                zoomTick = zooming ? zoomTick + 1 : zoomTick - 1;
                zoomTick = Math.min(MAX_ZOOM_TICK, zoomTick);
                zoomOutTimer--;
                if (zoomOutTimer < 0) {
                    zoomOut();
                }
            }
        }
    }

    public static void onEpicFightCameraSetupEnd(BuildCameraTransform.Post event) {
        if (zoomTick > 0 && EpicFightCameraAPI.getInstance().isTPSMode()) {
            setCameraAnimThirdPerson(event.getCameraApi().getCameraYRotO(), event.getCameraApi().getCameraYRot(), event.getCamera(), Minecraft.getInstance().options.getCameraType(), event.getPartialTick());
        }
    }

    @SubscribeEvent
    public static void onVanillaCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        if (zoomTick > 0 && !EpicFightCameraAPI.getInstance().isTPSMode()) {
            setCameraAnimThirdPerson(event.getYaw(), event.getYaw(), event.getCamera(), Minecraft.getInstance().options.getCameraType(), (float) event.getPartialTick());
        }
    }

    private static void setCameraAnimThirdPerson(float cameraYRotO, float cameraYRot, Camera camera, CameraType pov, float partialTick) {
        Entity entity = Minecraft.getInstance().getCameraEntity();
        if (entity == null) {
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
            double entityPosX = entity.xOld + (entity.getX() - entity.xOld) * partialTick;
            double entityPosY = entity.yOld + (entity.getY() - entity.yOld) * partialTick + entity.getEyeHeight();
            double entityPosZ = entity.zOld + (entity.getZ() - entity.zOld) * partialTick;
            float intpol = (float) zoomTick / (float) MAX_ZOOM_TICK;
            Vec3f interpolatedCorrection = new Vec3f(aimingCorrection.x * intpol, aimingCorrection.y * intpol, aimingCorrection.z * intpol);

            OpenMatrix4f rotationMatrix = MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, cameraYRotO, cameraYRot, partialTick, 0.9375F, 0.9375F, 0.9375F);
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

        BlockPos cameraPos = new BlockPos((int) totalX, (int) totalY, (int) totalZ);
        //防止视角卡墙里
        if (Minecraft.getInstance().level.getBlockState(cameraPos).is(Blocks.AIR)) {
            camera.setPosition(totalX, totalY, totalZ);
        }
    }

}
