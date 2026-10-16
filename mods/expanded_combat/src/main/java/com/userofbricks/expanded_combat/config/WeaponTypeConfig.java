package com.userofbricks.expanded_combat.config;

import com.userofbricks.expanded_combat.api.weapon_type.GripType;
import com.userofbricks.expanded_combat.config.gui.ConfigName;
import com.userofbricks.expanded_combat.config.gui.TooltipFrase;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

public class WeaponTypeConfig {
   @ConfigName("Durability Multiplier")
   double durabilityMultiplier;
   @ConfigName("Base Attack Damage")
   @Tooltip
   @TooltipFrase("Material tool damage is added to this")
   int baseAttackDamage;
   @ConfigName("Attack Speed")
   float attackSpeed;
   @ConfigName("Mending Bonus")
   float mendingBonus;
   @ConfigName("Knockback")
   float knockback;
   @ConfigName("Added Attack Range")
   @Tooltip
   @TooltipFrase("In Blocks")
   double attackRange;
   @ConfigName("Grip Type")
   GripType gripType;

   public WeaponTypeConfig(
      double durabilityMultiplier, int baseAttackDamage, float attackSpeed, float mendingBonus, float knockback, double attackRange, GripType gripType
   ) {
      this.durabilityMultiplier = durabilityMultiplier;
      this.baseAttackDamage = baseAttackDamage;
      this.attackSpeed = attackSpeed;
      this.mendingBonus = mendingBonus;
      this.knockback = knockback;
      this.attackRange = attackRange;
      this.gripType = gripType;
   }

   public double durabilityMultiplier() {
      return this.durabilityMultiplier;
   }

   public int baseAttackDamage() {
      return this.baseAttackDamage;
   }

   public float attackSpeed() {
      return this.attackSpeed;
   }

   public float mendingBonus() {
      return this.mendingBonus;
   }

   public float knockback() {
      return this.knockback;
   }

   public double attackRange() {
      return this.attackRange;
   }

   public GripType gripType() {
      return this.gripType;
   }

   public static class Builder {
      public boolean hasLargeModel = false;
      private double durabilityMultiplier = 1.0;
      private int baseAttackDamage = 0;
      private float attackSpeed = 0.0F;
      private float mendingBonus = 0.0F;
      private float knockback = 0.0F;
      private double attackRange = 0.0;
      private final GripType gripType;

      public Builder(GripType gripType) {
         this.gripType = gripType;
      }

      public WeaponTypeConfig.Builder durabilityMultiplier(double multiplier) {
         this.durabilityMultiplier = multiplier;
         return this;
      }

      public WeaponTypeConfig.Builder baseAttackDamage(int damage) {
         this.baseAttackDamage = damage;
         return this;
      }

      public WeaponTypeConfig.Builder attackSpeed(float speed) {
         this.attackSpeed = speed;
         return this;
      }

      public WeaponTypeConfig.Builder mendingBonus(float bonus) {
         this.mendingBonus = bonus;
         return this;
      }

      public WeaponTypeConfig.Builder knockback(float knockback) {
         this.knockback = knockback;
         return this;
      }

      public WeaponTypeConfig.Builder attackRange(double range) {
         this.attackRange = range;
         return this;
      }

      public WeaponTypeConfig.Builder hasLargeModel() {
         this.hasLargeModel = true;
         return this;
      }

      public WeaponTypeConfig build() {
         return new WeaponTypeConfig(
            this.durabilityMultiplier, this.baseAttackDamage, this.attackSpeed, this.mendingBonus, this.knockback, this.attackRange, this.gripType
         );
      }
   }
}
