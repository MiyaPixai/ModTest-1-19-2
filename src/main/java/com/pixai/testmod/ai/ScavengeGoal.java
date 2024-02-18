package com.pixai.testmod.ai;

import com.pixai.testmod.Modtest;
import com.pixai.testmod.util.EntityGetterUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

public class ScavengeGoal extends Goal {

    private final Mob mob;
    private final Level level;
    private final float speedModifier;
    private final int scavengeTime;
    private final int reach;
    private int currentTime;
    private List<ItemEntity> itemEntities;
    private int currentEntity = 0;
    private int scanCooldown = 0;

    public ScavengeGoal(Mob mob, float speedModifier, int reach, int scavengeTime) {
        this.mob = mob;
        this.level = mob.level;
        this.speedModifier = speedModifier;
        this.reach = reach;
        this.scavengeTime = scavengeTime;
    }

    @Override
    public boolean canUse() {
        if (!this.mob.canPickUpLoot())
            return false;

        if (this.scanCooldown-- <= 0) {
            this.itemEntities = EntityGetterUtil.getNearbyEntitiesOfClass(ItemEntity.class, this.level, this.mob.getBoundingBox().inflate(reach));
            this.currentEntity = 0;
            this.scanCooldown = 20;
        }

        if (!this.itemEntities.isEmpty()) {
            if (this.mob.canTakeItem(this.getItem().getItem())) {
                this.currentTime = this.scavengeTime;
                return true;
            } else {
                this.incrementIndex();
            }

        }
        return false;
    }

    private ItemEntity getItem() {
        if (this.itemEntities.isEmpty())
            return null;
        return this.itemEntities.get(this.currentEntity);
    }


    @Override
    public boolean canContinueToUse() {
        if ((this.itemEntities.isEmpty() || getItem() == null) && !this.mob.getNavigation().isDone()) {
            return false;
        }
        if (this.getItem() == null || !this.mob.canTakeItem(this.getItem().getItem()))
            return false;

        return this.currentTime > 0;
    }

    @Override
    public void start() {
        Entity item = getItem();
        if (item != null) {
            this.mob.getNavigation().moveTo(item, this.speedModifier);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.currentTime = 0;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.mob.getNavigation().isDone() && this.getItem().isAlive()) {
            if (this.mob.getNavigation().isStuck()) {
                this.mob.getNavigation().stop();
                this.currentTime = 0;
                this.incrementIndex();
            }
            this.mob.getLookControl().setLookAt(getItem());
        } else if (--this.currentTime > 0) {
            this.itemEntities.remove(this.getItem());
            this.currentEntity = 0;
        }
    }

    protected void incrementIndex() {
        this.currentEntity = ++this.currentEntity % this.itemEntities.size();
    }
}
