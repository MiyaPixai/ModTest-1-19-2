package com.pixai.testmod.entity;


import com.pixai.testmod.Modtest;
import com.pixai.testmod.ai.HealWithItemGoal;
import com.pixai.testmod.ai.RudimentaryCookGoal;
import com.pixai.testmod.ai.ScavengeGoal;
import com.pixai.testmod.util.RandomCollection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.function.Predicate;

public abstract class BokoblinBase extends Monster implements AlertableMob, HealableMob, RangedAttackMob, RudimentaryCookMob {

    private static final int INVENTORY_SIZE = 8;
    private Goal bokoblinMobTarget;
    protected final RandomCollection<Item> mainHandChance;
    protected float hornChance;
    private boolean canAlert = false;
    private boolean isAlerted = false;
    private SimpleContainer inventory = new SimpleContainer(INVENTORY_SIZE);
    private final RangedBowAttackGoal<AbstractSkeleton> bowGoal = new RangedBowAttackGoal(this, 1.0, 30, 15.0F);
    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2, false);

    protected BokoblinBase(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.mainHandChance = new RandomCollection<Item>();
    }

    //Alertable interface implementation
    public boolean canAlert() {
        return canAlert;
    }

    public boolean isAlerted() {
        return isAlerted;
    }

    public void alert(LivingEntity target) {
        isAlerted = true;
        this.setTarget(target);
    }

    //Healable interface implementation
    public boolean hasHealingItem() {
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            if (this.inventory.getItem(i).isEdible()) {
                return true;
            }
        }
        return false;
    }

    public boolean needHealing() {
        return this.getHealth() < this.getAttributeValue(Attributes.MAX_HEALTH) * 0.5;
    }

    //Cook interface Implmentation
    public ItemStack getFoodItem(Predicate<ItemStack> isCookableFood) {
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            if (isCookableFood.test(this.inventory.getItem(i))) {
                return this.inventory.getItem(i);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setAggressive(boolean p_21562_) {
        super.setAggressive(p_21562_);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @javax.annotation.Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        RandomSource randomsource = levelAccessor.getRandom();
        SpawnGroupData spawnGroupData = super.finalizeSpawn(levelAccessor, difficulty, spawnType, groupData, tag);
//        float f = difficulty.getSpecialMultiplier();
        this.setCanPickUpLoot(true);

        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(randomsource, difficulty);
        this.reassessAttackGoal();

        return spawnGroupData;
    }

    public void performRangedAttack(LivingEntity p_32141_, float p_32142_) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, (item) -> {
            return item instanceof BowItem;
        })));
        AbstractArrow abstractarrow = this.getArrow(itemstack, p_32142_);
        if (this.getMainHandItem().getItem() instanceof BowItem) {
            abstractarrow = ((BowItem) this.getMainHandItem().getItem()).customArrow(abstractarrow);
        }

        double d0 = p_32141_.getX() - this.getX();
        double d1 = p_32141_.getY(0.3333333333333333) - abstractarrow.getY();
        double d2 = p_32141_.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        abstractarrow.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(abstractarrow);
    }

    protected AbstractArrow getArrow(ItemStack p_32156_, float p_32157_) {
        return ProjectileUtil.getMobArrow(this, p_32156_, p_32157_);
    }

    @Override
    public boolean wantsToPickUp(ItemStack item) {
        if (canTakeItem(item)) {
            return true;
        }
        return super.wantsToPickUp(item);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource p_21385_, int p_21386_, boolean p_21387_) {
        super.dropCustomDeathLoot(p_21385_, p_21386_, p_21387_);
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
    }

    @Override
    protected void pickUpItem(ItemEntity item) {
        ItemStack itemstack = item.getItem();
        boolean pickedUp = false;
        if (this.equipItemIfPossible(itemstack)) {
            reassessAttackGoal();
            pickedUp = true;
        } else if (itemstack.is(Items.GOAT_HORN)) {
            ItemStack offhand = this.getItemBySlot(EquipmentSlot.OFFHAND);
            if (offhand.isEmpty()) {
                this.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, itemstack);
                this.canAlert = true;
                pickedUp = true;
            }
        } else {
            if (inventory.canAddItem(item.getItem())) {
                this.onItemPickup(item);
                this.take(item, item.getItem().getCount());
                ItemStack stack = this.inventory.addItem(itemstack);
                if (stack.isEmpty()) {
                    Modtest.LOGGER.debug("Discarding Item");
                    item.discard();
                } else {
                    itemstack.setCount(stack.getCount());
                }
            }
//            if (stackIndex != -1) {
//                ItemStack targetStack = this.itemStacks.get(stackIndex);
//
//                if (targetStack.isEmpty()) {
//                    this.itemStacks.set(stackIndex, itemstack);
//                } else {
//                    int countRemaining = itemStacks.get(stackIndex).getMaxStackSize() - itemStacks.get(stackIndex).getCount();
//                    if (itemstack.getCount() > countRemaining) {
//                        this.itemStacks.get(stackIndex).setCount(itemStacks.get(stackIndex).getMaxStackSize());
//                        itemstack.split(countRemaining);
//                    } else {
//                        this.itemStacks.get(stackIndex).setCount(itemStacks.get(stackIndex).getCount() + itemstack.getCount());
//                        pickedUp = true;
//                    }
//                }
//            }
        }

        if (pickedUp) {
            this.onItemPickup(item);
            this.take(item, itemstack.getCount());
            item.discard();
        }
    }

    @Override
    public boolean canHoldItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof TieredItem || item instanceof BowItem;
    }

    @Override
    public boolean canTakeItem(ItemStack item) {
        if (this.inventory.canAddItem(item)) {
            return true;
        }
        return super.canTakeItem(item);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource source, DifficultyInstance difficulty) {
        mainHandChance.SetRandomSource(source);
        Item mainHand = mainHandChance.next();
        if (mainHand != Items.AIR) {
            this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, new ItemStack(mainHand));
        }
        if (source.nextFloat() < this.hornChance) {
            ItemStack horn = new ItemStack(Items.GOAT_HORN);
            CompoundTag tag = horn.getOrCreateTag();
            tag.putString("instrument", "dream_goat_horn");

            horn.setTag(tag);
            this.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, horn);
            this.canAlert = true;
        }
    }

    @Override
    protected void registerGoals() {
        this.bokoblinMobTarget = new NearestAttackableTargetGoal(this, Animal.class, 10, false, false, (target) -> {
            return (target instanceof Sheep || target instanceof Pig || target instanceof Cow) && !((Animal) target).isBaby();
        });

        this.goalSelector.addGoal(1, new FloatGoal(this));//float
        this.goalSelector.addGoal(3, new HealWithItemGoal(this));//if low hp, flee and heal

        this.goalSelector.addGoal(5, new ScavengeGoal(this, 0.8f, 20, 10));
        this.goalSelector.addGoal(6, new RudimentaryCookGoal<BokoblinBase>(this, 0.75f));//cook food if fire camp nearby
        //this.goalSelector.addGoal(7, new DropInContainerGoal(this, true));//if inventory filled and container exists nearby


        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));//random idling
        this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 0.5D));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));//Retaliate
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true));//hunt player
        this.targetSelector.addGoal(6, this.bokoblinMobTarget);//hunt passive mobs
    }

    private void reassessAttackGoal() {
        if (this.level != null && !this.level.isClientSide) {
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, (item) -> {
                return item instanceof BowItem;
            }));
            if (itemstack.is(Items.BOW)) {
                int i = 20;
                if (this.level.getDifficulty() != Difficulty.HARD) {
                    i = 40;
                }

                this.bowGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(4, this.bowGoal);
            } else {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }
        }
    }

    public void readAdditionalSaveData(CompoundTag p_34725_) {
        super.readAdditionalSaveData(p_34725_);
        this.inventory.fromTag(p_34725_.getList("Inventory", 10));
    }

    public void addAdditionalSaveData(CompoundTag p_34751_) {
        super.addAdditionalSaveData(p_34751_);
        p_34751_.put("Inventory", this.inventory.createTag());
    }
}
