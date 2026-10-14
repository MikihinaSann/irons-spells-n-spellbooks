package net.neoforged.neoforge.common.world.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NeoForge's TicketController — owns forced-chunk tickets for a mod, backed by
 * vanilla {@link TicketType#FORCED} region tickets with reference counting so
 * multiple owners can share a chunk.
 */
public class TicketController {
    /**
     * Called when a level loads with previously-registered tickets still
     * outstanding — mirrors NeoForge's {@code LoadingCallback}.
     */
    @FunctionalInterface
    public interface LoadingCallback {
        void onLoading(ServerLevel level, TicketHelper ticketHelper);
    }

    /** Global reference counts: level -> chunk -> number of forcing owners. */
    private static final Map<ResourceKey<Level>, Map<ChunkPos, Integer>> REFCOUNTS = new ConcurrentHashMap<>();

    private final ResourceLocation id;
    private final LoadingCallback callback;
    private final Map<ResourceKey<Level>, TicketHelper> helpersMap = new HashMap<>();

    public TicketController(ResourceLocation id) {
        this(id, null);
    }

    public TicketController(ResourceLocation id, LoadingCallback callback) {
        this.id = id;
        this.callback = callback;
    }

    public ResourceLocation getId() {
        return id;
    }

    LoadingCallback callback() {
        return callback;
    }

    private TicketHelper helper(ServerLevel level) {
        return helpersMap.computeIfAbsent(level.dimension(), k -> new TicketHelper(level, this));
    }

    /**
     * Force or release a chunk ticket owned by an {@link Entity} or
     * {@link BlockPos}. Returns whether the operation changed anything.
     */
    public <T> boolean forceChunk(ServerLevel level, T owner, int x, int z, boolean add, boolean ticking) {
        TicketHelper helper = helper(level);
        ChunkPos pos = new ChunkPos(x, z);
        if (owner instanceof Entity entity) {
            return apply(helper.entitySet(entity.getUUID()), level, pos, add);
        }
        if (owner instanceof BlockPos blockPos) {
            return apply(helper.blockSet(blockPos.immutable()), level, pos, add);
        }
        if (owner instanceof UUID uuid) {
            return apply(helper.entitySet(uuid), level, pos, add);
        }
        return false;
    }

    private boolean apply(TicketSet set, ServerLevel level, ChunkPos pos, boolean add) {
        if (add) {
            if (!set.add(pos)) {
                return false;
            }
            force(level, pos);
            return true;
        }
        if (!set.remove(pos)) {
            return false;
        }
        unforce(level, pos);
        return true;
    }

    private void force(ServerLevel level, ChunkPos pos) {
        Map<ChunkPos, Integer> counts = REFCOUNTS.computeIfAbsent(level.dimension(), k -> new ConcurrentHashMap<>());
        if (counts.merge(pos, 1, Integer::sum) == 1) {
            level.getChunkSource().addRegionTicket(TicketType.FORCED, pos, 2, pos);
        }
    }

    void unforce(ServerLevel level, ChunkPos pos) {
        Map<ChunkPos, Integer> counts = REFCOUNTS.get(level.dimension());
        if (counts == null) {
            return;
        }
        int remaining = counts.merge(pos, -1, Integer::sum);
        if (remaining <= 0) {
            counts.remove(pos);
            level.getChunkSource().removeRegionTicket(TicketType.FORCED, pos, 2, pos);
        }
    }

    /** Invoked by the compat bootstrap when a server level loads. */
    public void onLevelLoad(ServerLevel level) {
        if (callback != null) {
            callback.onLoading(level, helper(level));
        }
    }

    /** Invoked by the compat bootstrap when a server level unloads. */
    public void onLevelUnload(ServerLevel level) {
        helpersMap.remove(level.dimension());
        REFCOUNTS.remove(level.dimension());
    }
}
