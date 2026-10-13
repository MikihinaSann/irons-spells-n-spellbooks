package net.neoforged.neoforge.client.event;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

/** NeoForge RenderTooltipEvent - Color fires when the tooltip box colors are resolved. */
public abstract class RenderTooltipEvent extends Event {
    private final ItemStack stack;
    private final GuiGraphics graphics;
    private final int x;
    private final int y;
    private final Font font;
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
}
