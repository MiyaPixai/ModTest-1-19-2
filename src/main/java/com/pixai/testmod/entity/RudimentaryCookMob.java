package com.pixai.testmod.entity;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface RudimentaryCookMob {
    public ItemStack getFoodItem(Predicate<ItemStack> hasRecipe);
}
