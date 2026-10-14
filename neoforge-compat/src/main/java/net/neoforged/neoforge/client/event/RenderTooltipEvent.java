package net.neoforged.neoforge.client.event;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

/** NeoForge RenderTooltipEvent - Color fires when the tooltip box colors are resolved. */
public abstract class RenderTooltipEvent extends Event {
    private final ItemStack stack;
    private final GuiGraphics graphics;
    protected int x;
    protected int y;
    protected Font font;
    private final List<ClientTooltipComponent> components;
    private final PoseStack poseStack;

    protected RenderTooltipEvent(ItemStack stack, GuiGraphics graphics, int x, int y, Font font,
            List<ClientTooltipComponent> components, PoseStack poseStack) {
        this.stack = stack;
        this.graphics = graphics;
        this.x = x;
        this.y = y;
        this.font = font;
        this.components = components;
        this.poseStack = poseStack;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public GuiGraphics getGraphics() {
        return graphics;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Font getFont() {
        return font;
    }

    public List<ClientTooltipComponent> getComponents() {
        return components;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    /** Fired when tooltip background/border colors are resolved; all are writable. */
    public static class Color extends RenderTooltipEvent {
        private int backgroundStart;
        private int backgroundEnd;
        private int borderStart;
        private int borderEnd;

        public Color(ItemStack stack, GuiGraphics graphics, int x, int y, Font font,
                int backgroundStart, int borderStart, int borderEnd,
                List<ClientTooltipComponent> components, PoseStack poseStack) {
            super(stack, graphics, x, y, font, components, poseStack);
            this.backgroundStart = backgroundStart;
            this.backgroundEnd = backgroundStart;
            this.borderStart = borderStart;
            this.borderEnd = borderEnd;
        }

        public int getBackgroundStart() {
            return backgroundStart;
        }

        public void setBackgroundStart(int backgroundStart) {
            this.backgroundStart = backgroundStart;
        }

        public int getBackgroundEnd() {
            return backgroundEnd;
        }

        public void setBackgroundEnd(int backgroundEnd) {
            this.backgroundEnd = backgroundEnd;
        }

        public int getBorderStart() {
            return borderStart;
        }

        public void setBorderStart(int borderStart) {
            this.borderStart = borderStart;
        }

        public int getBorderEnd() {
            return borderEnd;
        }

        public void setBorderEnd(int borderEnd) {
            this.borderEnd = borderEnd;
        }
    }

    /**
     * Fired before a tooltip's components are computed — listeners may append or
     * replace elements. NeoForge 1.21.1, ported verbatim shape.
     */
    public static class GatherComponents extends Event implements ICancellableEvent {
        private final ItemStack itemStack;
        private final int screenWidth;
        private final int screenHeight;
        private final List<Either<FormattedText, TooltipComponent>> tooltipElements;
        private int maxWidth;

        public GatherComponents(ItemStack itemStack, int screenWidth, int screenHeight, List<Either<FormattedText, TooltipComponent>> tooltipElements, int maxWidth) {
            this.itemStack = itemStack;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.tooltipElements = tooltipElements instanceof ArrayList<Either<FormattedText, TooltipComponent>> ? tooltipElements : new ArrayList<>(tooltipElements);
            this.maxWidth = maxWidth;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public int getScreenWidth() {
            return screenWidth;
        }

        public int getScreenHeight() {
            return screenHeight;
        }

        public List<Either<FormattedText, TooltipComponent>> getTooltipElements() {
            return tooltipElements;
        }

        public int getMaxWidth() {
            return maxWidth;
        }

        public void setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
        }
    }

    /**
     * Fired before the tooltip renders; cancelable. NeoForge 1.21.1 shape.
     */
    public static class Pre extends RenderTooltipEvent implements ICancellableEvent {
        private final int screenWidth;
        private final int screenHeight;
        private final net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner positioner;

        public Pre(ItemStack stack, GuiGraphics graphics, int x, int y, int screenWidth, int screenHeight, Font font,
                List<ClientTooltipComponent> components, net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner positioner) {
            super(stack, graphics, x, y, font, components, graphics.pose());
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.positioner = positioner;
        }

        public int getScreenWidth() {
            return screenWidth;
        }

        public int getScreenHeight() {
            return screenHeight;
        }

        public net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner getTooltipPositioner() {
            return positioner;
        }

        public void setFont(Font font) {
            this.font = font;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
