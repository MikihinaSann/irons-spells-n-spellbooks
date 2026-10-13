package net.neoforged.neoforge.items;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Slot backed by an {@link IItemHandler}.
 */
public class SlotItemHandler extends Slot {
    private static final Container EMPTY = new SimpleContainer(0);
    private final IItemHandler itemHandler;
    private final int index;

    public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(EMPTY, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.isEmpty() || !itemHandler.isItemValid(index, stack)) {
            return false;
        }
        return itemHandler.insertItem(index, stack, true).getCount() < stack.getCount();
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return itemHandler.getStackInSlot(index);
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        if (itemHandler instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(index, stack);
        } else {
            itemHandler.extractItem(index, Integer.MAX_VALUE, false);
            itemHandler.insertItem(index, stack, false);
        }
        setChanged();
    }

    @Override
    public void onQuickCraft(@NotNull ItemStack oldStack, @NotNull ItemStack newStack) {
    }

    @Override
    public int getMaxStackSize() {
        return itemHandler.getSlotLimit(index);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);
        IItemHandler handler = this.getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(index);
        if (handler instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(index, ItemStack.EMPTY);
            ItemStack remainder = modifiable.insertItem(index, maxAdd, true);
            modifiable.setStackInSlot(index, currentStack);
            return maxInput - remainder.getCount();
        }
        ItemStack remainder = handler.insertItem(index, maxAdd, true);
        int current = currentStack.getCount();
        int added = maxInput - remainder.getCount();
        return current + added;
    }

    @Override
    public boolean mayPickup(@NotNull Player playerIn) {
        return !itemHandler.extractItem(index, 1, true).isEmpty();
    }

    @Override
    @NotNull
    public ItemStack remove(int amount) {
        return itemHandler.extractItem(index, amount, false);
    }
}
