package io.redspace.ironsspellbooks.platform.fabric;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Buckets {@link RegisterGuiLayersEvent} registrations by the vanilla layer
 * they anchor to, so {@code GuiLayerOrderMixin} can render them at the same
 * position NeoForge would have placed them inside the vanilla HUD chain.
 * Registrations keep their order within each slot.
 */
public final class GuiLayerSlots {

    public enum Slot {
        /** BELOW crosshair — rendered at HEAD of {@code Gui.renderCrosshair}. */
        BEFORE_CROSSHAIR,
        /** ABOVE crosshair / BELOW hotbar — rendered at RETURN of {@code Gui.renderCrosshair}. */
        AFTER_CROSSHAIR,
        /** ABOVE hotbar/jump_bar/experience_bar, BELOW player_health — inside renderHotbarAndDecorations. */
        AFTER_XP,
        /** ABOVE health/armor/food/air/vehicle bars, BELOW selected item name. */
        AFTER_STATUS,
        /** ABOVE_ALL — appended as an extra LayeredDraw layer after every vanilla layer. */
        TOP,
        /** Unmapped anchors — rendered at HudRenderCallback (after the whole HUD). */
        FALLBACK
    }

    private static final EnumMap<Slot, List<RegisterGuiLayersEvent.Registration>> SLOTS = new EnumMap<>(Slot.class);
    private static final Set<ResourceLocation> WARNED = new HashSet<>();

    static {
        for (Slot slot : Slot.values()) {
            SLOTS.put(slot, new ArrayList<>());
        }
    }

    private GuiLayerSlots() {
    }

    public static void classify(RegisterGuiLayersEvent.Registration registration) {
        Slot slot = slotFor(registration);
        if (slot == Slot.FALLBACK && WARNED.add(registration.id())) {
            IronsSpellbooks.LOGGER.warn("GUI layer {} anchored at {} {} has no vanilla injection slot; it will draw at the HudRenderCallback fallback",
                    registration.id(), registration.order(), registration.relativeTo());
        }
        SLOTS.get(slot).add(registration);
    }

    private static Slot slotFor(RegisterGuiLayersEvent.Registration r) {
        if (r.order() == RegisterGuiLayersEvent.Order.ABOVE_ALL) {
            return Slot.TOP;
        }
        ResourceLocation anchor = r.relativeTo();
        if (anchor == null) {
            return Slot.FALLBACK;
        }
        return switch (r.order()) {
            case BELOW -> {
                if (anchor.equals(VanillaGuiLayers.CROSSHAIR)) {
                    yield Slot.BEFORE_CROSSHAIR;
                }
                if (anchor.equals(VanillaGuiLayers.HOTBAR)) {
                    yield Slot.AFTER_CROSSHAIR;
                }
                if (anchor.equals(VanillaGuiLayers.PLAYER_HEALTH)) {
                    yield Slot.AFTER_XP;
                }
                if (anchor.equals(VanillaGuiLayers.SELECTED_ITEM_NAME)) {
                    yield Slot.AFTER_STATUS;
                }
                yield Slot.FALLBACK;
            }
            case ABOVE -> {
                if (anchor.equals(VanillaGuiLayers.CROSSHAIR)) {
                    yield Slot.AFTER_CROSSHAIR;
                }
                if (anchor.equals(VanillaGuiLayers.HOTBAR)
                        || anchor.equals(VanillaGuiLayers.JUMP_BAR)
                        || anchor.equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
                    yield Slot.AFTER_XP;
                }
                if (anchor.equals(VanillaGuiLayers.PLAYER_HEALTH)
                        || anchor.equals(VanillaGuiLayers.ARMOR_LEVEL)
                        || anchor.equals(VanillaGuiLayers.FOOD_LEVEL)
                        || anchor.equals(VanillaGuiLayers.AIR_LEVEL)
                        || anchor.equals(VanillaGuiLayers.VEHICLE_HEALTH)) {
                    yield Slot.AFTER_STATUS;
                }
                yield Slot.FALLBACK;
            }
            default -> Slot.FALLBACK;
        };
    }

    public static List<RegisterGuiLayersEvent.Registration> get(Slot slot) {
        return SLOTS.get(slot);
    }

    public static void render(Slot slot, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        for (var registration : SLOTS.get(slot)) {
            registration.layer().render(guiGraphics, deltaTracker);
        }
    }
}
