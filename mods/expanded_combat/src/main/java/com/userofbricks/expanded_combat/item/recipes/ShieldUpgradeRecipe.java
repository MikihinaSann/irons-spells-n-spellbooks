package com.userofbricks.expanded_combat.item.recipes;

import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.data_components.ShieldMaterials;
import com.userofbricks.expanded_combat.init.DataMaps;
import com.userofbricks.expanded_combat.init.ECItems;
import com.userofbricks.expanded_combat.init.ECRecipeSerializerInit;
import com.userofbricks.expanded_combat.init.ItemDataComponents;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ShieldUpgradeRecipe extends ShieldSmithingRecipie {
   @Override
   public boolean matches(ShieldSmithingRecipeInput inventory, @Nonnull Level world) {
      ItemStack base = inventory.getItem(0);
      ShieldMaterials shieldMaterials = (ShieldMaterials)base.get(ItemDataComponents.SHIELD_MATERIALS);
      if (shieldMaterials == null) {
         shieldMaterials = (ShieldMaterials)base.getItemHolder().getData(DataMaps.SHIELD_MATERIALS);
      }

      if (shieldMaterials == null) {
         return false;
      } else if (!inventory.getItem(1).isEmpty()) {
         return false;
      } else if (!inventory.getItem(2).isEmpty()) {
         return false;
      } else if (!inventory.getItem(4).isEmpty()) {
         return false;
      } else if (!inventory.getItem(5).isEmpty()) {
         return false;
      } else if (inventory.getItem(3).isEmpty()) {
         return false;
      } else {
         Material addition_m_material = (Material)inventory.getItem(3).getItemHolder().getData(DataMaps.SHIELD_INGREDIENT_MAP);
         return addition_m_material != null && addition_m_material.crafting().isSingleAddition()
            ? shieldMaterials.canReplaceUL(addition_m_material)
               || shieldMaterials.canReplaceUR(addition_m_material)
               || shieldMaterials.canReplaceDL(addition_m_material)
               || shieldMaterials.canReplaceDR(addition_m_material)
               || shieldMaterials.canReplaceM(addition_m_material)
            : false;
      }
   }

   @NotNull
   @Override
   public ItemStack assemble(ShieldSmithingRecipeInput inventory, @NotNull Provider access) {
      ItemStack base = inventory.getItem(0);
      ShieldMaterials shieldMaterials = (ShieldMaterials)base.get(ItemDataComponents.SHIELD_MATERIALS);
      if (shieldMaterials == null) {
         shieldMaterials = (ShieldMaterials)base.getItemHolder().getData(DataMaps.SHIELD_MATERIALS);
      }

      if (shieldMaterials == null) {
         return ItemStack.EMPTY;
      } else {
         Material addition_m_material = (Material)inventory.getItem(3).getItemHolder().getData(DataMaps.SHIELD_INGREDIENT_MAP);
         if (addition_m_material == null) {
            return ItemStack.EMPTY;
         } else {
            Material result_ul_material = shieldMaterials.canReplaceUL(addition_m_material) ? addition_m_material : shieldMaterials.ULMaterial();
            Material result_ur_material = shieldMaterials.canReplaceUR(addition_m_material) ? addition_m_material : shieldMaterials.URMaterial();
            Material result_dl_material = shieldMaterials.canReplaceDL(addition_m_material) ? addition_m_material : shieldMaterials.DLMaterial();
            Material result_dr_material = shieldMaterials.canReplaceDR(addition_m_material) ? addition_m_material : shieldMaterials.DRMaterial();
            Material result_m_material = shieldMaterials.canReplaceM(addition_m_material) ? addition_m_material : shieldMaterials.MMaterial();
            ItemStack result = new ItemStack((ItemLike)ECItems.SHIELD.get());
            if (result_ul_material.defense().fireResistant()
               || result_ur_material.defense().fireResistant()
               || result_m_material.defense().fireResistant()
               || result_dl_material.defense().fireResistant()
               || result_dr_material.defense().fireResistant()) {
               result = new ItemStack((ItemLike)ECItems.SHIELD_FIRE_RESISTANT.get());
            }

            result.set(
               ItemDataComponents.SHIELD_MATERIALS,
               new ShieldMaterials(
                  result_ul_material, result_ur_material, result_dl_material, result_dr_material, result_m_material, shieldMaterials.lastRepairNumber()
               )
            );
            return result;
         }
      }
   }

   @NotNull
   @Override
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ECRecipeSerializerInit.EC_UPGRADING_SHIELD_SERIALIZER.get();
   }
}
