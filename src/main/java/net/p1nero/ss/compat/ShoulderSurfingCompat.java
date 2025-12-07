package net.p1nero.ss.compat;

import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import net.minecraft.client.Minecraft;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.skill.sword_soaring.SwordSoaringSkill;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@SuppressWarnings("unused") // Referenced in src/main/resources/shouldersurfing_plugin.json
public class ShoulderSurfingCompat implements IShoulderSurfingPlugin {
    @Override
    public void register(IShoulderSurfingRegistrar registrar) {
        registrar.registerCameraCouplingCallback(new ForceCameraCouplingWhileSwordSoaringFlying());
    }

    private static class ForceCameraCouplingWhileSwordSoaringFlying implements ICameraCouplingCallback {

        @Override
        public boolean isForcingCameraCoupling(Minecraft minecraft) {
            final LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getEntityPatch(minecraft.player, LocalPlayerPatch.class);
            if (localPlayerPatch == null) {
                return false;
            }
            return isPlayerSwordSoaringFlying(localPlayerPatch);
        }

        private static boolean isPlayerSwordSoaringFlying(@NotNull LocalPlayerPatch localPlayerPatch) {
            return localPlayerPatch.getSkillCapability().listSkillContainers()
                    .filter(container -> container != null && container.getSkill() instanceof SwordSoaringSkill)
                    .anyMatch(container -> container.getDataManager().getDataValue(SwordSoaringDatakeys.FLYING.get()));
        }
    }
}
