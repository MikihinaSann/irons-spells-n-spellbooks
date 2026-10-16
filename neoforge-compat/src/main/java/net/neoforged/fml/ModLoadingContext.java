package net.neoforged.fml;

/**
 * Minimal stand-in for NeoForge's ModLoadingContext. The active container is
 * set while a @Mod class is being constructed.
 */
public class ModLoadingContext {
    private static final ModLoadingContext INSTANCE = new ModLoadingContext();
    private static final ThreadLocal<ModContainer> ACTIVE = new ThreadLocal<>();

    public static ModLoadingContext get() {
        return INSTANCE;
    }

    public ModContainer getActiveContainer() {
        ModContainer container = ACTIVE.get();
        return container != null ? container : new ModContainer();
    }

    public static void setActiveContainer(ModContainer container) {
        ACTIVE.set(container);
    }

    public static void clearActiveContainer() {
        ACTIVE.remove();
    }

    private static final java.util.Map<Class<?>, Object> EXTENSION_POINTS = new java.util.HashMap<>();

    /**
     * NeoForge registers config screens / display-test factories through this.
     * Stored for later use; nothing consumes them yet on Fabric.
     */
    @SuppressWarnings("unchecked")
    public <T> void registerExtensionPoint(Class<T> point, java.util.function.Supplier<? extends T> factory) {
        EXTENSION_POINTS.put(point, factory.get());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getExtensionPoint(Class<T> point) {
        return (T) EXTENSION_POINTS.get(point);
    }
}
