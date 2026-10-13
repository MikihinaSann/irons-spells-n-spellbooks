package net.neoforged.neoforge.event;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects "add extra blocks to a BlockEntityType" requests.
 */
public class BlockEntityTypeAddBlocksEvent extends Event {
    private final List<Entry> entries = new ArrayList<>();

    public record Entry(BlockEntityType<?> type, List<Block> blocks) {
    }

    public void addBlocks(BlockEntityType<?> type, Block... blocks) {
        entries.add(new Entry(type, List.of(blocks)));
    }

    /** Adds the blocks to the type's valid block set immediately. */
    public void modify(BlockEntityType<?> type, Block... blocks) {
        type.validBlocks.addAll(List.of(blocks));
        entries.add(new Entry(type, List.of(blocks)));
    }

    public void addBlock(BlockEntityType<?> type, Block block) {
        entries.add(new Entry(type, List.of(block)));
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
