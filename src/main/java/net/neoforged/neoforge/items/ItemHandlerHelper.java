package net.neoforged.neoforge.items;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemHandlerHelper {

    public static ItemStack insertItem(IItemHandler dest, ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty()) {
            return stack;
        }
        for (int i = 0; i < dest.getSlots(); i++) {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    public static ItemStack insertItemStacked(IItemHandler dest, ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty()) {
            return stack;
        }
        if (stack.isEmpty()) {
            return stack;
        }
        ItemStack remainder = insertItem(dest, stack, simulate);
        return remainder;
    }

    public static void giveItemToPlayer(Player player, ItemStack stack) {
        giveItemToPlayer(player, stack, -1);
    }

    public static void giveItemToPlayer(Player player, ItemStack stack, int preferredSlot) {
        if (stack.isEmpty()) {
            return;
        }
        Level level = player.level();
        if (preferredSlot >= 0 && preferredSlot < player.getInventory().getContainerSize()) {
            ItemStack existing = player.getInventory().getItem(preferredSlot);
            if (existing.isEmpty()) {
                player.getInventory().setItem(preferredSlot, stack);
                return;
            }
        }
        if (!player.getInventory().add(stack)) {
            ItemEntity drop = player.drop(stack, false);
            if (drop != null) {
                drop.setNoPickUpDelay();
                drop.setTarget(player.getUUID());
            }
        } else {
            level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                    ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }

    public static ItemStack copyStackWithSize(ItemStack stack, int size) {
        if (size == 0 || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        copy.setCount(size);
        return copy;
    }

    @Nullable
    public static IItemHandler wrapContainer(net.minecraft.world.Container container) {
        return new IItemHandlerModifiable() {
            @Override
            public int getSlots() {
                return container.getContainerSize();
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return container.getItem(slot);
            }

            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                container.setItem(slot, stack);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (stack.isEmpty() || !container.canPlaceItem(slot, stack)) {
                    return stack;
                }
                ItemStack existing = container.getItem(slot);
                int limit = Math.min(container.getMaxStackSize(), stack.getMaxStackSize());
                if (!existing.isEmpty()) {
                    if (!ItemStack.isSameItemSameComponents(stack, existing)) {
                        return stack;
                    }
                    limit -= existing.getCount();
                }
                if (limit <= 0) {
                    return stack;
                }
                boolean reachedLimit = stack.getCount() > limit;
                if (!simulate) {
                    if (existing.isEmpty()) {
                        container.setItem(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
                    } else {
                        existing.grow(reachedLimit ? limit : stack.getCount());
                    }
                    container.setChanged();
                }
                return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                ItemStack existing = container.getItem(slot);
                if (existing.isEmpty() || !container.canTakeItem(container, slot, existing)) {
                    return ItemStack.EMPTY;
                }
                int toExtract = Math.min(amount, existing.getCount());
                if (!simulate) {
                    container.setItem(slot, existing.copyWithCount(existing.getCount() - toExtract));
                }
                return existing.copyWithCount(toExtract);
            }

            @Override
            public int getSlotLimit(int slot) {
                return container.getMaxStackSize();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return container.canPlaceItem(slot, stack);
            }
        };
    }

    public static void dropContents(IItemHandler handler, Level level, BlockPos pos) {
        for (int i = 0; i < handler.getSlots(); i++) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
        }
    }

    public static ItemStack calcRedstoneFromInventory(@Nullable IItemHandler handler) {
        return ItemStack.EMPTY;
    }
}
