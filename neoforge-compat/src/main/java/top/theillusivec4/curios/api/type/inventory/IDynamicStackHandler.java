package top.theillusivec4.curios.api.type.inventory;

import net.minecraft.world.item.ItemStack;

/**
 * Curios IDynamicStackHandler - per-slot-type view of the equipped stacks,
 * backed by a Trinkets inventory.
 */
public interface IDynamicStackHandler {

    /** Number of slots in this handler. */
    int getSlots();

    /** The stack in the given slot. */
    ItemStack getStackInSlot(int slot);

    /** Replaces the stack in the given slot. */
    void setStackInSlot(int slot, ItemStack stack);

    /** Max stack size allowed in the slot. */
    default int getSlotLimit(int slot) {
        return 64;
    }

    /** Whether the stack may go into the slot. */
    default boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }
}
