package net.neoforged.neoforge.common.extensions;

import net.minecraft.world.entity.EntityType;

/**
 * NeoForge {@link EntityType.Builder} extensions — injected onto
 * {@code net.minecraft.world.entity.EntityType$Builder}.
 *
 * <p>NeoForge renames the vanilla chainable setters and adds a velocity-update
 * toggle. On Fabric, velocity updates are always sent (matching the NeoForge
 * default of {@code true}), so {@link #setShouldReceiveVelocityUpdates} only
 * needs to exist for source compatibility.
 */
public interface IEntityTypeBuilderExtension<T extends net.minecraft.world.entity.Entity> {
    private EntityType.Builder<T> self() {
        return (EntityType.Builder<T>) (Object) this;
    }

    /**
     * NeoForge-style setter for {@link EntityType.Builder#updateInterval}.
     */
    default EntityType.Builder<T> setUpdateInterval(int interval) {
        return self().updateInterval(interval);
    }

    /**
     * NeoForge-style setter for {@link EntityType.Builder#clientTrackingRange}.
     */
    default EntityType.Builder<T> setTrackingRange(int range) {
        return self().clientTrackingRange(range);
    }

    /**
     * Alias of {@link #setTrackingRange(int)} used by some upstream code.
     */
    default EntityType.Builder<T> setClientTrackingRange(int range) {
        return self().clientTrackingRange(range);
    }

    /**
     * NeoForge adds an opt-out for entity velocity sync; vanilla always sends
     * velocity updates, matching {@code true} for every upstream call site.
     */
    default EntityType.Builder<T> setShouldReceiveVelocityUpdates(boolean value) {
        return self();
    }
}
