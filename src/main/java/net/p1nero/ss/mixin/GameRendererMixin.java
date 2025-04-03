package net.p1nero.ss.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.p1nero.ss.item.VatanseverItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 取消抖动
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$bobView(PoseStack pMatrixStack, float pPartialTicks, CallbackInfo ci){
        if(Minecraft.getInstance().player != null){
            if(Minecraft.getInstance().player.getMainHandItem().getItem() instanceof VatanseverItem){
                ci.cancel();
            }
        }
    }
}