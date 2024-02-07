package com.pixai.testmod.ai;

import com.pixai.testmod.entity.AlertableMob;
import com.pixai.testmod.entity.HealableMob;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class HealWithItemGoal extends Goal {

    private final HealableMob mob;

    public HealWithItemGoal(HealableMob mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return this.mob.hasHealingItem() && this.mob.needHealing();
    }
}
