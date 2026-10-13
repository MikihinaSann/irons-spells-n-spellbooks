package net.neoforged.neoforge.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * Spawn egg whose entity type is looked up lazily (deferred registration).
 */
public class DeferredSpawnEggItem extends SpawnEggItem {
    private final Supplier<? extends EntityType<? extends Mob>> entityTypeSupplier;

    public DeferredSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> entityTypeSupplier, int backgroundColor, int highlightColor, Properties properties) {
        super(null, backgroundColor, highlightColor, properties);
        this.entityTypeSupplier = entityTypeSupplier;
    }

    public DeferredSpawnEggItem(DeferredHolder<EntityType<?>, ? extends EntityType<? extends Mob>> entityType, int backgroundColor, int highlightColor, Properties properties) {
        this((Supplier<? extends EntityType<? extends Mob>>) entityType::get, backgroundColor, highlightColor, properties);
    }

    @Override
    public EntityType<?> getType(net.minecraft.world.item.ItemStack stack) {
        return entityTypeSupplier.get();
    }

    /**
     * NeoForge exposes a registry-aware lookup; Fabric only needs the stack form
     * above.
     */
    public EntityType<? extends Mob> getType(net.minecraft.core.HolderLookup.Provider registries) {
        return entityTypeSupplier.get();
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        EntityType<? extends Mob> type = entityTypeSupplier.get();
        return type == null ? FeatureFlagSet.of() : type.requiredFeatures();
    }
}
