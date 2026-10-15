package net.alshanex.alshanex_familiars.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.alshanex.alshanex_familiars.recipe_types.RitualRecipeType;
import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.alshanex.alshanex_familiars.registry.RecipeRegistry;
import net.alshanex.alshanex_familiars.util.rituals.FlyingItem;
import net.alshanex.alshanex_familiars.util.rituals.RitualHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class RitualAutomatorBlockEntity extends BlockEntity {
   private static final int FLIGHT_DURATION = 20;
   private ResourceLocation selectedRecipeId;
   private BlockPos cachedCorePos;
   private final List<FlyingItem> flyingItems = new ArrayList<>();
   private int cooldown = 0;

   public RitualAutomatorBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityRegistry.RITUAL_AUTOMATOR.get(), pos, state);
   }

   public void setRecipe(ResourceLocation id) {
      this.selectedRecipeId = id;
      this.setChanged();
      this.sync();
   }

   public ResourceLocation getSelectedRecipeId() {
      return this.selectedRecipeId;
   }

   public List<FlyingItem> getFlyingItems() {
      return this.flyingItems;
   }

   public void tick(Level level, BlockPos pos, BlockState state) {
      this.processFlyingItems(level);
      if (!level.isClientSide) {
         if (this.cooldown > 0) {
            this.cooldown--;
         } else {
            this.cooldown = 10;
            if (this.selectedRecipeId != null) {
               if (this.cachedCorePos == null || !(level.getBlockEntity(this.cachedCorePos) instanceof RitualCorePedestalEntity)) {
                  this.findCore();
                  if (this.cachedCorePos == null) {
                     return;
                  }
               }

               RitualCorePedestalEntity core = (RitualCorePedestalEntity)level.getBlockEntity(this.cachedCorePos);
               if (core != null) {
                  List<BlockEntity> pedestals = RitualHelper.getSurroundingPedestals(this.cachedCorePos, level);
                  if (pedestals.size() >= 4) {
                     IItemHandler storage = (IItemHandler)level.getCapability(ItemHandler.BLOCK, pos.below(), null);
                     if (storage != null) {
                        Optional<RecipeHolder<RitualRecipeType>> recipeOpt = level.getRecipeManager()
                           .getAllRecipesFor((RecipeType<RitualRecipeType>)(RecipeType<?>)RecipeRegistry.RITUAL_RECIPE_TYPE.get())
                           .stream()
                           .filter(r -> r.id().equals(this.selectedRecipeId))
                           .findFirst();
                        if (!recipeOpt.isEmpty()) {
                           RitualRecipeType recipe = (RitualRecipeType)recipeOpt.get().value();
                           if (!this.performCollection(level, core, recipe, storage)) {
                              if (!this.performCleanup(level, core, pedestals, recipe, storage)) {
                                 this.performSupply(level, core, pedestals, recipe, storage);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean performCollection(Level level, RitualCorePedestalEntity core, RitualRecipeType recipe, IItemHandler storage) {
      ItemStack coreItem = core.getHeldItem();
      if (!coreItem.isEmpty() && ItemStack.isSameItemSameComponents(coreItem, recipe.result())) {
         core.setHeldItem(ItemStack.EMPTY);
         this.spawnFlyingItem(coreItem.copy(), core.getBlockPos(), this.worldPosition, true);
         return true;
      } else {
         return false;
      }
   }

   private boolean performCleanup(Level level, RitualCorePedestalEntity core, List<BlockEntity> pedestals, RitualRecipeType recipe, IItemHandler storage) {
      if (!core.getHeldItem().isEmpty()) {
         boolean isResult = ItemStack.isSameItemSameComponents(core.getHeldItem(), recipe.result());
         boolean isCentral = recipe.centralItem().test(core.getHeldItem());
         if (!isResult && !isCentral && !this.isTargeted(core.getBlockPos())) {
            ItemStack invalid = core.getHeldItem().copy();
            core.setHeldItem(ItemStack.EMPTY);
            this.spawnFlyingItem(invalid, core.getBlockPos(), this.worldPosition, true);
            return true;
         }
      }

      for (BlockEntity be : pedestals) {
         if (be instanceof FamiliarStatueEntity pedestal && !pedestal.getHeldItem().isEmpty() && !this.isTargeted(pedestal.getBlockPos())) {
            boolean isValidIngredient = false;
            ItemStack pedestalItem = pedestal.getHeldItem();

            for (RitualRecipeType.RitualIngredient input : recipe.inputs()) {
               if (input.ingredient().test(pedestalItem)) {
                  isValidIngredient = true;
                  break;
               }
            }

            if (!isValidIngredient) {
               ItemStack invalid = pedestalItem.copy();
               pedestal.setHeldItem(ItemStack.EMPTY);
               this.spawnFlyingItem(invalid, pedestal.getBlockPos(), this.worldPosition, true);
               return true;
            }
         }
      }

      return false;
   }

   private void performSupply(Level level, RitualCorePedestalEntity core, List<BlockEntity> pedestals, RitualRecipeType recipe, IItemHandler storage) {
      if (core.getHeldItem().isEmpty() && !this.isTargeted(core.getBlockPos())) {
         ItemStack extracted = this.extractItemForIngredient(storage, recipe.centralItem(), 1, true);
         if (!extracted.isEmpty()) {
            this.extractItemForIngredient(storage, recipe.centralItem(), 1, false);
            this.spawnFlyingItem(extracted, this.worldPosition, core.getBlockPos(), false);
            return;
         }
      }

      List<RitualRecipeType.RitualIngredient> needed = new ArrayList<>(recipe.inputs());

      for (BlockEntity be : pedestals) {
         if (be instanceof FamiliarStatueEntity pedestal && !pedestal.getHeldItem().isEmpty()) {
            this.removeNeed(needed, pedestal.getHeldItem());
         }
      }

      for (FlyingItem fly : this.flyingItems) {
         if (!fly.isRetrieving()) {
            this.removeNeed(needed, fly.getStack());
         }
      }

      for (RitualRecipeType.RitualIngredient ingredient : needed) {
         Optional<FamiliarStatueEntity> emptyPedestal = pedestals.stream()
            .filter(bex -> bex instanceof FamiliarStatueEntity)
            .map(bex -> (FamiliarStatueEntity)bex)
            .filter(p -> p.getHeldItem().isEmpty() && !this.isTargeted(p.getBlockPos()))
            .findFirst();
         if (emptyPedestal.isPresent()) {
            ItemStack extracted = this.extractItemForIngredient(storage, ingredient.ingredient(), 1, true);
            if (!extracted.isEmpty()) {
               this.extractItemForIngredient(storage, ingredient.ingredient(), 1, false);
               this.spawnFlyingItem(extracted, this.worldPosition, emptyPedestal.get().getBlockPos(), false);
               return;
            }
         }
      }
   }

   private void removeNeed(List<RitualRecipeType.RitualIngredient> needed, ItemStack stack) {
      for (int i = 0; i < needed.size(); i++) {
         RitualRecipeType.RitualIngredient ing = needed.get(i);
         if (ing.ingredient().test(stack)) {
            needed.remove(i);
            return;
         }
      }
   }

   private boolean isTargeted(BlockPos pos) {
      for (FlyingItem fly : this.flyingItems) {
         if (fly.getTargetPos().equals(pos) || fly.getSourcePos().equals(pos)) {
            return true;
         }
      }

      return false;
   }

   private void findCore() {
      MutableBlockPos p = new MutableBlockPos();

      for (int x = -10; x <= 10; x++) {
         for (int y = -5; y <= 5; y++) {
            for (int z = -10; z <= 10; z++) {
               p.setWithOffset(this.worldPosition, x, y, z);
               if (this.level.getBlockEntity(p) instanceof RitualCorePedestalEntity) {
                  this.cachedCorePos = p.immutable();
                  return;
               }
            }
         }
      }
   }

   private ItemStack extractItemForIngredient(IItemHandler handler, Ingredient ingredient, int amount, boolean simulate) {
      for (int i = 0; i < handler.getSlots(); i++) {
         ItemStack inSlot = handler.getStackInSlot(i);
         if (ingredient.test(inSlot)) {
            return handler.extractItem(i, amount, simulate);
         }
      }

      return ItemStack.EMPTY;
   }

   private void spawnFlyingItem(ItemStack stack, BlockPos from, BlockPos to, boolean retrieving) {
      this.flyingItems.add(new FlyingItem(stack, from, to, 20, retrieving));
      this.setChanged();
      this.sync();
   }

   private void processFlyingItems(Level level) {
      List<FlyingItem> toRemove = new ArrayList<>();

      for (FlyingItem fly : this.flyingItems) {
         fly.tick();
         if (fly.hasArrived() && !level.isClientSide) {
            this.completeFlight(fly);
            toRemove.add(fly);
         }
      }

      if (!toRemove.isEmpty()) {
         this.flyingItems.removeAll(toRemove);
         this.setChanged();
         this.sync();
      }
   }

   private void completeFlight(FlyingItem fly) {
      if (fly.isRetrieving()) {
         this.returnToStorageOrDrop(fly.getStack());
      } else {
         BlockEntity target = this.level.getBlockEntity(fly.getTargetPos());
         boolean success = false;
         if (target instanceof FamiliarStatueEntity pedestal) {
            if (pedestal.getHeldItem().isEmpty()) {
               pedestal.setHeldItem(fly.getStack());
               success = true;
            }
         } else if (target instanceof RitualCorePedestalEntity core && core.getHeldItem().isEmpty()) {
            core.setHeldItem(fly.getStack());
            success = true;
         }

         if (!success) {
            this.returnToStorageOrDrop(fly.getStack());
         }
      }
   }

   private void returnToStorageOrDrop(ItemStack stack) {
      if (!stack.isEmpty()) {
         IItemHandler storage = (IItemHandler)this.level.getCapability(ItemHandler.BLOCK, this.worldPosition.below(), null);
         if (storage != null) {
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(storage, stack, false);
            if (!remainder.isEmpty()) {
               Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY() + 1, this.worldPosition.getZ(), remainder);
            }
         } else {
            Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY() + 1, this.worldPosition.getZ(), stack);
         }
      }
   }

   private void sync() {
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      if (this.selectedRecipeId != null) {
         tag.putString("RecipeId", this.selectedRecipeId.toString());
      }

      ListTag flyList = new ListTag();

      for (FlyingItem fly : this.flyingItems) {
         flyList.add(fly.save(registries));
      }

      tag.put("FlyingItems", flyList);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (tag.contains("RecipeId")) {
         this.selectedRecipeId = ResourceLocation.parse(tag.getString("RecipeId"));
      }

      this.flyingItems.clear();
      if (tag.contains("FlyingItems")) {
         ListTag flyList = tag.getList("FlyingItems", 10);

         for (int i = 0; i < flyList.size(); i++) {
            this.flyingItems.add(FlyingItem.load(flyList.getCompound(i), registries));
         }
      }
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      CompoundTag tag = pkt.getTag();
      if (tag != null) {
         this.loadWithComponents(tag, lookupProvider);
      }

      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }
}
