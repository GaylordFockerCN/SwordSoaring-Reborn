package net.p1nero.ss.entity.vatansever_storm.client;

import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;

import java.util.List;
import java.util.Map;

public class VatanseverStormMesh extends AnimatedMesh {
    public VatanseverStormMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
        super(arrayMap, parent, properties, parts);
    }
}