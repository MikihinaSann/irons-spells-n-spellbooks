package com.somake.somakespells.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

public class EffectBalanceConfig {
   private static final Builder BUILDER = new Builder();
   public static final BooleanValue MODIFY_VANILLA_SPEED_EFFECT = BUILDER.comment(
         "If true, overrides vanilla minecraft:speed scaling to 10% per level (I=10%, II=20%, III=30%)."
      )
      .define("modifyVanillaSpeedEffect", false);
   public static final BooleanValue REMOVE_BORN_IN_CHAOS_RAMPAGE_SPEED = BUILDER.comment(
         "If true, removes MOVEMENT_SPEED from Born In Chaos effects: light/medium/strong/furious/rampant rampage and obsession."
      )
      .define("removeBornInChaosRampageSpeed", false);
   public static final DoubleValue BORN_IN_CHAOS_LIGHT_RAMPAGE_ATTACK_DAMAGE = BUILDER.comment("Attack damage bonus for born_in_chaos_v1:light_rampage.")
      .defineInRange("bornInChaosLightRampageAttackDamage", 3.0, -1000.0, 1000.0);
   public static final DoubleValue BORN_IN_CHAOS_MEDIUM_RAMPAGE_ATTACK_DAMAGE = BUILDER.comment("Attack damage bonus for born_in_chaos_v1:medium_rampage.")
      .defineInRange("bornInChaosMediumRampageAttackDamage", 5.0, -1000.0, 1000.0);
   public static final DoubleValue BORN_IN_CHAOS_STRONG_RAMPAGE_ATTACK_DAMAGE = BUILDER.comment("Attack damage bonus for born_in_chaos_v1:strong_rampage.")
      .defineInRange("bornInChaosStrongRampageAttackDamage", 8.0, -1000.0, 1000.0);
   public static final DoubleValue BORN_IN_CHAOS_FURIOUS_RAMPAGE_ATTACK_DAMAGE = BUILDER.comment("Attack damage bonus for born_in_chaos_v1:furious_rampage.")
      .defineInRange("bornInChaosFuriousRampageAttackDamage", 10.0, -1000.0, 1000.0);
   public static final DoubleValue BORN_IN_CHAOS_RAMPANT_RAMPAGE_ATTACK_DAMAGE = BUILDER.comment("Attack damage bonus for born_in_chaos_v1:rampant_rampage.")
      .defineInRange("bornInChaosRampantRampageAttackDamage", 15.0, -1000.0, 1000.0);
   public static final BooleanValue ADJUST_BORN_IN_CHAOS_RABBIT_AGILITY = BUILDER.comment(
         "If true, sets born_in_chaos_v1:rabbit_agility to +50% movement speed and -50% attack damage."
      )
      .define("adjustBornInChaosRabbitAgility", false);
   public static final BooleanValue REMOVE_BORN_IN_CHAOS_PREDATORS_DESIRE_SPEED = BUILDER.comment(
         "If true, removes MOVEMENT_SPEED from born_in_chaos_v1:predators_desire."
      )
      .define("removeBornInChaosPredatorsDesireSpeed", false);
   public static final BooleanValue MODIFY_BORN_IN_CHAOS_STONE_PERSISTENCE = BUILDER.comment(
         "If true, stone_persistence no longer applies Resistance IV every tick, and gains +5 Armor attribute."
      )
      .define("modifyBornInChaosStonePersistence", false);
   public static final BooleanValue REDUCE_TRANSFORMING_EASTER_CAKE_DURATION = BUILDER.comment(
         "If true, born_in_chaos_v1:transforming_easter_cake effects last 1-2 minutes instead of 10-30 minutes."
      )
      .define("reduceTransformingEasterCakeDuration", false);
   public static final BooleanValue REMOVE_BORN_IN_CHAOS_STIMULATION_SPEED = BUILDER.comment(
         "If true, removes MOVEMENT_SPEED from born_in_chaos_v1:stimulation."
      )
      .define("removeBornInChaosStimulationSpeed", false);
   public static final DoubleValue BORN_IN_CHAOS_STIMULATION_ATTACK_SPEED_PER_LEVEL = BUILDER.comment(
         "Attack speed bonus per level for born_in_chaos_v1:stimulation."
      )
      .defineInRange("bornInChaosStimulationAttackSpeedPerLevel", 0.25, -10.0, 10.0);
   public static final DoubleValue BORN_IN_CHAOS_OBSESSION_ARMOR_PER_LEVEL = BUILDER.comment("Armor bonus per level for born_in_chaos_v1:obsession.")
      .defineInRange("bornInChaosObsessionArmorPerLevel", 15.0, -1000.0, 1000.0);
   public static final DoubleValue BORN_IN_CHAOS_OBSESSION_ARMOR_TOUGHNESS_PER_LEVEL = BUILDER.comment(
         "Armor toughness bonus per level for born_in_chaos_v1:obsession."
      )
      .defineInRange("bornInChaosObsessionArmorToughnessPerLevel", 5.0, -1000.0, 1000.0);
   public static final DoubleValue BORN_IN_CHAOS_OBSESSION_ATTACK_DAMAGE_PER_LEVEL = BUILDER.comment(
         "Attack damage bonus per level for born_in_chaos_v1:obsession."
      )
      .defineInRange("bornInChaosObsessionAttackDamagePerLevel", 5.0, -1000.0, 1000.0);
   public static final DoubleValue BORN_IN_CHAOS_OBSESSION_ATTACK_SPEED_PER_LEVEL = BUILDER.comment(
         "Attack speed bonus per level for born_in_chaos_v1:obsession."
      )
      .defineInRange("bornInChaosObsessionAttackSpeedPerLevel", 0.35, -10.0, 10.0);
   public static final DoubleValue BORN_IN_CHAOS_OBSESSION_MAX_HEALTH_PER_LEVEL = BUILDER.comment(
         "Max health multiplier per level for born_in_chaos_v1:obsession."
      )
      .defineInRange("bornInChaosObsessionMaxHealthPerLevel", 0.5, -10.0, 10.0);
   public static final DoubleValue BORN_IN_CHAOS_OBSESSION_MOVEMENT_SPEED_PER_LEVEL = BUILDER.comment(
         "Movement speed multiplier per level for born_in_chaos_v1:obsession."
      )
      .defineInRange("bornInChaosObsessionMovementSpeedPerLevel", 0.35, -10.0, 10.0);
   public static final ModConfigSpec SPEC = BUILDER.build();
}
