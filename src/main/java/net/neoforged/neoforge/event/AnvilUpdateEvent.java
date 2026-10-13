package net.neoforged.neoforge.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

public class AnvilUpdateEvent extends Event implements ICancellableEvent {
    private final ItemStack left;
    private final ItemStack right;
    private final Player player;
    private ItemStack output;
    private String itemName;
    private int cost;
    private int materialCost;
    private final boolean isAnvilRecipe;

    public AnvilUpdateEvent(ItemStack left, ItemStack right, @Nullable String name, int cost, Player player) {
        this(left, right, name, cost, player, false);
    }

    public AnvilUpdateEvent(ItemStack left, ItemStack right, @Nullable String name, int cost, Player player, boolean isAnvilRecipe) {
        this.left = left;
        this.right = right;
        this.player = player;
        this.itemName = name;
        this.cost = cost;
        this.isAnvilRecipe = isAnvilRecipe;
    }

    public ItemStack getLeft() {
        return left;
    }

    public ItemStack getRight() {
        return right;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getOutput() {
        return output;
    }

    public void setOutput(ItemStack output) {
        this.output = output;
    }

    @Nullable
    public String getItemName() {
        return itemName;
    }

    @Nullable
    public String getName() {
        return itemName;
    }

    public void setItemName(@Nullable String name) {
        this.itemName = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getMaterialCost() {
        return materialCost;
    }

    public void setMaterialCost(int materialCost) {
        this.materialCost = materialCost;
    }

    public boolean isAnvilRecipe() {
        return isAnvilRecipe;
    }
}
