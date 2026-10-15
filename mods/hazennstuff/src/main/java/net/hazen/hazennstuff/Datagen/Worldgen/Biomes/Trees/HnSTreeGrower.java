package net.hazen.hazennstuff.Datagen.Worldgen.Biomes.Trees;

import java.util.Optional;
import net.hazen.hazennstuff.Datagen.Worldgen.HnSConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

public class HnSTreeGrower {
   public static final TreeGrower FROSTBITE_BIRCH = new TreeGrower(
      "hazennstufffrostbite_birch", Optional.empty(), Optional.of(HnSConfiguredFeatures.FROSTBITE_BIRCH_KEY), Optional.empty()
   );
   public static final TreeGrower WISEWOOD = new TreeGrower(
      "hazennstuffwisewood", Optional.empty(), Optional.of(HnSConfiguredFeatures.WISEWOOD_KEY), Optional.empty()
   );
}
