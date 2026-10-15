package com.chadate.spellelemental.integration.jei.recipe;

import net.minecraft.world.item.ItemStack;

public record SwordOilApplicationRecipe(
   ItemStack inputSword, ItemStack swordOil, ItemStack swordStand, ItemStack outputSword, String elementType, int elementAmount
) {
   public ItemStack getInputSword() {
      return this.inputSword;
   }

   public ItemStack getSwordOil() {
      return this.swordOil;
   }

   public ItemStack getSwordStand() {
      return this.swordStand;
   }

   public ItemStack getOutputSword() {
      return this.outputSword;
   }

   public String getElementType() {
      return this.elementType;
   }

   public int getElementAmount() {
      return this.elementAmount;
   }
}
