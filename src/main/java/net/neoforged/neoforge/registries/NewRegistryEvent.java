package net.neoforged.neoforge.registries;

import net.minecraft.core.Registry;
import net.neoforged.bus.api.Event;

/**
 * Registry-creation event stand-in. Dispatched once during mod init.
 */
public class NewRegistryEvent extends Event {
    public <T> Registry<T> create(RegistryBuilder<T> builder) {
        return builder.create();
    }

    public <T> void register(Registry<T> registry) {
    }
}
