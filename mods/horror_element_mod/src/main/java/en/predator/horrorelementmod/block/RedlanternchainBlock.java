package en.predator.horrorelementmod.block;

import en.predator.horrorelementmod.init.HorrorElementModModBlocks;
import en.predator.horrorelementmod.procedures.RedlanternchainNeighbourBlockChangesProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedlanternchainBlock extends Block {
   public RedlanternchainBlock() {
      super(Properties.of().sound(SoundType.LANTERN).strength(1.0F, 10.0F).lightLevel(s -> 10).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return true;
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Shapes.or(box(5.0, 1.0, 5.0, 11.0, 8.0, 11.0), box(6.0, 8.0, 6.0, 10.0, 10.0, 10.0));
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return new ItemStack((ItemLike)HorrorElementModModBlocks.REDLANTERN.get());
   }

   public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
      super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
      RedlanternchainNeighbourBlockChangesProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }
}
