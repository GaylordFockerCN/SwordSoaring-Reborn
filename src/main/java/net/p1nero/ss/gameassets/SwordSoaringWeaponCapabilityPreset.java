package net.p1nero.ss.gameassets;

import com.p1nero.invincible.capability.item.ComboWeaponCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.p1nero.ss.SwordSoaringMod;
import net.p1nero.ss.client.sound.SwordSoaringSounds;
import net.p1nero.ss.gameassets.animations.VatanseverAnimations;
import net.p1nero.ss.gameassets.skills.VatanseverSkills;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = SwordSoaringMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class
SwordSoaringWeaponCapabilityPreset {
    public static final Function<Item, CapabilityItem.Builder> VATANSEVER = (item) ->
            ComboWeaponCapability.builder().category(SwordSoaringWeaponCategories.ARTIFACT_SPIRIT)
                    .styleProvider((livingEntityPatch) -> CapabilityItem.Styles.TWO_HAND)
                    .collider(SwordSoaringColliders.VATANSEVER)
                    .hitSound(EpicFightSounds.BLADE_HIT.get())
                    .swingSound(SwordSoaringSounds.NO_SOUND.get())
                    .hitParticle(EpicFightParticles.HIT_BLADE.get())
                    .canBePlacedOffhand(false)
                    .innateSkill(CapabilityItem.Styles.TWO_HAND, (itemStack) -> VatanseverSkills.VATANSEVER_INNATE)
                    .passiveSkill(VatanseverSkills.VATANSEVER_PASSIVE)
                    .newStyleCombo(CapabilityItem.Styles.TWO_HAND, Animations.SWORD_AUTO1)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.IDLE, VatanseverAnimations.VATANSEVER_IDLE)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.WALK, VatanseverAnimations.VATANSEVER_WALK)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.CHASE, VatanseverAnimations.VATANSEVER_RUN)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.RUN, VatanseverAnimations.VATANSEVER_RUN)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.JUMP, VatanseverAnimations.VATANSEVER_RUN)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.FALL, VatanseverAnimations.VATANSEVER_FALL)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.FLOAT, VatanseverAnimations.VATANSEVER_FLOAT)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.FLY, VatanseverAnimations.VATANSEVER_FLY)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.DEATH, VatanseverAnimations.VATANSEVER_DEATH)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.SWIM, VatanseverAnimations.VATANSEVER_SWIM)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.KNEEL, VatanseverAnimations.VATANSEVER_SNEAK)
                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.SNEAK, VatanseverAnimations.VATANSEVER_SNEAK);
    @SubscribeEvent
    public static void register(WeaponCapabilityPresetRegistryEvent event) {
        event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath(SwordSoaringMod.MOD_ID, "vatansever"), VATANSEVER);
    }

}
