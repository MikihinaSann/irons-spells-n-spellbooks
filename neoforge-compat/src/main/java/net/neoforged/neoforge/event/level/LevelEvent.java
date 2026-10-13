package net.neoforged.neoforge.event.level;

import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;

public abstract class LevelEvent extends Event {
    private final LevelAccessor level;

    public LevelEvent(LevelAccessor level) {
        this.level = level;
    }

    public LevelAccessor getLevel() {
        return level;
    }

    public static class Load extends LevelEvent {
        public Load(LevelAccessor level) {
            super(level);
        }
    }

    public static class Unload extends LevelEvent {
        public Unload(LevelAccessor level) {
            super(level);
        }
    }

    public static class Save extends LevelEvent {
        public Save(LevelAccessor level) {
            super(level);
        }
    }

    public static class CreateSpawnPosition extends LevelEvent {
        private final net.minecraft.server.level.ServerLevel serverLevel;
        private final net.minecraft.world.level.storage.ServerLevelData settings;
        private net.minecraft.core.BlockPos spawnPos;
        private boolean canceled;

        public CreateSpawnPosition(net.minecraft.server.level.ServerLevel level, net.minecraft.world.level.storage.ServerLevelData settings) {
            super(level);
            this.serverLevel = level;
            this.settings = settings;
        }

        public net.minecraft.world.level.storage.ServerLevelData getSettings() {
            return settings;
        }

        public net.minecraft.core.BlockPos getSpawnPos() {
            return spawnPos;
        }

        public void setSpawnPos(net.minecraft.core.BlockPos pos) {
            this.spawnPos = pos;
        }

        @Override
        public boolean isCanceled() {
            return canceled;
        }

        @Override
        public void setCanceled(boolean cancel) {
            this.canceled = cancel;
        }
    }
}
