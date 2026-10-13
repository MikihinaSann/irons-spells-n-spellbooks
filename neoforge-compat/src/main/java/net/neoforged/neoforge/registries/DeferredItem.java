package net.neoforged.neoforge.registries;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * NeoForge-style typed holder for items.
 */
public class DeferredItem<T extends Item> extends DeferredHolder<Item, T> {
    public DeferredItem(ResourceKey<?> registryKey, ResourceLocation location, Supplier<T> supplier) {
        super(registryKey, location, supplier);
    }

    /** Creates a new stack of size 1. */
    public ItemStack toStack() {
        return new ItemStack(this);
    }

    /** NeoForge's {@code DeferredItem.createItem(ResourceLocation)}. */
    public static <T extends Item> DeferredItem<T> createItem(ResourceLocation key) {
        return new DeferredItem<>(
                net.minecraft.core.registries.Registries.ITEM, key, () -> null);
    }

    public ItemStack toStack(int count) {
        return new ItemStack(this, count);
    }
}
