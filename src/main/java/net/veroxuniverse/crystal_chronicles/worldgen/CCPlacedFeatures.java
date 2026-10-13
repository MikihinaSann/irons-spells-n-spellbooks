package net.veroxuniverse.crystal_chronicles.worldgen;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.veroxuniverse.crystal_chronicles.registry.CCBlocks;

public class CCPlacedFeatures {
   public static final ResourceKey<PlacedFeature> BRONCHUS_PLACED_KEY = registerKey("flesh/bronchus_placed");
   public static final ResourceKey<PlacedFeature> BRONCHUS_PLACED_CAVE_KEY = registerKey("flesh/bronchus_cave");

   public static void bootstrap(BootstrapContext<PlacedFeature> context) {
      HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
      register(
         context,
         BRONCHUS_PLACED_KEY,
         configuredFeatures.getOrThrow(CCConfiguredFeatures.BRONCHUS_KEY),
         VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1F, 2), (Block)CCBlocks.ALVEOLUS.get())
      );
      register(
         context,
         BRONCHUS_PLACED_CAVE_KEY,
         configuredFeatures.getOrThrow(CCConfiguredFeatures.BRONCHUS_KEY),
         List.of(
            CountOnEveryLayerPlacement.of(5),
            NoiseThresholdCountPlacement.of(0.5, 0, 1),
            BlockPredicateFilter.forPredicate(
               BlockPredicate.allOf(
                  BlockPredicate.wouldSurvive(((Block)CCBlocks.ALVEOLUS.get()).defaultBlockState(), Vec3i.ZERO),
                  BlockPredicate.matchesBlocks(new Vec3i(0, 5, 0), List.of(Blocks.AIR)),
                  BlockPredicate.matchesBlocks(new Vec3i(0, 10, 0), List.of(Blocks.AIR))
               )
            ),
            BiomeFilter.biome()
         )
      );
   }

   private static ResourceKey<PlacedFeature> registerKey(String name) {
      return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath("crystal_chronicles", name));
   }

   private static ResourceKey<PlacedFeature> registerFleshKey(String name) {
      return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath("crystal_chronicles", name));
   }

   private static void register(
      BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers
   ) {
      context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
   }
}
