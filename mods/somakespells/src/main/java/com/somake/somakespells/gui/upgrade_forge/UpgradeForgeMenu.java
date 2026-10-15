package com.somake.somakespells.gui.upgrade_forge;

import com.somake.somakespells.item.AbstractAnimatedGrimoireItem;
import com.somake.somakespells.item.DormantGrimoireProgressHelper;
import com.somake.somakespells.item.spellbooks.SpellbookProgressHelper;
import com.somake.somakespells.recipe.upgrade_forge.UpgradeForgeRecipe;
import com.somake.somakespells.registries.ModMenus;
import com.somake.somakespells.registries.ModRecipes;
import io.redspace.ironsspellbooks.item.SpellBook;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class UpgradeForgeMenu extends AbstractContainerMenu {
   public static final int GUI_WIDTH = 256;
   public static final int GUI_HEIGHT = 166;
   public static final int BOOK_SLOT_X = 17;
   public static final int BOOK_SLOT_Y = 34;
   public static final int MATERIAL_TOP_X = 70;
   public static final int MATERIAL_TOP_Y = 14;
   public static final int MATERIAL_LEFT_X = 52;
   public static final int MATERIAL_LEFT_Y = 32;
   public static final int MATERIAL_BOTTOM_X = 70;
   public static final int MATERIAL_BOTTOM_Y = 50;
   public static final int MATERIAL_RIGHT_X = 88;
   public static final int MATERIAL_RIGHT_Y = 32;
   public static final int RESULT_SLOT_X = 134;
   public static final int RESULT_SLOT_Y = 32;
   private static final int PLAYER_SLOT_COUNT = 36;
   private static final int BOOK_SLOT_INDEX = 36;
   private static final int FIRST_MATERIAL_SLOT_INDEX = 37;
   private static final int RESULT_SLOT_INDEX = 41;
   private final Player player;
   private final Container forgeContainer;
   private final ResultContainer resultContainer = new ResultContainer();
   private final Container materialContainer = new SimpleContainer(4) {
      public void setChanged() {
         super.setChanged();
         UpgradeForgeMenu.this.slotsChanged(this);
      }
   };
   private UpgradeForgeRecipe currentRecipe;
   private int errorCode;

   public UpgradeForgeMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
      this(containerId, inventory, new SimpleContainer(1));
   }

   public UpgradeForgeMenu(int containerId, Inventory inventory, Container forgeContainer) {
      super(ModMenus.UPGRADE_FORGE_MENU.get(), containerId);
      checkContainerSize(forgeContainer, 1);
      this.player = inventory.player;
      this.forgeContainer = forgeContainer;
      forgeContainer.startOpen(this.player);
      this.addPlayerInventory(inventory);
      this.addPlayerHotbar(inventory);
      this.addSlot(new Slot(forgeContainer, 0, 17, 34) {
         public boolean mayPlace(ItemStack stack) {
            return UpgradeForgeMenu.this.isUpgradeableBase(stack);
         }

         public int getMaxStackSize() {
            return 1;
         }

         public void setChanged() {
            super.setChanged();
            UpgradeForgeMenu.this.slotsChanged(this.container);
         }
      });
      this.addSlot(new Slot(this.materialContainer, 0, 70, 14) {
         public boolean mayPlace(ItemStack stack) {
            return true;
         }
      });
      this.addSlot(new Slot(this.materialContainer, 1, 52, 32) {
         public boolean mayPlace(ItemStack stack) {
            return true;
         }
      });
      this.addSlot(new Slot(this.materialContainer, 2, 70, 50) {
         public boolean mayPlace(ItemStack stack) {
            return true;
         }
      });
      this.addSlot(new Slot(this.materialContainer, 3, 88, 32) {
         public boolean mayPlace(ItemStack stack) {
            return true;
         }
      });
      this.addSlot(new Slot(this.resultContainer, 0, 134, 32) {
         public boolean mayPlace(ItemStack stack) {
            return false;
         }

         public boolean mayPickup(Player player) {
            return false;
         }
      });
      this.setupResult();
   }

   public void slotsChanged(Container container) {
      super.slotsChanged(container);
      this.setupResult();
   }

   public boolean clickMenuButton(Player player, int id) {
      if (id != 0 || !this.stillValid(player)) {
         return false;
      } else {
         return player.level().isClientSide() ? this.canUpgrade() : this.doUpgrade();
      }
   }

   public boolean canUpgrade() {
      return this.currentRecipe != null && this.errorCode == 0 && !this.getPreviewResult().isEmpty();
   }

   public int getErrorCode() {
      return this.errorCode;
   }

   public ItemStack getPreviewResult() {
      return this.resultContainer.getItem(0);
   }

   private void setupResult() {
      ItemStack bookStack = this.getBookSlot().getItem();
      this.currentRecipe = this.resolveRecipe(bookStack);
      if (bookStack.isEmpty()) {
         this.errorCode = 0;
         this.resultContainer.setItem(0, ItemStack.EMPTY);
      } else if (this.currentRecipe == null) {
         this.errorCode = 1;
         this.resultContainer.setItem(0, ItemStack.EMPTY);
      } else if (!this.matchesRecipeProgress(bookStack, this.currentRecipe)) {
         this.errorCode = 2;
         this.resultContainer.setItem(0, ItemStack.EMPTY);
      } else if (!this.hasRequiredMaterials(this.currentRecipe)) {
         this.errorCode = 3;
         this.resultContainer.setItem(0, ItemStack.EMPTY);
      } else {
         this.errorCode = 0;
         this.resultContainer.setItem(0, this.currentRecipe.createUpgradedBook(bookStack));
      }
   }

   private boolean doUpgrade() {
      ItemStack sourceBook = this.getBookSlot().getItem();
      UpgradeForgeRecipe recipe = this.resolveRecipe(sourceBook);
      int[] materialSlots = recipe == null ? null : this.findMaterialSlots(recipe);
      if (!sourceBook.isEmpty() && sourceBook.getCount() == 1 && recipe != null && this.matchesRecipeProgress(sourceBook, recipe) && materialSlots != null) {
         ItemStack upgraded = this.createUpgradedBook(sourceBook, recipe);
         if (upgraded.isEmpty()) {
            this.setupResult();
            return false;
         } else {
            this.consumeRequirements(recipe, materialSlots);
            this.getBookSlot().set(upgraded);
            this.setupResult();
            return true;
         }
      } else {
         this.setupResult();
         return false;
      }
   }

   private ItemStack createUpgradedBook(ItemStack sourceBook, UpgradeForgeRecipe recipe) {
      ItemStack upgraded = recipe.createUpgradedBook(sourceBook);
      SpellbookProgressHelper.resetProgress(upgraded);
      DormantGrimoireProgressHelper.resetProgress(upgraded);
      return upgraded;
   }

   private void consumeRequirements(UpgradeForgeRecipe recipe, int[] materialSlots) {
      for (int ingredientIndex = 0; ingredientIndex < recipe.ingredients().size(); ingredientIndex++) {
         Slot slot = this.getMaterialSlot(materialSlots[ingredientIndex]);
         ItemStack stack = slot.getItem();
         stack.shrink(recipe.ingredients().get(ingredientIndex).count());
         if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }
      }
   }

   private boolean hasRequiredMaterials(UpgradeForgeRecipe recipe) {
      return this.findMaterialSlots(recipe) != null;
   }

   private int[] findMaterialSlots(UpgradeForgeRecipe recipe) {
      int ingredientCount = recipe.ingredients().size();
      if (ingredientCount != 4) {
         return null;
      } else {
         int[] materialSlots = new int[ingredientCount];
         boolean[] usedSlots = new boolean[4];
         return this.matchMaterialSlot(recipe, 0, materialSlots, usedSlots) ? materialSlots : null;
      }
   }

   private boolean matchMaterialSlot(UpgradeForgeRecipe recipe, int ingredientIndex, int[] materialSlots, boolean[] usedSlots) {
      if (ingredientIndex >= recipe.ingredients().size()) {
         return true;
      } else {
         UpgradeForgeRecipe.CountedIngredient ingredient = recipe.ingredients().get(ingredientIndex);

         for (int slotIndex = 0; slotIndex < 4; slotIndex++) {
            if (!usedSlots[slotIndex] && ingredient.test(this.getMaterialSlot(slotIndex).getItem())) {
               usedSlots[slotIndex] = true;
               materialSlots[ingredientIndex] = slotIndex;
               if (this.matchMaterialSlot(recipe, ingredientIndex + 1, materialSlots, usedSlots)) {
                  return true;
               }

               usedSlots[slotIndex] = false;
            }
         }

         return false;
      }
   }

   private UpgradeForgeRecipe resolveRecipe(ItemStack bookStack) {
      if (bookStack.isEmpty()) {
         return null;
      } else {
         List<UpgradeForgeRecipe> recipes = this.player
            .level()
            .getRecipeManager()
            .getAllRecipesFor((RecipeType<UpgradeForgeRecipe>)(RecipeType<?>)ModRecipes.UPGRADE_FORGE_TYPE.get())
            .stream()
            .map(holder -> holder.value())
            .filter(candidate -> candidate.matchesBase(bookStack))
            .toList();
         if (recipes.isEmpty()) {
            return null;
         } else {
            Optional<UpgradeForgeRecipe> matchingRecipe = recipes.stream().filter(candidate -> this.matchesRecipeProgress(bookStack, candidate)).findFirst();
            if (matchingRecipe.isPresent()) {
               return matchingRecipe.get();
            } else {
               boolean hasBranchingGrimoireRecipes = recipes.stream().anyMatch(candidate -> candidate.requiredGrimoirePath().isPresent());
               return hasBranchingGrimoireRecipes ? null : recipes.getFirst();
            }
         }
      }
   }

   private boolean matchesRecipeProgress(ItemStack bookStack, UpgradeForgeRecipe recipe) {
      return recipe.requiredGrimoirePath().isPresent()
         ? DormantGrimoireProgressHelper.hasAlignment(bookStack, recipe.requiredGrimoirePath().get())
         : !recipe.requiresMaxProgress() || SpellbookProgressHelper.hasCompletedAllTracks(bookStack, recipe.school());
   }

   private Slot getBookSlot() {
      return (Slot)this.slots.get(36);
   }

   private Slot getMaterialSlot(int index) {
      return (Slot)this.slots.get(37 + index);
   }

   public ItemStack quickMoveStack(Player player, int index) {
      if (index >= 0 && index < this.slots.size()) {
         Slot slot = (Slot)this.slots.get(index);
         if (!slot.hasItem()) {
            return ItemStack.EMPTY;
         } else {
            ItemStack sourceStack = slot.getItem();
            ItemStack copy = sourceStack.copy();
            if (index < 36) {
               if (this.isUpgradeableBase(sourceStack)) {
                  if (!this.moveItemStackTo(sourceStack, 36, 37, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (!this.moveItemStackTo(sourceStack, 37, 41, false)) {
                  return ItemStack.EMPTY;
               }
            } else {
               if (index >= 41) {
                  return ItemStack.EMPTY;
               }

               if (!this.moveItemStackTo(sourceStack, 0, 36, false)) {
                  return ItemStack.EMPTY;
               }
            }

            if (sourceStack.isEmpty()) {
               slot.set(ItemStack.EMPTY);
            } else {
               slot.setChanged();
            }

            slot.onTake(player, sourceStack);
            return copy;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean stillValid(Player player) {
      return this.forgeContainer.stillValid(player);
   }

   public void removed(Player player) {
      super.removed(player);
      this.forgeContainer.stopOpen(player);
      if (!player.level().isClientSide()) {
         this.clearContainer(player, this.materialContainer);
      }
   }

   private static boolean isUpgradeableBookLike(ItemStack stack) {
      return stack.getItem() instanceof SpellBook || stack.getItem() instanceof AbstractAnimatedGrimoireItem;
   }

   private boolean isUpgradeableBase(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         return isUpgradeableBookLike(stack)
            ? true
            : this.player
               .level()
               .getRecipeManager()
               .getAllRecipesFor((RecipeType<UpgradeForgeRecipe>)(RecipeType<?>)ModRecipes.UPGRADE_FORGE_TYPE.get())
               .stream()
               .map(holder -> holder.value())
               .anyMatch(recipe -> recipe.matchesBase(stack));
      }
   }

   private void addPlayerInventory(Inventory inventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
         }
      }
   }

   private void addPlayerHotbar(Inventory inventory) {
      for (int slot = 0; slot < 9; slot++) {
         this.addSlot(new Slot(inventory, slot, 8 + slot * 18, 142));
      }
   }

   public UpgradeForgeMenu.ThemeColors getThemeColors() {
      if (this.currentRecipe == null) {
         return UpgradeForgeMenu.ThemeColors.DEFAULT;
      } else {
         String var1 = this.currentRecipe.school().getPath();

         return switch (var1) {
            case "fire" -> new UpgradeForgeMenu.ThemeColors(16757575, 13131050, 9079434, 14244956, 6542449);
            case "ice" -> new UpgradeForgeMenu.ThemeColors(14283775, 7256550, 9079434, 14244956, 6542449);
            case "lightning" -> new UpgradeForgeMenu.ThemeColors(15398143, 8040191, 9079434, 14244956, 6542449);
            case "holy" -> new UpgradeForgeMenu.ThemeColors(16774084, 14070874, 9079434, 14244956, 6542449);
            case "nature" -> new UpgradeForgeMenu.ThemeColors(11463300, 5151313, 9079434, 14244956, 6542449);
            case "ender" -> new UpgradeForgeMenu.ThemeColors(13939711, 9133254, 9079434, 14244956, 6542449);
            case "blood" -> new UpgradeForgeMenu.ThemeColors(15833497, 12139580, 9079434, 14244956, 6542449);
            case "evocation" -> new UpgradeForgeMenu.ThemeColors(15790320, 12105912, 9079434, 14244956, 6542449);
            case "eldritch" -> new UpgradeForgeMenu.ThemeColors(9429211, 3116934, 9079434, 14244956, 6542449);
            case "spirit" -> new UpgradeForgeMenu.ThemeColors(13170175, 7521498, 9079434, 14244956, 6542449);
            case "symmetry" -> new UpgradeForgeMenu.ThemeColors(12900351, 7243736, 9079434, 14244956, 6542449);
            case "aqua" -> new UpgradeForgeMenu.ThemeColors(9431295, 3909577, 9079434, 14244956, 6542449);
            case "geo" -> new UpgradeForgeMenu.ThemeColors(13219740, 9138516, 9079434, 14244956, 6542449);
            case "sound" -> new UpgradeForgeMenu.ThemeColors(15128063, 10187737, 9079434, 14244956, 6542449);
            default -> UpgradeForgeMenu.ThemeColors.DEFAULT;
         };
      }
   }

   public record ThemeColors(int titleColor, int ingredientColor, int emptyColor, int errorColor, int successColor) {
      public static final UpgradeForgeMenu.ThemeColors DEFAULT = new UpgradeForgeMenu.ThemeColors(16757575, 13131050, 9079434, 14244956, 6542449);
   }
}
