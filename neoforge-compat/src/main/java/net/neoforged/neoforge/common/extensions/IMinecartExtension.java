package net.neoforged.neoforge.common.extensions;

import net.minecraft.world.entity.vehicle.AbstractMinecart;

/**
 * NeoForge's {@code AbstractMinecart} extensions — rail hooks extracted from
 * {@code moveAlongTrack}.
 */
public interface IMinecartExtension {
    private AbstractMinecart self() {
        return (AbstractMinecart) (Object) this;
    }

    /** Whether this minecart can carry a passenger. */
    default boolean canBeRidden() {
        return self().getMinecartType() == AbstractMinecart.Type.RIDEABLE;
    }

    /** Whether vanilla rail logic (slope boost, powered rails) applies. */
    default boolean shouldDoRailFunctions() {
        return true;
    }

    /** Gravity acceleration applied while moving along a sloped rail. */
    default double getSlopeAdjustment() {
        return 0.0078125D;
    }
}
