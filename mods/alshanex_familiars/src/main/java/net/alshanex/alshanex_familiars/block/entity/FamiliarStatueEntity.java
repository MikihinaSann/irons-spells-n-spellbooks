package net.alshanex.alshanex_familiars.block.entity;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FamiliarStatueEntity extends BlockEntity {
   private static final String NBT_HELD_ITEM = "heldItem";
   private ItemStack heldItem = ItemStack.EMPTY;

   public FamiliarStatueEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
      super(type, pos, blockState);
   }

   public ItemStack getHeldItem() {
      return this.heldItem;
   }

   public void setHeldItem(ItemStack newItem) {
      this.heldItem = newItem;
      this.setChanged();
   }

   public void drops() {
      SimpleContainer simpleContainer = new SimpleContainer(new ItemStack[]{this.heldItem});
      Containers.dropContents(this.level, this.worldPosition, simpleContainer);
   }

   protected void loadAdditional(CompoundTag pTag, Provider pRegistries) {
      super.loadAdditional(pTag, pRegistries);
      this.readNBT(pTag, pRegistries);
   }

   protected void saveAdditional(@Nonnull CompoundTag tag, Provider registryAccess) {
      this.writeNBT(tag, registryAccess);
   }

   public void setChanged() {
      super.setChanged();
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider pRegistries) {
      return this.saveWithoutMetadata(pRegistries);
   }

   private CompoundTag writeNBT(CompoundTag nbt, Provider pRegistries) {
      if (!this.heldItem.isEmpty()) {
         nbt.put("heldItem", this.heldItem.save(pRegistries));
      } else {
         nbt.put("heldItem", new CompoundTag());
      }

      return nbt;
   }

   private CompoundTag readNBT(CompoundTag nbt, Provider pRegistries) {
      if (nbt.contains("heldItem")) {
         this.heldItem = ItemStack.parseOptional(pRegistries, nbt.getCompound("heldItem"));
      }

      return nbt;
   }
}
