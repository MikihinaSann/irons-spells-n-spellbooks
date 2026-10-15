package com.chadate.spellelemental.util;

import com.chadate.spellelemental.register.ModAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class AttributeModifierUtil {
   public static final ResourceLocation PHYSICAL_RESISTANCE_DOWN_ID = ResourceLocation.fromNamespaceAndPath("spellelemental", "physical_resistance_down");

   public static void applyPhysicalResistanceDown(LivingEntity entity, double amount, int duration) {
      AttributeInstance instance = entity.getAttribute(ModAttributes.PHYSICAL_DAMAGE_RESIST);
      if (instance != null) {
         if (amount > 0.0) {
            amount = -amount;
         }

         removePhysicalResistanceDown(entity);
         AttributeModifier modifier = new AttributeModifier(PHYSICAL_RESISTANCE_DOWN_ID, amount, Operation.ADD_MULTIPLIED_BASE);
         instance.addPermanentModifier(modifier);
         if (duration > 0) {
         }
      }
   }

   public static void removePhysicalResistanceDown(LivingEntity entity) {
      AttributeInstance instance = entity.getAttribute(ModAttributes.PHYSICAL_DAMAGE_RESIST);
      if (instance != null) {
         instance.removeModifier(PHYSICAL_RESISTANCE_DOWN_ID);
      }
   }

   public static boolean hasPhysicalResistanceDown(LivingEntity entity) {
      AttributeInstance instance = entity.getAttribute(ModAttributes.PHYSICAL_DAMAGE_RESIST);
      return instance == null ? false : instance.getModifier(PHYSICAL_RESISTANCE_DOWN_ID) != null;
   }

   public static double getPhysicalResistanceDownAmount(LivingEntity entity) {
      AttributeInstance instance = entity.getAttribute(ModAttributes.PHYSICAL_DAMAGE_RESIST);
      if (instance == null) {
         return 0.0;
      } else {
         AttributeModifier modifier = instance.getModifier(PHYSICAL_RESISTANCE_DOWN_ID);
         return modifier != null ? Math.abs(modifier.amount()) : 0.0;
      }
   }

   public static void applyTemporaryPhysicalResistanceDown(LivingEntity entity, double percentage) {
      percentage = Math.max(0.0, Math.min(1.0, percentage));
      double modifierAmount = -percentage;
      applyPhysicalResistanceDown(entity, modifierAmount, -1);
   }
}
