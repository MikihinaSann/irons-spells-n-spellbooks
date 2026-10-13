package net.neoforged.neoforge.event.tick;

import net.neoforged.bus.api.Event;

public abstract class TickEvent extends Event {
    public enum Type {
        WORLD, PLAYER, CLIENT, SERVER, LEVEL, ENTITY
    }
}
