package net.neoforged.neoforge.common;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * NeoForge's per-tick background worker queue. Workers are added by mods and
 * ticked once per server tick until {@link IWorker#hasWork()} returns false.
 * The drain hook is registered from the compat bootstrap.
 */
public final class WorldWorkerManager {
    private static final List<IWorker> WORKERS = new CopyOnWriteArrayList<>();
    /** Max milliseconds of worker time per server tick. */
    private static final long BUDGET_NANOS = 45_000_000L;

    private WorldWorkerManager() {
    }

    public static void addWorker(IWorker worker) {
        WORKERS.add(worker);
    }

    public static void tick() {
        if (WORKERS.isEmpty()) {
            return;
        }
        long deadline = System.nanoTime() + BUDGET_NANOS;
        for (IWorker worker : WORKERS) {
            if (System.nanoTime() >= deadline) {
                break;
            }
            try {
                if (!worker.hasWork() || !worker.doWork()) {
                    WORKERS.remove(worker);
                }
            } catch (Throwable t) {
                WORKERS.remove(worker);
                org.slf4j.LoggerFactory.getLogger("neoforge_compat")
                        .error("WorldWorker threw", t);
            }
        }
    }

    public interface IWorker {
        boolean hasWork();

        /** @return true while more work remains in this tick. */
        boolean doWork();
    }
}
