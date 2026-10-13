package net.neoforged.neoforge.common;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

/** NeoForge BasicItemListing - a simple emergent/cost/result villager trade. */
public class BasicItemListing implements VillagerTrades.ItemListing {
    protected final ItemCost[] itemCosts;
    protected final ItemStack forSale;
    protected final int maxTrades;
    protected final int xp;
    protected final float priceMult;

    public BasicItemListing(ItemCost[] itemCosts, ItemStack forSale, int maxTrades, int xp, float priceMult) {
        this.itemCosts = itemCosts;
        this.forSale = forSale;
        this.maxTrades = maxTrades;
        this.xp = xp;
        this.priceMult = priceMult;
    }

    public BasicItemListing(ItemCost cost, ItemStack forSale, int maxTrades, int xp, float priceMult) {
        this(new ItemCost[] { cost }, forSale, maxTrades, xp, priceMult);
    }

    public BasicItemListing(int emeralds, ItemStack forSale, int maxTrades, int xp, float mult) {
        this(new ItemCost[] { new ItemCost(net.minecraft.world.item.Items.EMERALD, emeralds) }, forSale, maxTrades, xp, mult);
    }

    public BasicItemListing(ItemCost cost1, ItemCost cost2, ItemStack forSale, int maxTrades, int xp, float mult) {
        this(new ItemCost[] { cost1, cost2 }, forSale, maxTrades, xp, mult);
    }

    @Override
    public MerchantOffer getOffer(Entity entity, RandomSource random) {
        ItemCost secondary = itemCosts.length > 1 ? itemCosts[1] : null;
        return new MerchantOffer(itemCosts[0], java.util.Optional.ofNullable(secondary), forSale, maxTrades, xp, priceMult);
    }
}
