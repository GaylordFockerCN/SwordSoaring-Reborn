package net.p1nero.ss.animation;

import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class LinkArtifactSpiritAnimation extends ActionAnimation implements ILinkArtifactSpiritAnimation{
    private StaticAnimation artifactSpiritAnimation;
    public LinkArtifactSpiritAnimation(float convertTime, String path, Armature armature, StaticAnimation artifactSpiritAnimation) {
        super(convertTime, path, armature);
        this.artifactSpiritAnimation = artifactSpiritAnimation;
    }

    public LinkArtifactSpiritAnimation(float convertTime, float postDelay, String path, Armature armature, StaticAnimation artifactSpiritAnimation) {
        super(convertTime, postDelay, path, armature);
        this.artifactSpiritAnimation = artifactSpiritAnimation;
    }

    @Override
    public void begin(LivingEntityPatch<?> entityPatch) {
        super.begin(entityPatch);
        this.callArtifactSpiritAnimation(entityPatch);
    }

    @Override
    public LinkArtifactSpiritAnimation setArtifactSpiritAnimation(StaticAnimation artifactSpiritAnimation) {
        this.artifactSpiritAnimation = artifactSpiritAnimation;
        return this;
    }

    @Override
    public StaticAnimation getArtifactSpiritAnimation() {
        return artifactSpiritAnimation;
    }

}