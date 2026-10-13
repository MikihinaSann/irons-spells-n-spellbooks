package net.neoforged.fml;

import net.fabricmc.api.EnvType;

public enum LogicalSide {
    CLIENT, SERVER;

    public boolean isServer() {
        return !isClient();
    }

    public boolean isClient() {
        return this == CLIENT;
    }

    public static LogicalSide get(EnvType env) {
        return env == EnvType.CLIENT ? CLIENT : SERVER;
    }
}
