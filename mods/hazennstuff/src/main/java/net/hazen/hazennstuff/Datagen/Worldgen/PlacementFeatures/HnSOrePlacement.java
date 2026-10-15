package net.hazen.hazennstuff.Datagen.Worldgen.PlacementFeatures;

import java.util.List;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class HnSOrePlacement {
   public static List<PlacementModifier> orePlacement(PlacementModifier pCountPlacement, PlacementModifier pHeightRange) {
      return List.of(pCountPlacement, InSquarePlacement.spread(), pHeightRange, BiomeFilter.biome());
   }

   public static List<PlacementModifier> commonOrePlacement(int pCount, PlacementModifier pHeightRange) {
      return orePlacement(CountPlacement.of(pCount), pHeightRange);
   }

   public static List<PlacementModifier> rareOrePlacement(int pChance, PlacementModifier pHeightRange) {
      return orePlacement(RarityFilter.onAverageOnceEvery(pChance), pHeightRange);
   }
}
