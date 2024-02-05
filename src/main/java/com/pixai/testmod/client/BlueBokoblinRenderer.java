package com.pixai.testmod.client;

import com.pixai.testmod.entity.BlueBokoblin;
import com.pixai.testmod.entity.RedBokoblin;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class BlueBokoblinRenderer extends HumanoidMobRenderer<BlueBokoblin, HumanoidModel<BlueBokoblin>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("modtest:textures/entity/bokoblin/bokoblin_blue.png");

    public BlueBokoblinRenderer(EntityRendererProvider.Context context, HumanoidModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    public BlueBokoblinRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<BlueBokoblin>(context.bakeLayer(ModelLayers.PLAYER), true), 34);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BlueBokoblin bokoblin) {
        return TEXTURE;
    }
}
