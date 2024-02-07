package com.pixai.testmod.entity;

import net.minecraft.world.entity.LivingEntity;

public interface AlertableMob {
    public boolean canAlert();
    public boolean isAlerted();

    public void alert(LivingEntity target);
}
