package net.p1nero.ss.util.vfx;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.p1nero.ss.util.AnimationUtils;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.*;

public class ParticleVFX {

    public static void createHexagramParticle(ParticleOptions particleOptions, Level level, Vec3 center, float interval, float radius, float yRot) {
        createHexagramParticle(particleOptions, level, center, interval, radius, yRot, 0, 0.01F, 0);
    }

    public static void createHexagramParticle(ParticleOptions particleOptions, Level level, Vec3 center, float interval, float radius, float yRot, float xSpeed, float ySpeed, float zSpeed) {
        for (Vec3 pos : generateHexagram(center, radius, interval, yRot)) {
            level.addParticle(particleOptions, pos.x, pos.y, pos.z, xSpeed, ySpeed, zSpeed);
        }
    }

    public static List<Vec3> generateHexagram(Vec3 center, double radius,
                                              double interval, double yRot) {
        double rad = Math.toRadians(yRot);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        // 生成基础顶点（带顺时针旋转）
        List<Vec3> vertices = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            double dx = radius * Math.cos(angle);
            double dz = radius * Math.sin(angle);

            // 修正后的旋转矩阵（顺时针）
            double rx = dx * cos - dz * sin;
            double rz = dx * sin + dz * cos;

            vertices.add(new Vec3(
                    center.x + rx,
                    center.y,
                    center.z + rz
            ));
        }

        // 连接关系保持不变
        int[][] connections = {{0, 2}, {2, 4}, {4, 0}, {1, 3}, {3, 5}, {5, 1}};

        // 路径采样（带动态间隔）
        Set<Vec3> path = new LinkedHashSet<>();
        for (int[] edge : connections) {
            Vec3 start = vertices.get(edge[0]);
            Vec3 end = vertices.get(edge[1]);

            double dx = end.x - start.x;
            double dz = end.z - start.z;
            double length = Math.hypot(dx, dz);

            int steps = (int) Math.ceil(length / interval);
            for (int i = 0; i <= steps; i++) {
                double ratio = i / (double) steps;
                path.add(new Vec3(
                        start.x + dx * ratio,
                        center.y,
                        start.z + dz * ratio
                ));
            }
        }
        return new ArrayList<>(path);
    }

    public static void createBigDipperXYParticle(ParticleOptions particleOptions, Level level, Vec3 center, float interval, float radius, float yRot, float zRot) {
        createBigDipperXYParticle(particleOptions, level, center, interval, radius, yRot, zRot, 0, 0, 0);
    }

    public static void createBigDipperXYParticle(ParticleOptions particleOptions, Level level, Vec3 center, float interval, float radius, float yRot, float zRot, float xSpeed, float ySpeed, float zSpeed) {
        for (Vec3 pos : generateBigDipperXY(center, radius, interval, yRot, zRot)) {
            level.addParticle(particleOptions, pos.x, pos.y, pos.z, xSpeed, ySpeed, zSpeed);
        }
    }

    public static void createBigDipperXZParticle(ParticleOptions particleOptions, Level level, Vec3 center, float interval, float radius, float yRot) {
        createBigDipperXZParticle(particleOptions, level, center, interval, radius, yRot, 0, 0, 0);
    }

    public static void createBigDipperXZParticle(ParticleOptions particleOptions, Level level, Vec3 center, float interval, float radius, float yRot, float xSpeed, float ySpeed, float zSpeed) {
        for (Vec3 pos : generateBigDipperXZ(center, radius, interval, yRot)) {
            level.addParticle(particleOptions, pos.x, pos.y, pos.z, xSpeed, ySpeed, zSpeed);
        }
    }

    // 北斗七星标准连接关系（索引对应星点）
    private static final int[][] CONNECTIONS = {
            {0, 1}, {1, 2}, {2, 3},
            {3, 4}, {4, 5}, {5, 6}
    };

    /**
     * 生成北斗七星路径坐标
     *
     * @param center   中心点坐标
     * @param scale    整体缩放比例
     * @param interval 采样间隔
     */
    public static List<Vec3> generateBigDipperXZ(Vec3 center, double scale, double interval, double yRot) {
        scale *= 0.5;
        // 基础坐标定义保持不变
        Vec3[] basePoints = {
                new Vec3(-4.5, 0, -2),   // 天枢
                new Vec3(-5, 0, 1.0),   // 天璇
                new Vec3(-2, 0, 3.0),    // 天玑
                new Vec3(0.0, 0, 0),    // 天权
                new Vec3(2, 0, 0.5),    // 玉衡
                new Vec3(3.5, 0, 1.0),    // 开阳
                new Vec3(6.0, 0, 0)   // 摇光
        };

        // 转换为弧度
        double radian = Math.toRadians(yRot);
        double cos = Math.cos(radian);
        double sin = Math.sin(radian);

        List<Vec3> vertices = new ArrayList<>();
        for (Vec3 p : basePoints) {
            // 先缩放
            double scaledX = p.x * scale;
            double scaledZ = p.z * scale;

            // 应用旋转变换 (绕Y轴)
            double rotatedX = scaledX * cos - scaledZ * sin;
            double rotatedZ = scaledX * sin + scaledZ * cos;

            // 平移到中心点
            double x = center.x + rotatedX;
            double z = center.z + rotatedZ;
            vertices.add(new Vec3(x, center.y, z));
        }

        if (interval == -1) {
            return new ArrayList<>(vertices);
        }

        // 生成路径点
        Set<Vec3> path = new LinkedHashSet<>();
        for (int[] conn : CONNECTIONS) {
            Vec3 start = vertices.get(conn[0]);
            Vec3 end = vertices.get(conn[1]);

            double dx = end.x - start.x;
            double dz = end.z - start.z;
            double length = Math.sqrt(dx * dx + dz * dz);

            int steps = (int) Math.ceil(length / interval);
            for (int i = 0; i <= steps; i++) {
                double t = (double) i / steps;
                double x = start.x + dx * t;
                double z = start.z + dz * t;
                path.add(new Vec3(x, center.y, z));
            }
        }
        return new ArrayList<>(path);
    }

    public static List<Vec3> generateBigDipperXY(Vec3 center, double scale, double interval, double yRot, double zRot) {
        scale *= 0.5;
        // 基础坐标定义（XY平面，Z=0）
        Vec3[] basePoints = {
                new Vec3(-4.5, -2, 0),   // 天枢
                new Vec3(-5, 1.0, 0),   // 天璇
                new Vec3(-2, 3.0, 0),    // 天玑
                new Vec3(0.0, 0, 0),    // 天权
                new Vec3(2, 0.5, 0),    // 玉衡
                new Vec3(3.5, 1.0, 0),    // 开阳
                new Vec3(6.0, 0, 0)   // 摇光
        };

        // 计算旋转参数（绕Y轴）
        double rad = Math.toRadians(yRot);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double zRad = Math.toRadians(zRot);
        double zCos = Math.cos(zRad);
        double zSin = Math.sin(zRad);

        // 生成旋转后的顶点
        List<Vec3> vertices = new ArrayList<>();
        for (Vec3 p : basePoints) {
            // 缩放原始坐标
            double scaledX = p.x * scale;
            double scaledY = p.y * scale;
            //xy上自转
            double zRotatedX = scaledX * zCos - scaledY * zSin;
            double zRotatedY = scaledX * zSin + scaledY * zCos;
            // 应用绕Y轴旋转（XY平面 → XZ平面）
            double rotatedX = zRotatedX * cos;
            double rotatedZ = zRotatedX * sin;

            // 平移到中心点
            vertices.add(new Vec3(
                    center.x + rotatedX,
                    center.y + zRotatedY,  // Y轴直接叠加
                    center.z + rotatedZ
            ));
        }

        if (interval == -1) {
            return new ArrayList<>(vertices);
        }

        // 生成路径点（带间隔采样）
        Set<Vec3> path = new LinkedHashSet<>();
        for (int[] conn : CONNECTIONS) {
            Vec3 start = vertices.get(conn[0]);
            Vec3 end = vertices.get(conn[1]);

            // 计算线段向量
            double dx = end.x - start.x;
            double dz = end.z - start.z;
            double dy = end.y - start.y;
            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // 动态步长采样
            int steps = (int) Math.ceil(length / interval);
            for (int i = 0; i <= steps; i++) {
                double t = (double) i / steps;
                path.add(new Vec3(
                        start.x + dx * t,
                        start.y + dy * t,
                        start.z + dz * t
                ));
            }
        }
        return new ArrayList<>(path);
    }

    public static void createLineBetweenJoint(LivingEntityPatch<?> entityPatch, Joint joint1, Joint joint2, ParticleOptions particleOptions, int count) {
        Vec3 pos1 = AnimationUtils.getJointWorldPos(entityPatch, joint1);
        Vec3 pos2 = AnimationUtils.getJointWorldPos(entityPatch, joint2);
        LivingEntity livingEntity = entityPatch.getOriginal();
        createLineSegmentParticles(livingEntity.level, pos1, pos2, particleOptions, count, livingEntity.getDeltaMovement());
    }


    public static void createRandomLine(Level level, Vec3 center, ParticleOptions particleOptions, double minSpeed, double maxSpeed, int particleCount) {
        RandomSource random1 = level.random;

        for (int i = 0; i < particleCount; i++) {
            double t = (double) i / (particleCount - 1);
            double distance = t * 5.0;

            double angle = random1.nextDouble() * 2 * Math.PI;
            double pitch = random1.nextDouble() * Math.PI - Math.PI / 2;

            double offsetX = Math.cos(angle) * Math.cos(pitch);
            double offsetY = Math.sin(pitch);
            double offsetZ = Math.sin(angle) * Math.cos(pitch);

            double x = center.x() + offsetX * distance;
            double y = center.y() + offsetY * distance;
            double z = center.z() + offsetZ * distance;
            double speed = minSpeed + random1.nextDouble() * (maxSpeed - minSpeed);
            level.addParticle(particleOptions,
                    x, y, z,
                    offsetX * speed, offsetY * speed, offsetZ * speed
            );
        }
    }

    public static void createJointRandomLine(LivingEntityPatch<?> entityPatch, Joint joint, ParticleOptions particleOptions, double minSpeed, double maxSpeed, int particleCount) {
        LivingEntity entity = entityPatch.getOriginal();
        Level level = entity.level;
        Vec3 vec3 = AnimationUtils.getJointWorldPos(entityPatch, joint);
        RandomSource random = level.random;
        for (int i = 0; i < particleCount; i++) {
            double t = (double) i / (particleCount - 1);
            double distance = t * 5.0;

            double angle = random.nextDouble() * 2 * Math.PI;
            double pitch = random.nextDouble() * Math.PI - Math.PI / 2;

            double offsetX = Math.cos(angle) * Math.cos(pitch);
            double offsetY = Math.sin(pitch);
            double offsetZ = Math.sin(angle) * Math.cos(pitch);

            double x = vec3.x() + offsetX * distance;
            double y = vec3.y() + offsetY * distance;
            double z = vec3.z() + offsetZ * distance;
            double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);
            if (level.isClientSide) {
                level.addParticle(particleOptions,
                        x + entity.getX(), y + entity.getY(), z + entity.getZ(),
                        offsetX * speed, offsetY * speed, offsetZ * speed);
            }
        }
    }

    public static void createSphereParticles(Level level, Vec3 center, ParticleOptions particleOptions, double radius, double minSpeed, double maxSpeed, int particleCount) {
        RandomSource random = level.random;
        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double pitch = Math.acos(2 * random.nextDouble() - 1) - Math.PI / 2;

            double offsetX = Math.cos(angle) * Math.cos(pitch);
            double offsetY = Math.sin(pitch);
            double offsetZ = Math.sin(angle) * Math.cos(pitch);

            double x = center.x() + offsetX * radius;
            double y = center.y() + offsetY * radius;
            double z = center.z() + offsetZ * radius;
            double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);

            level.addParticle(particleOptions, x, y, z, offsetX * speed, offsetY * speed, offsetZ * speed);
        }
    }

    public static void createJointSphereParticles(LivingEntityPatch<?> entityPatch, Joint joint, ParticleOptions particleOptions, double radius, double minSpeed, double maxSpeed, int particleCount) {
        LivingEntity entity = entityPatch.getOriginal();
        Level level = entity.level;
        Vec3 vec3 = AnimationUtils.getJointWorldPos(entityPatch, joint);
        RandomSource random = level.random;
        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double pitch = Math.acos(2 * random.nextDouble() - 1) - Math.PI / 2;

            double offsetX = Math.cos(angle) * Math.cos(pitch);
            double offsetY = Math.sin(pitch);
            double offsetZ = Math.sin(angle) * Math.cos(pitch);

            double x = vec3.x() + offsetX * radius;
            double y = vec3.y() + offsetY * radius;
            double z = vec3.z() + offsetZ * radius;
            double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);
            if (level.isClientSide) {
                level.addParticle(particleOptions, x + entity.getX(), y + entity.getY(), z + entity.getZ(), offsetX * speed, offsetY * speed, offsetZ * speed);
            }
        }
    }

    public static void createRingParticles(Level level, Vec3 center, ParticleOptions particleOptions, double radius, double minSpeed, double maxSpeed, int particleCount) {
        RandomSource random = level.random;
        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;

            double x = center.x() + Math.cos(angle) * radius;
            double y = center.y();
            double z = center.z() + Math.sin(angle) * radius;


            double speedX = -Math.sin(angle);
            double speedZ = Math.cos(angle);
            double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);

            level.addParticle(particleOptions, x, y, z, speedX * speed, 0, speedZ * speed);
        }
    }

    public static void createJointRingParticles(LivingEntityPatch<?> entityPatch, Joint joint, ParticleOptions particleOptions, double radius, double minSpeed, double maxSpeed, int particleCount) {
        LivingEntity entity = entityPatch.getOriginal();
        Level level = entity.level;
        Vec3 vec3 = AnimationUtils.getJointWorldPos(entityPatch, joint);
        RandomSource random = level.random;
        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;

            double x = vec3.x() + Math.cos(angle) * radius;
            double y = vec3.y();
            double z = vec3.z() + Math.sin(angle) * radius;


            double speedX = -Math.sin(angle);
            double speedZ = Math.cos(angle);
            double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);

            level.addParticle(particleOptions, x + entity.getX(), y + entity.getY(), z + entity.getZ(), speedX * speed, 0, speedZ * speed);
        }
    }

    public static void createRandomInSphereParticles(Level level, Vec3 center, ParticleOptions particleOptions, double radius, double minSpeed, double maxSpeed, int particleCount) {
        RandomSource random = level.random;
        for (int i = 0; i < particleCount; i++) {

            double r = radius * Math.cbrt(random.nextDouble());
            double angle = random.nextDouble() * 2 * Math.PI;
            double pitch = Math.acos(2 * random.nextDouble() - 1) - Math.PI / 2;

            double x = center.x() + r * Math.cos(angle) * Math.cos(pitch);
            double y = center.y() + r * Math.sin(pitch);
            double z = center.z() + r * Math.sin(angle) * Math.cos(pitch);
            double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);

            double dirAngle = random.nextDouble() * 2 * Math.PI;
            double dirPitch = random.nextDouble() * Math.PI - Math.PI / 2;
            double dirX = Math.cos(dirAngle) * Math.cos(dirPitch);
            double dirY = Math.sin(dirPitch);
            double dirZ = Math.sin(dirAngle) * Math.cos(dirPitch);

            level.addParticle(particleOptions, x, y, z, dirX * speed, dirY * speed, dirZ * speed);
        }
    }

    public static void createJointRandomInSphereParticles(LivingEntityPatch<?> entityPatch, Joint joint, ParticleOptions particleOptions, double radius, double minSpeed, double maxSpeed, int particleCount) {
        LivingEntity entity = entityPatch.getOriginal();
        Level level = entity.level;
        Vec3 vec3 = AnimationUtils.getJointWorldPos(entityPatch, joint);
        RandomSource random = level.random;
        for (int i = 0; i < particleCount; i++) {

            double r = radius * Math.cbrt(random.nextDouble());
            double angle = random.nextDouble() * 2 * Math.PI;
            double pitch = Math.acos(2 * random.nextDouble() - 1) - Math.PI / 2;

            double x = vec3.x() + r * Math.cos(angle) * Math.cos(pitch);
            double y = vec3.y() + r * Math.sin(pitch);
            double z = vec3.z() + r * Math.sin(angle) * Math.cos(pitch);
            double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);

            double dirAngle = random.nextDouble() * 2 * Math.PI;
            double dirPitch = random.nextDouble() * Math.PI - Math.PI / 2;
            double dirX = Math.cos(dirAngle) * Math.cos(dirPitch);
            double dirY = Math.sin(dirPitch);
            double dirZ = Math.sin(dirAngle) * Math.cos(dirPitch);
            if (level.isClientSide) {
                level.addParticle(particleOptions, x + entity.getX(), y + entity.getY(), z + entity.getZ(), dirX * speed, dirY * speed, dirZ * speed);
            }
        }
    }

    public static void createLineSegmentParticles(Level level, Vec3 start, Vec3 end, ParticleOptions particleOptions, int particleCount, Vec3 deltaMovement) {
        Vec3 direction = end.subtract(start);
        for (int i = 0; i < particleCount; i++) {
            double t = (double) i / (particleCount - 1);
            Vec3 pos = start.add(direction.scale(t));
            level.addParticle(particleOptions, pos.x, pos.y, pos.z, deltaMovement.x, deltaMovement.y, deltaMovement.z);
        }
    }

    public static void createLineSegmentParticles(Level level, Vec3 start, Vec3 end, ParticleOptions particleOptions, double minSpeed, double maxSpeed, int particleCount) {
        RandomSource random = level.random;
        Vec3 direction = end.subtract(start);
        for (int i = 0; i < particleCount; i++) {
            double t = (double) i / (particleCount - 1);
            Vec3 pos = start.add(direction.scale(t));

            double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);
            double dirX = direction.x / particleCount;
            double dirY = direction.y / particleCount;
            double dirZ = direction.z / particleCount;

            level.addParticle(particleOptions, pos.x, pos.y, pos.z, dirX * speed, dirY * speed, dirZ * speed);
        }
    }

    public static void createMovingParticles(Level level, Vec3 start, Vec3 end, ParticleOptions particleOptions, double minSpeed, double maxSpeed, int particleCount) {
        RandomSource random = level.random;
        Vec3 direction = end.subtract(start).normalize();
        for (int i = 0; i < particleCount; i++) {
            double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);
            level.addParticle(particleOptions,
                    start.x, start.y, start.z,
                    direction.x * speed,
                    direction.y * speed,
                    direction.z * speed
            );
        }
    }
}