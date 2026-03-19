package net.p1nero.ss.skill.sword_soaring;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.keymapping.SwordSoaringKeyMappings;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SwordSoaringSkillElytra extends SwordSoaringSkill {

    public SwordSoaringSkillElytra(Builder builder) {
        super(builder);
    }

    @Override
    public boolean canExecute(SkillContainer container) {
        PlayerPatch<?> executor = container.getExecutor();
        return super.canExecute(container) && !executor.getOriginal().onGround();
    }

    @Override
    public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
        super.executeOnServer(container, args);
        ServerPlayerPatch executor = container.getServerExecutor();
        executor.getOriginal().startFallFlying();
    }

    public AnimationManager.AnimationAccessor<? extends StaticAnimation> getFlyingAnim(){
        return flying;
    }

    @Override
    public void flyingTick(SkillContainer container) {
        if (container.getExecutor().isLogicalClient()) {
            if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get()) && container.getExecutor().hasStamina(consumption + 0.1F) && SwordSoaringMod.isValidSword(container.getExecutor().getOriginal().getMainHandItem())) {
                boolean accelerating = SwordSoaringKeyMappings.ACCELERATION.isDown();
                
                if (accelerating != container.getDataManager().getDataValue(SwordSoaringDatakeys.SPEED_UP.get())) {
                    container.getDataManager().setDataSync(SwordSoaringDatakeys.SPEED_UP.get(), accelerating);
                }
                LocalPlayer localPlayer = ((LocalPlayer) container.getExecutor().getOriginal());
                if(EpicFightCameraAPI.getInstance().isTPSMode()) {
                    localPlayer.setYRot(EpicFightCameraAPI.getInstance().getCameraYRot());
                    localPlayer.yRotO = EpicFightCameraAPI.getInstance().getCameraYRotO();
                    localPlayer.setXRot(EpicFightCameraAPI.getInstance().getCameraXRot());
                    localPlayer.xRotO = EpicFightCameraAPI.getInstance().getCameraXRotO();
                }
            }
        } else {
            if (container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get())) {
                container.getExecutor().resetActionTick();
                if (container.getDataManager().getDataValue(SwordSoaringDatakeys.SPEED_UP.get())) {
                    container.getExecutor().setStamina(container.getExecutor().getStamina() - consumption);
                }
                if(container.getExecutor().getOriginal().isUnderWater()){
                    stopFlying(container, ((ServerPlayer) container.getExecutor().getOriginal()));
                }
            }
        }
        if(container.getDataManager().getDataValue(SwordSoaringDatakeys.SPEED_UP.get()) && container.getExecutor().hasStamina(consumption)){
            //移速控制，只加速不匀速
            Vec3 accelerationSpeed = container.getExecutor().getOriginal().getViewVector(1.0F).normalize().scale(speed);
            Vec3 currentDeltaMovement = container.getExecutor().getOriginal().getDeltaMovement();
            double currentLength = currentDeltaMovement.length();
            double speedLength = accelerationSpeed.length();
            if (currentLength < speedLength) {
                container.getExecutor().getOriginal().setDeltaMovement(currentDeltaMovement.add(accelerationSpeed.scale((speedLength - currentLength) * 0.1)));
            }
        }
    }
    
    public void stopFlying(SkillContainer container, ServerPlayer serverPlayer){
        container.getDataManager().setDataSync(SwordSoaringDatakeys.FLYING.get(), false);
        container.getDataManager().setDataSync(SwordSoaringDatakeys.SPEED_UP.get(), false);
        container.getDataManager().setDataSync(SwordSoaringDatakeys.COOLDOWN_TIMER.get(), cooldown);
        serverPlayer.stopFallFlying();
    }

}
