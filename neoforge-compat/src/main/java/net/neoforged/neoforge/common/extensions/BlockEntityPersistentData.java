package net.neoforged.neoforge.common.extensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.WeakHashMap;

/**
 * Backing store for {@link IBlockEntityExtension#getPersistentData()}. Kept
 * outside the interface so we don't need mixin state on BlockEntity.
 */
public final class BlockEntityPersistentData {
    private static final WeakHashMap<BlockEntity, CompoundTag> DATA = new WeakHashMap<>();

    private BlockEntityPersistentData() {
    }

    public static CompoundTag get(BlockEntity entity) {
        return DATA.computeIfAbsent(entity, e -> new CompoundTag());
    }
}
