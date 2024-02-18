package com.pixai.testmod;

import com.machinezoo.noexception.throwing.ThrowingConsumer;
import com.mojang.logging.LogUtils;
import com.pixai.testmod.entity.EntityRegistry;
import com.pixai.testmod.item.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Modtest.MODID)
@Mod.EventBusSubscriber(modid = Modtest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Modtest
{
    public static final String MODID = "modtest";
    public static final ClientSetup client = new ClientSetup();

    public static final Logger LOGGER = LogUtils.getLogger();

    public Modtest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        EntityRegistry.ENTITY_REGISTER.register(modEventBus);
        ItemRegistry.ITEM_REGISTRY.register(modEventBus);

        modEventBus.addListener(this::setupClient);
    }

    private void setupClient(FMLClientSetupEvent event)
    {
        client.ClientInit();
    }
}
