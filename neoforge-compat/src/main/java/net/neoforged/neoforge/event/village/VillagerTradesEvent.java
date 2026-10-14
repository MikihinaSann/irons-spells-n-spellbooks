package net.neoforged.neoforge.event.village;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.neoforged.bus.api.Event;

import java.util.List;

/**
 * NeoForge's {@code VillagerTradesEvent} — fired once per profession so mods
 * can edit the global {@link VillagerTrades#TRADES} table.
 */
public class VillagerTradesEvent extends Event {
    private final Int2ObjectMap<List<VillagerTrades.ItemListing>> trades;
    private final VillagerProfession type;

    public VillagerTradesEvent(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, VillagerProfession type) {
        this.trades = trades;
        this.type = type;
    }

    public Int2ObjectMap<List<VillagerTrades.ItemListing>> getTrades() {
        return trades;
    }

    public VillagerProfession getType() {
        return type;
    }
}
