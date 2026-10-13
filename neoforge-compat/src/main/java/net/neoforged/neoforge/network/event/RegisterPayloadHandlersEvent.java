package net.neoforged.neoforge.network.event;

import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collects payload registrations per-namespace; drained during platform init.
 */
public class RegisterPayloadHandlersEvent extends Event {
    private final Map<String, PayloadRegistrar> registrars = new LinkedHashMap<>();

    public PayloadRegistrar registrar(String namespace) {
        return registrars.computeIfAbsent(namespace, PayloadRegistrar::new);
    }

    public Map<String, PayloadRegistrar> getRegistrars() {
        return registrars;
    }
}
