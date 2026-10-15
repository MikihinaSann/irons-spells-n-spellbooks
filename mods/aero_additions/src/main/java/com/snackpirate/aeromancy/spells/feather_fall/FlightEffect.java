package com.snackpirate.aeromancy.spells.feather_fall;

import com.snackpirate.aeromancy.Aeromancy;
import com.snackpirate.aeromancy.data.AAItemTags;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import java.util.Objects;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;

public class FlightEffect extends MagicMobEffect {
   public FlightEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
      if (pLivingEntity.tickCount % 20 == 0) {
         int weightScore = calculateWeightScore(pLivingEntity);
         AttributeInstance instance = pLivingEntity.getAttributes().getInstance(Attributes.GRAVITY);
         if (instance != null) {
            instance.removeModifier(Aeromancy.id("flight.gravity"));
            if (weightScore != 0) {
               instance.addOrUpdateTransientModifier(new AttributeModifier(Aeromancy.id("flight.gravity"), weightScore / 4.0F - 0.08, Operation.ADD_VALUE));
            }
         }
      }

      return super.applyEffectTick(pLivingEntity, pAmplifier);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
      Objects.requireNonNull(pLivingEntity.getAttribute(Attributes.GRAVITY)).removeModifier(Aeromancy.id("flight.gravity"));
      super.onEffectRemoved(pLivingEntity, pAmplifier);
   }

   private static int calculateWeightScore(LivingEntity living) {
      int totalScore = 0;

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stackInSlot = living.getItemBySlot(slot);
         int itemScore;
         if (stackInSlot.is(AAItemTags.FLIGHT_LIGHT) || !living.hasItemInSlot(slot)) {
            itemScore = 0;
         } else if (stackInSlot.is(AAItemTags.FLIGHT_HEAVY)) {
            itemScore = 4;
         } else if (stackInSlot.is(ItemTags.ANVIL)) {
            itemScore = 10;
         } else {
            itemScore = 2;
         }

         totalScore += itemScore;
      }

      return totalScore;
   }
}
