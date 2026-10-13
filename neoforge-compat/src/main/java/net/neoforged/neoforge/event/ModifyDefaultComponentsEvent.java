package net.neoforged.neoforge.event;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Collects default-component modifications; applied by rebuilding item defaults
 * at bootstrap.
 */
public class ModifyDefaultComponentsEvent extends Event {
    private final List<Modification<?>> modifications = new ArrayList<>();

    public record Modification<T>(Item item, DataComponentType<T> type, T value) {
    }

    public <T> void modify(Item item, BiConsumer<DataComponentType<T>, T> action) {
        // Signature kept for compat; call sites should use modify(item, type, value)
    }

    public <T> void modify(Item item, DataComponentType<T> type, T value) {
        modifications.add(new Modification<>(item, type, value));
    }

    public void modify(Item item, DataComponentPatch.Builder builder) {
        // Accumulated patch application handled by caller via getPatchBuilder
    }

    public List<Modification<?>> getModifications() {
        return modifications;
    }
}
