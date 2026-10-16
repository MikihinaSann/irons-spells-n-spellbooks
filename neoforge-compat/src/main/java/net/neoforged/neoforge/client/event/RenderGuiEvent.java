package net.neoforged.neoforge.client.event;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.Event;

/**
 * NeoForge-style GUI render event. Fired from Fabric's HudRenderCallback.
 */
public abstract class RenderGuiEvent extends Event {
    private final GuiGraphics guiGraphics;
    private final DeltaTracker partialTick;

    public RenderGuiEvent(GuiGraphics guiGraphics, DeltaTracker partialTick) {
        this.guiGraphics = guiGraphics;
        this.partialTick = partialTick;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public DeltaTracker getPartialTick() {
        return partialTick;
    }

    /** Fired before the vanilla HUD renders (posted just before Post). */
    public static class Pre extends RenderGuiEvent {
        public Pre(GuiGraphics guiGraphics, DeltaTracker partialTick) {
            super(guiGraphics, partialTick);
        }
    }

    /** Fired after the vanilla HUD has rendered. */
    public static class Post extends RenderGuiEvent {
        public Post(GuiGraphics guiGraphics, DeltaTracker partialTick) {
            super(guiGraphics, partialTick);
        }
    }
}
