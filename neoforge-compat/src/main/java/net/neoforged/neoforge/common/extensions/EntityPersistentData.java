package net.neoforged.neoforge.common.extensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import java.util.WeakHashMap;

/**
 * Backing store for {@code IEntityExtension#getPersistentData()} - mirrors
 * {@link BlockEntityPersistentData}.
 */
public final class EntityPersistentData {
    private static final WeakHashMap<Entity, CompoundTag> DATA = new WeakHashMap<>();

    private EntityPersistentData() {
    }

    public static CompoundTag get(Entity entity) {
        return DATA.computeIfAbsent(entity, e -> new CompoundTag());
    }
}
