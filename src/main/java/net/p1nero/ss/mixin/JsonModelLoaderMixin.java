package net.p1nero.ss.mixin;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.p1nero.ss.entity.LongArmature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.main.EpicFightMod;

import java.util.Set;

@Mixin(value = JsonModelLoader.class, remap = false)
public abstract class JsonModelLoaderMixin {

    @Shadow private JsonObject rootJson;

    @Shadow
    private static TransformSheet getTransformSheet(float[] times, float[] trasnformMatrix, OpenMatrix4f invLocalTransform, boolean correct) {
        return null;
    }

    @Inject(method = "loadStaticAnimation", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$inject(StaticAnimation animation, CallbackInfo ci) {
        if (this.rootJson == null) {
            throw new IllegalStateException("[ModelParsingError]Can't find animation path: " + animation);
        }

        JsonArray array = this.rootJson.get("animation").getAsJsonArray();
        boolean action = animation instanceof ActionAnimation;
        boolean attack = animation instanceof AttackAnimation;
        boolean noTransformData = !action && FMLEnvironment.dist == Dist.DEDICATED_SERVER;
        boolean root = true;
        Armature armature = animation.getArmature();

        Set<String> allowedJoints = Sets.newLinkedHashSet();

        if (attack) {
            for (AttackAnimation.Phase phase : ((AttackAnimation)animation).phases) {
                Joint joint = armature.getRootJoint();

                for (Pair<Joint, Collider> colliderInfo : phase.getColliders()) {
                    if(armature instanceof LongArmature){
                        allowedJoints.add(colliderInfo.getFirst().getName());//直接全同意就好了不检查
                    } else {
                        int pathIndex = armature.searchPathIndex(colliderInfo.getFirst().getName());

                        while (joint != null) {
                            allowedJoints.add(joint.getName());
                            int nextJoint = pathIndex % 10;

                            if (nextJoint > 0) {
                                pathIndex /= 10;
                                joint = joint.getSubJoints().get(nextJoint - 1);
                            } else {
                                joint = null;
                            }
                        }
                    }
                }
            }
        } else if (action) {
            allowedJoints.add("Root");
        }

        for (JsonElement element : array) {
            JsonObject keyObject = element.getAsJsonObject();
            String name = keyObject.get("name").getAsString();

            if (attack && FMLEnvironment.dist == Dist.DEDICATED_SERVER && !allowedJoints.contains(name)) {
                if (name.equals("Coord")) {
                    root = false;
                }

                continue;
            }

            Joint joint = armature.searchJointByName(name);

            if (joint == null) {
                if (name.equals("Coord") && action) {
                    JsonArray timeArray = keyObject.getAsJsonArray("time");
                    JsonArray transformArray = keyObject.getAsJsonArray("transform");
                    int timeNum = timeArray.size();
                    int matrixNum = transformArray.size();
                    float[] times = new float[timeNum];
                    float[] transforms = new float[matrixNum * 16];

                    for (int i = 0; i < timeNum; i++) {
                        times[i] = timeArray.get(i).getAsFloat();
                    }

                    for (int i = 0; i < matrixNum; i++) {
                        JsonArray matrixJson = transformArray.get(i).getAsJsonArray();

                        for (int j = 0; j < 16; j++) {
                            transforms[i * 16 + j] = matrixJson.get(j).getAsFloat();
                        }
                    }

                    TransformSheet sheet = getTransformSheet(times, transforms, new OpenMatrix4f(), true);
                    ((ActionAnimation)animation).addProperty(AnimationProperty.ActionAnimationProperty.COORD, sheet);
                    root = false;
                    continue;
                } else {
                    EpicFightMod.LOGGER.warn("[EpicFightMod] Can't find the joint " + name + " in the animation file, " + animation);
                    continue;
                }
            }

            JsonArray timeArray = keyObject.getAsJsonArray("time");
            JsonArray transformArray = keyObject.getAsJsonArray("transform");
            int timeNum = timeArray.size();
            int matrixNum = transformArray.size();
            float[] times = new float[timeNum];
            float[] transforms = new float[matrixNum * 16];

            for (int i = 0; i < timeNum; i++) {
                times[i] = timeArray.get(i).getAsFloat();
            }

            for (int i = 0; i < matrixNum; i++) {
                JsonArray matrixJson = transformArray.get(i).getAsJsonArray();

                for (int j = 0; j < 16; j++) {
                    transforms[i * 16 + j] = matrixJson.get(j).getAsFloat();
                }
            }

            TransformSheet sheet = getTransformSheet(times, transforms, OpenMatrix4f.invert(joint.getLocalTrasnform(), null), root);

            if (!noTransformData) {
                animation.addSheet(name, sheet);
            }

            animation.setTotalTime(times[times.length - 1]);
            root = false;
        }
        ci.cancel();
    }
}