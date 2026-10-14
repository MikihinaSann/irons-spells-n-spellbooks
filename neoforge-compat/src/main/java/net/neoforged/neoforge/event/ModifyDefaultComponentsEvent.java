package net.neoforged.neoforge.event;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.Event;

/**
 * NeoForge's default-components event: listeners patch item component maps via
 * {@link #modify}. The collected patch builders are applied to the items'
 * {@code components} by {@link #apply()} after the event finishes dispatching.
 */
public class ModifyDefaultComponentsEvent extends Event {
    private final Map<Item, DataComponentPatch.Builder> changes = new IdentityHashMap<>();

    private DataComponentPatch.Builder builder(Item item) {
        return changes.computeIfAbsent(item, i -> {
            DataComponentPatch.Builder builder = DataComponentPatch.builder();
            i.components().forEach(builder::set);
            return builder;
        });
    }

    public <T> void modify(ItemLike item, DataComponentType<T> type, T value) {
        builder(item.asItem()).set(type, value);
    }

    public <T> void modify(ItemLike item, Supplier<DataComponentType<T>> type, T value) {
        modify(item, type.get(), value);
    }

    /**
     * Applies the listener's patch edits to the item's patch builder.
     */
    public void modify(ItemLike item, Consumer<DataComponentPatch.Builder> patch) {
        patch.accept(builder(item.asItem()));
    }

    /**
     * Applies all collected patches onto the items' component maps. Called once
     * by the compat layer after the event finishes dispatching.
     */
    public void apply() {
        changes.forEach((item, builder) -> item.modifyDefaultComponentsFrom(builder.build()));
    }
}
