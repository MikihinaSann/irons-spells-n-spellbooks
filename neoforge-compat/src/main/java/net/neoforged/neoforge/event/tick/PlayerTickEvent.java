package net.neoforged.neoforge.event.tick;

import net.minecraft.world.entity.player.Player;

public abstract class PlayerTickEvent extends TickEvent {
    private final Player player;

    public PlayerTickEvent(Player player) {
        this.player = player;
    }

    public Player getEntity() {
        return player;
    }

    public static class Pre extends PlayerTickEvent {
        public Pre(Player player) {
            super(player);
        }
    }

    public static class Post extends PlayerTickEvent {
        public Post(Player player) {
            super(player);
        }
    }
}
