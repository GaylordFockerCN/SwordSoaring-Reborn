package net.p1nero.ss.entity.vatansever.client;

import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;

import java.util.List;
import java.util.Map;

public class VatanseverMesh extends AnimatedMesh {

    public final ModelPart<AnimatedVertexIndicator> L1;
    public final ModelPart<AnimatedVertexIndicator> L2;
    public final ModelPart<AnimatedVertexIndicator> L3;
    public final ModelPart<AnimatedVertexIndicator> R1;
    public final ModelPart<AnimatedVertexIndicator> R2;
    public final ModelPart<AnimatedVertexIndicator> R3;
    public final List<ModelPart<AnimatedVertexIndicator>> swordLists;
    public VatanseverMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
        super(arrayMap, parent, properties, parts);
        this.L1 = this.getOrLogException(parts, "sss_1_l");
        this.L2 = this.getOrLogException(parts, "sss_2_l");
        this.L3 = this.getOrLogException(parts, "sss_3_l");
        this.R1 = this.getOrLogException(parts, "sss_1_r");
        this.R2 = this.getOrLogException(parts, "sss_2_r");
        this.R3 = this.getOrLogException(parts, "sss_3_r");
        swordLists = List.of(R3, L3, R2, L2, R1, L1);
    }
}