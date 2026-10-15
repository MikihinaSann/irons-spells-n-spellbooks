package en.predator.horrorelementmod.block;

import en.predator.horrorelementmod.procedures.RedlanternBlockIsPlacedByProcedure;
import en.predator.horrorelementmod.procedures.RedlanternBlockValidPlacementConditionProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedlanternBlock extends Block {
   public RedlanternBlock() {
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
      return Shapes.or(box(5.0, 0.0, 5.0, 11.0, 7.0, 11.0), box(6.0, 7.0, 6.0, 10.0, 9.0, 10.0));
   }

   public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
      if (worldIn instanceof LevelAccessor world) {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         return RedlanternBlockValidPlacementConditionProcedure.execute(world, x, y, z);
      } else {
         return super.canSurvive(blockstate, worldIn, pos);
      }
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
      return !state.canSurvive(world, currentPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
   }

   public void setPlacedBy(Level world, BlockPos pos, BlockState blockstate, LivingEntity entity, ItemStack itemstack) {
      super.setPlacedBy(world, pos, blockstate, entity, itemstack);
      RedlanternBlockIsPlacedByProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }
}
