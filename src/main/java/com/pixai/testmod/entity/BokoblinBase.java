package com.pixai.testmod.entity;


import com.pixai.testmod.ai.BokoblinAttackGoal;
import com.pixai.testmod.ai.DetectTargetGoal;
import com.pixai.testmod.ai.HealWithItemGoal;
import com.pixai.testmod.util.RandomCollection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

public abstract class BokoblinBase extends Monster implements AlertableMob, HealableMob {
    private Goal bokoblinMobTarget;
    protected final RandomCollection<Item> mainHandChance;
    protected float hornChance;
    private boolean canAlert = false;
    private boolean isAlerted = false;
    private final SimpleContainer inventory = new SimpleContainer(4);
    private static final List<Item> HEALING_ITEMS = Arrays.asList(Items.BEEF, Items.PORKCHOP, Items.MUTTON, Items.CHICKEN);

    protected BokoblinBase(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        mainHandChance = new RandomCollection<Item>();
    }

    public boolean canAlert() {
        return canAlert;
    }

    public boolean isAlerted() {
        return isAlerted;
    }

    public boolean hasHealingItem() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {

            if (HEALING_ITEMS.contains(inventory.getItem(i).getItem())) {
                return true;
            }
        }
        return false;
    }

    public boolean needHealing() {
        return this.getHealth() < this.getAttributeValue(Attributes.MAX_HEALTH) * 0.5;
    }

    public void alert(LivingEntity target) {
        isAlerted = true;
        this.setTarget(target);
    }

    @Override
    protected void setItemSlotAndDropWhenKilled(EquipmentSlot p_21469_, ItemStack p_21470_) {
        super.setItemSlotAndDropWhenKilled(p_21469_, p_21470_);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource source, DifficultyInstance difficulty) {
        mainHandChance.SetRandomSource(source);
        Item mainHand = mainHandChance.next();
        if (mainHand != Items.AIR) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(mainHand));
        }
        if (source.nextFloat() < this.hornChance) {
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.GOAT_HORN));
            this.canAlert = true;
        }
    }

    @Override
    protected void registerGoals() {
        this.bokoblinMobTarget = new NearestAttackableTargetGoal(this, Animal.class, 10, false, false, (target) -> {
            return (target instanceof Sheep || target instanceof Pig || target instanceof Cow) && !((Animal) target).isBaby();
        });

        this.goalSelector.addGoal(1, new FloatGoal(this));//float
        this.goalSelector.addGoal(2, new HealWithItemGoal(this));//if low hp, flee and heal
        //this.goalSelector.addGoal(2,new BokoblinAttackGoal(this,1.5,false)); //Detect and alart if player in range
        this.goalSelector.addGoal(3, new BokoblinAttackGoal(this, 1.5, false));
        this.goalSelector.addGoal(3, new DetectTargetGoal(this, true));//if alerted, hunt target down
        //this.goalSelector.addGoal(5, new ScavengeGoal(this, true));//Scavenge goal, seek healing items from the floor
        //this.goalSelector.addGoal(6, new DropInContainerGoal(this, true));//if inventory filled and container exists nearby

        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));//random idling
        this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 0.5D));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));//Retaliate
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));//hunt player
        this.targetSelector.addGoal(6, this.bokoblinMobTarget);//hunt passive mobs
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @javax.annotation.Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        RandomSource randomsource = levelAccessor.getRandom();
        SpawnGroupData spawnGroupData = super.finalizeSpawn(levelAccessor, difficulty, spawnType, groupData, tag);
        float f = difficulty.getSpecialMultiplier();
        this.setCanPickUpLoot(true);

        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(randomsource, difficulty);

        return (SpawnGroupData) spawnGroupData;
    }

    @Override
    public boolean wantsToPickUp(ItemStack item) {

        return super.wantsToPickUp(item);
    }

    @Override
    protected void pickUpItem(ItemEntity p_21471_) {
        super.pickUpItem(p_21471_);

    }

    @Override
    public boolean doHurtTarget(Entity target) {
        return super.doHurtTarget(target);
    }
}
