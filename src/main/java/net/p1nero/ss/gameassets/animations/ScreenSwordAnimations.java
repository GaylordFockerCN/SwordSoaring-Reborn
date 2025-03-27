package net.p1nero.ss.gameassets.animations;

import com.p1nero.invincible.api.animation.StaticAnimationProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.p1nero.ss.animation.ArtifactSpiritMultiPhaseAttackAnimation;
import net.p1nero.ss.entity.sword.screen_sword.ScreenSwordArmature;
import net.p1nero.ss.gameassets.SwordSoaringArmatures;
import net.p1nero.ss.util.AnimationUtils;
import net.p1nero.ss.util.vfx.ParticleVFX;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static net.p1nero.ss.util.AnimationUtils.*;

public class ScreenSwordAnimations {
    public static StaticAnimation SCREEN_SWORD_IDLE;
    public static StaticAnimation KILL_AURA_1;
    public static StaticAnimation KILL_AURA_1_SUMMON;
    public static StaticAnimation KILL_AURA_2;
    public static StaticAnimation KILL_AURA_2_SUMMON;
    public static StaticAnimation SCREEN_SWORD;
    public static StaticAnimation SCREEN_SWORD_SUMMON;
    public static StaticAnimation PLAYER_SUMMON_RAIN_SWORD;
    public static StaticAnimation PLAYER_SUMMON_SCREEN_SWORD;
    public static StaticAnimation PLAYER_SUMMON_KILL_AURA_1;
    public static StaticAnimation PLAYER_SUMMON_KILL_AURA_2;

    public static AnimationEvent.TimeStampedEvent RESET_ANIM = AnimationEvent.TimeStampedEvent.create(0.49F, ((livingEntityPatch, staticAnimation, objects) -> {
        livingEntityPatch.reserveAnimation(staticAnimation);
    }), AnimationEvent.Side.SERVER);

    public static AnimationEvent.TimeStampedEvent spawnSummonParticle(float time, Supplier<ParticleOptions> particleOptionsSupplier1, Supplier<ParticleOptions> particleOptionsSupplier2) {
        return AnimationEvent.TimeStampedEvent.create(time, (entityPatch, self, params) -> {
            OpenMatrix4f transformMatrix = entityPatch.getArmature().getBindedTransformFor(entityPatch.getArmature().getPose(0.0F), entityPatch.getArmature() instanceof HumanoidArmature ? Armatures.BIPED.toolR : Armatures.BIPED.rootJoint);
            transformMatrix.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul((new OpenMatrix4f()).rotate(-((float) Math.toRadians(entityPatch.getOriginal().yBodyRotO + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F)), transformMatrix, transformMatrix);
            int n = 70;
            double r = 0.1;
            for (int i = 0; i < n; ++i) {
                double theta = 6.28 * (new Random()).nextDouble();
                double phi = Math.acos(2.0 * (new Random()).nextDouble() - 1.0);
                double x = r * Math.sin(phi) * Math.cos(theta);
                double y = r * Math.sin(phi) * Math.sin(theta);
                double z = r * Math.cos(phi);
                entityPatch.getOriginal().level.addParticle(particleOptionsSupplier1.get(), (double) transformMatrix.m30 + entityPatch.getOriginal().getX(), (double) transformMatrix.m31 + entityPatch.getOriginal().getY(), (double) transformMatrix.m32 + entityPatch.getOriginal().getZ(), (float) x, (float) y, (float) z);
                if (i % 2 == 0) {
                    entityPatch.getOriginal().level.addParticle(particleOptionsSupplier2.get(), (double) transformMatrix.m30 + entityPatch.getOriginal().getX(), (double) transformMatrix.m31 + entityPatch.getOriginal().getY(), (double) transformMatrix.m32 + entityPatch.getOriginal().getZ(), (float) x, (float) y, (float) z);
                }
            }

        }, AnimationEvent.Side.CLIENT);
    }

    public static AnimationEvent.TimeStampedEvent spawnParticles(float time, List<Joint> joints, int count, Supplier<ParticleOptions> particleOptions) {
        return AnimationEvent.TimeStampedEvent.create(time, ((livingEntityPatch, staticAnimation, objects) -> {
            for (Joint joint : joints) {
                Vec3 worldPos = AnimationUtils.getJointWorldPos(livingEntityPatch, joint);
                for (int i = 0; i < count; i++) {
                    ((ServerLevel) livingEntityPatch.getOriginal().level).sendParticles(particleOptions.get(), worldPos.x, worldPos.y, worldPos.z, count, 0, 0, 0, 0.1);
                }
            }
        }), AnimationEvent.Side.SERVER);
    }

    public static AnimationEvent.TimeStampedEvent spawnFireParticle(float time, List<Joint> joints, int count) {
        return spawnParticles(time, joints, count, () -> ParticleTypes.FLAME);
    }

    public static AnimationEvent.TimeStampedEvent spawnStaticHexagram(float time, Supplier<ParticleOptions> particleOptions, float interval, float radius, float height) {
        return AnimationEvent.TimeStampedEvent.create(time, ((livingEntityPatch, staticAnimation, objects) -> ParticleVFX.createHexagramParticle(particleOptions.get(), livingEntityPatch.getOriginal().level, livingEntityPatch.getOriginal().position().add(0, height, 0), interval, radius, time / staticAnimation.getTotalTime() * 360)), AnimationEvent.Side.CLIENT);
    }

    public static AnimationEvent.TimeStampedEvent spawnStaticHexagram(float time, Supplier<ParticleOptions> particleOptions, float interval, float radius) {
        return spawnStaticHexagram(time, particleOptions, interval, radius, 1.0F);
    }

    /**
     * 六个点乱序的
     */
    public static AnimationEvent.TimeStampedEvent spawnDynamicHexagram(float time, Supplier<ParticleOptions> particleOptions, int count, ScreenSwordArmature armature) {
        return AnimationEvent.TimeStampedEvent.create(time, ((livingEntityPatch, staticAnimation, objects) -> {
            ParticleVFX.createLineBetweenJoint(livingEntityPatch, armature.W4, armature.W3, particleOptions.get(), count);
            ParticleVFX.createLineBetweenJoint(livingEntityPatch, armature.W3, armature.W5, particleOptions.get(), count);
            ParticleVFX.createLineBetweenJoint(livingEntityPatch, armature.W5, armature.W4, particleOptions.get(), count);
            ParticleVFX.createLineBetweenJoint(livingEntityPatch, armature.W1, armature.W2, particleOptions.get(), count);
            ParticleVFX.createLineBetweenJoint(livingEntityPatch, armature.W2, armature.W6, particleOptions.get(), count);
            ParticleVFX.createLineBetweenJoint(livingEntityPatch, armature.W6, armature.W1, particleOptions.get(), count);
        }), AnimationEvent.Side.CLIENT);
    }

    public static AnimationEvent onEndPlay(StaticAnimationProvider provider) {
        return AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.reserveAnimation(provider.get());
        }), AnimationEvent.Side.SERVER);
    }
    public static AnimationEvent.TimeStampedEvent setGlowing(float time){
        return AnimationEvent.TimeStampedEvent.create(time, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.getOriginal().setGlowingTag(true)), AnimationEvent.Side.SERVER);
    }

    public static AnimationEvent.TimeStampedEvent playSound(float time, Supplier<SoundEvent> soundEventSupplier, float minPitch, float maxPitch, float volume){
        return AnimationEvent.TimeStampedEvent.create(time, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(soundEventSupplier.get(), volume, minPitch, maxPitch)), AnimationEvent.Side.SERVER);
    }

    public static AnimationEvent SET_GLOWING = AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.getOriginal().setGlowingTag(true)), AnimationEvent.Side.SERVER);

    public static void buildScreenSwordAnim() {
        ScreenSwordArmature screenSwordArmature = SwordSoaringArmatures.screenSwordArmature;

        SCREEN_SWORD_IDLE = new StaticAnimation(true, "screen_sword/screen_sword_idle", screenSwordArmature)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 2.0F));
        KILL_AURA_1 = new ArtifactSpiritMultiPhaseAttackAnimation(0.001F, "screen_sword/kill_aura_1", screenSwordArmature, AnimationUtils.getPhases(screenSwordArmature.joints, 0.5F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.2F))
                .addEvents(RESET_ANIM,
                        spawnDynamicHexagram(0.05F, () -> ParticleTypes.WAX_ON, 30, screenSwordArmature),
                        playSound(0.05F, ()->SoundEvents.AMETHYST_CLUSTER_STEP, -0.5F, 0.5F, 2.5F),
                        spawnStaticHexagram(0.25F, () -> ParticleTypes.END_ROD, 0.2F, 2, 0),
                        playSound(0.25F, ()->SoundEvents.AMETHYST_CLUSTER_STEP, -0.5F, 0.5F, 2.5F),
                        playSound(0.45F, ()->SoundEvents.AMETHYST_BLOCK_STEP, -0.5F, 0.5F, 2.5F),
                        spawnDynamicHexagram(0.45F, () -> ParticleTypes.WAX_OFF, 30, screenSwordArmature));
        KILL_AURA_1_SUMMON = new ActionAnimation(0.15F, "screen_sword/kill_aura_1_summon", screenSwordArmature)
                .addEvents(spawnDynamicHexagram(0.05F, () -> ParticleTypes.WAX_ON, 30, screenSwordArmature),
                        spawnStaticHexagram(0.25F, () -> ParticleTypes.END_ROD, 0.2F, 4),
                        spawnDynamicHexagram(0.45F, () -> ParticleTypes.WAX_OFF, 30, screenSwordArmature))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, SET_GLOWING)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, onEndPlay(() -> KILL_AURA_1));
        KILL_AURA_2 = new ArtifactSpiritMultiPhaseAttackAnimation(0.001F, "screen_sword/kill_aura_2", screenSwordArmature, AnimationUtils.getPhases(screenSwordArmature.joints, 0.5F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.2F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.create((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(SoundEvents.FIRE_AMBIENT, 0.0F, 0.0F), AnimationEvent.Side.SERVER))
                .addEvents(RESET_ANIM,
                        spawnFireParticle(0.1F, screenSwordArmature.joints, 5),
                        spawnParticles(0.3F, screenSwordArmature.joints, 5, EpicFightParticles.BLOOD::get),
                        spawnSummonParticle(0.49F, () -> ParticleTypes.FLAME, () -> ParticleTypes.LAVA));
        KILL_AURA_2_SUMMON = new ActionAnimation(0.15F, "screen_sword/kill_aura_2_summon", screenSwordArmature)
                .addEvents(spawnParticles(0.3F, screenSwordArmature.joints, 5, EpicFightParticles.BLOOD::get))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, onEndPlay(() -> KILL_AURA_2));
        SCREEN_SWORD = new ActionAnimation(0.001F, "screen_sword/screen_sword", screenSwordArmature)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.1F))
                .addEvents(RESET_ANIM);
        SCREEN_SWORD_SUMMON = new ActionAnimation(0.15F, "screen_sword/screen_sword_summon", screenSwordArmature)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, SET_GLOWING)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, onEndPlay(() -> SCREEN_SWORD));

        HumanoidArmature biped = Armatures.BIPED;
        PLAYER_SUMMON_RAIN_SWORD = new ActionAnimation(0.15F, "screen_sword/rain_sword_summon_player", biped)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.7F, ((livingEntityPatch, staticAnimation, objects) -> {
                    groundSplit(livingEntityPatch, 0, 0, 0, 0, 0, 2, 0);
                }), AnimationEvent.Side.BOTH),
                        spawnSummonParticle(0.5F, () -> ParticleTypes.END_ROD, () -> ParticleTypes.WAX_OFF),
                        spawnSummonParticle(2.3F, () -> ParticleTypes.END_ROD, () -> ParticleTypes.CLOUD));
        PLAYER_SUMMON_SCREEN_SWORD = new ActionAnimation(0.15F, "screen_sword/screen_sword_summon_player", biped)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.7F, ((livingEntityPatch, staticAnimation, objects) -> {
                    groundSplit(livingEntityPatch, 0, 0, 0, 0, 0, 2, 0);
                    ParticleVFX.createSphereParticles(livingEntityPatch.getOriginal().level, livingEntityPatch.getOriginal().getEyePosition(), ParticleTypes.END_ROD, 5, 0.1, 0.2, 100);
                }), AnimationEvent.Side.BOTH),
                        spawnSummonParticle(2.3F, () -> ParticleTypes.END_ROD, () -> ParticleTypes.CLOUD),
                        playSound(1.7F, ()->SoundEvents.ANVIL_LAND, -0.5F, 0.0F, 1F),
                        playSound(1.7F, ()->SoundEvents.BELL_BLOCK, -0.5F, 0.0F, 2.5F),
                        setGlowing(1.7F));
        PLAYER_SUMMON_KILL_AURA_1 = new ActionAnimation(0.15F, "screen_sword/kill_aura_1_summon_player", biped)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.7F, ((livingEntityPatch, staticAnimation, objects) -> {
                    groundSplit(livingEntityPatch, 0, 0, 0, 0, 0, 2, 0);
                }), AnimationEvent.Side.BOTH),
                        playSound(0.25F, ()->SoundEvents.AMETHYST_CLUSTER_STEP, 0, 0.1F, 0.6F),
                        playSound(0.45F, ()->SoundEvents.AMETHYST_CLUSTER_STEP, 0, 0.5F, 0.8F),
                        playSound(0.7F, ()->SoundEvents.AMETHYST_CLUSTER_STEP, 0.5F, 1.0F, 1.0F),
                        spawnStaticHexagram(0.7F, () -> ParticleTypes.END_ROD, 0.2F, 2, 3.0F),
                        spawnStaticHexagram(0.7F, () -> ParticleTypes.END_ROD, 0.2F, 2),
                        spawnStaticHexagram(1.7F, () -> ParticleTypes.WAX_OFF, 0.2F, 4),
                        spawnStaticHexagram(0.7F, () -> ParticleTypes.WAX_ON, 0.2F, 3),
                        spawnSummonParticle(2.3F, () -> ParticleTypes.END_ROD, () -> ParticleTypes.WAX_OFF));
        PLAYER_SUMMON_KILL_AURA_2 = new ActionAnimation(0.15F, "screen_sword/kill_aura_2_summon_player", biped)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.7F, ((livingEntityPatch, staticAnimation, objects) -> {
                    groundSplit(livingEntityPatch, 0, 0, 0, 0, 0, 2, 0);
                }), AnimationEvent.Side.BOTH),
                        AnimationEvent.TimeStampedEvent.create(0.7F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(SoundEvents.FIRE_EXTINGUISH, 0.0F, 0.3F)), AnimationEvent.Side.SERVER),
                        spawnSummonParticle(0.5F, () -> ParticleTypes.FLAME, () -> ParticleTypes.LAVA),
                        spawnSummonParticle(2.3F, () -> ParticleTypes.FLAME, () -> ParticleTypes.LAVA));
    }
}
