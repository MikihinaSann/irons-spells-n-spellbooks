package en.predator.horrorelementmod.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class RedlanternchainNeighbourBlockChangesProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world.getBlockState(BlockPos.containing(x, y + 1.0, z)).getBlock() != Blocks.CHAIN
         && !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()) {
         world.destroyBlock(BlockPos.containing(x, y, z), false);
      }
   }
}
