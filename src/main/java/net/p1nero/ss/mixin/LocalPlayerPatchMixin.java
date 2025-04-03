package net.p1nero.ss.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.item.VatanseverItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

@Mixin(value = LocalPlayerPatch.class, remap = false)
public abstract class LocalPlayerPatchMixin extends AbstractClientPlayerPatch<LocalPlayer> {

    @Shadow
    private LivingEntity rayTarget;
    @Inject(method = "clientTick",at = @At(value = "INVOKE", target = "Lyesman/epicfight/network/EpicFightNetworkManager;sendToServer(Ljava/lang/Object;)V"), cancellable = true)
    public void tick(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if(this.getOriginal().getMainHandItem().getItem() instanceof VatanseverItem){
            if(rayTarget instanceof AbstractArtifactSpiritEntity){
                rayTarget = null;
                ci.cancel();
            }
        }
    }
}