package net.alshanex.alshanex_familiars.util;

import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.magic_arrow.MagicArrowProjectile;
import io.redspace.ironsspellbooks.entity.spells.magic_missile.MagicMissileProjectile;
import java.util.Random;
import net.alshanex.alshanex_familiars.entity.misc.ChorusFlowerEntity;
import net.alshanex.alshanex_familiars.entity.misc.DragonEggEntity;
import net.alshanex.alshanex_familiars.entity.misc.EndStoneEntity;
import net.alshanex.alshanex_familiars.entity.misc.PurpurBricksEntity;
import net.alshanex.alshanex_familiars.entity.misc.PurpurPilarEntity;
import net.alshanex.alshanex_familiars.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class FrozenUtils {
   public static void freezeSurroundingBlocks(BlockPos center, double radius, Level level) {
      for (int x = (int)Math.floor(-radius); x <= Math.ceil(radius); x++) {
         for (int y = -3; y <= 3; y++) {
            for (int z = (int)Math.floor(-radius); z <= Math.ceil(radius); z++) {
               BlockPos currentPos = center.offset(x, y, z);
               BlockState blockState = level.getBlockState(currentPos);
               double distance = Math.sqrt(x * x + z * z);
               boolean isAboveCenter = currentPos.equals(center.above()) || currentPos.equals(center.above(2));
               if (distance <= radius
                  && !blockState.is(AlshanexFamiliarsTags.BLOCK_FREEZE_BLACKLIST)
                  && blockState.getFluidState().getType() == Fluids.EMPTY
                  && !blockState.isAir()
                  && !isIceOrSnow(blockState.getBlock())
                  && !blockState.is(AlshanexFamiliarsTags.FREEZE_IGNORE)
                  && Block.isShapeFullBlock(blockState.getShape(level, currentPos))) {
                  for (Direction direction : Direction.values()) {
                     BlockPos adjacentPos = currentPos.relative(direction);
                     BlockState adjacentState = level.getBlockState(adjacentPos);
                     if (!adjacentState.isAir() && !adjacentState.is(AlshanexFamiliarsTags.FREEZE_DESTROYABLE)) {
                        if (adjacentState.getBlock() == BlockRegistry.ICE_SURFACE_BLOCK.get()) {
                           BlockState updatedState = (BlockState)adjacentState.setValue(MultifaceBlock.getFaceProperty(direction.getOpposite()), true);
                           level.setBlockAndUpdate(adjacentPos, updatedState);
                        }
                     } else {
                        BlockState iceSurfaceState = (BlockState)((Block)BlockRegistry.ICE_SURFACE_BLOCK.get())
                           .defaultBlockState()
                           .setValue(MultifaceBlock.getFaceProperty(direction.getOpposite()), true);
                        level.setBlockAndUpdate(adjacentPos, iceSurfaceState);
                     }
                  }
               } else if (distance <= radius && (blockState.getBlock() == Blocks.WATER || blockState.is(AlshanexFamiliarsTags.FREEZE_IGNORE)) && !isAboveCenter
                  )
                {
                  if (blockState.is(AlshanexFamiliarsTags.FREEZE_IGNORE)) {
                     if ((Boolean)ServerConfigs.SPELL_GREIFING.get()) {
                        level.setBlockAndUpdate(currentPos, Blocks.FROSTED_ICE.defaultBlockState());
                     }
                  } else {
                     level.setBlockAndUpdate(currentPos, Blocks.FROSTED_ICE.defaultBlockState());
                  }
               } else if (distance <= radius && blockState.getBlock() == Blocks.LAVA && !isAboveCenter) {
                  level.setBlockAndUpdate(currentPos, Blocks.MAGMA_BLOCK.defaultBlockState());
               }
            }
         }
      }
   }

   public static boolean isIceOrSnow(Block block) {
      return block instanceof IceBlock || block instanceof SnowLayerBlock || block instanceof PowderSnowBlock || block == BlockRegistry.ICE_SURFACE_BLOCK.get();
   }

   public static AbstractMagicProjectile getMagicProjectile(LivingEntity caster) {
      Random random = new Random();
      int number = random.nextInt(20) + 1;
      if (number == 1) {
         return new DragonEggEntity(caster.level(), caster);
      } else if (number == 2) {
         return new ChorusFlowerEntity(caster.level(), caster);
      } else if (number <= 6) {
         return new EndStoneEntity(caster.level(), caster);
      } else if (number <= 9) {
         return new PurpurBricksEntity(caster.level(), caster);
      } else if (number <= 12) {
         return new PurpurPilarEntity(caster.level(), caster);
      } else {
         return (AbstractMagicProjectile)(number <= 16 ? new MagicArrowProjectile(caster.level(), caster) : new MagicMissileProjectile(caster.level(), caster));
      }
   }
}
