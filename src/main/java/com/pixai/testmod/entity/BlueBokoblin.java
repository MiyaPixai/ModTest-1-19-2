package com.pixai.testmod.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BlueBokoblin extends BokoblinBase{
    protected BlueBokoblin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        mainHandChance.add(40, Items.AIR)
                .add(5,Items.WOODEN_SWORD)
                .add(5,Items.WOODEN_PICKAXE)
                .add(5,Items.WOODEN_AXE)
                .add(5,Items.WOODEN_SHOVEL)
                .add(5,Items.WOODEN_HOE)
                .add(5,Items.STONE_SWORD)
                .add(5,Items.STONE_PICKAXE)
                .add(5,Items.STONE_AXE)
                .add(5,Items.STONE_SHOVEL)
                .add(5,Items.STONE_HOE)
                .add(10,Items.BOW);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23000000417232513)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.MAX_HEALTH,32)
                .add(Attributes.ARMOR, 0.0);
    };

}
