package en.predator.horrorelementmod.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public class RedlanternBlockValidPlacementConditionProcedure {
   public static boolean execute(LevelAccessor world, double x, double y, double z) {
      return world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude() || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).canOcclude();
   }
}
