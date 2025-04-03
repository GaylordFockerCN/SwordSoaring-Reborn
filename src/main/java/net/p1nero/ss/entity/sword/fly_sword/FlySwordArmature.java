package net.p1nero.ss.entity.sword.fly_sword;

import com.google.common.collect.ImmutableList;
import net.p1nero.ss.entity.ReplaceableArmature;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.Map;

public class FlySwordArmature extends ReplaceableArmature {
    public final Joint body;
    public FlySwordArmature(int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
        super(jointNumber, rootJoint, jointMap);
        body = getOrLogException(jointMap, "root");
        joints.add(body);
    }
}