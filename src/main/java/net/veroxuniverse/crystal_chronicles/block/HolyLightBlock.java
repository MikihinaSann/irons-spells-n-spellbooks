package net.veroxuniverse.crystal_chronicles.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.veroxuniverse.crystal_chronicles.registry.CCBlocks;

public class HolyLightBlock extends Block {
   private static final int TICK_RATE = 20;

   public HolyLightBlock(Properties props) {
      super(props);
   }

   @Override
   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
      super.onPlace(state, level, pos, oldState, isMoving);
      if (!level.isClientSide) {
         this.updateSegments(level, pos, state);
         level.scheduleTick(pos, this, 20);
      }
   }

   @Override
   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
      super.neighborChanged(state, level, pos, neighborBlock, neighborPos, moved);
      if (!level.isClientSide && neighborPos.getY() < pos.getY()) {
         this.updateSegments(level, pos, state);
      }
   }

   @Override
   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      this.updateSegments(level, pos, state);
      level.scheduleTick(pos, this, 20);
   }

   @Override
   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!level.isClientSide && !state.is(newState.getBlock())) {
         this.clearSegments(level, pos);
      }

      super.onRemove(state, level, pos, newState, isMoving);
   }

   private void updateSegments(Level level, BlockPos pos, BlockState state) {
      int totalLength = this.computeLength(level, pos);

      for (int i = 1; i <= Math.min(3, totalLength); i++) {
         BlockPos belowPos = pos.below(i);
         BlockState belowState = level.getBlockState(belowPos);

         Block targetBlock = switch (i) {
            case 1 -> (Block)CCBlocks.HOLY_LIGHT_1.get();
            case 2 -> (Block)CCBlocks.HOLY_LIGHT_2.get();
            case 3 -> (Block)CCBlocks.HOLY_LIGHT_3.get();
            default -> null;
         };
         if (targetBlock != null && !belowState.is(targetBlock) && (belowState.canBeReplaced() || this.isSegmentBlock(belowState))) {
            level.setBlock(belowPos, targetBlock.defaultBlockState(), 3);
         }
      }

      int startY = pos.getY() - 4;
      int endY = pos.getY() - totalLength;
      if (totalLength > 3) {
         for (int y = startY; y > endY; y--) {
            BlockPos segmentPos = new BlockPos(pos.getX(), y, pos.getZ());
            BlockState segmentState = level.getBlockState(segmentPos);
            if (!segmentState.is((Block)CCBlocks.HOLY_LIGHT_4.get()) && (segmentState.canBeReplaced() || this.isSegmentBlock(segmentState))) {
               level.setBlock(segmentPos, ((Block)CCBlocks.HOLY_LIGHT_4.get()).defaultBlockState(), 3);
            }
         }
      }

      int cleanupY = pos.getY() - totalLength - 1;

      for (int yx = cleanupY; yx >= level.getMinBuildHeight(); yx--) {
         BlockPos cleanupPos = new BlockPos(pos.getX(), yx, pos.getZ());
         BlockState cleanupState = level.getBlockState(cleanupPos);
         if (this.isSegmentBlock(cleanupState)) {
            level.setBlock(cleanupPos, Blocks.AIR.defaultBlockState(), 3);
         } else if (!cleanupState.isAir()) {
            break;
         }
      }
   }

   private void clearSegments(Level level, BlockPos pos) {
      int i = 1;

      while (true) {
         BlockPos belowPos = pos.below(i);
         BlockState belowState = level.getBlockState(belowPos);
         if (!this.isSegmentBlock(belowState)) {
            return;
         }

         level.setBlock(belowPos, Blocks.AIR.defaultBlockState(), 3);
         i++;
      }
   }

   private boolean isSegmentBlock(BlockState state) {
      return state.is((Block)CCBlocks.HOLY_LIGHT_1.get())
         || state.is((Block)CCBlocks.HOLY_LIGHT_2.get())
         || state.is((Block)CCBlocks.HOLY_LIGHT_3.get())
         || state.is((Block)CCBlocks.HOLY_LIGHT_4.get());
   }

   private int computeLength(Level level, BlockPos pos) {
      int length = 0;

      for (int y = pos.getY() - 1; y >= level.getMinBuildHeight(); y--) {
         BlockPos belowPos = new BlockPos(pos.getX(), y, pos.getZ());
         BlockState belowState = level.getBlockState(belowPos);
         if (this.isSegmentBlock(belowState)) {
            length++;
         } else {
            boolean isAllowedTransparent = belowState.isAir()
               || belowState.is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "glass")))
               || belowState.is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "glass_panes")))
               || belowState.is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "transparent")));
            if (!isAllowedTransparent) {
               break;
            }

            length++;
         }
      }

      return length;
   }
}
