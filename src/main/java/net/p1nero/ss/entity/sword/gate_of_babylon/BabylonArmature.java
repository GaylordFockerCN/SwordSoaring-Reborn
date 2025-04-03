package net.p1nero.ss.entity.sword.gate_of_babylon;

import net.p1nero.ss.entity.ReplaceableArmature;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BabylonArmature extends ReplaceableArmature {

    public BabylonArmature(int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
        super(jointNumber, rootJoint, jointMap);
        for(int i = 1; i <= 44; i++){
            //你问为什么没有009？因为猪鼻merlin把它掰弯了，含泪抛弃
            if(i == 9){
                continue;
            }
            joints.add(getOrLogException(jointMap, "W." + String.format("%03d", i)));
        }
    }
}