package net.neoforged.neoforge.client;

/**
 * NeoForge adds {@code leftHeight}/{@code rightHeight} fields to {@code Gui}
 * for HUD layering. We store them statically here; a Gui mixin keeps them
 * fresh during render.
 */
public final class GuiHeights {
    public static int leftHeight;
    public static int rightHeight;

    private GuiHeights() {
    }
}
