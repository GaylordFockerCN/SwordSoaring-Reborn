package net.p1nero.ss.entity.sword.sword_convergence;

import net.p1nero.ss.entity.ReplaceableArmature;
import yesman.epicfight.api.animation.Joint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WanArmature extends ReplaceableArmature {
    public final List<Joint> wanJoints = new ArrayList<>();

    public WanArmature(int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
        super(jointNumber, rootJoint, jointMap);
        wanJoints.add(getOrLogException(jointMap, "root_1"));
        for(int i = 1; i <= 255; i++){
            if(i <= 15){
                wanJoints.add(getOrLogException(jointMap, "root_1." + String.format("%03d", i)));
            }
            String name = "s." + String.format("%03d", i);
            if(jointMap.containsKey(name)){
                joints.add(getOrLogException(jointMap, name));
            }
        }
        Collections.shuffle(joints);
    }
}