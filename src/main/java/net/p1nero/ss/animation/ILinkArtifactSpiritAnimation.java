package net.p1nero.ss.animation;

import net.minecraft.world.entity.Entity;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.skill.weapon_passive.ArtifactSpiritPassiveSkill;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public interface ILinkArtifactSpiritAnimation {
    default void callArtifactSpiritAnimation(LivingEntityPatch<?> ownerPatch){
        if(ownerPatch instanceof ServerPlayerPatch serverPlayerPatch){
            SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
            if(manager.hasData(ArtifactSpiritPassiveSkill.ARTIFACT_SPIRIT_ENTITY_ID)){
                Entity entity = serverPlayerPatch.getOriginal().level.getEntity(manager.getDataValue(ArtifactSpiritPassiveSkill.ARTIFACT_SPIRIT_ENTITY_ID));
                if(entity != null){
                    AbstractArtifactSpiritPatch<?> spiritPatch = EpicFightCapabilities.getEntityPatch(entity, AbstractArtifactSpiritPatch.class);
                    if(spiritPatch != null && getArtifactSpiritAnimation() != null){
                        spiritPatch.playAnimationSynchronized(getArtifactSpiritAnimation(), getConvertTime());
                    }
                }
            }
        }
    }
    StaticAnimation setArtifactSpiritAnimation(StaticAnimation artifactSpiritAnimation);
    StaticAnimation getArtifactSpiritAnimation();

    float getConvertTime();
}