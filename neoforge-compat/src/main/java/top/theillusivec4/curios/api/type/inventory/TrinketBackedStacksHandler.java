package top.theillusivec4.curios.api.type.inventory;

import java.util.Map;

import dev.emi.trinkets.api.TrinketInventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

/**
 * {@link ICurioStacksHandler} + {@link IDynamicStackHandler} backed by a
 * Trinkets {@link TrinketInventory}.
 */
public class TrinketBackedStacksHandler implements ICurioStacksHandler {
    private final String identifier;
    private final TrinketInventory inventory;
    private final IDynamicStackHandler stacks = new StackHandler();

    public TrinketBackedStacksHandler(String identifier, TrinketInventory inventory) {
        this.identifier = identifier;
        this.inventory = inventory;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public IDynamicStackHandler getStacks() {
        return stacks;
    }

    @Override
    public IDynamicStackHandler getCosmeticStacks() {
        // Trinkets has no cosmetic/render split; cosmetic view == main view.
        return stacks;
    }

    @Override
    public int getSlots() {
        return inventory.getSlotType().getAmount();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean canToggleRendering() {
        return true;
    }

    @Override
    public Map<ResourceLocation, AttributeModifier> getModifiers() {
        return Map.of();
    }

    @Override
    public Map<ResourceLocation, AttributeModifier> getSizeModifiers() {
        return Map.of();
    }

    @Override
    public void addPermanentModifier(AttributeModifier modifier) {
        // Trinkets slot counts are data-driven; per-item size modifiers are not
        // modeled. Equipped item modifiers that grow slots are a curios concept
        // with no trinkets equivalent - no-op (documented gap).
    }

    @Override
    public void removeModifier(ResourceLocation id) {
    }

    private class StackHandler implements IDynamicStackHandler {
        @Override
        public int getSlots() {
            return TrinketBackedStacksHandler.this.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inventory.getItem(slot);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            inventory.setItem(slot, stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return TrinketBackedStacksHandler.this.inventory.canPlaceItem(slot, stack);
        }
    }
}
