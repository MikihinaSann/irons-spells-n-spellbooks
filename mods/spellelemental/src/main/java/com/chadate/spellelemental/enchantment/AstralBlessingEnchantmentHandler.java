package com.chadate.spellelemental.enchantment;

import com.chadate.spellelemental.register.ModAttributes;
import com.chadate.spellelemental.register.ModEnchantments;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

@EventBusSubscriber(
   modid = "spellelemental"
)
public class AstralBlessingEnchantmentHandler {
   private static final double ASTRAL_BLESSING_PER_LEVEL = 20.0;
   private static final ResourceLocation ASTRAL_BLESSING_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("spellelemental", "astral_blessing_enchantment");

   @SubscribeEvent
   public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
      LivingEntity entity = event.getEntity();
      EquipmentSlot slot = event.getSlot();
      if (isArmorSlot(slot)) {
         updateAstralBlessingAttribute(entity);
      }
   }

   public static void updateAstralBlessingAttribute(LivingEntity entity) {
      AttributeInstance attributeInstance = entity.getAttribute(ModAttributes.ASTRAL_BLESSING);
      if (attributeInstance != null) {
         attributeInstance.removeModifier(ASTRAL_BLESSING_MODIFIER_ID);
         int totalEnchantmentLevel = calculateTotalAstralBlessingLevel(entity);
         if (totalEnchantmentLevel > 0) {
            double bonusValue = totalEnchantmentLevel * 20.0;
            AttributeModifier modifier = new AttributeModifier(ASTRAL_BLESSING_MODIFIER_ID, bonusValue, Operation.ADD_VALUE);
            attributeInstance.addPermanentModifier(modifier);
         }
      }
   }

   private static int calculateTotalAstralBlessingLevel(LivingEntity entity) {
      int totalLevel = 0;

      for (EquipmentSlot slot : getArmorSlots()) {
         ItemStack armorStack = entity.getItemBySlot(slot);
         if (!armorStack.isEmpty()) {
            Registry<Enchantment> enchantmentRegistry = entity.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            Optional<Reference<Enchantment>> enchantmentHolder = enchantmentRegistry.getHolder(ModEnchantments.ASTRAL_BLESSING);
            if (enchantmentHolder.isPresent()) {
               int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel((Holder)enchantmentHolder.get(), armorStack);
               totalLevel += enchantmentLevel;
            }
         }
      }

      return totalLevel;
   }

   private static boolean isArmorSlot(EquipmentSlot slot) {
      return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
   }

   private static EquipmentSlot[] getArmorSlots() {
      return new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   }

   public static double getAstralBlessingPerLevel() {
      return 20.0;
   }
}
