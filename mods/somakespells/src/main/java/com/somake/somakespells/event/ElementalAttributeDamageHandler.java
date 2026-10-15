package com.somake.somakespells.event;

import com.somake.somakespells.registries.ModAttributes;
import com.somake.somakespells.registries.ModDamageTypes;
import dev.shadowsoffire.apothic_attributes.util.AttributesUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

public final class ElementalAttributeDamageHandler {
   private static final float MIN_DAMAGE = 0.001F;

   private ElementalAttributeDamageHandler() {
   }

   public static LivingEntity getEligibleAttacker(LivingEntity target, DamageSource source, float damage) {
      if (target.level().isClientSide() || damage <= 0.001F || target.invulnerableTime > 10) {
         return null;
      } else if (!source.is(ModDamageTypes.FIRE_DAMAGE) && !source.is(ModDamageTypes.ICE_DAMAGE) && AttributesUtil.isPhysicalDamage(source)) {
         return source.getDirectEntity() instanceof LivingEntity attacker ? attacker : null;
      } else {
         return null;
      }
   }

   public static void applyAuxiliaryDamages(LivingEntity attacker, LivingEntity target) {
      applyAuxiliaryDamage(
         attacker,
         target,
         getAttributeValueOrZero(attacker, ModAttributes.FIRE_DAMAGE),
         ModDamageTypes.FIRE_DAMAGE,
         ElementalAttributeDamageHandler::applyPostFireDamage
      );
      applyAuxiliaryDamage(
         attacker,
         target,
         getAttributeValueOrZero(attacker, ModAttributes.ICE_DAMAGE),
         ModDamageTypes.ICE_DAMAGE,
         ElementalAttributeDamageHandler::applyPostIceDamage
      );
   }

   private static void applyAuxiliaryDamage(
      LivingEntity attacker,
      LivingEntity target,
      float bonusDamage,
      ResourceKey<DamageType> damageType,
      ElementalAttributeDamageHandler.PostHitEffect postHitEffect
   ) {
      if (!(bonusDamage <= 0.001F) && !target.isDeadOrDying()) {
         float scaledBonusDamage = scaleWithAttackStrength(attacker, bonusDamage);
         if (!(scaledBonusDamage <= 0.001F)) {
            target.invulnerableTime = 0;
            if (target.hurt(attacker.damageSources().source(damageType, attacker), scaledBonusDamage)) {
               postHitEffect.apply(attacker, target, scaledBonusDamage);
            }
         }
      }
   }

   private static float scaleWithAttackStrength(LivingEntity attacker, float baseDamage) {
      if (attacker instanceof Player player) {
         float attackStrength = player.getAttackStrengthScale(0.5F);
         float multiplier = 0.2F + attackStrength * attackStrength * 0.8F;
         return baseDamage * multiplier;
      } else {
         return baseDamage;
      }
   }

   private static float getAttributeValueOrZero(LivingEntity entity, Holder<Attribute> attribute) {
      if (entity != null && attribute != null) {
         AttributeInstance attributeInstance = entity.getAttribute(attribute);
         return attributeInstance != null ? (float)attributeInstance.getValue() : 0.0F;
      } else {
         return 0.0F;
      }
   }

   private static void applyPostFireDamage(LivingEntity attacker, LivingEntity target, float damageDealt) {
      target.setRemainingFireTicks(target.getRemainingFireTicks() + (int)(10.0F * damageDealt));
   }

   private static void applyPostIceDamage(LivingEntity attacker, LivingEntity target, float damageDealt) {
      int duration = (int)Math.min(150.0F, 15.0F * damageDealt);
      int amp = Math.max(0, Mth.log2(Math.max(1, Math.round(damageDealt / 5.0F))));
      target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amp));
   }

   @FunctionalInterface
   private interface PostHitEffect {
      void apply(LivingEntity var1, LivingEntity var2, float var3);
   }
}
