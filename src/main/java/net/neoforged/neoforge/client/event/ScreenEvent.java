package net.neoforged.neoforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

public abstract class ScreenEvent extends Event {
    private final Screen screen;

    public ScreenEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }

    /** Fired when a screen is opened/changed; can substitute the screen. */
    public static class Opening extends ScreenEvent implements ICancellableEvent {
        private Screen newScreen;
        private final Screen currentScreen;

        public Opening(Screen currentScreen, Screen newScreen) {
            super(currentScreen);
            this.currentScreen = currentScreen;
            this.newScreen = newScreen;
        }

        public Screen getCurrentScreen() {
            return currentScreen;
        }

        public Screen getNewScreen() {
            return newScreen;
        }

        public void setNewScreen(Screen screen) {
            this.newScreen = screen;
        }
    }

    public static class Init extends ScreenEvent {
        private final java.util.List<net.minecraft.client.gui.components.events.GuiEventListener> listenersList;

        public Init(Screen screen, java.util.List<net.minecraft.client.gui.components.events.GuiEventListener> listeners) {
            super(screen);
            this.listenersList = listeners;
        }

        public java.util.List<net.minecraft.client.gui.components.events.GuiEventListener> getListenersList() {
            return listenersList;
        }

        public void addListener(net.minecraft.client.gui.components.events.GuiEventListener listener) {
            listenersList.add(listener);
        }

        public void removeListener(net.minecraft.client.gui.components.events.GuiEventListener listener) {
            listenersList.remove(listener);
        }

        public static class Pre extends Init {
            public Pre(Screen screen, java.util.List<net.minecraft.client.gui.components.events.GuiEventListener> listeners) {
                super(screen, listeners);
            }
        }

        public static class Post extends Init {
            public Post(Screen screen, java.util.List<net.minecraft.client.gui.components.events.GuiEventListener> listeners) {
                super(screen, listeners);
            }
        }
    }

    public static class Render extends ScreenEvent {
        private final net.minecraft.client.gui.GuiGraphics guiGraphics;
        private final int mouseX;
        private final int mouseY;
        private final float partialTick;

        public Render(Screen screen, net.minecraft.client.gui.GuiGraphics guiGraphics,
                      int mouseX, int mouseY, float partialTick) {
            super(screen);
            this.guiGraphics = guiGraphics;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.partialTick = partialTick;
        }

        public net.minecraft.client.gui.GuiGraphics getGuiGraphics() {
            return guiGraphics;
        }

        public int getMouseX() {
            return mouseX;
        }

        public int getMouseY() {
            return mouseY;
        }

        public float getPartialTick() {
            return partialTick;
        }

        public static class Pre extends Render implements ICancellableEvent {
            public Pre(Screen screen, net.minecraft.client.gui.GuiGraphics guiGraphics,
                       int mouseX, int mouseY, float partialTick) {
                super(screen, guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        public static class Post extends Render {
            public Post(Screen screen, net.minecraft.client.gui.GuiGraphics guiGraphics,
                        int mouseX, int mouseY, float partialTick) {
                super(screen, guiGraphics, mouseX, mouseY, partialTick);
            }
        }
    }

    public static class KeyPressed extends ScreenEvent implements ICancellableEvent {
        private final int keyCode;
        private final int scanCode;
        private final int modifiers;

        public KeyPressed(Screen screen, int keyCode, int scanCode, int modifiers) {
            super(screen);
            this.keyCode = keyCode;
            this.scanCode = scanCode;
            this.modifiers = modifiers;
        }

        public int getKeyCode() {
            return keyCode;
        }

        public int getScanCode() {
            return scanCode;
        }

        public int getModifiers() {
            return modifiers;
        }

        public static class Pre extends KeyPressed {
            public Pre(Screen screen, int keyCode, int scanCode, int modifiers) {
                super(screen, keyCode, scanCode, modifiers);
            }
        }

        public static class Post extends KeyPressed {
            public Post(Screen screen, int keyCode, int scanCode, int modifiers) {
                super(screen, keyCode, scanCode, modifiers);
            }
        }
    }

    public static class KeyReleased extends ScreenEvent implements ICancellableEvent {
        private final int keyCode;
        private final int scanCode;
        private final int modifiers;

        public KeyReleased(Screen screen, int keyCode, int scanCode, int modifiers) {
            super(screen);
            this.keyCode = keyCode;
            this.scanCode = scanCode;
            this.modifiers = modifiers;
        }

        public static class Pre extends KeyReleased {
            public Pre(Screen screen, int keyCode, int scanCode, int modifiers) {
                super(screen, keyCode, scanCode, modifiers);
            }
        }

        public static class Post extends KeyReleased {
            public Post(Screen screen, int keyCode, int scanCode, int modifiers) {
                super(screen, keyCode, scanCode, modifiers);
            }
        }
    }

    public static class MouseButtonPressed extends ScreenEvent implements ICancellableEvent {
        private final double mouseX;
        private final double mouseY;
        private final int button;

        public MouseButtonPressed(Screen screen, double mouseX, double mouseY, int button) {
            super(screen);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.button = button;
        }

        public double getMouseX() {
            return mouseX;
        }

        public double getMouseY() {
            return mouseY;
        }

        public int getButton() {
            return button;
        }

        public static class Pre extends MouseButtonPressed {
            public Pre(Screen screen, double mouseX, double mouseY, int button) {
                super(screen, mouseX, mouseY, button);
            }
        }

        public static class Post extends MouseButtonPressed {
            public Post(Screen screen, double mouseX, double mouseY, int button) {
                super(screen, mouseX, mouseY, button);
            }
        }
    }

    public static class MouseButtonReleased extends ScreenEvent implements ICancellableEvent {
        private final double mouseX;
        private final double mouseY;
        private final int button;

        public MouseButtonReleased(Screen screen, double mouseX, double mouseY, int button) {
            super(screen);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.button = button;
        }

        public static class Pre extends MouseButtonReleased {
            public Pre(Screen screen, double mouseX, double mouseY, int button) {
                super(screen, mouseX, mouseY, button);
            }
        }

        public static class Post extends MouseButtonReleased {
            public Post(Screen screen, double mouseX, double mouseY, int button) {
                super(screen, mouseX, mouseY, button);
            }
        }
    }

    public static class MouseScrolled extends ScreenEvent implements ICancellableEvent {
        private final double mouseX;
        private final double mouseY;
        private final double scrollX;
        private final double scrollY;

        public MouseScrolled(Screen screen, double mouseX, double mouseY, double scrollX, double scrollY) {
            super(screen);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.scrollX = scrollX;
            this.scrollY = scrollY;
        }

        public double getScrollDeltaX() {
            return scrollX;
        }

        public double getScrollDeltaY() {
            return scrollY;
        }

        public double getMouseX() {
            return mouseX;
        }

        public double getMouseY() {
            return mouseY;
        }

        public static class Pre extends MouseScrolled {
            public Pre(Screen screen, double mouseX, double mouseY, double scrollX, double scrollY) {
                super(screen, mouseX, mouseY, scrollX, scrollY);
            }
        }

        public static class Post extends MouseScrolled {
            public Post(Screen screen, double mouseX, double mouseY, double scrollX, double scrollY) {
                super(screen, mouseX, mouseY, scrollX, scrollY);
            }
        }
    }

    public static class Closing extends ScreenEvent {
        public Closing(Screen screen) {
            super(screen);
        }
    }

    public static class CharacterTyped extends ScreenEvent implements ICancellableEvent {
        private final char codePoint;
        private final int modifiers;

        public CharacterTyped(Screen screen, char codePoint, int modifiers) {
            super(screen);
            this.codePoint = codePoint;
            this.modifiers = modifiers;
        }

        public char getCodePoint() {
            return codePoint;
        }

        public int getModifiers() {
            return modifiers;
        }

        public static class Pre extends CharacterTyped {
            public Pre(Screen screen, char codePoint, int modifiers) {
                super(screen, codePoint, modifiers);
            }
        }

        public static class Post extends CharacterTyped {
            public Post(Screen screen, char codePoint, int modifiers) {
                super(screen, codePoint, modifiers);
            }
        }
    }
}
