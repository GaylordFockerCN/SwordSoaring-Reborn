package net.p1nero.ss.entity.sword.gate_of_babylon.client;

import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;

import java.util.Map;

public class BabylonMesh extends AnimatedMesh {
    public BabylonMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
        super(arrayMap, parent, properties, parts);
    }
}