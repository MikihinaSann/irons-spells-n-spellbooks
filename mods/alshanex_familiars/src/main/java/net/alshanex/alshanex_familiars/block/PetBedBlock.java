package net.alshanex.alshanex_familiars.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.alshanex.alshanex_familiars.block.entity.PetBedBlockEntity;
import net.alshanex.familiarslib.block.AbstractFamiliarBedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PetBedBlock extends AbstractFamiliarBedBlock {
   public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   private static final VoxelShape PART_1 = box(14.0, 0.0, 0.0, 16.0, 1.0, 2.0);
   private static final VoxelShape PART_2 = box(0.0, 5.0, 13.0, 16.0, 7.0, 16.0);
   private static final VoxelShape PART_3 = box(0.0, 1.0, 0.0, 16.0, 5.0, 16.0);
   private static final VoxelShape PART_4 = box(13.0, 5.0, 3.0, 16.0, 7.0, 13.0);
   private static final VoxelShape PART_5 = box(0.0, 5.0, 3.0, 3.0, 7.0, 13.0);
   private static final VoxelShape PART_6 = box(14.0, 0.0, 14.0, 16.0, 1.0, 16.0);
   private static final VoxelShape PART_7 = box(0.0, 0.0, 14.0, 2.0, 1.0, 16.0);
   private static final VoxelShape PART_8 = box(0.0, 0.0, 0.0, 2.0, 1.0, 2.0);
   private static final VoxelShape SHAPE_NORTH = Shapes.or(PART_1, new VoxelShape[]{PART_2, PART_3, PART_4, PART_5, PART_6, PART_7, PART_8});
   private static final VoxelShape SHAPE_SOUTH = Shapes.or(
      rotateBox(PART_1, Rotation.CLOCKWISE_180),
      new VoxelShape[]{
         rotateBox(PART_2, Rotation.CLOCKWISE_180),
         rotateBox(PART_3, Rotation.CLOCKWISE_180),
         rotateBox(PART_4, Rotation.CLOCKWISE_180),
         rotateBox(PART_5, Rotation.CLOCKWISE_180),
         rotateBox(PART_6, Rotation.CLOCKWISE_180),
         rotateBox(PART_7, Rotation.CLOCKWISE_180),
         rotateBox(PART_8, Rotation.CLOCKWISE_180)
      }
   );
   private static final VoxelShape SHAPE_EAST = Shapes.or(
      rotateBox(PART_1, Rotation.CLOCKWISE_90),
      new VoxelShape[]{
         rotateBox(PART_2, Rotation.CLOCKWISE_90),
         rotateBox(PART_3, Rotation.CLOCKWISE_90),
         rotateBox(PART_4, Rotation.CLOCKWISE_90),
         rotateBox(PART_5, Rotation.CLOCKWISE_90),
         rotateBox(PART_6, Rotation.CLOCKWISE_90),
         rotateBox(PART_7, Rotation.CLOCKWISE_90),
         rotateBox(PART_8, Rotation.CLOCKWISE_90)
      }
   );
   private static final VoxelShape SHAPE_WEST = Shapes.or(
      rotateBox(PART_1, Rotation.COUNTERCLOCKWISE_90),
      new VoxelShape[]{
         rotateBox(PART_2, Rotation.COUNTERCLOCKWISE_90),
         rotateBox(PART_3, Rotation.COUNTERCLOCKWISE_90),
         rotateBox(PART_4, Rotation.COUNTERCLOCKWISE_90),
         rotateBox(PART_5, Rotation.COUNTERCLOCKWISE_90),
         rotateBox(PART_6, Rotation.COUNTERCLOCKWISE_90),
         rotateBox(PART_7, Rotation.COUNTERCLOCKWISE_90),
         rotateBox(PART_8, Rotation.COUNTERCLOCKWISE_90)
      }
   );
   public static final MapCodec<PetBedBlock> CODEC = simpleCodec(PetBedBlock::new);

   public PetBedBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, COLOR});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(COLOR, DyeColor.WHITE);
   }

   public BlockState setColor(BlockState state, DyeColor color) {
      return (BlockState)state.setValue(COLOR, color);
   }

   private static VoxelShape rotateBox(VoxelShape shape, Rotation rotation) {
      List<AABB> boxes = shape.toAabbs();
      VoxelShape rotatedShape = Shapes.empty();

      for (AABB box : boxes) {
         double x1 = box.minX;
         double y1 = box.minY;
         double z1 = box.minZ;
         double x2 = box.maxX;
         double y2 = box.maxY;
         double z2 = box.maxZ;

         rotatedShape = Shapes.or(rotatedShape, switch (rotation) {
            case CLOCKWISE_90 -> Shapes.box(1.0 - z2, y1, x1, 1.0 - z1, y2, x2);
            case CLOCKWISE_180 -> Shapes.box(1.0 - x2, y1, 1.0 - z2, 1.0 - x1, y2, 1.0 - z1);
            case COUNTERCLOCKWISE_90 -> Shapes.box(z1, y1, 1.0 - x2, z2, y2, 1.0 - x1);
            default -> Shapes.box(x1, y1, z1, x2, y2, z2);
         });
      }

      return rotatedShape;
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      switch ((Direction)state.getValue(FACING)) {
         case SOUTH:
            return SHAPE_SOUTH;
         case EAST:
            return SHAPE_EAST;
         case WEST:
            return SHAPE_WEST;
         default:
            return SHAPE_NORTH;
      }
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public MapCodec<PetBedBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new PetBedBlockEntity(pos, state);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (stack.getItem() instanceof DyeItem) {
         DyeColor dyeColor = ((DyeItem)stack.getItem()).getDyeColor();
         if (level.getBlockEntity(pos) instanceof PetBedBlockEntity petBed) {
            if (state.getValue(COLOR) == dyeColor) {
               return ItemInteractionResult.FAIL;
            }

            if (!level.isClientSide) {
               level.setBlock(pos, (BlockState)state.setValue(COLOR, dyeColor), 3);
               petBed.setColor(dyeColor);
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }
      }

      return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
   }
}
