package net.neoforged.neoforge.event.village;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.neoforged.bus.api.Event;

import java.util.List;

public class WandererTradesEvent extends Event {
    private final List<VillagerTrades.ItemListing> generic;
    private final List<VillagerTrades.ItemListing> rare;

    public WandererTradesEvent(List<VillagerTrades.ItemListing> generic, List<VillagerTrades.ItemListing> rare) {
        this.generic = generic;
        this.rare = rare;
    }

    public List<VillagerTrades.ItemListing> getGenericTrades() {
        return generic;
    }

    public List<VillagerTrades.ItemListing> getRareTrades() {
        return rare;
    }
}
