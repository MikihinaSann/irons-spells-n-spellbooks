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
}
