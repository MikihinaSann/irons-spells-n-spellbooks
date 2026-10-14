package net.neoforged.neoforge.client.event;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.internal.fabric.ClientTooltipFactories;

import java.util.function.Function;

/**
 * Client mod-bus event registering {@link TooltipComponent} →
 * {@link ClientTooltipComponent} factories, mirrored by the
 * {@code ClientTooltipComponent#create} dispatch mixin.
 */
public class RegisterClientTooltipComponentFactoriesEvent extends Event implements IModBusEvent {
    public <T extends TooltipComponent> void register(Class<T> type,
            Function<T, ClientTooltipComponent> factory) {
        ClientTooltipFactories.register(type, factory);
    }
}
