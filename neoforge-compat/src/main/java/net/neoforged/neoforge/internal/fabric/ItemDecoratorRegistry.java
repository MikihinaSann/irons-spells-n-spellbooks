package net.neoforged.neoforge.internal.fabric;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.ItemDecorator;

/** Store backing {@code RegisterItemDecorationsEvent}. */
public final class ItemDecoratorRegistry {
    private static final Map<Item, ItemDecorator> DECORATORS = new ConcurrentHashMap<>();

    private ItemDecoratorRegistry() {}

    public static Map<Item, ItemDecorator> map() {
        return DECORATORS;
    }

    public static ItemDecorator get(Item item) {
        return DECORATORS.get(item);
    }
}
