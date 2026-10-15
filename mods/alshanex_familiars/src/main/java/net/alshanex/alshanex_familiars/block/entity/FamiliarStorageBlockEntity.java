package net.alshanex.alshanex_familiars.block.entity;

import net.alshanex.alshanex_familiars.block.FamiliarStorageBlock;
import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FamiliarStorageBlockEntity extends AbstractFamiliarStorageBlockEntity {
   public FamiliarStorageBlockEntity(BlockPos pos, BlockState blockState) {
      super(BlockEntityRegistry.FAMILIAR_STORAGE.get(), pos, blockState);
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return super.getUpdateTag(registries);
   }

   public void handleUpdateTag(CompoundTag tag, Provider registries) {
      super.handleUpdateTag(tag, registries);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   protected Direction getFacingDirection() {
      BlockState state = this.getBlockState();
      return state.getBlock() instanceof FamiliarStorageBlock ? ((Direction)state.getValue(FamiliarStorageBlock.FACING)).getOpposite() : Direction.NORTH;
   }
}
