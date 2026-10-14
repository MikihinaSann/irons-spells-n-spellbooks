package net.neoforged.neoforge.common.world.chunk;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.internal.fabric.TicketControllerRegistry;

/**
 * Mod-bus event collecting {@link TicketController}s, mirroring NeoForge's
 * {@code RegisterTicketControllersEvent}.
 */
public class RegisterTicketControllersEvent extends Event implements IModBusEvent {
    public void register(TicketController controller) {
        TicketControllerRegistry.register(controller);
    }
}
