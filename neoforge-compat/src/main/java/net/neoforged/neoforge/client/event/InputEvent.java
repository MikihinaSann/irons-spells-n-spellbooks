package net.neoforged.neoforge.client.event;

import net.neoforged.bus.api.Event;

public abstract class InputEvent extends Event {

    /**
     * Fired when a mouse button is pressed/released in-game (not in a screen).
     */
    public static class MouseButton extends InputEvent {
        private final int button;
        private final int action;
        private final int modifiers;

        public MouseButton(int button, int action, int modifiers) {
            this.button = button;
            this.action = action;
            this.modifiers = modifiers;
        }

        public int getButton() {
            return button;
        }

        public int getAction() {
            return action;
        }

        public int getModifiers() {
            return modifiers;
        }

        public static class Pre extends MouseButton implements net.neoforged.bus.api.ICancellableEvent {
            private boolean useItem = true;
            private boolean swingHand = true;

            public Pre(int button, int action, int modifiers) {
                super(button, action, modifiers);
            }

            public void setUseItem(boolean useItem) {
                this.useItem = useItem;
            }

            public boolean isUseItem() {
                return useItem;
            }

            public void setSwingHand(boolean swingHand) {
                this.swingHand = swingHand;
            }

            public boolean isSwingHand() {
                return swingHand;
            }
        }

        public static class Post extends MouseButton {
            public Post(int button, int action, int modifiers) {
                super(button, action, modifiers);
            }
        }
    }

    /**
     * Fired when a key is pressed in-game (not in a screen).
     */
    public static class Key extends InputEvent {
        private final int key;
        private final int keyCode;
        private final int scanCode;
        private final int action;
        private final int modifiers;

        public Key(int key, int scanCode, int action, int modifiers) {
            this.key = key;
            this.keyCode = key;
            this.scanCode = scanCode;
            this.action = action;
            this.modifiers = modifiers;
        }

        public int getKey() {
            return key;
        }

        public int getKeyCode() {
            return keyCode;
        }

        public int getScanCode() {
            return scanCode;
        }

        public int getAction() {
            return action;
        }

        public int getModifiers() {
            return modifiers;
        }
    }

    /**
     * Fired when the mouse is scrolled in-game.
     */
    public static class MouseScrollingEvent extends InputEvent implements net.neoforged.bus.api.ICancellableEvent {
        private final double scrollDeltaX;
        private final double scrollDeltaY;
        private final double mouseX;
        private final double mouseY;
        private final boolean leftDown;
        private final boolean middleDown;
        private final boolean rightDown;

        public MouseScrollingEvent(double scrollDeltaX, double scrollDeltaY, boolean leftDown, boolean middleDown,
                                   boolean rightDown, double mouseX, double mouseY) {
            this.scrollDeltaX = scrollDeltaX;
            this.scrollDeltaY = scrollDeltaY;
            this.leftDown = leftDown;
            this.middleDown = middleDown;
            this.rightDown = rightDown;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public double getScrollDeltaX() {
            return scrollDeltaX;
        }

        public double getScrollDeltaY() {
            return scrollDeltaY;
        }

        public double getScrollDelta() {
            return scrollDeltaY;
        }

        public boolean isLeftDown() {
            return leftDown;
        }

        public boolean isMiddleDown() {
            return middleDown;
        }

        public boolean isRightDown() {
            return rightDown;
        }

        public double getMouseX() {
            return mouseX;
        }

        public double getMouseY() {
            return mouseY;
        }
    }

    /**
     * Fired when an interaction key (use/attack/pick block) is triggered.
     */
    public static class InteractionKeyMappingTriggered extends InputEvent implements net.neoforged.bus.api.ICancellableEvent {
        private final int button;
        private final net.minecraft.client.KeyMapping keyMapping;
        private final net.minecraft.world.InteractionHand hand;
        private boolean handSwing = true;
        private boolean pickBlock = false;

        public InteractionKeyMappingTriggered(int button, net.minecraft.client.KeyMapping keyMapping,
                                              net.minecraft.world.InteractionHand hand) {
            this.button = button;
            this.keyMapping = keyMapping;
            this.hand = hand;
        }

        public net.minecraft.client.KeyMapping getKeyMapping() {
            return keyMapping;
        }

        public net.minecraft.world.InteractionHand getHand() {
            return hand;
        }

        public boolean isAttack() {
            return button == 0;
        }

        public boolean isUseItem() {
            return button == 1;
        }

        public boolean isPickBlock() {
            return button == 2;
        }

        public void setSwingHand(boolean value) {
            this.handSwing = value;
        }

        public boolean shouldSwingHand() {
            return handSwing;
        }
    }
}
