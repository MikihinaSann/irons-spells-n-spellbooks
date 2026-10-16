package com.userofbricks.expanded_combat.item;

import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.api.registry.ShieldMaterialUseTick;
import com.userofbricks.expanded_combat.data_components.ShieldMaterials;
import com.userofbricks.expanded_combat.datagen.LangStrings;
import com.userofbricks.expanded_combat.init.ItemDataComponents;
import com.userofbricks.expanded_combat.init.PluginInit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ECShieldItem extends ShieldItem {
   public ECShieldItem(Properties properties) {
      super(properties.stacksTo(1).component(ItemDataComponents.SHIELD_MATERIALS, ShieldMaterials.DEFAULT));
   }

   public int getMaxDamage(ItemStack stack) {
      int durability = 336;
      int ul = getUpperLeftMaterial(stack).durability().addedShieldDurability();
      int ur = getUpperRightMaterial(stack).durability().addedShieldDurability();
      int dl = getDownLeftMaterial(stack).durability().addedShieldDurability();
      int dr = getDownRightMaterial(stack).durability().addedShieldDurability();
      int m = getMiddleMaterial(stack).durability().addedShieldDurability();
      return durability + ul + ur + dl + dr + m;
   }

   public boolean canBeDepleted() {
      return true;
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      if (repair.getItem() instanceof EnchantedBookItem) {
         return false;
      } else {
         Material ul = getUpperLeftMaterial(toRepair);
         Material ur = getUpperRightMaterial(toRepair);
         Material dl = getDownLeftMaterial(toRepair);
         Material dr = getDownRightMaterial(toRepair);
         Material m = getMiddleMaterial(toRepair);
         ShieldMaterials shieldMaterials = (ShieldMaterials)toRepair.getOrDefault(ItemDataComponents.SHIELD_MATERIALS, ShieldMaterials.DEFAULT);
         int last = shieldMaterials.lastRepairNumber() + 1;
         if (last >= 5) {
            last = 0;
         }

         List<Material> slotMaterials = Arrays.asList(ul, ur, dl, dr, m);
         Material currentSlotMaterial = slotMaterials.get(last);
         toRepair.set(ItemDataComponents.SHIELD_MATERIALS, shieldMaterials.updateLastRepair(last));
         Ingredient ingredient = currentSlotMaterial.repairItem() != null ? currentSlotMaterial.repairItem().get() : Ingredient.EMPTY;
         return !ingredient.isEmpty() && ingredient.test(repair);
      }
   }

   public float getXpRepairRatio(ItemStack stack) {
      return 2.0F + this.getMendingBonus(stack);
   }

   public float getMendingBonus(ItemStack stack) {
      float ul = getUpperLeftMaterial(stack).enchanting().mendingBonus() / 5.0F;
      float ur = getUpperRightMaterial(stack).enchanting().mendingBonus() / 5.0F;
      float dl = getDownLeftMaterial(stack).enchanting().mendingBonus() / 5.0F;
      float dr = getDownRightMaterial(stack).enchanting().mendingBonus() / 5.0F;
      float m = getMiddleMaterial(stack).enchanting().mendingBonus() / 5.0F;
      return ul + ur + dl + dr + m;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, TooltipContext pContext, List<Component> list, TooltipFlag flag) {
      com.userofbricks.expanded_combat.data_components.ShieldMaterials shieldTooltip =
            stack.get(ItemDataComponents.SHIELD_MATERIALS.get());
      if (shieldTooltip != null) {
         shieldTooltip.addToTooltip(pContext, list::add, flag);
      }
      if (this.getMendingBonus(stack) != 0.0F) {
         if (this.getMendingBonus(stack) > 0.0F) {
            list.add(
               1,
               Component.translatable(LangStrings.GOLD_MENDING_TOOLTIP)
                  .withStyle(ChatFormatting.BLUE)
                  .append(Component.literal(ChatFormatting.BLUE + " " + ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(this.getMendingBonus(stack))))
            );
         } else if (this.getMendingBonus(stack) < 0.0F) {
            list.add(
               1,
               Component.translatable(LangStrings.GOLD_MENDING_TOOLTIP)
                  .withStyle(ChatFormatting.RED)
                  .append(Component.literal(ChatFormatting.RED + " " + ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(this.getMendingBonus(stack))))
            );
         }
      }

      super.appendHoverText(stack, pContext, list, flag);
   }

   public static double getBaseProtection(ItemStack stack) {
      double ul = getUpperLeftMaterial(stack).defense().baseProtectionAmmount() / 5.0F;
      double ur = getUpperRightMaterial(stack).defense().baseProtectionAmmount() / 5.0F;
      double dl = getDownLeftMaterial(stack).defense().baseProtectionAmmount() / 5.0F;
      double dr = getDownRightMaterial(stack).defense().baseProtectionAmmount() / 5.0F;
      double m = getMiddleMaterial(stack).defense().baseProtectionAmmount() / 5.0F;
      return ul + ur + dl + dr + m;
   }

   public static double getPercentageProtection(ItemStack stack) {
      double ul = getUpperLeftMaterial(stack).defense().afterBasePercentReduction() / 5.0F;
      double ur = getUpperRightMaterial(stack).defense().afterBasePercentReduction() / 5.0F;
      double dl = getDownLeftMaterial(stack).defense().afterBasePercentReduction() / 5.0F;
      double dr = getDownRightMaterial(stack).defense().afterBasePercentReduction() / 5.0F;
      double m = getMiddleMaterial(stack).defense().afterBasePercentReduction() / 5.0F;
      return ul + ur + dl + dr + m;
   }

   public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
      return enchantment == Enchantments.BINDING_CURSE ? false : super.supportsEnchantment(stack, enchantment);
   }

   public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int timeUsing) {
      super.onUseTick(level, livingEntity, itemStack, timeUsing);
      Map<Material, Integer> materials = new HashMap<>();

      for (Material material : Arrays.asList(
         getUpperLeftMaterial(itemStack),
         getUpperRightMaterial(itemStack),
         getMiddleMaterial(itemStack),
         getDownLeftMaterial(itemStack),
         getDownRightMaterial(itemStack)
      )) {
         if (materials.containsKey(material)) {
            materials.replace(material, materials.get(material) + 1);
         } else {
            materials.put(material, 1);
         }
      }

      for (Entry<Material, Integer> materialx : materials.entrySet()) {
         ShieldMaterialUseTick useTick = PluginInit.getShieldUseTickEntry(materialx.getKey());
         if (useTick != null) {
            useTick.onUseTick().apply(level, livingEntity, itemStack, timeUsing, materialx.getValue());
         }
      }
   }

   public static Material getUpperLeftMaterial(ItemStack stack) {
      return ((ShieldMaterials)stack.getOrDefault(ItemDataComponents.SHIELD_MATERIALS, ShieldMaterials.DEFAULT)).ULMaterial();
   }

   public static Material getUpperRightMaterial(ItemStack stack) {
      return ((ShieldMaterials)stack.getOrDefault(ItemDataComponents.SHIELD_MATERIALS, ShieldMaterials.DEFAULT)).URMaterial();
   }

   public static Material getDownLeftMaterial(ItemStack stack) {
      return ((ShieldMaterials)stack.getOrDefault(ItemDataComponents.SHIELD_MATERIALS, ShieldMaterials.DEFAULT)).DLMaterial();
   }

   public static Material getDownRightMaterial(ItemStack stack) {
      return ((ShieldMaterials)stack.getOrDefault(ItemDataComponents.SHIELD_MATERIALS, ShieldMaterials.DEFAULT)).DRMaterial();
   }

   public static Material getMiddleMaterial(ItemStack stack) {
      return ((ShieldMaterials)stack.getOrDefault(ItemDataComponents.SHIELD_MATERIALS, ShieldMaterials.DEFAULT)).MMaterial();
   }

   public static ItemStack makeShieldBeMaterial(ItemStack stack, Material material) {
      stack.set(ItemDataComponents.SHIELD_MATERIALS, new ShieldMaterials(material, material, material, material, material, 0));
      return stack;
   }
}
