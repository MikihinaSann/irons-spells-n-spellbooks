package com.chadate.spellelemental.block.entity;

import com.chadate.spellelemental.register.ModBlockEntities;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SwordStandBlockEntity extends BlockEntity implements WorldlyContainer {
   private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

   public SwordStandBlockEntity(BlockPos pos, BlockState blockState) {
      super(ModBlockEntities.SWORD_STAND.get(), pos, blockState);
   }

   public ItemStack getSword() {
      return (ItemStack)this.items.getFirst();
   }

   public void setSword(ItemStack sword) {
      ItemStack copy = sword.copy();
      if (!copy.isEmpty()) {
         copy.setCount(1);
      }

      this.items.set(0, copy);
      this.onContentsChanged();
   }

   public boolean isEmpty() {
      return ((ItemStack)this.items.getFirst()).isEmpty();
   }

   public ItemStack removeItem() {
      ItemStack result = ((ItemStack)this.items.getFirst()).copy();
      this.items.set(0, ItemStack.EMPTY);
      this.onContentsChanged();
      return result;
   }

   protected void saveAdditional(@NotNull CompoundTag tag, @NotNull Provider registries) {
      super.saveAdditional(tag, registries);
      ContainerHelper.saveAllItems(tag, this.items, registries);
   }

   protected void loadAdditional(@NotNull CompoundTag tag, @NotNull Provider registries) {
      super.loadAdditional(tag, registries);
      ContainerHelper.loadAllItems(tag, this.items, registries);
      ItemStack sword = (ItemStack)this.items.getFirst();
      if (!sword.isEmpty() && sword.getCount() != 1) {
         sword.setCount(1);
      }
   }

   @NotNull
   public CompoundTag getUpdateTag(@NotNull Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      this.saveAdditional(tag, registries);
      return tag;
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull Provider lookupProvider) {
      super.handleUpdateTag(tag, lookupProvider);
      this.items.set(0, ItemStack.EMPTY);
      this.loadAdditional(tag, lookupProvider);
   }

   public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, @NotNull Provider lookupProvider) {
      super.onDataPacket(net, pkt, lookupProvider);
      CompoundTag tag = pkt.getTag();
      this.items.set(0, ItemStack.EMPTY);
      this.loadAdditional(tag, lookupProvider);
   }

   private void onContentsChanged() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 27);
      }
   }

   public int getContainerSize() {
      return 1;
   }

   @NotNull
   public ItemStack getItem(int slot) {
      return (ItemStack)this.items.getFirst();
   }

   @NotNull
   public ItemStack removeItem(int slot, int amount) {
      ItemStack result = ContainerHelper.removeItem(this.items, 0, amount);
      if (!result.isEmpty()) {
         this.onContentsChanged();
      }

      return result;
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int slot) {
      ItemStack result = (ItemStack)this.items.getFirst();
      this.items.set(0, ItemStack.EMPTY);
      return result;
   }

   public void setItem(int slot, @NotNull ItemStack stack) {
      ItemStack copy = stack.copy();
      if (!copy.isEmpty()) {
         copy.setCount(1);
      }

      this.items.set(0, copy);
      this.onContentsChanged();
   }

   public boolean stillValid(@NotNull Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this;
   }

   public void clearContent() {
      this.items.set(0, ItemStack.EMPTY);
      this.onContentsChanged();
   }

   @NotNull
   public int[] getSlotsForFace(@NotNull Direction side) {
      return new int[]{0};
   }

   public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, Direction direction) {
      return this.canPlaceItem(slot, stack);
   }

   public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction direction) {
      return true;
   }

   public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
      return stack.getItem() instanceof SwordItem && ((ItemStack)this.items.getFirst()).isEmpty();
   }
}
