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

public class BlueBokoblinRenderer extends HumanoidMobRenderer<Mob, PiglinModel<Mob>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("modtest:textures/entity/bokoblin/bokoblin_blue.png");

    public BlueBokoblinRenderer(EntityRendererProvider.Context context) {
        super(context, createModel(context.getModelSet(), ModelLayers.PIGLIN, false), 0.4f);
    }

    private static PiglinModel<Mob> createModel(EntityModelSet modelSet, ModelLayerLocation location, boolean hasEars) {
        PiglinModel<Mob> model = new PiglinModel<>(modelSet.bakeLayer(location));
        if (hasEars) {
            model.rightEar.visible = false;
        }

        return model;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Mob bokoblin) {
        return TEXTURE;
    }
}
