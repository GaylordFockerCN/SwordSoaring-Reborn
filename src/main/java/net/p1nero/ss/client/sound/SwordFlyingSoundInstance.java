package net.p1nero.ss.client.sound;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.skill.sword_soaring.SwordSoaringSkill;
import net.p1nero.ss.skill.sword_soaring.SwordSoaringSkillElytra;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

@OnlyIn(Dist.CLIENT)
public class SwordFlyingSoundInstance extends AbstractTickableSoundInstance {
    private final LocalPlayer player;
    private final LocalPlayerPatch playerPatch;
    private int time;

    public SwordFlyingSoundInstance(LocalPlayerPatch playerPatch) {
        super(SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, RandomSource.create());
        this.playerPatch = playerPatch;
        this.player = playerPatch.getOriginal();
        this.looping = true;
        this.delay = 0;
        this.volume = 0.1F;
    }

    public void tick() {
        ++this.time;
        if(playerPatch.getSkill(SwordSoaringSkillSlots.SWORD_SOARING).getSkill() instanceof SwordSoaringSkillElytra){
            this.stop();
            return;
        }
        if (!this.player.isRemoved() && (this.time <= 20 || playerPatch.getSkill(SwordSoaringSkillSlots.SWORD_SOARING).getDataManager().getDataValue(SwordSoaringSkill.FLYING))) {
            this.x = (float)this.player.getX();
            this.y = (float)this.player.getY();
            this.z = (float)this.player.getZ();
            float f = (float)this.player.getDeltaMovement().lengthSqr();
            if ((double)f >= 1.0E-7D) {
                this.volume = Mth.clamp(f / 4.0F, 0.0F, 1.0F);
            } else {
                this.volume = 0.0F;
            }

            if (this.time < 20) {
                this.volume = 0.0F;
            } else if (this.time < 40) {
                this.volume *= (float)(this.time - 20) / 20.0F;
            }

            if (this.volume > 0.8F) {
                this.pitch = 1.0F + (this.volume - 0.8F);
            } else {
                this.pitch = 1.0F;
            }

        } else {
            this.stop();
        }
    }
}