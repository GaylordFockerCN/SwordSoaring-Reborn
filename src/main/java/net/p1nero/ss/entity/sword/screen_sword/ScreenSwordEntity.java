package net.p1nero.ss.entity.sword.screen_sword;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.p1nero.ss.entity.AbstractArtifactSpiritEntity;
import net.p1nero.ss.entity.SwordSoaringEntities;
import net.p1nero.ss.entity.sword.AbstractSwordEntity;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.skill.sword_controller.ScreenSwordSkill;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class ScreenSwordEntity extends AbstractSwordEntity {
    private int maxTickCount = -1;
    public ScreenSwordEntity(EntityType<? extends AbstractArtifactSpiritEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ScreenSwordEntity(Player owner, int maxTickCount){
        super(SwordSoaringEntities.SCREEN_SWORD.get(), owner.getMainHandItem().copy(), owner);
        this.maxTickCount = maxTickCount;
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide){
            ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getEntityPatch(getOwner(), ServerPlayerPatch.class);
            if(serverPlayerPatch != null){
                SkillDataManager dataManager = serverPlayerPatch.getSkill(SwordSoaringSkillSlots.SWORD_CONTROLLER).getDataManager();
                //时间到了或次数用尽就紫砂
                if(dataManager.hasData(ScreenSwordSkill.PROTECT_COUNT)){
                    if(dataManager.getDataValue(ScreenSwordSkill.PROTECT_COUNT) <= 0){
                        dataManager.setDataSync(ScreenSwordSkill.PROTECT_COUNT, 0, serverPlayerPatch.getOriginal());
                        if(getOwner().isCurrentlyGlowing()){
                            getOwner().setGlowingTag(false);
                        }
                        this.discard();
                        return;
                    }
                }
                if(tickCount == maxTickCount){
                    if(getOwner().isCurrentlyGlowing()){
                        getOwner().setGlowingTag(false);
                    }
                    this.discard();
                }
            }
        }
    }

    @Override
    protected void moveToOwner(LivingEntity owner) {
        setYRot(0);
        setYBodyRot(0);
        setYHeadRot(0);
        setPos(owner.position());
    }

    @Override
    protected Item getOriginalItem() {
        return null;
    }
}