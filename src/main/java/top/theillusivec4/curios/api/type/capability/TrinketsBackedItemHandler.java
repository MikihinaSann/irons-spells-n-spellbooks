package top.theillusivec4.curios.api.type.capability;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

/**
 * {@link ICuriosItemHandler} implemented on top of a Trinkets
 * {@link TrinketComponent}. Curios slot identifiers map to Trinkets slot names
 * (without the group prefix).
 */
public class TrinketsBackedItemHandler implements ICuriosItemHandler {
    private final TrinketComponent component;
    private final LivingEntity wearer;

    public TrinketsBackedItemHandler(TrinketComponent component, LivingEntity wearer) {
        this.component = component;
        this.wearer = wearer;
    }

    @Override
    public LivingEntity getWearer() {
        return wearer;
    }

    private SlotContext toContext(SlotReference ref) {
        return new SlotContext(ref.inventory().getSlotType().getName(), wearer, ref.index(), false, true);
    }

    @Override
    public List<SlotResult> findCurios(Predicate<ItemStack> predicate) {
        List<SlotResult> results = new ArrayList<>();
        for (var tuple : component.getEquipped(predicate)) {
            results.add(new SlotResult(toContext(tuple.getA()), tuple.getB()));
        }
        return results;
    }

    @Override
    public Optional<SlotResult> findCurio(String identifier, int index) {
        int remaining = index;
        for (var group : component.getInventory().values()) {
            TrinketInventory inv = group.get(identifier);
            if (inv != null) {
                if (remaining < inv.getSlotType().getAmount()) {
                    ItemStack stack = inv.getItem(remaining);
                    if (!stack.isEmpty()) {
                        return Optional.of(new SlotResult(
                                new SlotContext(identifier, wearer, index, false, true), stack));
                    }
                    return Optional.empty();
                }
                remaining -= inv.getSlotType().getAmount();
            }
        }
        return Optional.empty();
    }

    @Override
    public List<SlotResult> findCurios(String identifier) {
        List<SlotResult> results = new ArrayList<>();
        for (var group : component.getInventory().values()) {
            TrinketInventory inv = group.get(identifier);
            if (inv != null) {
                for (int i = 0; i < inv.getSlotType().getAmount(); i++) {
                    ItemStack stack = inv.getItem(i);
                    if (!stack.isEmpty()) {
                        results.add(new SlotResult(new SlotContext(identifier, wearer, i, false, true), stack));
                    }
                }
            }
        }
        return results;
    }

    @Override
    public boolean setEquippedCurio(String identifier, int index, ItemStack stack) {
        int remaining = index;
        for (var group : component.getInventory().values()) {
            TrinketInventory inv = group.get(identifier);
            if (inv != null) {
                if (remaining < inv.getSlotType().getAmount()) {
                    inv.setItem(remaining, stack);
                    return true;
                }
                remaining -= inv.getSlotType().getAmount();
            }
        }
        return false;
    }

    @Override
    public Set<String> getSlotIdentifiers() {
        Set<String> ids = new TreeSet<>();
        for (var group : component.getInventory().values()) {
            ids.addAll(group.keySet());
        }
        return ids;
    }

    @Override
    public net.neoforged.neoforge.items.IItemHandlerModifiable getEquippedCurios() {
        List<TrinketInventory> inventories = new ArrayList<>();
        for (var group : component.getInventory().values()) {
            inventories.addAll(group.values());
        }
        return new net.neoforged.neoforge.items.IItemHandlerModifiable() {
            @Override
            public int getSlots() {
                return inventories.stream().mapToInt(inv -> inv.getSlotType().getAmount()).sum();
            }

            private TrinketInventory resolve(int slot) {
                int remaining = slot;
                for (TrinketInventory inv : inventories) {
                    if (remaining < inv.getSlotType().getAmount()) {
                        slotIndex = remaining;
                        return inv;
                    }
                    remaining -= inv.getSlotType().getAmount();
                }
                return null;
            }

            private int slotIndex;

            @Override
            public ItemStack getStackInSlot(int slot) {
                TrinketInventory inv = resolve(slot);
                return inv == null ? ItemStack.EMPTY : inv.getItem(slotIndex);
            }

            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                TrinketInventory inv = resolve(slot);
                if (inv != null) {
                    inv.setItem(slotIndex, stack);
                }
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                TrinketInventory inv = resolve(slot);
                if (inv == null) {
                    return stack;
                }
                ItemStack existing = inv.getItem(slotIndex);
                if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(existing, stack)) {
                    return stack;
                }
                int toMove = Math.min(stack.getCount(), inv.getSlotType().getAmount() > 0 ? 1 : 0);
                if (toMove <= 0) {
                    return stack;
                }
                if (!simulate) {
                    inv.setItem(slotIndex, stack.copyWithCount(existing.getCount() + toMove));
                }
                return stack.copyWithCount(stack.getCount() - toMove);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                TrinketInventory inv = resolve(slot);
                if (inv == null) {
                    return ItemStack.EMPTY;
                }
                ItemStack existing = inv.getItem(slotIndex);
                if (existing.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                int toExtract = Math.min(amount, existing.getCount());
                if (!simulate) {
                    inv.setItem(slotIndex, existing.copyWithCount(existing.getCount() - toExtract));
                }
                return existing.copyWithCount(toExtract);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }
}
