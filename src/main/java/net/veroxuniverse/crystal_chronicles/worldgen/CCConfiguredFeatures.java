package net.veroxuniverse.crystal_chronicles.worldgen;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration.TreeConfigurationBuilder;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.veroxuniverse.crystal_chronicles.block.BronchusBlock;
import net.veroxuniverse.crystal_chronicles.registry.CCBlocks;

public class CCConfiguredFeatures {
   public static final ResourceKey<ConfiguredFeature<?, ?>> BRONCHUS_KEY = registerKey("bronchus_tree");

   public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
      register(
         context,
         BRONCHUS_KEY,
         CCFeatures.BRONCHUS_TREE_FEATURE.get(),
         new TreeConfigurationBuilder(
               BlockStateProvider.simple(((Block)CCBlocks.BRONCHUS.get()).defaultBlockState().setValue(BronchusBlock.FACING, Direction.UP)),
               new StraightTrunkPlacer(4, 7, 0),
               BlockStateProvider.simple(((Block)CCBlocks.ALVEOLUS_BLOCK.get()).defaultBlockState()),
               new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
               new TwoLayersFeatureSize(1, 0, 1)
            )
            .dirt(BlockStateProvider.simple(((Block)CCBlocks.FLESH_BLOCK.get()).defaultBlockState()))
            .build()
      );
   }

   public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
      return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath("crystal_chronicles", name));
   }

   private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
      BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration
   ) {
      context.register(key, new ConfiguredFeature(feature, configuration));
   }
}
