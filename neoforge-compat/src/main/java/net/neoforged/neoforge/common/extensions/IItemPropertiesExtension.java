package net.neoforged.neoforge.common.extensions;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * NeoForge's Item.Properties convenience overloads — injected onto
 * {@code Item.Properties} via Loom {@code injected_interfaces}.
 */
public interface IItemPropertiesExtension {
    private Item.Properties self() {
        return (Item.Properties) this;
    }

    /** Deferred-holder friendly component overload. */
    default <T> Item.Properties component(Supplier<? extends DataComponentType<T>> type, T value) {
        return self().component(type.get(), value);
    }
}
