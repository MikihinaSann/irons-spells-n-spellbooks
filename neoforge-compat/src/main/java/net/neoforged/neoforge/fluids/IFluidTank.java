package net.neoforged.neoforge.fluids;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Single-tank fluid handler contract.
 */
public interface IFluidTank {

    @NotNull
    FluidStack getFluid();

    int getFluidAmount();

    int getCapacity();

    boolean isFluidValid(FluidStack stack);

    int fill(FluidStack resource, IFluidHandler.FluidAction action);

    @NotNull
    FluidStack drain(int maxDrain, IFluidHandler.FluidAction action);

    @NotNull
    FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action);
}
