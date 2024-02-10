package com.pixai.testmod.entity;

import com.pixai.testmod.util.RandomCollection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class RedBokoblin extends BokoblinBase{

    protected RedBokoblin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.mainHandChance
                .add(65,Items.AIR)
                .add(5,Items.WOODEN_SWORD)
                .add(5,Items.WOODEN_PICKAXE)
                .add(5,Items.WOODEN_AXE)
                .add(5,Items.WOODEN_SHOVEL)
                .add(5,Items.WOODEN_HOE)
                .add(10,Items.BOW);
        this.hornChance = 0.10f;
    }



    public static AttributeSupplier.Builder createAttributes(){
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23000000417232513)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.MAX_HEALTH,20)
                .add(Attributes.ARMOR, 0.0);
    };

}
