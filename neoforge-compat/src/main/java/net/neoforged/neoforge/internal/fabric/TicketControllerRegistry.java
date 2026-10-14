package net.neoforged.neoforge.internal.fabric;

import net.neoforged.neoforge.common.world.chunk.TicketController;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** All {@link TicketController}s collected via the mod-bus registration event. */
public final class TicketControllerRegistry {
    private static final List<TicketController> CONTROLLERS = new CopyOnWriteArrayList<>();

    private TicketControllerRegistry() {
    }

    public static void register(TicketController controller) {
        CONTROLLERS.add(controller);
    }

    public static List<TicketController> all() {
        return CONTROLLERS;
    }
}
