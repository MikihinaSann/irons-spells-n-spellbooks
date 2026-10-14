package net.neoforged.neoforge.common.world.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-controller, per-level view of forced chunks, mirroring NeoForge's
 * {@code TicketHelper}.
 */
public class TicketHelper {
    private final ServerLevel level;
    private final TicketController controller;
    private final Map<UUID, TicketSet> entityTickets = new HashMap<>();
    private final Map<BlockPos, TicketSet> blockTickets = new HashMap<>();

    public TicketHelper(ServerLevel level, TicketController controller) {
        this.level = level;
        this.controller = controller;
    }

    public Map<UUID, TicketSet> getEntityTickets() {
        return entityTickets;
    }

    public Map<BlockPos, TicketSet> getBlockTickets() {
        return blockTickets;
    }

    public void removeTicket(UUID owner, ChunkPos pos) {
        TicketSet set = entityTickets.get(owner);
        if (set != null && set.remove(pos)) {
            controller.unforce(level, pos);
            if (set.isEmpty()) {
                entityTickets.remove(owner);
            }
        }
    }

    public void removeTicket(BlockPos owner, ChunkPos pos) {
        TicketSet set = blockTickets.get(owner);
        if (set != null && set.remove(pos)) {
            controller.unforce(level, pos);
            if (set.isEmpty()) {
                blockTickets.remove(owner);
            }
        }
    }

    public void removeAllTickets(UUID owner) {
        TicketSet set = entityTickets.remove(owner);
        if (set != null) {
            set.forEach(pos -> controller.unforce(level, pos));
        }
    }

    public void removeAllTickets(BlockPos owner) {
        TicketSet set = blockTickets.remove(owner);
        if (set != null) {
            set.forEach(pos -> controller.unforce(level, pos));
        }
    }

    TicketSet entitySet(UUID owner) {
        return entityTickets.computeIfAbsent(owner, k -> new TicketSet());
    }

    TicketSet blockSet(BlockPos owner) {
        return blockTickets.computeIfAbsent(owner, k -> new TicketSet());
    }

    ServerLevel level() {
        return level;
    }
}
