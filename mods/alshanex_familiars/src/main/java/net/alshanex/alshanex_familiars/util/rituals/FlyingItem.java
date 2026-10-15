package net.alshanex.alshanex_familiars.util.rituals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class FlyingItem {
   private ItemStack stack;
   private BlockPos sourcePos;
   private BlockPos targetPos;
   private int ticksRemaining;
   private int totalTicks;
   private boolean isRetrieving;

   public FlyingItem(ItemStack stack, BlockPos source, BlockPos target, int duration, boolean retrieving) {
      this.stack = stack;
      this.sourcePos = source;
      this.targetPos = target;
      this.ticksRemaining = duration;
      this.totalTicks = duration;
      this.isRetrieving = retrieving;
   }

   public FlyingItem() {
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public BlockPos getSourcePos() {
      return this.sourcePos;
   }

   public BlockPos getTargetPos() {
      return this.targetPos;
   }

   public int getTicksRemaining() {
      return this.ticksRemaining;
   }

   public int getTotalTicks() {
      return this.totalTicks;
   }

   public boolean isRetrieving() {
      return this.isRetrieving;
   }

   public void tick() {
      this.ticksRemaining--;
   }

   public boolean hasArrived() {
      return this.ticksRemaining <= 0;
   }

   public CompoundTag save(Provider provider) {
      CompoundTag tag = new CompoundTag();
      if (!this.stack.isEmpty()) {
         tag.put("Stack", this.stack.saveOptional(provider));
      }

      tag.putLong("Source", this.sourcePos.asLong());
      tag.putLong("Target", this.targetPos.asLong());
      tag.putInt("Ticks", this.ticksRemaining);
      tag.putInt("Total", this.totalTicks);
      tag.putBoolean("Retrieving", this.isRetrieving);
      return tag;
   }

   public static FlyingItem load(CompoundTag tag, Provider provider) {
      FlyingItem item = new FlyingItem();
      if (tag.contains("Stack")) {
         item.stack = ItemStack.parseOptional(provider, tag.getCompound("Stack"));
      } else {
         item.stack = ItemStack.EMPTY;
      }

      item.sourcePos = BlockPos.of(tag.getLong("Source"));
      item.targetPos = BlockPos.of(tag.getLong("Target"));
      item.ticksRemaining = tag.getInt("Ticks");
      item.totalTicks = tag.getInt("Total");
      item.isRetrieving = tag.getBoolean("Retrieving");
      return item;
   }
}
