package io.redspace.ironslib.statue.block.statue_block.player;

import io.redspace.ironslib.registry.IronsLibRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class LegacyPlayerStatueBlockEntity extends PlayerStatueBlockEntity {
   public LegacyPlayerStatueBlockEntity(BlockPos pos, BlockState blockState) {
      super(pos, blockState);
   }

   @NotNull
   public BlockEntityType<?> getType() {
      return (BlockEntityType<?>)IronsLibRegistries.Legacy.BlockRegistry.STATUE_BLOCK_ENTITY.get();
   }
}
