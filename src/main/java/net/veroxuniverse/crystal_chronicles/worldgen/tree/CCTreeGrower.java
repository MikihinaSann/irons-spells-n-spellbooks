package net.veroxuniverse.crystal_chronicles.worldgen.tree;

import java.util.Optional;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.veroxuniverse.crystal_chronicles.worldgen.CCConfiguredFeatures;

public class CCTreeGrower {
   public static final TreeGrower BRONCHUS = new TreeGrower(
      "crystal_chronicles:bronchus", Optional.empty(), Optional.of(CCConfiguredFeatures.BRONCHUS_KEY), Optional.empty()
   );
}
