package net.neoforged.neoforge.common.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeGenerationSettings.PlainBuilder;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class BiomeGenerationSettingsBuilder extends PlainBuilder {
   public BiomeGenerationSettingsBuilder(BiomeGenerationSettings orig) {
      orig.getCarvingStages().forEach(k -> {
         this.carvers.put(k, new ArrayList());
         orig.getCarvers(k).forEach(v -> ((List)this.carvers.get(k)).add(v));
      });
      orig.features().forEach(l -> {
         ArrayList<Holder<PlacedFeature>> featureList = new ArrayList<>();
         l.forEach(featureList::add);
         this.features.add(featureList);
      });
   }

   public List<Holder<PlacedFeature>> getFeatures(Decoration stage) {
      this.addFeatureStepsUpTo(stage.ordinal());
      return (List<Holder<PlacedFeature>>)this.features.get(stage.ordinal());
   }

   public List<Holder<ConfiguredWorldCarver<?>>> getCarvers(Carving stage) {
      return this.carvers.computeIfAbsent(stage, key -> new ArrayList<>());
   }
}
