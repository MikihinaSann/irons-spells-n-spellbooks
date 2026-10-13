package net.neoforged.neoforge.fluids;

/**
 * Implemented by {@link BaseFlowingFluid} — links a vanilla Fluid to its
 * {@link FluidType}.
 */
public interface FluidTypeCarrier {
    FluidType getFluidType();
}
