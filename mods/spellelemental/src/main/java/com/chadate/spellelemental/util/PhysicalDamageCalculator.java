package com.chadate.spellelemental.util;

import com.chadate.spellelemental.register.ModAttributes;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.neoforge.common.Tags.DamageTypes;

public class PhysicalDamageCalculator {
   public static PhysicalDamageCalculator.PhysicalDamageResult calculateDamage(
      float originalDamage, LivingEntity attacker, LivingEntity target, DamageSource source
   ) {
      if (!isPhysicalDamage(source)) {
         return new PhysicalDamageCalculator.PhysicalDamageResult(originalDamage, 1.0F, 1.0F, false);
      } else {
         float damageBoost = getAttributeValue(attacker, ModAttributes.PHYSICAL_DAMAGE_BOOST);
         float resistance = getAttributeValue(target, ModAttributes.PHYSICAL_DAMAGE_RESIST);
         float resistanceMultiplier = calculateResistanceMultiplier(resistance);
         float finalDamage = originalDamage * damageBoost * resistanceMultiplier;
         return new PhysicalDamageCalculator.PhysicalDamageResult(finalDamage, damageBoost, resistanceMultiplier, true);
      }
   }

   public static float calculateResistanceMultiplier(float resistance) {
      float delta = resistance - 1.0F;
      if (delta < 0.0F) {
         return Math.min(2.0F, 1.0F - delta * 0.5F);
      } else {
         return delta < 0.75F ? Math.max(0.1F, 1.0F - delta) : Math.max(0.05F, 1.0F / (1.0F + 4.0F * delta));
      }
   }

   private static float getAttributeValue(LivingEntity entity, Holder<Attribute> attribute) {
      if (entity == null) {
         return 1.0F;
      } else {
         AttributeInstance instance = entity.getAttribute(attribute);
         return instance != null ? (float)instance.getValue() : 1.0F;
      }
   }

   private static boolean isPhysicalDamage(DamageSource source) {
      return source.is(DamageTypes.IS_PHYSICAL);
   }

   public record PhysicalDamageResult(float finalDamage, float damageMultiplier, float resistanceMultiplier, boolean wasModified) {
   }
}
