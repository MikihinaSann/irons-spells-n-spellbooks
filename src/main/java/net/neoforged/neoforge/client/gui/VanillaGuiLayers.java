package net.neoforged.neoforge.client.gui;

import net.minecraft.resources.ResourceLocation;

/**
 * Vanilla HUD layer identifiers (matching NeoForge's VanillaGuiLayers).
 */
public final class VanillaGuiLayers {

    private VanillaGuiLayers() {
    }

    private static ResourceLocation minecraft(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static final ResourceLocation HOTBAR = minecraft("hotbar");
    public static final ResourceLocation PLAYER_HEALTH = minecraft("player_health");
    public static final ResourceLocation AIR_LEVEL = minecraft("air_level");
    public static final ResourceLocation ARMOR_LEVEL = minecraft("armor_level");
    public static final ResourceLocation FOOD_LEVEL = minecraft("food_level");
    public static final ResourceLocation EXPERIENCE_LEVEL = minecraft("experience_level");
    public static final ResourceLocation EXPERIENCE_BAR = minecraft("experience_bar");
    public static final ResourceLocation BOSS_OVERLAY = minecraft("boss_overlay");
    public static final ResourceLocation SCOREBOARD_SIDEBAR = minecraft("scoreboard_sidebar");
    public static final ResourceLocation TITLE = minecraft("title");
    public static final ResourceLocation SUBTITLE = minecraft("subtitle");
    public static final ResourceLocation DEBUG_OVERLAY = minecraft("debug_overlay");
    public static final ResourceLocation EFFECT_PANEL = minecraft("effect_panel");
    public static final ResourceLocation SELECTED_ITEM_NAME = minecraft("selected_item_name");
    public static final ResourceLocation VEHICLE_HEALTH = minecraft("vehicle_health");
    public static final ResourceLocation JUMP_BAR = minecraft("jump_bar");
    public static final ResourceLocation CROSSHAIR = minecraft("crosshair");
}
