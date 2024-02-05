package com.pixai.testmod;

import com.pixai.testmod.client.BlueBokoblinRenderer;
import com.pixai.testmod.client.RedBokoblinRenderer;
import com.pixai.testmod.entity.EntityRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Modtest.MODID, value = Dist.CLIENT)
public class ClientSetup {
    public void ClientInit(){
        EntityRenderers.register(EntityRegistry.BLUE_BOKOBLIN.get(), BlueBokoblinRenderer::new);
        EntityRenderers.register(EntityRegistry.RED_BOKOBLIN.get(), RedBokoblinRenderer::new);

    }
}
