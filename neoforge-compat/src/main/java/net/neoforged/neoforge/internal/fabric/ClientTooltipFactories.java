package net.neoforged.neoforge.internal.fabric;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Registry behind {@code RegisterClientTooltipComponentFactoriesEvent}, read
 * by the {@code ClientTooltipComponent#create} mixin before the vanilla
 * instanceof chain.
 */
public final class ClientTooltipFactories {
    private static final Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> FACTORIES = new ConcurrentHashMap<>();

    private ClientTooltipFactories() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends TooltipComponent> void register(Class<T> type,
            Function<T, ClientTooltipComponent> factory) {
        FACTORIES.put(type, (Function<TooltipComponent, ClientTooltipComponent>) factory);
    }

    public static ClientTooltipComponent create(TooltipComponent component) {
        Function<TooltipComponent, ClientTooltipComponent> factory = FACTORIES.get(component.getClass());
        return factory != null ? factory.apply(component) : null;
    }
}
