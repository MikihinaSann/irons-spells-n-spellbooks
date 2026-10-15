package net.alshanex.alshanex_familiars.block;

import com.mojang.serialization.MapCodec;
import net.alshanex.alshanex_familiars.block.entity.RitualAutomatorBlockEntity;
import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.alshanex.alshanex_familiars.util.familiars.AlshanexFamiliarManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RitualAutomatorBlock extends BaseEntityBlock {
   public static final MapCodec<RitualAutomatorBlock> CODEC = simpleCodec(RitualAutomatorBlock::new);

   public RitualAutomatorBlock(Properties properties) {
      super(properties);
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new RitualAutomatorBlockEntity(pos, state);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (level.isClientSide) {
         AlshanexFamiliarManager.openRitualAutomatorScreen(pos);
      }

      return ItemInteractionResult.SUCCESS;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return createTickerHelper(blockEntityType, BlockEntityRegistry.RITUAL_AUTOMATOR.get(), (lvl, pos, st, be) -> be.tick(lvl, pos, st));
   }
}
