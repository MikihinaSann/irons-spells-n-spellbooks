package net.neoforged.api.distmarker;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

/**
 * DistExecutor backed by FabricLoader's environment type.
 */
public final class DistExecutor {
    private DistExecutor() {
    }

    public static void unsafeRunWhenOn(Dist dist, Supplier<Runnable> toRun) {
        if (currentDist() == dist) {
            toRun.get().run();
        }
    }

    public static <T> T unsafeCallWhenOn(Dist dist, Supplier<T> toRun) {
        if (currentDist() == dist) {
            return toRun.get();
        }
        return null;
    }

    public static void unsafeRunForDist(Supplier<Supplier<Runnable>> clientTarget, Supplier<Supplier<Runnable>> serverTarget) {
        unsafeCallForDist(clientTarget, serverTarget).run();
    }

    public static <T> T unsafeCallForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return (currentDist() == Dist.CLIENT ? clientTarget : serverTarget).get().get();
    }

    public static Dist currentDist() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Dist.CLIENT : Dist.DEDICATED_SERVER;
    }
}
