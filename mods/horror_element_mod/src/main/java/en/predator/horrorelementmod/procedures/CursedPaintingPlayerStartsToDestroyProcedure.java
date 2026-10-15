package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.HorrorElementModMod;
import en.predator.horrorelementmod.init.HorrorElementModModBlocks;
import en.predator.horrorelementmod.network.HorrorElementModModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class CursedPaintingPlayerStartsToDestroyProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (!HorrorElementModModVariables.MapVariables.get(world).SCREAMER) {
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)HorrorElementModModBlocks.CURSED_PAINTING_SCREAMER.get()).defaultBlockState();
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
         HorrorElementModModVariables.MapVariables.get(world).SCREAMER = true;
         HorrorElementModModVariables.MapVariables.get(world).syncData(world);
         HorrorElementModMod.queueServerWork(6000, () -> {
            HorrorElementModModVariables.MapVariables.get(world).SCREAMER = false;
            HorrorElementModModVariables.MapVariables.get(world).syncData(world);
         });
      }
   }
}
