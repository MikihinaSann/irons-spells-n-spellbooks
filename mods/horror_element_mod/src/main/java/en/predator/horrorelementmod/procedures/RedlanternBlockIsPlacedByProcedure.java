package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.init.HorrorElementModModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class RedlanternBlockIsPlacedByProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world.getBlockState(BlockPos.containing(x, y + 1.0, z)).getBlock() == Blocks.CHAIN) {
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)HorrorElementModModBlocks.REDLANTERNCHAIN.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOld : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
               } catch (Exception var15) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
      } else if (world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()) {
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)HorrorElementModModBlocks.REDLANTERNCHAIN.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldx));
               } catch (Exception var14) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
      }
   }
}
