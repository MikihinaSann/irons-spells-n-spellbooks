package net.neoforged.neoforge.event.furnace;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired to determine the fuel value for an ItemStack.
 */
public class FurnaceFuelBurnTimeEvent extends Event implements ICancellableEvent {
    private final ItemStack itemStack;
    @Nullable
    private final RecipeType<?> recipeType;
    private int burnTime;

    public FurnaceFuelBurnTimeEvent(ItemStack itemStack, int burnTime, @Nullable RecipeType<?> recipeType) {
        this.itemStack = itemStack;
        this.burnTime = burnTime;
        this.recipeType = recipeType;
    }

    /**
     * Get the ItemStack "fuel" in question.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get the recipe type for which to obtain the burn time, if known.
     */
    @Nullable
    public RecipeType<?> getRecipeType() {
        return recipeType;
    }

    /**
     * Set the burn time for the given ItemStack.
     * Setting it to 0 will prevent the item from being used as fuel, overriding vanilla's decision.
     */
    public void setBurnTime(int burnTime) {
        if (burnTime >= 0) {
            this.burnTime = burnTime;
            setCanceled(true);
        }
    }

    /**
     * The resulting value of this event, the burn time for the ItemStack.
     */
    public int getBurnTime() {
        return burnTime;
    }
}
