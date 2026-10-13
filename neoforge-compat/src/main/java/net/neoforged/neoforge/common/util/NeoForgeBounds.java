package net.neoforged.neoforge.common.util;

import net.minecraft.world.phys.AABB;

/** NeoForge patches {@code AABB.INFINITE}; we keep it here instead. */
public final class NeoForgeBounds {
    public static final AABB INFINITE = new AABB(
            -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE,
            Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

    private NeoForgeBounds() {
    }
}
