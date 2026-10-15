package com.chadate.spellelemental.event.physical;

import com.chadate.spellelemental.util.PhysicalDamageCalculator;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

public class PhysicalEventHandler {
   public static void applyPhysicalBonus(Pre event) {
      if (!event.getEntity().level().isClientSide()) {
         LivingEntity target = event.getEntity();
         DamageSource source = event.getSource();
         float originalDamage = event.getNewDamage();
         LivingEntity attacker = null;
         if (source.getEntity() instanceof LivingEntity livingAttacker) {
            attacker = livingAttacker;
         }

         PhysicalDamageCalculator.PhysicalDamageResult result = PhysicalDamageCalculator.calculateDamage(originalDamage, attacker, target, source);
         if (result.wasModified()) {
            event.setNewDamage(result.finalDamage());
         }
      }
   }
}
