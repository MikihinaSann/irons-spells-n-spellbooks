package net.neoforged.neoforge.internal.versions.neoforge;

/** Shim NeoForge version constants - reports the NeoForge version our shim targets. */
public class NeoForgeVersion {
    public static final String GROUP = "net.neoforged";
    public static final String SPEC = "21.1";
    public static final String VERSION = "21.1.213";
    public static final String MOD_ID = "neoforge";

    public static String getGroup() {
        return GROUP;
    }

    public static String getSpec() {
        return SPEC;
    }

    public static String getVersion() {
        return VERSION;
    }
}
