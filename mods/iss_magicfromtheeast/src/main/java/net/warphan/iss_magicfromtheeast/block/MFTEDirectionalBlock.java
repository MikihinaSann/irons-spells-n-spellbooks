package net.warphan.iss_magicfromtheeast.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class MFTEDirectionalBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<MFTEDirectionalBlock> CODEC = simpleCodec(MFTEDirectionalBlock::new);

   public MFTEDirectionalBlock(Properties p_54120_) {
      super(p_54120_);
   }

   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
      Direction direction = p_49820_.getHorizontalDirection();
      return (BlockState)this.defaultBlockState().setValue(FACING, direction.getOpposite());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> p_49915_) {
      p_49915_.add(new Property[]{FACING});
   }
}
