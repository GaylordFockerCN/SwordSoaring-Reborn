package net.p1nero.ss.entity.sword.sword_convergence.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.entity.AbstractArtifactSpiritPatch;
import net.p1nero.ss.entity.ReplaceableArmature;
import net.p1nero.ss.entity.sword.sword_convergence.SwordConvergenceEntity;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.skill.sword_controller.WanJianGuiZongSkill;
import net.p1nero.ss.util.MathUtils;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class PatchedSwordConvergenceRandomReplaceableLayer<E extends SwordConvergenceEntity, T extends AbstractArtifactSpiritPatch<E>, M extends EntityModel<E>, AM extends AnimatedMesh> extends PatchedLayer<E, T, M, RenderLayer<E, M>, AM> {
    public PatchedSwordConvergenceRandomReplaceableLayer() {
        super(null);
    }

    @Override
    protected void renderLayer(T entityPatch, E entity, RenderLayer<E, M> vanillaLayer, PoseStack postStack, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
        if (entityPatch.getArmature() instanceof ReplaceableArmature armature) {
            renderItemInJoint(entity, entityPatch, armature, poses, buffer, postStack, LightTexture.FULL_BRIGHT);
        }
    }

    /**
     * 根据玩家物品栏物品随机替换Joint的位置渲染
     */
    public static void renderItemInJoint(SwordConvergenceEntity entity, AbstractArtifactSpiritPatch<?> artifactSpiritPatch, ReplaceableArmature armature, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
        if (entity.getOwner() != null) {
            List<ItemStack> babylons = entity.getValidBabylonItems();
            if (babylons.isEmpty()) {
                return;
            }

            int lifeTime = 0;
            if(entity.getOwnerPatch() instanceof PlayerPatch<?> playerPatch){
                SkillDataManager manager = playerPatch.getSkill(SwordSoaringSkillSlots.SWORD_CONTROLLER).getDataManager();
                if(manager.hasData(WanJianGuiZongSkill.COOLDOWN_TIMER)){
                    int cooldown = manager.getDataValue(WanJianGuiZongSkill.COOLDOWN_TIMER);
                    lifeTime = WanJianGuiZongSkill.getMaxCooldown() - cooldown;
                }
            }
            List<Joint> jointList = armature.getJoints(artifactSpiritPatch);
            Random random = new Random(entity.getSeed());
            //慢慢出现，1tick解放2根
            for (int i = 0; i < jointList.size() && i < lifeTime / 2; i++) {
                Joint joint = jointList.get(i);
                ItemStack itemStack;
                if (i < babylons.size()) {
                    itemStack = babylons.get(i);//尽可能都用上
                } else {
                    itemStack = babylons.get(random.nextInt(babylons.size()));
                }
                if (itemStack != null) {
                    OpenMatrix4f jointTransform = poses[joint.getId()];
                    if (entity.getStartTransform(joint.getId()) == null && jointTransform.toScaleVector().length() > 0) {
                        entity.bindStartTransform(joint.getId(), MathUtils.removeScale(jointTransform));
                    }

                    //画在Joint上
                    poseStack.pushPose();
                    MathUtils.mulPoseStack(poseStack, jointTransform);
                    ItemTransforms.TransformType transformType = ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
                    Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderItem(artifactSpiritPatch.getOriginal(), itemStack, transformType, false, poseStack, buffer, packedLight);
                    poseStack.popPose();
                }
            }
        }
    }
}