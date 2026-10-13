package net.neoforged.neoforge.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge entity extension methods — injected onto Entity via Loom
 * {@code injected_interfaces}. All are safe defaults; NeoForge-specific hooks
 * are handled by the platform bridge where applicable.
 */
public interface IEntityExtension {
    private Entity self() {
        return (Entity) this;
    }

    /**
     * Whether the entity is "in fluid" for the given fluid type — the
     * NeoForge-typed variant is approximated by vanilla fluid checks.
     */
    default boolean isInFluidType() {
        return self().isInLiquid();
    }

    default boolean canSwimInFluidType(Object fluidType) {
        return self().isInLiquid();
    }

    /**
     * NeoForge extension — whether this entity is pushed by the given
     * {@link net.neoforged.neoforge.fluids.FluidType}.
     */
    default boolean isPushedByFluid(net.neoforged.neoforge.fluids.FluidType type) {
        return self().isPushedByFluid();
    }

    /** Whether passengers should visually "sit" on this entity. */
    default boolean shouldRiderSit() {
        return true;
    }

    /** Whether the rider should face the entity's facing direction. */
    default boolean shouldRiderFaceForward(Player player) {
        return true;
    }

    /** Called when the entity is removed from its level (NeoForge hook). */
    default void onRemovedFromLevel() {
    }

    /** Whether this entity has been added to its level. */
    default boolean isAddedToLevel() {
        return self().level() != null && !self().isRemoved();
    }

    /**
     * Whether this entity can drown in the given fluid type.
     */
    default boolean canDrownInFluidType(net.neoforged.neoforge.fluids.FluidType fluidType) {
        return true;
    }

    /**
     * Whether this entity is a multipart entity (e.g. vanilla EnderDragon or a
     * NeoForge-style multipart entity) whose sub-parts should be treated as the
     * whole for targeting purposes.
     */
    default boolean isMultipartEntity() {
        return false;
    }

    /**
     * The sub-parts of a multipart entity, or {@code null} when none.
     */
    @Nullable
    default PartEntity<?>[] getParts() {
        return null;
    }

    /**
     * Whether a rider entity may interact (use item / attack) while riding.
     */
    default boolean canRiderInteract() {
        return false;
    }

    /**
     * Returns the entities that "carry" this entity for pathfinding
     * through portals, etc. NeoForge exposes a mutable drop-capture list here;
     * our port keeps the simple default.
     */
    @Nullable
    default java.util.Collection<net.minecraft.world.entity.item.ItemEntity> captureDrops() {
        return null;
    }

    /**
     * NeoForge's persistent-data bag on every entity. Our port keeps a
     * per-instance {@link CompoundTag} via a weak map (not persisted across
     * world saves — matches the ported mods' in-session usage).
     */
    default CompoundTag getPersistentData() {
        return net.neoforged.neoforge.common.extensions.EntityPersistentData.get(self());
    }
}
