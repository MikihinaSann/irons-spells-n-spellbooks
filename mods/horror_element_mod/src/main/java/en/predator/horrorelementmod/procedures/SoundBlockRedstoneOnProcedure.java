package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.HorrorElementModMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SoundBlockRedstoneOnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if ((new Object() {
         public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null ? blockEntity.getPersistentData().getBoolean(tag) : false;
         }
      }).getValue(world, BlockPos.containing(x, y, z), "cooldown")) {
         if (!(new Object() {
            public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
               BlockEntity blockEntity = world.getBlockEntity(pos);
               return blockEntity != null ? blockEntity.getPersistentData().getBoolean(tag) : false;
            }
         }).getValue(world, BlockPos.containing(x, y, z), "soundplaying")) {
            if ((new Object() {
               public double getValue(LevelAccessor world, BlockPos pos, String tag) {
                  BlockEntity blockEntity = world.getBlockEntity(pos);
                  return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
               }
            }).getValue(world, BlockPos.containing(x, y, z), "soundtoplay") == 0.0) {
               if (!world.isClientSide()) {
                  BlockPos _bp = BlockPos.containing(x, y, z);
                  BlockEntity _blockEntity = world.getBlockEntity(_bp);
                  BlockState _bs = world.getBlockState(_bp);
                  if (_blockEntity != null) {
                     _blockEntity.getPersistentData().putBoolean("soundplaying", true);
                  }

                  if (world instanceof Level _level) {
                     _level.sendBlockUpdated(_bp, _bs, _bs, 3);
                  }
               }

               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:kids_laughing")),
                        SoundSource.HOSTILE,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:kids_laughing")),
                        SoundSource.HOSTILE,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               HorrorElementModMod.queueServerWork(6000, () -> {
                  if (!world.isClientSide()) {
                     BlockPos _bpx = BlockPos.containing(x, y, z);
                     BlockEntity _blockEntityx = world.getBlockEntity(_bpx);
                     BlockState _bsx = world.getBlockState(_bpx);
                     if (_blockEntityx != null) {
                        _blockEntityx.getPersistentData().putBoolean("soundplaying", false);
                     }

                     if (world instanceof Level _levelx) {
                        _levelx.sendBlockUpdated(_bpx, _bsx, _bsx, 3);
                     }
                  }
               });
            } else if ((new Object() {
               public double getValue(LevelAccessor world, BlockPos pos, String tag) {
                  BlockEntity blockEntity = world.getBlockEntity(pos);
                  return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
               }
            }).getValue(world, BlockPos.containing(x, y, z), "soundtoplay") == 1.0) {
               if (!world.isClientSide()) {
                  BlockPos _bpx = BlockPos.containing(x, y, z);
                  BlockEntity _blockEntityx = world.getBlockEntity(_bpx);
                  BlockState _bsx = world.getBlockState(_bpx);
                  if (_blockEntityx != null) {
                     _blockEntityx.getPersistentData().putBoolean("soundplaying", true);
                  }

                  if (world instanceof Level _levelx) {
                     _levelx.sendBlockUpdated(_bpx, _bsx, _bsx, 3);
                  }
               }

               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:breaking_glass")),
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:breaking_glass")),
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               HorrorElementModMod.queueServerWork(6000, () -> {
                  if (!world.isClientSide()) {
                     BlockPos _bpxx = BlockPos.containing(x, y, z);
                     BlockEntity _blockEntityxx = world.getBlockEntity(_bpxx);
                     BlockState _bsxx = world.getBlockState(_bpxx);
                     if (_blockEntityxx != null) {
                        _blockEntityxx.getPersistentData().putBoolean("soundplaying", false);
                     }

                     if (world instanceof Level _levelxx) {
                        _levelxx.sendBlockUpdated(_bpxx, _bsxx, _bsxx, 3);
                     }
                  }
               });
            } else if ((new Object() {
               public double getValue(LevelAccessor world, BlockPos pos, String tag) {
                  BlockEntity blockEntity = world.getBlockEntity(pos);
                  return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
               }
            }).getValue(world, BlockPos.containing(x, y, z), "soundtoplay") == 2.0) {
               if (!(new Object() {
                  public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
                     BlockEntity blockEntity = world.getBlockEntity(pos);
                     return blockEntity != null ? blockEntity.getPersistentData().getBoolean(tag) : false;
                  }
               }).getValue(world, BlockPos.containing(x, y, z), "soundplaying")) {
                  if (!world.isClientSide()) {
                     BlockPos _bpxx = BlockPos.containing(x, y, z);
                     BlockEntity _blockEntityxx = world.getBlockEntity(_bpxx);
                     BlockState _bsxx = world.getBlockState(_bpxx);
                     if (_blockEntityxx != null) {
                        _blockEntityxx.getPersistentData().putBoolean("soundplaying", true);
                     }

                     if (world instanceof Level _levelxx) {
                        _levelxx.sendBlockUpdated(_bpxx, _bsxx, _bsxx, 3);
                     }
                  }

                  if (world instanceof Level _levelxx) {
                     if (!_levelxx.isClientSide()) {
                        _levelxx.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:laboratory_alarm")),
                           SoundSource.HOSTILE,
                           1.0F,
                           1.0F
                        );
                     } else {
                        _levelxx.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:laboratory_alarm")),
                           SoundSource.HOSTILE,
                           1.0F,
                           1.0F,
                           false
                        );
                     }
                  }

                  HorrorElementModMod.queueServerWork(6000, () -> {
                     if (!world.isClientSide()) {
                        BlockPos _bpxxx = BlockPos.containing(x, y, z);
                        BlockEntity _blockEntityxxx = world.getBlockEntity(_bpxxx);
                        BlockState _bsxxx = world.getBlockState(_bpxxx);
                        if (_blockEntityxxx != null) {
                           _blockEntityxxx.getPersistentData().putBoolean("soundplaying", false);
                        }

                        if (world instanceof Level _levelxxx) {
                           _levelxxx.sendBlockUpdated(_bpxxx, _bsxxx, _bsxxx, 3);
                        }
                     }
                  });
               }
            } else if ((new Object() {
               public double getValue(LevelAccessor world, BlockPos pos, String tag) {
                  BlockEntity blockEntity = world.getBlockEntity(pos);
                  return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
               }
            }).getValue(world, BlockPos.containing(x, y, z), "soundtoplay") == 3.0) {
               if (!world.isClientSide()) {
                  BlockPos _bpxxx = BlockPos.containing(x, y, z);
                  BlockEntity _blockEntityxxx = world.getBlockEntity(_bpxxx);
                  BlockState _bsxxx = world.getBlockState(_bpxxx);
                  if (_blockEntityxxx != null) {
                     _blockEntityxxx.getPersistentData().putBoolean("soundplaying", true);
                  }

                  if (world instanceof Level _levelxxx) {
                     _levelxxx.sendBlockUpdated(_bpxxx, _bsxxx, _bsxxx, 3);
                  }
               }

               if (world instanceof Level _levelxxx) {
                  if (!_levelxxx.isClientSide()) {
                     _levelxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:predator_clicking_noise")),
                        SoundSource.HOSTILE,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:predator_clicking_noise")),
                        SoundSource.HOSTILE,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               HorrorElementModMod.queueServerWork(6000, () -> {
                  if (!world.isClientSide()) {
                     BlockPos _bpxxxx = BlockPos.containing(x, y, z);
                     BlockEntity _blockEntityxxxx = world.getBlockEntity(_bpxxxx);
                     BlockState _bsxxxx = world.getBlockState(_bpxxxx);
                     if (_blockEntityxxxx != null) {
                        _blockEntityxxxx.getPersistentData().putBoolean("soundplaying", false);
                     }

                     if (world instanceof Level _levelxxxx) {
                        _levelxxxx.sendBlockUpdated(_bpxxxx, _bsxxxx, _bsxxxx, 3);
                     }
                  }
               });
            } else if ((new Object() {
               public double getValue(LevelAccessor world, BlockPos pos, String tag) {
                  BlockEntity blockEntity = world.getBlockEntity(pos);
                  return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
               }
            }).getValue(world, BlockPos.containing(x, y, z), "soundtoplay") == 4.0) {
               if (!world.isClientSide()) {
                  BlockPos _bpxxxx = BlockPos.containing(x, y, z);
                  BlockEntity _blockEntityxxxx = world.getBlockEntity(_bpxxxx);
                  BlockState _bsxxxx = world.getBlockState(_bpxxxx);
                  if (_blockEntityxxxx != null) {
                     _blockEntityxxxx.getPersistentData().putBoolean("soundplaying", true);
                  }

                  if (world instanceof Level _levelxxxx) {
                     _levelxxxx.sendBlockUpdated(_bpxxxx, _bsxxxx, _bsxxxx, 3);
                  }
               }

               if (world instanceof Level _levelxxxx) {
                  if (!_levelxxxx.isClientSide()) {
                     _levelxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:heart_beating")),
                        SoundSource.AMBIENT,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:heart_beating")),
                        SoundSource.AMBIENT,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               HorrorElementModMod.queueServerWork(6000, () -> {
                  if (!world.isClientSide()) {
                     BlockPos _bpxxxxx = BlockPos.containing(x, y, z);
                     BlockEntity _blockEntityxxxxx = world.getBlockEntity(_bpxxxxx);
                     BlockState _bsxxxxx = world.getBlockState(_bpxxxxx);
                     if (_blockEntityxxxxx != null) {
                        _blockEntityxxxxx.getPersistentData().putBoolean("soundplaying", false);
                     }

                     if (world instanceof Level _levelxxxxx) {
                        _levelxxxxx.sendBlockUpdated(_bpxxxxx, _bsxxxxx, _bsxxxxx, 3);
                     }
                  }
               });
            }
         }
      } else if ((new Object() {
         public double getValue(LevelAccessor world, BlockPos pos, String tag) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
         }
      }).getValue(world, BlockPos.containing(x, y, z), "soundtoplay") == 0.0) {
         if (world instanceof Level _levelxxxxx) {
            if (!_levelxxxxx.isClientSide()) {
               _levelxxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:kids_laughing")),
                  SoundSource.HOSTILE,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:kids_laughing")),
                  SoundSource.HOSTILE,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }
      } else if ((new Object() {
         public double getValue(LevelAccessor world, BlockPos pos, String tag) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
         }
      }).getValue(world, BlockPos.containing(x, y, z), "soundtoplay") == 1.0) {
         if (world instanceof Level _levelxxxxxx) {
            if (!_levelxxxxxx.isClientSide()) {
               _levelxxxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:breaking_glass")),
                  SoundSource.BLOCKS,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxxx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:breaking_glass")),
                  SoundSource.BLOCKS,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }
      } else if ((new Object() {
         public double getValue(LevelAccessor world, BlockPos pos, String tag) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
         }
      }).getValue(world, BlockPos.containing(x, y, z), "soundtoplay") == 2.0) {
         if (!(new Object() {
            public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
               BlockEntity blockEntity = world.getBlockEntity(pos);
               return blockEntity != null ? blockEntity.getPersistentData().getBoolean(tag) : false;
            }
         }).getValue(world, BlockPos.containing(x, y, z), "soundplaying")) {
            if (!world.isClientSide()) {
               BlockPos _bpxxxxx = BlockPos.containing(x, y, z);
               BlockEntity _blockEntityxxxxx = world.getBlockEntity(_bpxxxxx);
               BlockState _bsxxxxx = world.getBlockState(_bpxxxxx);
               if (_blockEntityxxxxx != null) {
                  _blockEntityxxxxx.getPersistentData().putBoolean("soundplaying", true);
               }

               if (world instanceof Level _levelxxxxxxx) {
                  _levelxxxxxxx.sendBlockUpdated(_bpxxxxx, _bsxxxxx, _bsxxxxx, 3);
               }
            }

            if (world instanceof Level _levelxxxxxxx) {
               if (!_levelxxxxxxx.isClientSide()) {
                  _levelxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:laboratory_alarm")),
                     SoundSource.BLOCKS,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:laboratory_alarm")),
                     SoundSource.BLOCKS,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            HorrorElementModMod.queueServerWork(2400, () -> {
               if (!world.isClientSide()) {
                  BlockPos _bpxxxxxx = BlockPos.containing(x, y, z);
                  BlockEntity _blockEntityxxxxxx = world.getBlockEntity(_bpxxxxxx);
                  BlockState _bsxxxxxx = world.getBlockState(_bpxxxxxx);
                  if (_blockEntityxxxxxx != null) {
                     _blockEntityxxxxxx.getPersistentData().putBoolean("soundplaying", false);
                  }

                  if (world instanceof Level _levelxxxxxxxx) {
                     _levelxxxxxxxx.sendBlockUpdated(_bpxxxxxx, _bsxxxxxx, _bsxxxxxx, 3);
                  }
               }
            });
         }
      } else if ((new Object() {
         public double getValue(LevelAccessor world, BlockPos pos, String tag) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
         }
      }).getValue(world, BlockPos.containing(x, y, z), "soundtoplay") == 3.0) {
         if (!world.isClientSide()) {
            BlockPos _bpxxxxxx = BlockPos.containing(x, y, z);
            BlockEntity _blockEntityxxxxxx = world.getBlockEntity(_bpxxxxxx);
            BlockState _bsxxxxxx = world.getBlockState(_bpxxxxxx);
            if (_blockEntityxxxxxx != null) {
               _blockEntityxxxxxx.getPersistentData().putBoolean("soundplaying", true);
            }

            if (world instanceof Level _levelxxxxxxxx) {
               _levelxxxxxxxx.sendBlockUpdated(_bpxxxxxx, _bsxxxxxx, _bsxxxxxx, 3);
            }
         }

         if (world instanceof Level _levelxxxxxxxx) {
            if (!_levelxxxxxxxx.isClientSide()) {
               _levelxxxxxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:predator_clicking_noise")),
                  SoundSource.HOSTILE,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxxxxx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:predator_clicking_noise")),
                  SoundSource.HOSTILE,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }
      } else if ((new Object() {
         public double getValue(LevelAccessor world, BlockPos pos, String tag) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
         }
      }).getValue(world, BlockPos.containing(x, y, z), "soundtoplay") == 4.0 && world instanceof Level _levelxxxxxxxxx) {
         if (!_levelxxxxxxxxx.isClientSide()) {
            _levelxxxxxxxxx.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:heart_beating")),
               SoundSource.AMBIENT,
               1.0F,
               1.0F
            );
         } else {
            _levelxxxxxxxxx.playLocalSound(
               x,
               y,
               z,
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("horror_element_mod:heart_beating")),
               SoundSource.AMBIENT,
               1.0F,
               1.0F,
               false
            );
         }
      }
   }
}
