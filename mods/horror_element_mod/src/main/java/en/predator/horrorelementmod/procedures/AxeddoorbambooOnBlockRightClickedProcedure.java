package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.init.HorrorElementModModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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

public class AxeddoorbambooOnBlockRightClickedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == HorrorElementModModBlocks.AXEDDOORBAMBOO.get()) {
         if (world instanceof ServerLevel _level) {
            ItemEntity entityToSpawn = new ItemEntity(_level, x, y + 1.0, z, new ItemStack(Items.IRON_AXE));
            entityToSpawn.setPickUpDelay(10);
            _level.addFreshEntity(entityToSpawn);
         }

         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
               );
            }
         }

         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)HorrorElementModModBlocks.AXED_DOOR_BAMBOO_FELL.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOld : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
               } catch (Exception var20) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(ParticleTypes.SMOKE, x, y - 1.0, z, 300, 0.5, 0.5, 0.5, 0.01);
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == HorrorElementModModBlocks.AXED_DOOR_DARKOAK.get()) {
         if (world instanceof ServerLevel _levelx) {
            ItemEntity entityToSpawn = new ItemEntity(_levelx, x, y + 1.0, z, new ItemStack(Items.IRON_AXE));
            entityToSpawn.setPickUpDelay(10);
            _levelx.addFreshEntity(entityToSpawn);
         }

         if (world instanceof Level _levelx) {
            if (!_levelx.isClientSide()) {
               _levelx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelx.playLocalSound(
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
               );
            }
         }

         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)HorrorElementModModBlocks.AXED_DOOR_DARK_OAK_FELL.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldx));
               } catch (Exception var19) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof ServerLevel _levelxx) {
            _levelxx.sendParticles(ParticleTypes.SMOKE, x, y - 1.0, z, 300, 0.5, 0.5, 0.5, 0.01);
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == HorrorElementModModBlocks.AXED_DOOR_JUNGLE.get()) {
         if (world instanceof ServerLevel _levelxx) {
            ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y + 1.0, z, new ItemStack(Items.IRON_AXE));
            entityToSpawn.setPickUpDelay(10);
            _levelxx.addFreshEntity(entityToSpawn);
         }

         if (world instanceof Level _levelxx) {
            if (!_levelxx.isClientSide()) {
               _levelxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxx.playLocalSound(
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
               );
            }
         }

         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)HorrorElementModModBlocks.AXED_DOOR_JUNGLE_FELL.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldxx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldxx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldxx));
               } catch (Exception var18) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof ServerLevel _levelxxx) {
            _levelxxx.sendParticles(ParticleTypes.SMOKE, x, y - 1.0, z, 300, 0.5, 0.5, 0.5, 0.01);
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == HorrorElementModModBlocks.AXED_DOOR_OAK.get()) {
         if (world instanceof ServerLevel _levelxxx) {
            ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.IRON_AXE));
            entityToSpawn.setPickUpDelay(10);
            _levelxxx.addFreshEntity(entityToSpawn);
         }

         if (world instanceof Level _levelxxx) {
            if (!_levelxxx.isClientSide()) {
               _levelxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxx.playLocalSound(
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
               );
            }
         }

         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)HorrorElementModModBlocks.AXED_DOOR_OAK_FELL.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldxxx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldxxx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldxxx));
               } catch (Exception var17) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof ServerLevel _levelxxxx) {
            _levelxxxx.sendParticles(ParticleTypes.SMOKE, x, y - 1.0, z, 300, 0.5, 0.5, 0.5, 0.01);
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == HorrorElementModModBlocks.DESTROYED_DOOR_IRON.get()) {
         if (world instanceof Level _levelxxxx) {
            if (!_levelxxxx.isClientSide()) {
               _levelxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxx.playLocalSound(
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
               );
            }
         }

         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)HorrorElementModModBlocks.DESTROYED_DOOR_IRON_FELL.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldxxxx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldxxxx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldxxxx));
               } catch (Exception var16) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof ServerLevel _levelxxxxx) {
            _levelxxxxx.sendParticles(ParticleTypes.SMOKE, x, y - 1.0, z, 300, 0.5, 0.5, 0.5, 0.01);
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == HorrorElementModModBlocks.AXED_DOOR_SPRUCE.get()) {
         if (world instanceof ServerLevel _levelxxxxx) {
            ItemEntity entityToSpawn = new ItemEntity(_levelxxxxx, x, y + 1.0, z, new ItemStack(Items.IRON_AXE));
            entityToSpawn.setPickUpDelay(10);
            _levelxxxxx.addFreshEntity(entityToSpawn);
         }

         if (world instanceof Level _levelxxxxx) {
            if (!_levelxxxxx.isClientSide()) {
               _levelxxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxx.playLocalSound(
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
               );
            }
         }

         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)HorrorElementModModBlocks.AXED_DOOR_SPRUCE_FELL.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldxxxxx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldxxxxx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldxxxxx));
               } catch (Exception var15) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof ServerLevel _levelxxxxxx) {
            _levelxxxxxx.sendParticles(ParticleTypes.SMOKE, x, y - 1.0, z, 300, 0.5, 0.5, 0.5, 0.01);
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == HorrorElementModModBlocks.AXED_DOOR_BIRCH.get()) {
         if (world instanceof ServerLevel _levelxxxxxx) {
            ItemEntity entityToSpawn = new ItemEntity(_levelxxxxxx, x, y + 1.0, z, new ItemStack(Items.IRON_AXE));
            entityToSpawn.setPickUpDelay(10);
            _levelxxxxxx.addFreshEntity(entityToSpawn);
         }

         if (world instanceof Level _levelxxxxxx) {
            if (!_levelxxxxxx.isClientSide()) {
               _levelxxxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxxx.playLocalSound(
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.break")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
               );
            }
         }

         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)HorrorElementModModBlocks.AXED_DOOR_BIRCH_FELL.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldxxxxxx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldxxxxxx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldxxxxxx));
               } catch (Exception var14) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof ServerLevel _levelxxxxxxx) {
            _levelxxxxxxx.sendParticles(ParticleTypes.SMOKE, x, y - 1.0, z, 300, 0.5, 0.5, 0.5, 0.01);
         }
      }
   }
}
