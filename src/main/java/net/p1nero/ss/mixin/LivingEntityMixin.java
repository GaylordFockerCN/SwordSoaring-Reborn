package net.p1nero.ss.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.p1nero.ss.SwordSoaring;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.item.VatanseverItem;
import net.p1nero.ss.skill.sword_soaring.SwordSoaringSkill;
import net.p1nero.ss.skill.sword_soaring.SwordSoaringSkillElytra;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract boolean hasEffect(MobEffect pEffect);

    @Shadow
    public abstract ItemStack getMainHandItem();

    @Shadow
    public abstract boolean isAlive();

    @Shadow
    protected int fallFlyTicks;

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "updateFallFlying", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$updateFallFlying(CallbackInfo ci) {
        if (this.getMainHandItem().getItem() instanceof VatanseverItem) {
            boolean flag = this.getSharedFlag(7);
            if (flag && !this.onGround && !this.isPassenger() && !this.hasEffect(MobEffects.LEVITATION)) {
                flag = this.getMainHandItem().elytraFlightTick((LivingEntity) (Object) this, this.fallFlyTicks);
            } else {
                flag = false;
            }

            if (!this.level.isClientSide) {
                this.setSharedFlag(7, flag);
            }
            ci.cancel();
        }
        if ((LivingEntity) (Object) this instanceof ServerPlayer serverPlayer) {
            ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);
            if(serverPlayerPatch == null || !SwordSoaring.isValidSword(serverPlayer.getMainHandItem())){
                return;
            }
            SkillContainer container = serverPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_SOARING);
            SkillDataManager manager = container.getDataManager();
            if(container.getSkill() instanceof SwordSoaringSkillElytra && manager.hasData(SwordSoaringSkillElytra.FLYING) && manager.getDataValue(SwordSoaringSkillElytra.FLYING)){
                this.setSharedFlag(7, true);
            }
            ci.cancel();
        }
    }

    @Inject(method = "playBlockFallSound", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$playBlockFallSound(CallbackInfo ci) {
        if (this.getMainHandItem().getItem() instanceof VatanseverItem) {
            ci.cancel();
        }
    }

    @Inject(method = "getFallDamageSound", at = @At("HEAD"), cancellable = true)
    private void sword_soaring$getFallDamageSound(int pHeight, CallbackInfoReturnable<SoundEvent> cir) {
        if (this.getMainHandItem().getItem() instanceof VatanseverItem) {
            cir.setReturnValue(EpicFightSounds.NO_SOUND.get());
        }
    }
}