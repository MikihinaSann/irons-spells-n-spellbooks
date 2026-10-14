package net.neoforged.neoforge.common.extensions;

import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * NeoForge {@link FluidState} extensions — injected onto
 * {@code net.minecraft.world.level.material.FluidState}.
 */
public interface IFluidStateExtension {
    private FluidState self() {
        return (FluidState) this;
    }

    /**
     * {@return the {@link FluidType} of this fluid state}
     */
    default FluidType getFluidType() {
        return self().getType().getFluidType();
    }
}
