package com.somake.somakespells.util;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.config.Config;
import com.somake.somakespells.item.ElementalChargeNecklaceItem;
import com.somake.somakespells.registries.ModAttributes;
import com.somake.somakespells.registries.ModCompatItems;
import com.somake.somakespells.registries.ModItems;
import com.somake.somakespells.ritual.RitualElementalMode;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record FireElementContext(RitualElementalMode mode, boolean fromHeldItem) {
   private static final float ELEMENTAL_ATTRIBUTE_DAMAGE_SCALING = 1.0F;

   public boolean isSoulFire() {
      return this.mode == RitualElementalMode.SOUL_FIRE;
   }

   public boolean isInfernalFire() {
      return this.mode == RitualElementalMode.INFERNAL_FIRE;
   }

   public boolean isNormalFire() {
      return this.mode == RitualElementalMode.NORMAL;
   }

   public static FireElementContext resolve(LivingEntity entity) {
      if (entity == null) {
         return new FireElementContext(RitualElementalMode.NORMAL, false);
      } else {
         FireElementContext heldContext = resolveFromHeldItems(entity);
         if (heldContext != null) {
            return heldContext;
         } else {
            FireElementContext necklaceContext = resolveFromNecklaces(entity);
            return necklaceContext != null ? necklaceContext : new FireElementContext(RitualElementalMode.NORMAL, false);
         }
      }
   }

   public static FireElementContext resolveForSpell(LivingEntity entity, AbstractSpell spell) {
      return resolveForSchool(entity, spell != null ? spell.getSchoolType() : null);
   }

   public static FireElementContext resolveForSchool(LivingEntity entity, SchoolType schoolType) {
      return !isFireSchool(schoolType) ? new FireElementContext(RitualElementalMode.NORMAL, false) : resolve(entity);
   }

   public static boolean isFireSchool(SchoolType schoolType) {
      return schoolType != null && SchoolRegistry.FIRE_RESOURCE.equals(schoolType.getId());
   }

   private static FireElementContext resolveFromHeldItems(LivingEntity entity) {
      FireElementContext mainHand = resolveFromStack(entity.getMainHandItem());
      return mainHand != null ? mainHand : resolveFromStack(entity.getOffhandItem());
   }

   private static FireElementContext resolveFromStack(ItemStack stack) {
      Item item = stack.getItem();
      if (ModCompatItems.isInfernalFireItem(item)) {
         return new FireElementContext(RitualElementalMode.INFERNAL_FIRE, true);
      } else if (item == ModItems.SOUL_FIRE.get()) {
         return new FireElementContext(RitualElementalMode.SOUL_FIRE, true);
      } else {
         return item == ModItems.FIRE.get() ? new FireElementContext(RitualElementalMode.NORMAL, true) : null;
      }
   }

   private static FireElementContext resolveFromNecklaces(LivingEntity entity) {
      if (BornInChaosCompat.isLoaded()
         && ElementalChargeNecklaceItem.findChargedEquippedStack(entity, (Item)ModCompatItems.INFERNAL_FIRE_NECKLACE.get()).isPresent()) {
         return new FireElementContext(RitualElementalMode.INFERNAL_FIRE, false);
      } else {
         return ElementalChargeNecklaceItem.findChargedEquippedStack(entity, ModItems.SOUL_FIRE_NECKLACE.get()).isPresent()
            ? new FireElementContext(RitualElementalMode.SOUL_FIRE, false)
            : null;
      }
   }

   public float getSpellPowerBonus(LivingEntity entity) {
      if (entity == null) {
         return 0.0F;
      } else {
         return switch (this.mode) {
            case SOUL_FIRE -> (getAttributeValueOrDefault(entity, ModAttributes.SOUL_FIRE_SPELL_POWER, 1.0F) - 1.0F) * 10.0F;
            case INFERNAL_FIRE -> BornInChaosCompat.isLoaded()
               ? (getAttributeValueOrDefault(entity, ModAttributes.INFERNAL_FIRE_SPELL_POWER, 1.0F) - 1.0F) * 10.0F
               : 0.0F;
            default -> 0.0F;
         };
      }
   }

   public float getSupportedDamageMultiplier(LivingEntity entity) {
      if (entity == null) {
         return 1.0F;
      } else {
         return switch (this.mode) {
            case SOUL_FIRE -> 1.0F + getSoulFireBaseDamageBonus() + getElementalAttributeBonusPercent(entity, ModAttributes.SOUL_FIRE_SPELL_POWER) * 1.0F;
            case INFERNAL_FIRE -> BornInChaosCompat.isLoaded()
               ? 1.0F + getInfernalFireBaseDamageBonus() + getElementalAttributeBonusPercent(entity, ModAttributes.INFERNAL_FIRE_SPELL_POWER) * 1.0F
               : 1.0F;
            default -> 1.0F;
         };
      }
   }

   public float applySupportedDamage(float baseDamage, LivingEntity entity) {
      return baseDamage * this.getSupportedDamageMultiplier(entity);
   }

   private static float getSoulFireBaseDamageBonus() {
      return (float)((Double)Config.SOUL_FIRE_BASE_DAMAGE_BONUS_PERCENT.get() / 100.0);
   }

   private static float getInfernalFireBaseDamageBonus() {
      return (float)((Double)Config.INFERNAL_FIRE_BASE_DAMAGE_BONUS_PERCENT.get() / 100.0);
   }

   private static float getElementalAttributeBonusPercent(LivingEntity entity, Holder<Attribute> attribute) {
      return Math.max(0.0F, getAttributeValueOrDefault(entity, attribute, 1.0F) - 1.0F);
   }

   private static float getAttributeValueOrDefault(LivingEntity entity, Holder<Attribute> attribute, float fallback) {
      if (entity != null && attribute != null) {
         AttributeInstance attributeInstance = entity.getAttribute(attribute);
         return attributeInstance != null ? (float)attributeInstance.getValue() : fallback;
      } else {
         return fallback;
      }
   }
}
