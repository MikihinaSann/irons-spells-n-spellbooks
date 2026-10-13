package net.neoforged.neoforge.common;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * String-keyed item ability stand-in mirroring NeoForge's ItemAbility.
 */
public final class ItemAbility {
    private static final Map<String, ItemAbility> ACTIONS = new ConcurrentHashMap<>();

    public static ItemAbility get(String name) {
        return ACTIONS.computeIfAbsent(name, ItemAbility::new);
    }

    public static Collection<ItemAbility> getAbilities() {
        return ACTIONS.values();
    }

    private final String name;

    private ItemAbility(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
