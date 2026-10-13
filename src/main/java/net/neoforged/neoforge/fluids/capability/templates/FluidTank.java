package net.neoforged.neoforge.fluids.capability.templates;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * A single-capacity fluid tank, matching NeoForge's FluidTank semantics.
 */
public class FluidTank implements IFluidTank, IFluidHandler {
    protected Predicate<FluidStack> validator;
    @NotNull
    protected FluidStack fluid = FluidStack.EMPTY;
    protected int capacity;

    public FluidTank(int capacity) {
        this(capacity, fluid -> true);
    }

    public FluidTank(int capacity, Predicate<FluidStack> validator) {
        this.capacity = capacity;
        this.validator = validator;
    }

    public FluidTank setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public FluidTank setValidator(Predicate<FluidStack> validator) {
        this.validator = validator;
        return this;
    }

    @Override
    @NotNull
    public FluidStack getFluid() {
        return fluid;
    }

    public void setFluid(FluidStack stack) {
        this.fluid = stack;
    }

    @Override
    public int getFluidAmount() {
        return fluid.getAmount();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    protected void onContentsChanged() {
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        if (action.simulate()) {
            if (fluid.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.is(resource.getFluid())) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = resource.copyWithAmount(Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return fluid.getAmount();
        }
        if (!fluid.is(resource.getFluid())) {
            return 0;
        }
        int filled = Math.min(capacity - fluid.getAmount(), resource.getAmount());
        if (filled > 0) {
            fluid.grow(filled);
            onContentsChanged();
        }
        return filled;
    }

    @NotNull
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (fluid.isEmpty() || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }
        int drained = Math.min(fluid.getAmount(), maxDrain);
        if (action.execute()) {
            FluidStack result = fluid.copyWithAmount(drained);
            fluid.shrink(drained);
            onContentsChanged();
            return result;
        }
        return fluid.copyWithAmount(drained);
    }

    @NotNull
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(fluid, resource)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    @NotNull
    public FluidStack getFluidInTank(int tank) {
        return fluid;
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return isFluidValid(stack);
    }
}
