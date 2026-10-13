package net.neoforged.neoforge.items;

import net.minecraft.world.item.ItemStack;

/**
 * NeoForge item-handler contract, backed by a Container on Fabric.
 */
public interface IItemHandler {

    int getSlots();

    ItemStack getStackInSlot(int slot);

    /**
     * Inserts the stack, returns remainder.
     */
    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    /**
     * Extracts up to {@code amount}, returns extracted stack.
     */
    ItemStack extractItem(int slot, int amount, boolean simulate);

    int getSlotLimit(int slot);

    default boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }
}
