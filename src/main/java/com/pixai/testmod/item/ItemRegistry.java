package com.pixai.testmod.item;

import com.pixai.testmod.Modtest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {

    private static final Item.Properties MISC_PROP = new Item.Properties().tab(CreativeModeTab.TAB_MISC);
    public static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Modtest.MODID);

    public static <I extends Mob> RegistryObject<Item> RegisterEggEntity(String name, RegistryObject<EntityType<I>> object, int primary, int secondary){
        return ITEM_REGISTRY.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(object, primary, secondary, MISC_PROP));
    }
}
