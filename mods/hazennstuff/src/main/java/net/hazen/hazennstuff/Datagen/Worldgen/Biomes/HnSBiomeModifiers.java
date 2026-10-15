package net.hazen.hazennstuff.Datagen.Worldgen.Biomes;

import net.hazen.hazennstuff.Datagen.Worldgen.PlacementFeatures.HnSPlacedFeatures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class HnSBiomeModifiers {
   public static final ResourceKey<BiomeModifier> ADD_ZENALITE_ORE = registerKey("add_zenalite_ore");
   public static final ResourceKey<BiomeModifier> ADD_SOLAR_CORE_ORE = registerKey("add_solar_core_ore");
   public static final ResourceKey<BiomeModifier> ADD_RUNESTONE_SLAG = registerKey("add_runestone_slag");
   public static final ResourceKey<BiomeModifier> ADD_CHLOROPHYTE_ORE = registerKey("add_chlorophyte_ore");
   public static final ResourceKey<BiomeModifier> ADD_DREADSTONE_ORE = registerKey("add_dreadstone_ore");

   public static void bootstrap(BootstrapContext<BiomeModifier> context) {
      HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
      HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
      context.register(
         ADD_RUNESTONE_SLAG,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(HnSPlacedFeatures.RUNESTONE_SLAG_PLACED_KEY)}),
            Decoration.UNDERGROUND_ORES
         )
      );
      context.register(
         ADD_SOLAR_CORE_ORE,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_NETHER),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(HnSPlacedFeatures.SOLAR_CORE_ORE_PLACED_KEY)}),
            Decoration.UNDERGROUND_ORES
         )
      );
      context.register(
         ADD_ZENALITE_ORE,
         new AddFeaturesBiomeModifier(
            HolderSet.direct(
               new Holder[]{biomes.getOrThrow(Biomes.END_BARRENS), biomes.getOrThrow(Biomes.END_MIDLANDS), biomes.getOrThrow(Biomes.END_HIGHLANDS)}
            ),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(HnSPlacedFeatures.ZENALITE_ORE_PLACED_KEY)}),
            Decoration.UNDERGROUND_ORES
         )
      );
      context.register(
         ADD_CHLOROPHYTE_ORE,
         new AddFeaturesBiomeModifier(
            HolderSet.direct(new Holder[]{biomes.getOrThrow(Biomes.JUNGLE), biomes.getOrThrow(Biomes.BAMBOO_JUNGLE)}),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(HnSPlacedFeatures.CHLOROPHYTE_PLACED_KEY)}),
            Decoration.UNDERGROUND_ORES
         )
      );
      context.register(
         ADD_DREADSTONE_ORE,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(HnSPlacedFeatures.DREADSTONE_PLACED_KEY)}),
            Decoration.UNDERGROUND_ORES
         )
      );
   }

   private static ResourceKey<BiomeModifier> registerKey(String name) {
      return ResourceKey.create(Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("hazennstuff", name));
   }
}
