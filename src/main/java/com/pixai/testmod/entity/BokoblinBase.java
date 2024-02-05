package com.pixai.testmod.entity;

import com.pixai.testmod.ai.BokoblinAttackGoal;
import com.pixai.testmod.util.RandomCollection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public abstract class BokoblinBase extends Monster {
    private Goal bokoblinMobTarget;
    protected final RandomCollection<Item> mainHandChance;
    protected float hornChance;

    protected BokoblinBase(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        mainHandChance = new RandomCollection<Item>();
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource source, DifficultyInstance difficulty){
        mainHandChance.SetRandomSource(source);
        Item mainHand = mainHandChance.next();
        if(mainHand!= Items.AIR){
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        }
        if(source.nextFloat()<this.hornChance){
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.GOAT_HORN));
        }
    }

    @Override
    protected void registerGoals() {
        this.bokoblinMobTarget = new NearestAttackableTargetGoal(this, Animal.class, 10, false, false, (target) -> {
            return target instanceof Sheep || target instanceof Pig || target instanceof Cow ;
        });


        this.goalSelector.addGoal(0,new BokoblinAttackGoal(this,1,false));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this,0.5D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class,true));
        this.targetSelector.addGoal(6, this.bokoblinMobTarget);
    }

    @Override
    public boolean wantsToPickUp(ItemStack item) {
        return super.wantsToPickUp(item);
    }

    @Override
    public boolean doHurtTarget(Entity p_21372_) {
        return super.doHurtTarget(p_21372_);
    }
}
