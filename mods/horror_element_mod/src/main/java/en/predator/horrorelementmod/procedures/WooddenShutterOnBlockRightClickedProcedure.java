package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.init.HorrorElementModModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class WooddenShutterOnBlockRightClickedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      BlockPos _bp = BlockPos.containing(x, y, z);
      BlockState _bs = ((Block)HorrorElementModModBlocks.WOODENSHUTTEROPENENED.get()).defaultBlockState();
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
      if (world instanceof Level _level) {
         if (!_level.isClientSide()) {
            _level.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.bamboo_wood_door.open")),
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
         } else {
            _level.playLocalSound(
               x,
               y,
               z,
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.bamboo_wood_door.open")),
               SoundSource.NEUTRAL,
               1.0F,
               1.0F,
               false
            );
         }
      }
   }
}
