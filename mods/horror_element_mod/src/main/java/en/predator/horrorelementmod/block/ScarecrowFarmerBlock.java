package en.predator.horrorelementmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.DeferredSoundType;

public class ScarecrowFarmerBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public ScarecrowFarmerBlock() {
      super(
         Properties.of()
            .ignitedByLava()
            .instrument(NoteBlockInstrument.BASS)
            .sound(
               new DeferredSoundType(
                  1.0F,
                  1.0F,
                  () -> (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.slime_block.break")),
                  () -> (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.wood.step")),
                  () -> (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.wood.place")),
                  () -> (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.wood.hit")),
                  () -> (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.wood.fall"))
               )
            )
            .strength(1.0F, 10.0F)
            .lightLevel(s -> 1)
            .noCollission()
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false)
      );
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false)
      );
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return state.getFluidState().isEmpty();
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case NORTH -> Shapes.or(
            box(4.0, 24.0, 4.0, 12.0, 32.0, 12.0),
            new VoxelShape[]{
               box(4.0, 12.0, 6.0, 12.0, 24.0, 10.0),
               box(3.75, 11.75, 5.75, 12.25, 24.25, 10.25),
               box(12.0, 20.0, 6.0, 24.0, 24.0, 10.0),
               box(11.75, 19.75, 5.75, 24.25, 24.25, 10.25),
               box(-8.0, 20.0, 6.0, 4.0, 24.0, 10.0),
               box(-8.25, 19.75, 5.75, 4.25, 24.25, 10.25),
               box(7.9, 0.0, 6.0, 11.9, 12.0, 10.0),
               box(7.65, -0.25, 5.75, 12.15, 12.25, 10.25),
               box(4.1, 0.0, 6.0, 8.1, 12.0, 10.0),
               box(3.85, -0.25, 5.75, 8.35, 12.25, 10.25),
               box(-12.0, 21.0, 7.0, 28.0, 23.0, 9.0)
            }
         );
         case EAST -> Shapes.or(
            box(4.0, 24.0, 4.0, 12.0, 32.0, 12.0),
            new VoxelShape[]{
               box(6.0, 12.0, 4.0, 10.0, 24.0, 12.0),
               box(5.75, 11.75, 3.75, 10.25, 24.25, 12.25),
               box(6.0, 20.0, 12.0, 10.0, 24.0, 24.0),
               box(5.75, 19.75, 11.75, 10.25, 24.25, 24.25),
               box(6.0, 20.0, -8.0, 10.0, 24.0, 4.0),
               box(5.75, 19.75, -8.25, 10.25, 24.25, 4.25),
               box(6.0, 0.0, 7.9, 10.0, 12.0, 11.9),
               box(5.75, -0.25, 7.65, 10.25, 12.25, 12.15),
               box(6.0, 0.0, 4.1, 10.0, 12.0, 8.1),
               box(5.75, -0.25, 3.85, 10.25, 12.25, 8.35),
               box(7.0, 21.0, -12.0, 9.0, 23.0, 28.0)
            }
         );
         case WEST -> Shapes.or(
            box(4.0, 24.0, 4.0, 12.0, 32.0, 12.0),
            new VoxelShape[]{
               box(6.0, 12.0, 4.0, 10.0, 24.0, 12.0),
               box(5.75, 11.75, 3.75, 10.25, 24.25, 12.25),
               box(6.0, 20.0, -8.0, 10.0, 24.0, 4.0),
               box(5.75, 19.75, -8.25, 10.25, 24.25, 4.25),
               box(6.0, 20.0, 12.0, 10.0, 24.0, 24.0),
               box(5.75, 19.75, 11.75, 10.25, 24.25, 24.25),
               box(6.0, 0.0, 4.1, 10.0, 12.0, 8.1),
               box(5.75, -0.25, 3.85, 10.25, 12.25, 8.35),
               box(6.0, 0.0, 7.9, 10.0, 12.0, 11.9),
               box(5.75, -0.25, 7.65, 10.25, 12.25, 12.15),
               box(7.0, 21.0, -12.0, 9.0, 23.0, 28.0)
            }
         );
         default -> Shapes.or(
            box(4.0, 24.0, 4.0, 12.0, 32.0, 12.0),
            new VoxelShape[]{
               box(4.0, 12.0, 6.0, 12.0, 24.0, 10.0),
               box(3.75, 11.75, 5.75, 12.25, 24.25, 10.25),
               box(-8.0, 20.0, 6.0, 4.0, 24.0, 10.0),
               box(-8.25, 19.75, 5.75, 4.25, 24.25, 10.25),
               box(12.0, 20.0, 6.0, 24.0, 24.0, 10.0),
               box(11.75, 19.75, 5.75, 24.25, 24.25, 10.25),
               box(4.1, 0.0, 6.0, 8.1, 12.0, 10.0),
               box(3.85, -0.25, 5.75, 8.35, 12.25, 10.25),
               box(7.9, 0.0, 6.0, 11.9, 12.0, 10.0),
               box(7.65, -0.25, 5.75, 12.15, 12.25, 10.25),
               box(-12.0, 21.0, 7.0, 28.0, 23.0, 9.0)
            }
         );
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING, WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
      return (BlockState)((BlockState)super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, flag);
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
      }

      return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
   }
}
