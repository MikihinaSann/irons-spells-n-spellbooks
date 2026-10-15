package net.warphan.iss_magicfromtheeast.registries;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class MFTEFeaturesRegistries {
   private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, "iss_magicfromtheeast");
   private static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(
      Registries.CONFIGURED_FEATURE, "iss_magicfromtheeast"
   );
   private static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registries.PLACED_FEATURE, "iss_magicfromtheeast");
   public static final ResourceKey<ConfiguredFeature<?, ?>> JADESTONE_ORE_FEATURE = configuredFeatureResourceKey("ore_jadestone_feature");
   public static final ResourceKey<PlacedFeature> JADESTONE_ORE_JUNGLE = placedFeatureResourceKey("ore_jadestone_jungle");
   public static final ResourceKey<PlacedFeature> JADESTONE_ORE_MOUNTAIN = placedFeatureResourceKey("ore_jadestone_mountain");
   public static final ResourceKey<BiomeModifier> ADD_JADESTONE_TO_JUNGLE = biomeModifierResourceKey("add_jadestone_jungle");
   public static final ResourceKey<BiomeModifier> ADD_JADESTONE_TO_MOUNTAIN = biomeModifierResourceKey("add_jadestone_mountain");

   public static void register(IEventBus eventBus) {
      FEATURES.register(eventBus);
      CONFIGURED_FEATURES.register(eventBus);
      PLACED_FEATURES.register(eventBus);
   }

   public static void boostrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
      RuleTest stoneTest = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
      RuleTest deepslateTest = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
      List<TargetBlockState> jadestoneList = List.of(
         OreConfiguration.target(stoneTest, ((Block)MFTEBlockRegistries.JADE_ORE.get()).defaultBlockState()),
         OreConfiguration.target(deepslateTest, ((Block)MFTEBlockRegistries.JADE_ORE_DEEPSLATE.get()).defaultBlockState())
      );
      FeatureUtils.register(context, JADESTONE_ORE_FEATURE, Feature.ORE, new OreConfiguration(jadestoneList, 10));
   }

   public static void boostrapPlacedFeature(BootstrapContext<PlacedFeature> context) {
      HolderGetter<ConfiguredFeature<?, ?>> holderGetter = context.lookup(CONFIGURED_FEATURES.getRegistryKey());
      Holder<ConfiguredFeature<?, ?>> holderJadestone = holderGetter.getOrThrow(JADESTONE_ORE_FEATURE);
      List<PlacementModifier> listJungle = List.of(
         CountPlacement.of(12),
         InSquarePlacement.spread(),
         HeightRangePlacement.triangle(VerticalAnchor.absolute(-56), VerticalAnchor.absolute(48)),
         BiomeFilter.biome()
      );
      List<PlacementModifier> listMountain = List.of(
         CountPlacement.of(30),
         InSquarePlacement.spread(),
         HeightRangePlacement.triangle(VerticalAnchor.absolute(64), VerticalAnchor.absolute(440)),
         BiomeFilter.biome()
      );
      PlacementUtils.register(context, JADESTONE_ORE_JUNGLE, holderJadestone, listJungle);
      PlacementUtils.register(context, JADESTONE_ORE_MOUNTAIN, holderJadestone, listMountain);
   }

   public static void boostrapBiomesModifier(BootstrapContext<BiomeModifier> context) {
      HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
      HolderGetter<PlacedFeature> features = context.lookup(PLACED_FEATURES.getRegistryKey());
      context.register(
         ADD_JADESTONE_TO_JUNGLE,
         new AddFeaturesBiomeModifier(tag(biomes, BiomeTags.IS_JUNGLE), feature(features, JADESTONE_ORE_JUNGLE), Decoration.UNDERGROUND_ORES)
      );
      context.register(
         ADD_JADESTONE_TO_MOUNTAIN,
         new AddFeaturesBiomeModifier(tag(biomes, BiomeTags.IS_MOUNTAIN), feature(features, JADESTONE_ORE_MOUNTAIN), Decoration.UNDERGROUND_ORES)
      );
   }

   private static ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureResourceKey(String name) {
      return ResourceKey.create(CONFIGURED_FEATURES.getRegistryKey(), ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", name));
   }

   private static ResourceKey<PlacedFeature> placedFeatureResourceKey(String name) {
      return ResourceKey.create(PLACED_FEATURES.getRegistryKey(), ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", name));
   }

   private static ResourceKey<BiomeModifier> biomeModifierResourceKey(String name) {
      return ResourceKey.create(Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", name));
   }

   private static HolderSet<Biome> tag(HolderGetter<Biome> holderGetter, TagKey<Biome> key) {
      return holderGetter.getOrThrow(key);
   }

   private static HolderSet<PlacedFeature> feature(HolderGetter<PlacedFeature> holderGetter, ResourceKey<PlacedFeature> features) {
      return HolderSet.direct(new Holder[]{holderGetter.getOrThrow(features)});
   }
}
