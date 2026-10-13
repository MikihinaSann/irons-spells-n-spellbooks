package net.neoforged.fml.event.lifecycle;

import net.neoforged.bus.api.Event;

import java.util.function.Supplier;

public class FMLClientSetupEvent extends Event {
    public void enqueueWork(Runnable work) {
        work.run();
    }

    public <T> void enqueueWork(Supplier<T> work) {
        work.get();
    }
}
