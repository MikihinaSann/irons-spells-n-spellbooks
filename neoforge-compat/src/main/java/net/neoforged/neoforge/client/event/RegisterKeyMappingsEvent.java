package net.neoforged.neoforge.client.event;

import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

public class RegisterKeyMappingsEvent extends Event {
    private final List<KeyMapping> mappings = new ArrayList<>();

    public void register(KeyMapping mapping) {
        mappings.add(mapping);
    }

    public List<KeyMapping> getMappings() {
        return mappings;
    }

    public void drain() {
        mappings.forEach(net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper::registerKeyBinding);
    }
}
