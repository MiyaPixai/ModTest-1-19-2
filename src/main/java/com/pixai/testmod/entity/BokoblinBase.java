package com.pixai.testmod.entity;


import com.pixai.testmod.ai.HealWithItemGoal;
import com.pixai.testmod.util.RandomCollection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
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
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.logging.ILogger;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

public abstract class BokoblinBase extends Monster implements AlertableMob, HealableMob {
public abstract class BokoblinBase extends Monster implements AlertableMob, HealableMob, RangedAttackMob {
    private Goal bokoblinMobTarget;
    protected final RandomCollection<Item> mainHandChance;
    protected float hornChance;
    private boolean canAlert = false;
    private boolean isAlerted = false;
    private final SimpleContainer inventory = new SimpleContainer(4);
    private static final List<Item> HEALING_ITEMS = Arrays.asList(Items.BEEF, Items.COOKED_BEEF,
            Items.PORKCHOP, Items.COOKED_PORKCHOP,
            Items.MUTTON, Items.COOKED_MUTTON,
            Items.CHICKEN, Items.COOKED_CHICKEN);

    private final RangedBowAttackGoal<AbstractSkeleton> bowGoal = new RangedBowAttackGoal(this, 1.0, 30, 15.0F);
    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2, false);

    protected BokoblinBase(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.mainHandChance = new RandomCollection<Item>();
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @javax.annotation.Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        RandomSource randomsource = levelAccessor.getRandom();
        SpawnGroupData spawnGroupData = super.finalizeSpawn(levelAccessor, difficulty, spawnType, groupData, tag);
//        float f = difficulty.getSpecialMultiplier();
        this.setCanPickUpLoot(true);

        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(randomsource, difficulty);
        this.reassessAttackGoal();


        return (SpawnGroupData) spawnGroupData;
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
        //this.goalSelector.addGoal(5, new ScavengeGoal(this, true));//Scavenge goal, seek healing items from the floor
        //this.goalSelector.addGoal(6, new DropInContainerGoal(this, true));//if inventory filled and container exists nearby
        this.goalSelector.addGoal(3, new HealWithItemGoal(this));//if low hp, flee and heal

        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));//random idling
        this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 0.5D));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));//Retaliate
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));//hunt player
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

}
