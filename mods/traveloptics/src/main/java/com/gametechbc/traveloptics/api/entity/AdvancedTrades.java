package com.gametechbc.traveloptics.api.entity;

import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

public class AdvancedTrades {
   public AdvancedTrades() {
      super();
   }

   public static class DualItemTrade extends AdvancedTrades.FlexibleTrade {
      public DualItemTrade(
         int tradeUses,
         ItemStack currency,
         int minCurrency,
         int maxCurrency,
         ItemStack middleItem,
         int minMiddle,
         int maxMiddle,
         ItemStack output,
         int minOutput,
         int maxOutput,
         float priceMultiplier
      ) {
         super(
            (trader, random) -> {
               int currencyAmount = random.nextIntBetweenInclusive(minCurrency, maxCurrency);
               int middleAmount = random.nextIntBetweenInclusive(minMiddle, maxMiddle);
               int outputAmount = random.nextIntBetweenInclusive(minOutput, maxOutput);
               return new MerchantOffer(
                  new ItemCost(currency.getItem(), currencyAmount),
                  Optional.of(new ItemCost(middleItem.getItem(), middleAmount)),
                  new ItemStack(output.getItem(), outputAmount),
                  tradeUses,
                  0,
                  priceMultiplier
               );
            }
         );
      }
   }

   public static class FlexibleTrade implements ItemListing {
      final BiFunction<Entity, RandomSource, MerchantOffer> getOffer;

      protected FlexibleTrade(BiFunction<Entity, RandomSource, MerchantOffer> getOffer) {
         super();
         this.getOffer = getOffer;
      }

      public static AdvancedTrades.FlexibleTrade of(BiFunction<Entity, RandomSource, MerchantOffer> getOffer) {
         return new AdvancedTrades.FlexibleTrade(getOffer);
      }

      public MerchantOffer getOffer(Entity trader, RandomSource random) {
         return this.getOffer.apply(trader, random);
      }
   }

   public static class SingleItemTrade extends AdvancedTrades.FlexibleTrade {
      public SingleItemTrade(
         int tradeUses, ItemStack currency, int minCurrency, int maxCurrency, ItemStack output, int minOutput, int maxOutput, float priceMultiplier
      ) {
         super(
            (trader, random) -> {
               int currencyAmount = random.nextIntBetweenInclusive(minCurrency, maxCurrency);
               int outputAmount = random.nextIntBetweenInclusive(minOutput, maxOutput);
               return new MerchantOffer(
                  new ItemCost(currency.getItem(), currencyAmount), new ItemStack(output.getItem(), outputAmount), tradeUses, 0, priceMultiplier
               );
            }
         );
      }
   }
}
