package net.neoforged.fml.event.lifecycle;

import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Lifecycle event supporting {@code enqueueWork} — work runs on the main
 * thread once the event finishes dispatching.
 */
public class ParallelDispatchEvent extends Event {
    private final List<Runnable> work = new ArrayList<>();

    public void enqueueWork(Runnable runnable) {
        work.add(runnable);
    }

    public <T> Supplier<T> enqueueWork(Supplier<T> supplier) {
        work.add(supplier::get);
        return supplier;
    }

    /** Called by the compat bootstrap after the event is posted. */
    public void drainWork() {
        work.forEach(Runnable::run);
        work.clear();
    }
}
