package net.alshanex.alshanex_familiars.block.entity;

import javax.annotation.Nonnull;
import net.alshanex.alshanex_familiars.block.PetBedBlock;
import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarBedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class PetBedBlockEntity extends AbstractFamiliarBedBlockEntity {
   private DyeColor color = DyeColor.WHITE;

   public PetBedBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityRegistry.PET_BED.get(), pos, state);
      if (state.hasProperty(PetBedBlock.COLOR)) {
         this.color = (DyeColor)state.getValue(PetBedBlock.COLOR);
      }
   }

   public DyeColor getColor() {
      return this.color;
   }

   public void setColor(DyeColor color) {
      this.color = color;
      this.setChanged();
      if (!this.level.isClientSide) {
         BlockState currentState = this.getBlockState();
         if (currentState.hasProperty(PetBedBlock.COLOR) && currentState.getValue(PetBedBlock.COLOR) != color) {
            this.level.setBlock(this.worldPosition, (BlockState)currentState.setValue(PetBedBlock.COLOR, color), 3);
         }

         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void loadAdditional(CompoundTag pTag, Provider pRegistries) {
      super.loadAdditional(pTag, pRegistries);
      if (pTag.contains("color")) {
         this.color = DyeColor.byName(pTag.getString("color"), DyeColor.WHITE);
         if (this.level != null && this.hasLevel()) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            if (state.hasProperty(PetBedBlock.COLOR) && state.getValue(PetBedBlock.COLOR) != this.color) {
               this.level.setBlock(this.worldPosition, (BlockState)state.setValue(PetBedBlock.COLOR, this.color), 3);
            }
         }
      }
   }

   protected void saveAdditional(@Nonnull CompoundTag tag, Provider registryAccess) {
      tag.putString("Color", this.color.getName());
   }

   public CompoundTag getUpdateTag(Provider pRegistries) {
      CompoundTag tag = new CompoundTag();
      tag.putString("Color", this.color.getName());
      return tag;
   }

   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      this.loadAdditional(tag, lookupProvider);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      this.handleUpdateTag(pkt.getTag(), lookupProvider);
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
   }
}
