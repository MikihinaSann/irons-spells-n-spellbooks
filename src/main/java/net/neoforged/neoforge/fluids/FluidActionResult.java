package net.neoforged.neoforge.fluids;

import net.minecraft.world.item.ItemStack;

/**
 * Result of a fluid interaction — the modified item stack + whether the action
 * succeeded.
 */
public record FluidActionResult(boolean isSuccess, ItemStack result) {
    public static final FluidActionResult FAILURE = new FluidActionResult(false, ItemStack.EMPTY);

    public boolean isSuccess() {
        return isSuccess;
    }

    public ItemStack getResult() {
        return result;
    }
}
