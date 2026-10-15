package com.chadate.spellelemental.event.crit;

import com.chadate.spellelemental.register.ModAttributes;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import net.minecraft.world.entity.LivingEntity;

public class SpellDamageCritHandler {
   public static void applyCritBonus(SpellDamageEvent event) {
      if (event.getSpellDamageSource().getEntity() instanceof LivingEntity attacker) {
         handleCrit(event, attacker);
      }
   }

   private static void handleCrit(SpellDamageEvent event, LivingEntity attacker) {
      if (attacker != null) {
         if (shouldTriggerSpellCrit(attacker)) {
            float critDamage = (float)calculateSpellCritDamage(event.getOriginalAmount(), attacker);
            event.setAmount(critDamage);
         }
      }
   }

   private static boolean shouldTriggerSpellCrit(LivingEntity attacker) {
      double baseCritChance = attacker.getAttributeValue(ModAttributes.SPELL_CRIT_RATE);
      return Math.random() < baseCritChance;
   }

   private static double calculateSpellCritDamage(float originalDamage, LivingEntity attacker) {
      double critMultiplier = attacker.getAttributeValue(ModAttributes.SPELL_CRIT_DAMAGE);
      return originalDamage * (1.0 + critMultiplier);
   }
}
