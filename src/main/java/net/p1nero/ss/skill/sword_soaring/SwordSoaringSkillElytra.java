package net.p1nero.ss.skill.sword_soaring;

import com.p1nero.invincible.api.animation.StaticAnimationProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.client.keymapping.SwordSoaringKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SwordSoaringSkillElytra extends SwordSoaringSkill {

    public SwordSoaringSkillElytra(Builder builder) {
        super(builder);
    }

    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return super.canExecute(executer) && !executer.getOriginal().isOnGround();
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        executer.getOriginal().startFallFlying();
    }

    public StaticAnimationProvider getFlyingAnim(){
        return flying;
    }

    /**
     * 取消鞘翅音效播放，不需要
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void executeOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
    }

    @Override
    public void flyingTick(SkillContainer container) {
        if (container.getExecuter().isLogicalClient()) {
            if (container.getDataManager().getDataValue(FLYING) && container.getExecuter().hasStamina(consumption + 0.1F) && SwordSoaring.isValidSword(container.getExecuter().getOriginal().getMainHandItem())) {
                LocalPlayer localPlayer = ((LocalPlayer) container.getExecuter().getOriginal());
                boolean accelerating = SwordSoaringKeyMappings.ACCELERATION.isDown();
                if (accelerating != container.getDataManager().getDataValue(ACCELERATING)) {
                    if (accelerating) {
                        container.getExecuter().playAnimationSynchronized(acceleration.get(), 0.0F);
                    } else {
                        container.getExecuter().playAnimationSynchronized(flying.get(), 0.0F);
                    }
                    container.getDataManager().setDataSync(ACCELERATING, accelerating, localPlayer);
                }
            }
        } else {
            if (container.getDataManager().getDataValue(FLYING)) {
                container.getExecuter().resetActionTick();
                if (container.getDataManager().getDataValue(ACCELERATING)) {
                    container.getExecuter().consumeStamina(consumption);
                }
                if(container.getExecuter().getOriginal().isUnderWater()){
                    stopFlying(container, ((ServerPlayer) container.getExecuter().getOriginal()));
                }
            }
        }
        if(container.getDataManager().getDataValue(ACCELERATING)){
            //移速控制，只加速不匀速
            Vec3 accelerationSpeed = container.getExecuter().getOriginal().getViewVector(1.0F).normalize().scale(speed);
            Vec3 currentDeltaMovement = container.getExecuter().getOriginal().getDeltaMovement();
            double currentLength = currentDeltaMovement.length();
            double speedLength = accelerationSpeed.length();
            if (currentLength < speedLength) {
                container.getExecuter().getOriginal().setDeltaMovement(currentDeltaMovement.add(accelerationSpeed.scale((speedLength - currentLength) * 0.1)));
            }
        }
    }
    
    public void stopFlying(SkillContainer container, ServerPlayer serverPlayer){
        container.getDataManager().setDataSync(FLYING, false, serverPlayer);
        container.getDataManager().setDataSync(ACCELERATING, false, serverPlayer);
        container.getDataManager().setDataSync(COOL_DOWN_TIMER, cooldown, serverPlayer);
        serverPlayer.stopFallFlying();
    }
}