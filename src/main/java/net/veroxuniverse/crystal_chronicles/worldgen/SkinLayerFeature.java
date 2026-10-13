package net.veroxuniverse.crystal_chronicles.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.veroxuniverse.crystal_chronicles.block.FleshBlock;
import net.veroxuniverse.crystal_chronicles.registry.CCBlocks;

public class SkinLayerFeature extends Feature<NoneFeatureConfiguration> {
   public SkinLayerFeature(Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
      WorldGenLevel level = ctx.level();
      RandomSource random = ctx.random();
      BlockPos origin = ctx.origin();
      int baseRadius = 2 + random.nextInt(2);
      boolean placedSomething = false;
      int maxOffset = baseRadius + 1;

      for (int dx = -maxOffset; dx <= maxOffset; dx++) {
         for (int dz = -maxOffset; dz <= maxOffset; dz++) {
            double distSq = (double)(dx * dx + dz * dz);
            double dist = Math.sqrt(distSq);
            if (!(dist > (double)baseRadius + 1.5)) {
               boolean tryPlace;
               if (dist <= (double)baseRadius - 0.5) {
                  tryPlace = true;
               } else if (dist <= (double)baseRadius + 0.5) {
                  tryPlace = random.nextFloat() < 0.7F;
               } else {
                  tryPlace = random.nextFloat() < 0.25F;
               }

               if (tryPlace) {
                  BlockPos pos = origin.offset(dx, 0, dz);
                  BlockPos below = pos.below();
                  if (level.isEmptyBlock(pos)) {
                     BlockState belowState = level.getBlockState(below);
                     if (belowState.getBlock() instanceof FleshBlock) {
                        BlockState skinState = ((Block)CCBlocks.SKIN_LAYER.get()).defaultBlockState();
                        level.setBlock(pos, skinState, 3);
                        if (belowState.hasProperty(FleshBlock.HAS_SKIN_ABOVE) && !belowState.getValue(FleshBlock.HAS_SKIN_ABOVE)) {
                           level.setBlock(below, belowState.setValue(FleshBlock.HAS_SKIN_ABOVE, Boolean.valueOf(true)), 3);
                        }

                        placedSomething = true;
                     }
                  }
               }
            }
         }
      }

      return placedSomething;
   }
}
