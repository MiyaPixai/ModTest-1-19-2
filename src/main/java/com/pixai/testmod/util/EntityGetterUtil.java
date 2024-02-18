package com.pixai.testmod.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class EntityGetterUtil {

    public static <T extends Entity> List<T> getNearbyEntitiesOfClass(Class<T> entityClass, Level level, AABB boundingBox) {
        return level.getEntitiesOfClass(entityClass, boundingBox);
    }

    public static <T extends Entity> T getNearestEntityOfClass(Class<T> entityClass, Level level, AABB boundingBox, Vec3 position) {

        List<T> entityList = level.getEntitiesOfClass(entityClass, boundingBox);

        if (entityList.isEmpty())
            return null;

        double maxDist = Double.MAX_VALUE;
        T entity = null;
        Iterator<T> iterator = entityList.iterator();

        while (true) {
            T nearbyEntity;
            double entityDist;
            do {
                if (!iterator.hasNext()) {
                    return entity;
                }

                nearbyEntity = iterator.next();

                entityDist = nearbyEntity.distanceToSqr(position);
            } while (maxDist != -1.0 && !(entityDist < maxDist));

            maxDist = entityDist;
            entity = nearbyEntity;
        }
    }

    public static <T extends BlockEntity> List<T> getNearbyBlockEntityOfClass(Class<T> blockClass, BlockPos epicenter, Level level) {
        List<LevelChunk> nearbyChunks = Lists.newArrayList();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                nearbyChunks.add(level.getChunkAt(epicenter.offset(i * LevelChunkSection.SECTION_WIDTH, 0, j * LevelChunkSection.SECTION_WIDTH)));
            }
        }

        Map<BlockPos, BlockEntity> nearbyBlocks = Maps.newHashMap();
        for (int i = 0; i < nearbyChunks.size(); i++) {
            nearbyBlocks.putAll(nearbyChunks.get(i).getBlockEntities());
        }

        List<T> nearbyBlocksOfClass = Lists.newArrayList();
        nearbyBlocks.forEach((BlockPos pos, BlockEntity block) -> {
            if (blockClass.isInstance(block)) {
                nearbyBlocksOfClass.add((T) block);
            }
        });

        return nearbyBlocksOfClass;
    }
}
