package net.alshanex.alshanex_familiars.block.entity;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.alshanex.alshanex_familiars.compat.FixedShrinkingRecipe;
import net.alshanex.alshanex_familiars.compat.ShrinkingRecipe;
import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.alshanex.alshanex_familiars.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class ShrinkingStationBlockEntity extends BlockEntity implements WorldlyContainer {
   private static final String NBT_ITEMS = "Items";
   private static final String NBT_IS_SHRINKABLE = "isShrinkable";
   private static final String NBT_COUNTER = "counter";
   public static final int INPUT_SLOT = 0;
   public static final int OUTPUT_SLOT = 1;
   private static final int CONTAINER_SIZE = 2;
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{1};
   private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1};
   private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
   private boolean isShrinkable = false;
   private int counter = 0;
   private RecipeHolder<ShrinkingRecipe> currentRecipe;
   private final IItemHandler[] itemHandlers = new IItemHandler[6];

   public ShrinkingStationBlockEntity(BlockPos pos, BlockState blockState) {
      super(BlockEntityRegistry.SHRINKING_STATION_BE.get(), pos, blockState);
   }

   public ItemStack getHeldItem() {
      ItemStack inputItem = (ItemStack)this.items.get(0);
      return !inputItem.isEmpty() ? inputItem : (ItemStack)this.items.get(1);
   }

   public ItemStack getInputItem() {
      return (ItemStack)this.items.get(0);
   }

   public ItemStack getOutputItem() {
      return (ItemStack)this.items.get(1);
   }

   public void setInputItem(ItemStack newItem) {
      this.items.set(0, newItem);
      this.updateShrinkableState();
      this.setChanged();
   }

   public void setOutputItem(ItemStack newItem) {
      this.items.set(1, newItem);
      this.setChanged();
   }

   private void updateShrinkableState() {
      if (this.level != null && !this.level.isClientSide) {
         ItemStack inputItem = (ItemStack)this.items.get(0);
         ItemStack outputItem = (ItemStack)this.items.get(1);
         this.counter = 0;
         if (!inputItem.isEmpty() && outputItem.isEmpty()) {
            List<RecipeHolder<ShrinkingRecipe>> matches = this.level
               .getRecipeManager()
               .getRecipesFor((RecipeType)RecipeRegistry.SHRINKING_RECIPE_TYPE.get(), new ShrinkingRecipe.Input(inputItem), this.level);
            this.currentRecipe = matches.stream()
               .filter(holder -> holder.value() instanceof FixedShrinkingRecipe)
               .findFirst()
               .or(() -> matches.stream().findFirst())
               .orElse(null);
            this.isShrinkable = this.currentRecipe != null;
         } else {
            this.isShrinkable = false;
            this.currentRecipe = null;
         }
      }
   }

   public boolean isShrinkable() {
      return this.isShrinkable;
   }

   public int getCounter() {
      return this.counter;
   }

   public int[] getSlotsForFace(Direction side) {
      return switch (side) {
         case UP -> SLOTS_FOR_UP;
         case DOWN -> SLOTS_FOR_DOWN;
         default -> SLOTS_FOR_SIDES;
      };
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
      return index != 0 ? false : ((ItemStack)this.items.get(0)).isEmpty() && ((ItemStack)this.items.get(1)).isEmpty();
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return index == 1 ? !((ItemStack)this.items.get(1)).isEmpty() : false;
   }

   public int getContainerSize() {
      return 2;
   }

   public boolean isEmpty() {
      for (ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int index) {
      return (ItemStack)this.items.get(index);
   }

   public ItemStack removeItem(int index, int count) {
      ItemStack result = ContainerHelper.removeItem(this.items, index, count);
      if (!result.isEmpty()) {
         if (index == 0) {
            this.updateShrinkableState();
         }

         this.setChanged();
      }

      return result;
   }

   public ItemStack removeItemNoUpdate(int index) {
      ItemStack result = ContainerHelper.takeItem(this.items, index);
      if (index == 0 && !result.isEmpty()) {
         this.updateShrinkableState();
      }

      return result;
   }

   public void setItem(int index, ItemStack stack) {
      this.items.set(index, stack);
      if (stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }

      if (index == 0) {
         this.updateShrinkableState();
      }

      this.setChanged();
   }

   public boolean stillValid(Player player) {
      return true;
   }

   public void clearContent() {
      this.items.clear();
      this.updateShrinkableState();
      this.setChanged();
   }

   public IItemHandler getItemHandler(@Nullable Direction side) {
      if (side == null) {
         return null;
      } else {
         int index = side.ordinal();
         if (this.itemHandlers[index] == null) {
            this.itemHandlers[index] = new SidedInvWrapper(this, side);
         }

         return this.itemHandlers[index];
      }
   }

   public void drops() {
      Containers.dropContents(this.level, this.worldPosition, this);
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
      ContainerHelper.saveAllItems(nbt, this.items, pRegistries);
      nbt.putBoolean("isShrinkable", this.isShrinkable);
      nbt.putInt("counter", this.counter);
      return nbt;
   }

   private CompoundTag readNBT(CompoundTag nbt, Provider pRegistries) {
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(nbt, this.items, pRegistries);
      this.isShrinkable = nbt.getBoolean("isShrinkable");
      this.counter = nbt.getInt("counter");
      return nbt;
   }

   public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
      if (!pLevel.isClientSide()) {
         if (!this.isShrinkable && !((ItemStack)this.items.get(0)).isEmpty() && ((ItemStack)this.items.get(1)).isEmpty()) {
            this.updateShrinkableState();
         }

         if (this.isShrinkable && this.currentRecipe != null) {
            if (this.counter < 100) {
               this.counter++;
               this.setChanged();
            }

            if (this.counter >= 100) {
               this.completeShrink(pLevel, pPos, pState);
            }
         } else if (this.counter > 0) {
            this.counter = 0;
            this.setChanged();
         }
      }
   }

   private void completeShrink(Level pLevel, BlockPos pPos, BlockState pState) {
      if (this.currentRecipe != null) {
         ItemStack result = ((ShrinkingRecipe)this.currentRecipe.value())
            .assemble(new ShrinkingRecipe.Input((ItemStack)this.items.get(0)), pLevel.registryAccess());
         if (!result.isEmpty()) {
            MagicManager.spawnParticles(
               pLevel,
               new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.EVOCATION.get()).getTargetingColor(), 6.0F),
               pPos.getCenter().x,
               pPos.getCenter().y + 0.165F,
               pPos.getCenter().z,
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
            this.items.set(1, result);
            ((ItemStack)this.items.get(0)).shrink(1);
            BlockState prevState = this.getBlockState();
            BlockState nextState = this.getBlockState();
            pLevel.sendBlockUpdated(pPos, prevState, nextState, 2);
         }

         this.counter = 0;
         this.updateShrinkableState();
         this.setChanged();
      }
   }
}
