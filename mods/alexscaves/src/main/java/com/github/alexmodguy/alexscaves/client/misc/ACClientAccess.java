package com.github.alexmodguy.alexscaves.client.misc;

import net.minecraft.client.Minecraft;

public final class ACClientAccess {
    private ACClientAccess() {
    }

    public static int biomeBlendRadius() {
        return Minecraft.getInstance().options.biomeBlendRadius().get();
    }
}
