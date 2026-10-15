package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.HorrorElementModMod;
import en.predator.horrorelementmod.init.HorrorElementModModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class GoldtrigeredEntityWalksOnTheBlockProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      HorrorElementModMod.queueServerWork(20, () -> {
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)HorrorElementModModBlocks.GOLDTRIGERRER.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOld : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
               } catch (Exception var14) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
      });
   }
}
