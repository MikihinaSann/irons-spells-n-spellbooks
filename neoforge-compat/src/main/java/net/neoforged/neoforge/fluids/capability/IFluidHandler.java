package net.neoforged.neoforge.fluids.capability;

import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * NeoForge fluid handler contract.
 */
public interface IFluidHandler {

    enum FluidAction {
        EXECUTE, SIMULATE;

        public boolean execute() {
            return this == EXECUTE;
        }

        public boolean simulate() {
            return this == SIMULATE;
        }
    }

    int getTanks();

    @NotNull
    FluidStack getFluidInTank(int tank);

    int getTankCapacity(int tank);

    boolean isFluidValid(int tank, @NotNull FluidStack stack);

    /**
     * Fills the handler, returns amount filled.
     */
    int fill(FluidStack resource, FluidAction action);

    /**
     * Drains the given fluid stack (by type and amount), returns drained stack.
     */
    @NotNull
    FluidStack drain(FluidStack resource, FluidAction action);

    /**
     * Drains up to {@code maxDrain} amount of any fluid, returns drained stack.
     */
    @NotNull
    FluidStack drain(int maxDrain, FluidAction action);
}
