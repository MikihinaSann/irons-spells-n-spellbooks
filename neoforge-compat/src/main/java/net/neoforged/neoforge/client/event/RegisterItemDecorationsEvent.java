package net.neoforged.neoforge.client.event;

import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.client.ItemDecorator;
import net.neoforged.neoforge.internal.fabric.ItemDecoratorRegistry;

/**
 * Posted on the mod bus during client setup; registered decorators run at the
 * head of {@code GuiGraphics#renderItemDecorations} for their item.
 */
public class RegisterItemDecorationsEvent extends Event implements IModBusEvent {
    private final Map<Item, ItemDecorator> decorators;

    public RegisterItemDecorationsEvent(Map<Item, ItemDecorator> decorators) {
        this.decorators = decorators;
    }

    public void register(ItemLike item, ItemDecorator decorator) {
        decorators.put(item.asItem(), decorator);
    }
}
