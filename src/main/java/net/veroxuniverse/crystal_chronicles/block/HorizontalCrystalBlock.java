package net.veroxuniverse.crystal_chronicles.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;

public class HorizontalCrystalBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<HorizontalCrystalBlock> CODEC = simpleCodec(HorizontalCrystalBlock::new);

   @Override
   public MapCodec<HorizontalCrystalBlock> codec() {
      return CODEC;
   }

   public HorizontalCrystalBlock(Properties properties) {
      super(properties);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }
}
