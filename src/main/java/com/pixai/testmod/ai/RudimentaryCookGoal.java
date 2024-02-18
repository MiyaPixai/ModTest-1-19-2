package com.pixai.testmod.ai;

import com.pixai.testmod.entity.RudimentaryCookMob;
import com.pixai.testmod.util.EntityGetterUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RudimentaryCookGoal<T extends Mob & RudimentaryCookMob> extends Goal {

    private final T mob;
    private final Level level;
    private final float speedModifier;
    private int campfireIndex = 0;
    private List<CampfireBlockEntity> campfires;
    private ChunkPos latestChunk;
    private boolean isCampfireReachable = false;
    private int scanCooldown = 0;

    public RudimentaryCookGoal(T mob, float speedModifier) {

        this.mob = mob;
        this.level = mob.level;
        this.speedModifier = speedModifier;
    }

    @Override
    public boolean canUse() {
        if (this.mob.chunkPosition() != this.latestChunk || this.scanCooldown-- < 0 ||
                (this.getCurrentCampfire() != null && this.getCurrentCampfire().isRemoved())) {
            this.latestChunk = this.mob.chunkPosition();
            this.campfireIndex = 0;
            this.campfires = EntityGetterUtil.getNearbyBlockEntityOfClass(CampfireBlockEntity.class, this.mob.blockPosition(), level);
            this.scanCooldown = 20;
            isCampfireReachable = true;
        }

        if (!campfires.isEmpty() && isCampfireReachable) {
            return this.canContinueToUse();
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.getFoodItemStack().isEmpty() && isCampfireReachable;
    }

    @Override
    public void start() {
        updatePath();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        super.tick();
        var fire = this.getCurrentCampfire();
        this.mob.getLookControl().setLookAt(Vec3.atCenterOf(fire.getBlockPos()));

        if (this.mob.getNavigation().isStuck() || fire.isRemoved()) {
            isCampfireReachable = false;
        }
        if (!isCampfireReachable && this.campfires.size() > 1) {
            this.campfireIndex = ++this.campfireIndex % this.campfires.size();
            isCampfireReachable = true;
            this.updatePath();
        }

        if (this.mob.getNavigation().isDone()) {
            if (this.mob.distanceToSqr(Vec3.atCenterOf(fire.getBlockPos())) < 9) {
                ItemStack foodItem = this.getFoodItemStack();
                fire.placeFood(null, foodItem, this.getCurrentCampfire().getCookableRecipe(foodItem).get().getCookingTime());
            } else {
                this.updatePath();
            }
        }
    }

    protected ItemStack getFoodItemStack() {
        return this.mob.getFoodItem((ItemStack item) -> this.getCurrentCampfire().getCookableRecipe(item).isPresent());
    }

    protected CampfireBlockEntity getCurrentCampfire() {
        if (this.campfires.isEmpty())
            return null;
        return this.campfires.get(this.campfireIndex);
    }

    private void updatePath() {
        CampfireBlockEntity campfire = this.getCurrentCampfire();
        if (campfire != null) {
            BlockPos safePos = campfire.getBlockPos().north().distSqr(this.mob.blockPosition()) < campfire.getBlockPos().south(1).distSqr(this.mob.blockPosition()) ?
                    campfire.getBlockPos().north() :
                    campfire.getBlockPos().south();
            this.mob.getNavigation().moveTo(safePos.getX(), safePos.getY(), safePos.getZ(), this.speedModifier);
        } else {
            isCampfireReachable = false;
        }
    }
}

