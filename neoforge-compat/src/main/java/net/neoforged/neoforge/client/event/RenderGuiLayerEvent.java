package net.neoforged.neoforge.client.event;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before and after a named HUD layer is rendered. Ported from NeoForge
 * 21.1.
 */
public abstract class RenderGuiLayerEvent extends Event {
    private final GuiGraphics guiGraphics;
    private final DeltaTracker partialTick;
    private final ResourceLocation name;
    private final LayeredDraw.Layer layer;

    @ApiStatus.Internal
    protected RenderGuiLayerEvent(GuiGraphics guiGraphics, DeltaTracker partialTick, ResourceLocation name, LayeredDraw.Layer layer) {
        this.guiGraphics = guiGraphics;
        this.partialTick = partialTick;
        this.name = name;
        this.layer = layer;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public DeltaTracker getPartialTick() {
        return partialTick;
    }

    public ResourceLocation getName() {
        return name;
    }

    public LayeredDraw.Layer getLayer() {
        return layer;
    }

    /**
     * Fired before the layer renders; canceling skips the layer.
     */
    public static class Pre extends RenderGuiLayerEvent implements ICancellableEvent {
        @ApiStatus.Internal
        public Pre(GuiGraphics guiGraphics, DeltaTracker partialTick, ResourceLocation name, LayeredDraw.Layer layer) {
            super(guiGraphics, partialTick, name, layer);
        }
    }

    /**
     * Fired after the layer renders.
     */
    public static class Post extends RenderGuiLayerEvent {
        @ApiStatus.Internal
        public Post(GuiGraphics guiGraphics, DeltaTracker partialTick, ResourceLocation name, LayeredDraw.Layer layer) {
            super(guiGraphics, partialTick, name, layer);
        }
    }
}
