package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.init.HorrorElementModModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class AxeAndLogOnBlockRightClickedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level _level) {
         if (!_level.isClientSide()) {
            _level.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.axe.strip")),
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
         } else {
            _level.playLocalSound(
               x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.axe.strip")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
            );
         }
      }

      BlockPos _bp = BlockPos.containing(x, y, z);
      BlockState _bs = ((Block)HorrorElementModModBlocks.BLOODED_LOG.get()).defaultBlockState();
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
      if (world instanceof ServerLevel _levelx) {
         ItemEntity entityToSpawn = new ItemEntity(_levelx, x, y + 1.0, z, new ItemStack(Items.IRON_AXE));
         entityToSpawn.setPickUpDelay(10);
         _levelx.addFreshEntity(entityToSpawn);
      }
   }
}
