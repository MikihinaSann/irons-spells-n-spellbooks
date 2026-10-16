package net.neoforged.neoforge.client.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.bus.api.Event;

/**
 * NeoForge-style events for {@link AbstractContainerScreen} render phases.
 */
public abstract class ContainerScreenEvent extends Event {
    private final AbstractContainerScreen<?> containerScreen;

    public ContainerScreenEvent(AbstractContainerScreen<?> containerScreen) {
        this.containerScreen = containerScreen;
    }

    public AbstractContainerScreen<?> getContainerScreen() {
        return containerScreen;
    }

    public abstract static class Render extends ContainerScreenEvent {
        private final GuiGraphics guiGraphics;
        private final int mouseX;
        private final int mouseY;

        public Render(AbstractContainerScreen<?> containerScreen, GuiGraphics guiGraphics,
                      int mouseX, int mouseY) {
            super(containerScreen);
            this.guiGraphics = guiGraphics;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public GuiGraphics getGuiGraphics() {
            return guiGraphics;
        }

        public int getMouseX() {
            return mouseX;
        }

        public int getMouseY() {
            return mouseY;
        }

        /** Fired at the end of {@code renderBackground} — behind the slots. */
        public static class Background extends Render {
            public Background(AbstractContainerScreen<?> containerScreen, GuiGraphics guiGraphics,
                              int mouseX, int mouseY) {
                super(containerScreen, guiGraphics, mouseX, mouseY);
            }
        }

        /** Fired at the end of {@code render} — above everything. */
        public static class Foreground extends Render {
            public Foreground(AbstractContainerScreen<?> containerScreen, GuiGraphics guiGraphics,
                              int mouseX, int mouseY) {
                super(containerScreen, guiGraphics, mouseX, mouseY);
            }
        }
    }
}
