package net.p1nero.ss.entity.vatansever_storm;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VatanseverStormArmature extends Armature {
    public final List<Joint> rootJoints = new ArrayList<>();

    public VatanseverStormArmature(int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
        super(jointNumber, rootJoint, jointMap);
        rootJoints.add(getOrLogException(jointMap, "root_1"));
        for(int i = 1; i <= 15; i++){
            if (i!=9){
                rootJoints.add(getOrLogException(jointMap, "root_1." + String.format("%03d", i)));
            }
        }
    }
}