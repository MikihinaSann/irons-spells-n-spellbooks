package com.userofbricks.expanded_combat.inventory.container;

import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.api.material.PlacementInShield;
import com.userofbricks.expanded_combat.init.DataMaps;
import com.userofbricks.expanded_combat.init.ECContainers;
import com.userofbricks.expanded_combat.init.ECRecipeSerializerInit;
import com.userofbricks.expanded_combat.init.ItemDataComponents;
import com.userofbricks.expanded_combat.item.recipes.IShieldSmithingRecipe;
import com.userofbricks.expanded_combat.item.recipes.ShieldSmithingRecipeInput;
import java.util.Collections;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
public class ShieldSmithingMenu extends ItemCombinerMenu {
   private final Level level;
   private RecipeHolder<IShieldSmithingRecipe> selectedRecipe;

   public ShieldSmithingMenu(int id, Inventory playerInventory, ContainerLevelAccess access) {
      super((MenuType)ECContainers.SHIELD_SMITHING.get(), id, playerInventory, access);
      this.level = playerInventory.player.level();
   }

   public ShieldSmithingMenu(int id, Inventory playerInventory) {
      this(id, playerInventory, ContainerLevelAccess.NULL);
   }

   @NotNull
   protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
      return ItemCombinerMenuSlotDefinition.create()
         .withSlot(
            0, 27, 47, stack -> stack.get(ItemDataComponents.SHIELD_MATERIALS) != null || stack.getItemHolder().getData(DataMaps.SHIELD_MATERIALS) != null
         )
         .withSlot(1, 67, 29, stack -> stack.getItemHolder().getData(DataMaps.SHIELD_INGREDIENT_MAP) != null)
         .withSlot(2, 85, 29, stack -> stack.getItemHolder().getData(DataMaps.SHIELD_INGREDIENT_MAP) != null)
         .withSlot(3, 76, 47, stack -> {
            Material value = (Material)stack.getItemHolder().getData(DataMaps.SHIELD_INGREDIENT_MAP);
            return value != null && value.defense().placementInShield() == PlacementInShield.ALL;
         })
         .withSlot(4, 67, 65, stack -> stack.getItemHolder().getData(DataMaps.SHIELD_INGREDIENT_MAP) != null)
         .withSlot(5, 86, 65, stack -> stack.getItemHolder().getData(DataMaps.SHIELD_INGREDIENT_MAP) != null)
         .withResultSlot(6, 134, 47)
         .build();
   }

   protected boolean isValidBlock(BlockState pState) {
      return true;
   }

   protected boolean mayPickup(Player pPlayer, boolean pHasStack) {
      return this.selectedRecipe != null
         && ((IShieldSmithingRecipe)this.selectedRecipe.value())
            .matches(
               new ShieldSmithingRecipeInput(
                  this.inputSlots.getItem(0),
                  this.inputSlots.getItem(1),
                  this.inputSlots.getItem(2),
                  this.inputSlots.getItem(3),
                  this.inputSlots.getItem(4),
                  this.inputSlots.getItem(5)
               ),
               this.level
            );
   }

   protected void onTake(Player player, ItemStack itemStack) {
      itemStack.onCraftedBy(player.level(), player, itemStack.getCount());
      this.resultSlots.awardUsedRecipes(player, Collections.singletonList(itemStack));
      this.shrinkStackInSlot(0);
      this.shrinkStackInSlot(1);
      this.shrinkStackInSlot(2);
      this.shrinkStackInSlot(3);
      this.shrinkStackInSlot(4);
      this.shrinkStackInSlot(5);
      this.access.execute((p_234653_0_, p_234653_1_) -> p_234653_0_.levelEvent(1044, p_234653_1_, 0));
   }

   private void shrinkStackInSlot(int slot) {
      ItemStack itemstack = this.inputSlots.getItem(slot);
      if (!itemstack.isEmpty()) {
         itemstack.shrink(1);
         this.inputSlots.setItem(slot, itemstack);
      }
   }

   public void createResult() {
      List<RecipeHolder<IShieldSmithingRecipe>> list = this.level
         .getRecipeManager()
         .getRecipesFor(
            (RecipeType)ECRecipeSerializerInit.SHIELD_TYPE.get(),
            new ShieldSmithingRecipeInput(
               this.inputSlots.getItem(0),
               this.inputSlots.getItem(1),
               this.inputSlots.getItem(2),
               this.inputSlots.getItem(3),
               this.inputSlots.getItem(4),
               this.inputSlots.getItem(5)
            ),
            this.level
         );
      if (list.isEmpty()) {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
      } else {
         RecipeHolder<IShieldSmithingRecipe> recipe = list.getFirst();
         ItemStack itemstack = ((IShieldSmithingRecipe)recipe.value())
            .assemble(
               new ShieldSmithingRecipeInput(
                  this.inputSlots.getItem(0),
                  this.inputSlots.getItem(1),
                  this.inputSlots.getItem(2),
                  this.inputSlots.getItem(3),
                  this.inputSlots.getItem(4),
                  this.inputSlots.getItem(5)
               ),
               this.level.registryAccess()
            );
         if (itemstack.isItemEnabled(this.level.enabledFeatures())) {
            this.selectedRecipe = recipe;
            this.resultSlots.setRecipeUsed(recipe);
            this.resultSlots.setItem(0, itemstack);
         } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
         }
      }
   }

   public boolean canTakeItemForPickAll(ItemStack p_94530_1_, Slot p_94530_2_) {
      return p_94530_2_.container != this.resultSlots && super.canTakeItemForPickAll(p_94530_1_, p_94530_2_);
   }
}
