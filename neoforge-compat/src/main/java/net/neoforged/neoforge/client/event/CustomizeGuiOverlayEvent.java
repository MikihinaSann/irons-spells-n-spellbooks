package net.neoforged.neoforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Customize a specific vanilla GUI overlay layer. The {@code BossEventProgress}
 * subtype controls the boss-event progress bar region.
 */
public class CustomizeGuiOverlayEvent extends Event {
    private final GuiGraphics guiGraphics;
    private final float partialTick;
    private final int width;
    private final int height;

    protected CustomizeGuiOverlayEvent(GuiGraphics guiGraphics, float partialTick, int width, int height) {
        this.guiGraphics = guiGraphics;
        this.partialTick = partialTick;
        this.width = width;
        this.height = height;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /** Fired while rendering the boss-bar layer; can adjust it. */
    public static class BossEventProgress extends CustomizeGuiOverlayEvent implements ICancellableEvent {
        private final net.minecraft.client.gui.components.LerpingBossEvent bossEvent;
        private int x;
        private int y;
        private int increment;

        public BossEventProgress(GuiGraphics guiGraphics, float partialTick, int width, int height,
                                 net.minecraft.client.gui.components.LerpingBossEvent bossEvent,
                                 int x, int y, int increment) {
            super(guiGraphics, partialTick, width, height);
            this.bossEvent = bossEvent;
            this.x = x;
            this.y = y;
            this.increment = increment;
        }

        public net.minecraft.client.gui.components.LerpingBossEvent getBossEvent() {
            return bossEvent;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getIncrement() {
            return increment;
        }

        public void setIncrement(int increment) {
            this.increment = increment;
        }
    }
}
