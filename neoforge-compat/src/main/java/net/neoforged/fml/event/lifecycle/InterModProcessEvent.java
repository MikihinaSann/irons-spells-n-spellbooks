package net.neoforged.fml.event.lifecycle;

import net.neoforged.bus.api.Event;

import java.util.stream.Stream;

public class InterModProcessEvent extends Event {
    public Stream<net.neoforged.fml.InterModComms.IMCMessage> getIMCStream() {
        return Stream.empty();
    }

    public Stream<net.neoforged.fml.InterModComms.IMCMessage> getIMCStream(java.util.function.Predicate<String> predicate) {
        return Stream.empty();
    }
}
