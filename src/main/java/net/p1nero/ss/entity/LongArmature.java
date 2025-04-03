package net.p1nero.ss.entity;

import com.google.common.collect.Maps;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;

import java.util.Map;

public abstract class LongArmature extends Armature {
    private final Map<String, Long> pathIndexMap;

    public LongArmature(int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
        super(jointNumber, rootJoint, jointMap);
        this.pathIndexMap = Maps.newHashMap();
    }

    @Override
    public OpenMatrix4f getBindedTransformForCurrentPose(Joint joint) {
        return this.getBindedTransformByJointIndex(this.getCurrentPose(), this.searchPathIndexLong(joint.getName()));
    }

    @Override
    public OpenMatrix4f getBindedTransformFor(Pose pose, Joint joint) {
        return this.getBindedTransformByJointIndex(pose, this.searchPathIndexLong(joint.getName()));
    }

    public OpenMatrix4f getBindedTransformByJointIndex(Pose pose, long pathIndex) {
        this.initializeTransform();
        return this.getBindedJointTransformByIndexInternal(pose, this.rootJoint, new OpenMatrix4f(), pathIndex);
    }

    private OpenMatrix4f getBindedJointTransformByIndexInternal(Pose pose, Joint joint, OpenMatrix4f parentTransform, long pathIndex) {
        JointTransform jt = pose.getOrDefaultTransform(joint.getName());
        OpenMatrix4f result = jt.getAnimationBindedMatrix(joint, parentTransform);
        int nextIndex = (int) (pathIndex % 10);
        return nextIndex > 0 ? this.getBindedJointTransformByIndexInternal(pose, joint.getSubJoints().get(nextIndex - 1), result, pathIndex / 10) : result;
    }

    @Override
    public int searchPathIndex(String joint) {
        long result = searchPathIndexLong(joint);
        return result > Integer.MAX_VALUE ? 0 : ((int) result);
    }

    public long searchPathIndexLong(String joint) {
        if (this.pathIndexMap.containsKey(joint)) {
            return this.pathIndexMap.get(joint);
        } else {
            String pathIndex = this.rootJoint.searchPath("", joint);
            if (pathIndex == null) {
                throw new IllegalArgumentException("failed to get joint path index for " + joint);
            } else {
                long pathIndex2Long = pathIndex.isEmpty() ? -1 : Long.parseLong(pathIndex);
                this.pathIndexMap.put(joint, pathIndex2Long);
                return pathIndex2Long;
            }
        }
    }
}