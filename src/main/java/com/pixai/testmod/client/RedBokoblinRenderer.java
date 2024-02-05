package com.pixai.testmod.client;

import com.pixai.testmod.entity.BlueBokoblin;
import com.pixai.testmod.entity.RedBokoblin;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class RedBokoblinRenderer extends HumanoidMobRenderer<RedBokoblin, HumanoidModel<RedBokoblin>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("modtest:textures/entity/bokoblin/bokoblin_red.png");

    public RedBokoblinRenderer(EntityRendererProvider.Context context){
        super(context, new PlayerModel<RedBokoblin>(context.bakeLayer(ModelLayers.PLAYER),true), 34);
    }

    public RedBokoblinRenderer(EntityRendererProvider.Context context, HumanoidModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(RedBokoblin bokoblin) {
        return TEXTURE;
    }
}
