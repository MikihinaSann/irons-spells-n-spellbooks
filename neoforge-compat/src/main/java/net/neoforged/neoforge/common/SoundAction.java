package net.neoforged.neoforge.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NeoForge's SoundAction — a named key for a fluid/block interaction sound
 * (e.g. bucket fill, vaporize). Actions are interned by name.
 */
public final class SoundAction {
    private static final Map<String, SoundAction> ACTIONS = new ConcurrentHashMap<>();

    private final String name;

    private SoundAction(String name) {
        this.name = name;
    }

    public static SoundAction get(String name) {
        return ACTIONS.computeIfAbsent(name, SoundAction::new);
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "SoundAction[" + name + "]";
    }
}
