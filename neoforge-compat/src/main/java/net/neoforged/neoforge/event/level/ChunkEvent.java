package net.neoforged.neoforge.event.level;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * NeoForge's chunk event base. {@link Load} is dispatched from Fabric's
 * {@code ServerChunkEvents.CHUNK_LOAD} (server-side only).
 */
public abstract class ChunkEvent extends LevelEvent {
    private final ChunkAccess chunk;

    public ChunkEvent(ChunkAccess chunk, LevelAccessor level) {
        super(level);
        this.chunk = chunk;
    }

    public ChunkAccess getChunk() {
        return this.chunk;
    }

    public static class Load extends ChunkEvent {
        private final boolean newChunk;

        public Load(ChunkAccess chunk, LevelAccessor level, boolean newChunk) {
            super(chunk, level);
            this.newChunk = newChunk;
        }

        public boolean isNewChunk() {
            return this.newChunk;
        }

        public boolean isFullChunk() {
            return this.getChunk() instanceof LevelChunk;
        }

        public ChunkPos getPos() {
            return this.getChunk().getPos();
        }
    }

    public static class Unload extends ChunkEvent {
        public Unload(ChunkAccess chunk, LevelAccessor level) {
            super(chunk, level);
        }

        public ChunkPos getPos() {
            return this.getChunk().getPos();
        }
    }
}
