package com.somake.somakespells.block.entity;

import com.somake.somakespells.event.PedestalRitualHandler;
import com.somake.somakespells.registries.ModBlockEntities;
import com.somake.somakespells.ritual.RitualElementalMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PedestalOfFireBlockEntity extends BlockEntity implements GeoBlockEntity {
   public static final int SLOT_COUNT = 16;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public int tickCount;
   private final NonNullList<ItemStack> items = NonNullList.withSize(16, ItemStack.EMPTY);
   private boolean ritualActive;
   private int ritualAnimationTick;
   private int ritualAnimationDuration;
   private int previewTicks;
   private RitualElementalMode ritualMode = RitualElementalMode.NORMAL;
   private BlockPos ritualAltarPos;

   public PedestalOfFireBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.PEDESTAL_OF_FIRE.get(), pos, state);
   }

   public static void commonTick(Level level, BlockPos pos, BlockState state, PedestalOfFireBlockEntity blockEntity) {
      blockEntity.tick(level, pos, state);
   }

   private void tick(Level level, BlockPos pos, BlockState state) {
      this.tickCount++;
      if (this.ritualActive && this.ritualAnimationTick < this.ritualAnimationDuration) {
         this.ritualAnimationTick++;
      }

      if (this.previewTicks > 0) {
         this.previewTicks--;
      }

      if (!level.isClientSide
         && this.ritualMode.isEmpowered()
         && this.ritualAltarPos != null
         && this.tickCount % 20 == 0
         && level instanceof ServerLevel serverLevel
         && !PedestalRitualHandler.isSoulFireLinkValid(level, this.ritualAltarPos, this.worldPosition)) {
         PedestalRitualHandler.resetSoulFireNetwork(serverLevel, this.ritualAltarPos);
      }
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public void beginRitualAnimation(int duration) {
      this.ritualActive = true;
      this.ritualAnimationTick = 0;
      this.ritualAnimationDuration = Math.max(1, duration);
      this.sync();
   }

   public void endRitualAnimation() {
      this.ritualActive = false;
      this.ritualAnimationTick = 0;
      this.ritualAnimationDuration = 0;
      this.sync();
   }

   public boolean isRitualActive() {
      return this.ritualActive;
   }

   public void setPreviewTicks(int ticks) {
      int updatedTicks = Math.max(this.previewTicks, ticks);
      if (updatedTicks != this.previewTicks) {
         this.previewTicks = updatedTicks;
         this.sync();
      }
   }

   public boolean isPreviewActive() {
      return this.previewTicks > 0 && !this.ritualActive;
   }

   public RitualElementalMode getRitualMode() {
      return this.ritualMode;
   }

   public boolean isEmpoweredRitualActive() {
      return this.ritualMode.isEmpowered() && this.ritualAltarPos != null;
   }

   public BlockPos getRitualAltarPos() {
      return this.ritualAltarPos;
   }

   public void activateRitualMode(RitualElementalMode ritualMode, BlockPos altarPos) {
      BlockPos updatedAltarPos = altarPos.immutable();
      if (this.ritualMode != ritualMode || !updatedAltarPos.equals(this.ritualAltarPos)) {
         this.ritualMode = ritualMode;
         this.ritualAltarPos = updatedAltarPos;
         this.sync();
      }
   }

   public void clearRitualMode() {
      if (this.ritualMode != RitualElementalMode.NORMAL || this.ritualAltarPos != null) {
         this.ritualMode = RitualElementalMode.NORMAL;
         this.ritualAltarPos = null;
         this.sync();
      }
   }

   public void activateSoulFire(BlockPos altarPos) {
      this.activateRitualMode(RitualElementalMode.SOUL_FIRE, altarPos);
   }

   public void clearSoulFire() {
      this.clearRitualMode();
   }

   public boolean isSoulFireActive() {
      return this.ritualMode == RitualElementalMode.SOUL_FIRE;
   }

   public BlockPos getSoulFireAltarPos() {
      return this.ritualAltarPos;
   }

   public float getRitualProgress(float partialTick) {
      return this.ritualActive && this.ritualAnimationDuration > 0
         ? Math.min(1.0F, (this.ritualAnimationTick + partialTick) / this.ritualAnimationDuration)
         : 0.0F;
   }

   public int getStoredItemCount() {
      int total = 0;

      for (ItemStack stack : this.items) {
         if (!stack.isEmpty()) {
            total += stack.getCount();
         }
      }

      return total;
   }

   public boolean containsOnly(Ingredient ingredient) {
      for (ItemStack stack : this.items) {
         if (!stack.isEmpty() && !ingredient.test(stack)) {
            return false;
         }
      }

      return true;
   }

   public int countMatchingItems(Ingredient ingredient) {
      int total = 0;

      for (ItemStack stack : this.items) {
         if (!stack.isEmpty() && ingredient.test(stack)) {
            total += stack.getCount();
         }
      }

      return total;
   }

   public boolean matchesIngredientCount(Ingredient ingredient, int count) {
      return this.containsOnly(ingredient) && this.countMatchingItems(ingredient) == count;
   }

   public void consumeIngredient(Ingredient ingredient, int count) {
      int remaining = count;

      for (int i = 0; i < this.items.size() && remaining > 0; i++) {
         ItemStack stack = (ItemStack)this.items.get(i);
         if (!stack.isEmpty() && ingredient.test(stack)) {
            int remove = Math.min(remaining, stack.getCount());
            stack.shrink(remove);
            if (stack.isEmpty()) {
               this.items.set(i, ItemStack.EMPTY);
            }

            remaining -= remove;
         }
      }

      this.sync();
   }

   public boolean addItem(ItemStack stack) {
      for (int i = 0; i < this.items.size(); i++) {
         if (((ItemStack)this.items.get(i)).isEmpty()) {
            this.items.set(i, stack);
            this.sync();
            return true;
         }
      }

      return false;
   }

   public ItemStack removeLastItem() {
      for (int i = this.items.size() - 1; i >= 0; i--) {
         ItemStack stack = (ItemStack)this.items.get(i);
         if (!stack.isEmpty()) {
            this.items.set(i, ItemStack.EMPTY);
            this.sync();
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   public void dropAll(Level level, BlockPos pos) {
      for (int i = 0; i < this.items.size(); i++) {
         ItemStack stack = (ItemStack)this.items.get(i);
         if (!stack.isEmpty()) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, stack);
            this.items.set(i, ItemStack.EMPTY);
         }
      }

      this.sync();
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      ContainerHelper.saveAllItems(tag, this.items, registries);
      tag.putBoolean("RitualActive", this.ritualActive);
      tag.putInt("RitualAnimationTick", this.ritualAnimationTick);
      tag.putInt("RitualAnimationDuration", this.ritualAnimationDuration);
      tag.putInt("PreviewTicks", this.previewTicks);
      tag.putString("RitualMode", this.ritualMode.id());
      if (this.ritualAltarPos != null) {
         tag.putLong("RitualAltarPos", this.ritualAltarPos.asLong());
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.items.clear();
      ContainerHelper.loadAllItems(tag, this.items, registries);
      this.ritualActive = tag.getBoolean("RitualActive");
      this.ritualAnimationTick = tag.getInt("RitualAnimationTick");
      this.ritualAnimationDuration = tag.getInt("RitualAnimationDuration");
      this.previewTicks = tag.getInt("PreviewTicks");
      if (tag.contains("RitualMode")) {
         this.ritualMode = RitualElementalMode.fromId(tag.getString("RitualMode"));
         this.ritualAltarPos = tag.contains("RitualAltarPos") ? BlockPos.of(tag.getLong("RitualAltarPos")) : null;
      } else {
         this.ritualMode = tag.getBoolean("SoulFireActive") ? RitualElementalMode.SOUL_FIRE : RitualElementalMode.NORMAL;
         this.ritualAltarPos = tag.contains("SoulFireAltarPos") ? BlockPos.of(tag.getLong("SoulFireAltarPos")) : null;
      }
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

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
