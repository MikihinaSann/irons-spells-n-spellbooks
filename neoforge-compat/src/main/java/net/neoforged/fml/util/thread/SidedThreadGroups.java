package net.neoforged.fml.util.thread;

/**
 * Stand-in for FML's SidedThreadGroups. Real FML moves the server thread into a
 * dedicated thread group at bootstrap; we cannot reparent the vanilla server
 * thread, so we locate the game thread's group (Server/Render/main) — all of
 * them share the launching thread's group in every environment we run in, while
 * netty worker threads use different groups. That preserves the check mods
 * actually use it for ("am I on a game thread?").
 */
public final class SidedThreadGroups {
    public static final ThreadGroup SERVER = findGameThreadGroup();
    public static final ThreadGroup CLIENT = SERVER;

    private SidedThreadGroups() {
    }

    private static ThreadGroup findGameThreadGroup() {
        try {
            ThreadGroup root = Thread.currentThread().getThreadGroup();
            while (root.getParent() != null) {
                root = root.getParent();
            }
            Thread[] threads = new Thread[root.activeCount() * 2 + 8];
            int count = root.enumerate(threads, true);
            Thread main = null;
            for (int i = 0; i < count; i++) {
                Thread t = threads[i];
                if (t == null) {
                    continue;
                }
                String name = t.getName();
                if ("Server thread".equals(name) || "Render thread".equals(name)) {
                    return t.getThreadGroup();
                }
                if ("main".equals(name)) {
                    main = t;
                }
            }
            if (main != null) {
                return main.getThreadGroup();
            }
        } catch (Throwable ignored) {
        }
        return Thread.currentThread().getThreadGroup();
    }
}
