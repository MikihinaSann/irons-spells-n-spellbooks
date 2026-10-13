package net.neoforged.neoforge.event.tick;

import net.minecraft.world.level.LevelAccessor;

public abstract class LevelTickEvent extends TickEvent {
    private final LevelAccessor level;

    public LevelTickEvent(LevelAccessor level) {
        this.level = level;
    }

    public net.minecraft.world.level.Level getLevel() {
        return (net.minecraft.world.level.Level) level;
    }

    public static class Pre extends LevelTickEvent {
        public Pre(LevelAccessor level) {
            super(level);
        }
    }

    public static class Post extends LevelTickEvent {
        public Post(LevelAccessor level) {
            super(level);
        }
    }
}
