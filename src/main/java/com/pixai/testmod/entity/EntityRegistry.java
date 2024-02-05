package com.pixai.testmod.entity;

import com.pixai.testmod.Modtest;
import com.pixai.testmod.item.ItemRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Modtest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry {


    public static final DeferredRegister<EntityType<?>> ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Modtest.MODID);
    public static final RegistryObject<EntityType<RedBokoblin>> RED_BOKOBLIN = ENTITY_REGISTER.register("red_bokoblin", () ->
            EntityType.Builder.of(RedBokoblin::new, MobCategory.MONSTER)
                    .sized(1.6F, 1.8F)
                    .build("red_bokoblin"));

    public static final RegistryObject<EntityType<BlueBokoblin>> BLUE_BOKOBLIN = ENTITY_REGISTER.register("blue_bokoblin", () ->
            EntityType.Builder.of(BlueBokoblin::new, MobCategory.MONSTER)
                    .sized(1.6F, 1.8F)
                    .build("blue_bokoblin"));

    public static final RegistryObject<Item> RED_BOKOBLIN_EGG = ItemRegistry.RegisterEggEntity("red_bokoblin", RED_BOKOBLIN, 0xE07B00, 0x5F656B);
    public static final RegistryObject<Item> BUE_BOKOBLIN_EGG = ItemRegistry.RegisterEggEntity("blue_bokoblin", BLUE_BOKOBLIN, 0xE07B00, 0x5F656B);


    @SubscribeEvent
    public static void initializeAttributes(EntityAttributeCreationEvent event) {
        SpawnPlacements.register(RED_BOKOBLIN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(BLUE_BOKOBLIN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        event.put(RED_BOKOBLIN.get(), RedBokoblin.createAttributes().build());
        event.put(BLUE_BOKOBLIN.get(), RedBokoblin.createAttributes().build());
    }
}
