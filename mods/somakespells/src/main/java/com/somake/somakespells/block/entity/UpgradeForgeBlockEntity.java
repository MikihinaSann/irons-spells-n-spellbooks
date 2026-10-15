package com.somake.somakespells.block.entity;

import com.somake.somakespells.gui.upgrade_forge.UpgradeForgeMenu;
import com.somake.somakespells.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class UpgradeForgeBlockEntity extends BlockEntity implements Container, MenuProvider, Clearable {
   private static final Component CONTAINER_TITLE = Component.translatable("block.somakespells.upgrade_forge");
   private ItemStack book = ItemStack.EMPTY;

   public UpgradeForgeBlockEntity(BlockPos pos, BlockState blockState) {
      super((BlockEntityType)ModBlockEntities.UPGRADE_FORGE.get(), pos, blockState);
   }

   public int getContainerSize() {
      return 1;
   }

   public boolean isEmpty() {
      return this.book.isEmpty();
   }

   @NotNull
   public ItemStack getItem(int slot) {
      return slot == 0 ? this.book : ItemStack.EMPTY;
   }

   @NotNull
   public ItemStack removeItem(int slot, int amount) {
      if (slot == 0 && !this.book.isEmpty() && amount > 0) {
         ItemStack removed = this.book.split(amount);
         if (this.book.isEmpty()) {
            this.book = ItemStack.EMPTY;
         }

         this.sync();
         return removed;
      } else {
         return ItemStack.EMPTY;
      }
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int slot) {
      if (slot == 0 && !this.book.isEmpty()) {
         ItemStack removed = this.book;
         this.book = ItemStack.EMPTY;
         this.sync();
         return removed;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setItem(int slot, ItemStack stack) {
      if (slot == 0) {
         this.book = stack.copy();
         this.book.limitSize(this.getMaxStackSize(this.book));
         this.sync();
      }
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this
         ? player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0
         : false;
   }

   public void clearContent() {
      this.book = ItemStack.EMPTY;
      this.sync();
   }

   public Component getDisplayName() {
      return CONTAINER_TITLE;
   }

   public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
      return new UpgradeForgeMenu(containerId, inventory, this);
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      if (!this.book.isEmpty()) {
         tag.put("Book", this.book.save(registries, new CompoundTag()));
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.book = tag.contains("Book") ? ItemStack.parseOptional(registries, tag.getCompound("Book")) : ItemStack.EMPTY;
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   private void sync() {
      this.setChanged();
      if (this.level != null) {
         BlockState state = this.getBlockState();
         this.level.sendBlockUpdated(this.worldPosition, state, state, 2);
      }
   }
}
