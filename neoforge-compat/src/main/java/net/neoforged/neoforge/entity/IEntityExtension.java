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

    /** NeoForge's persistent-data key stored under the entity's NBT. */
    String PERSISTED_NBT_TAG = "ForgeData";
    default Entity self() {
        return (Entity) this;
    }

    /**
     * Whether the entity is "in fluid" for the given fluid type — the
     * NeoForge-typed variant is approximated by vanilla fluid checks.
     */
    default boolean isInFluidType() {
        return self().isInLiquid();
    }

    default boolean canSwimInFluidType(net.neoforged.neoforge.fluids.FluidType fluidType) {
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

    /** Called when the entity is added to its level (NeoForge hook). */
    default void onAddedToLevel() {
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

    /**
     * The {@link net.neoforged.neoforge.fluids.FluidType} the entity's eyes are in.
     */
    default net.neoforged.neoforge.fluids.FluidType getEyeInFluidType() {
        return self().level().getFluidState(
                net.minecraft.core.BlockPos.containing(self().getX(), self().getEyeY(), self().getZ()))
                .getFluidType();
    }

    /**
     * Starts/stops drop capture — implemented by the entity api mixin so
     * {@code spawnAtLocation} feeds the capture list (NeoForge semantics).
     */
    @Nullable
    default java.util.Collection<net.minecraft.world.entity.item.ItemEntity> captureDrops(
            java.util.Collection<net.minecraft.world.entity.item.ItemEntity> value) {
        return value;
    }

    /** NeoForge: whether a rider may mount while inside this fluid type. */
    default boolean canBeRiddenUnderFluidType(net.neoforged.neoforge.fluids.FluidType type,
            net.minecraft.world.entity.Entity rider) {
        return false;
    }

    /**
     * NeoForge's per-fluid-type height lookup over the entity's fluidHeight
     * map — implemented by the entity api mixin, which can see the field.
     */
    default double getFluidTypeHeight(net.neoforged.neoforge.fluids.FluidType type) {
        return 0;
    }

    /** NeoForge: the fluid type the entity is deepest inside. */
    default net.neoforged.neoforge.fluids.FluidType getMaxHeightFluidType() {
        return net.neoforged.neoforge.common.NeoForgeMod.EMPTY_TYPE.value();
    }

    /**
     * NeoForge's {@code serializeNBT(Provider)} — full entity data without the
     * id (vanilla {@code saveWithoutId} equivalent).
     */
    default net.minecraft.nbt.CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        self().saveWithoutId(tag);
        return tag;
    }

    /**
     * NeoForge's entity capability lookup — resolves through
     * {@link net.neoforged.neoforge.capabilities.CapabilityRegistry}.
     */
    @Nullable
    default <T> T getCapability(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent.Capability<T> capability) {
        return net.neoforged.neoforge.capabilities.CapabilityRegistry.getEntityCapability(capability, self());
    }

    /**
     * NeoForge's two-arg form — the context object is unused by our capability
     * registry (it only applies to block/item lookups).
     */
    @Nullable
    default <T, C> T getCapability(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent.Capability<T> capability,
            @Nullable C context) {
        return getCapability(capability);
    }

}
