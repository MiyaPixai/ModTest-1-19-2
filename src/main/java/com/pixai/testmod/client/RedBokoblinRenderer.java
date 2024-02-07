package com.pixai.testmod.client;

import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class RedBokoblinRenderer extends HumanoidMobRenderer<Mob, PiglinModel<Mob>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("modtest:textures/entity/bokoblin/bokoblin_red.png");

    public RedBokoblinRenderer(EntityRendererProvider.Context context) {
        super(context, createModel(context.getModelSet(),ModelLayers.PIGLIN, false), 34);
    }

    private static PiglinModel<Mob> createModel(EntityModelSet modelSet, ModelLayerLocation location, boolean hideEars) {
        PiglinModel<Mob> model = new PiglinModel<>(modelSet.bakeLayer(location));
        if (hideEars) {
            model.rightEar.visible = false;
        }

        return model;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Mob bokoblin) {
        return TEXTURE;
    }
}
