package com.gametechbc.traveloptics.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

@EventBusSubscriber(
   modid = "traveloptics",
   bus = Bus.MOD
)
public class SpellsConfig {
   private static final Builder BUILDER = new Builder();
   public static final BooleanValue annihilationCauseExplode = BUILDER.comment(
         "If Annihilation spell can cause blocks to break/create explosion || Default: true"
      )
      .define("annihilation.cause_explosion", true);
   public static final BooleanValue annihilationCauseFire = BUILDER.comment(
         "If Annihilation spell can cause fire || Automatically disabled if cause_explosion is disabled || Default: true"
      )
      .define("annihilation.cause_fire", true);
   public static final BooleanValue abyssalBlastBlockBreaking = BUILDER.comment("Should Abyssal Blast break blocks || Default: true")
      .define("abyssal_blast.should_break_blocks", true);
   public static final BooleanValue abyssalBlastDealMagicDamage = BUILDER.comment(
         "Should Abyssal Blast spell deal magic damage (default boss behavior/Ignore armor/less integration with spell power & resist attributes) || Default: false"
      )
      .define("abyssal_blast.should_deal_magic_damage", false);
   public static final BooleanValue deathLaserBlockBreaking = BUILDER.comment("Should DeathLaser break blocks || Default: true")
      .define("death_laser.should_break_blocks", true);
   public static final BooleanValue deathLaserDealMagicDamage = BUILDER.comment(
         "Should DeathLaser spell deal magic damage (default boss behavior/Ignore armor/less integration with spell power & resist attributes) || Default: false"
      )
      .define("death_laser.should_deal_magic_damage", false);
   public static final DoubleValue aerialCollapseDamageReductionModifier = BUILDER.comment(
         "Should bosses take less percent-based damage from Aerial Collapse? || less than 1.0 will make them take less damage || Default 1.0"
      )
      .defineInRange("aerial_collapse.bosses_take_less_damage", 1.0, 0.1, 1.0);
   public static final BooleanValue limitGroupSummons = BUILDER.comment("Limit the number of active 'Group' summon spells to one at a time || Default: true")
      .define("summon.limit_group_summon", true);
   public static final BooleanValue limitKaijuSummons = BUILDER.comment("Limit the number of active 'Kaiju' summon spells to one at a time || Default: true")
      .define("summon.limit_kaiju_summon", true);
   public static final BooleanValue limitMinibossSummons = BUILDER.comment(
         "Limit the number of active 'Miniboss' summon spells to one at a time || Default: true"
      )
      .define("summon.limit_miniboss_summon", true);
   public static final DoubleValue titanlordScepterDamage = BUILDER.comment("Damage || Default 9.0").defineInRange("titanlord_scepter.damage", 9.0, 0.0, 100.0);
   public static final DoubleValue titanlordScepterAttackSpeed = BUILDER.comment("Attack Speed || Default -3.0")
      .defineInRange("titanlord_scepter.attack_speed", -3.0, -5.0, 5.0);
   public static final DoubleValue titanlordScepterSpellPower = BUILDER.comment("Variant Spell Power || MULTIPLY_BASE || Default 0.15")
      .defineInRange("titanlord_scepter.variant_spell_power", 0.15, 0.0, 1.0);
   public static final DoubleValue titanlordScepterCooldownReduction = BUILDER.comment("Cooldown Reduction || MULTIPLY_BASE || Default 0.15")
      .defineInRange("titanlord_scepter.cooldown_reduction", 0.15, 0.0, 1.0);
   public static final DoubleValue titanlordScepterManaRegen = BUILDER.comment("Mana Regen || MULTIPLY_BASE || Default 0.10")
      .defineInRange("titanlord_scepter.mana_regen", 0.1, 0.0, 1.0);
   public static final BooleanValue titanlordScepterGrantRecast = BUILDER.comment(
         "If Titanlord Scepter should grant a recast to Annihilation spell || Default: true"
      )
      .define("titanlord_scepter.grant_annihilation_recast", true);
   public static final IntValue abyssalSecretsSpellSlots = BUILDER.comment("Extra spell slots for Archive of abyssal secrets || Default 10 + 2 built-in")
      .defineInRange("archive_of_abyssal_secrets.extra_slots", 10, 4, Integer.MAX_VALUE);
   public static final DoubleValue abyssalSecretsMaxMana = BUILDER.comment("Max Mana || ADDITION || Default 300.0")
      .defineInRange("archive_of_abyssal_secrets.max_mana", 300.0, 50.0, 1000.0);
   public static final DoubleValue abyssalSecretsEnderSpellPower = BUILDER.comment("Ender Spell Power || MULTIPLY_BASE || Default 0.15")
      .defineInRange("archive_of_abyssal_secrets.ender_spell_power", 0.15, 0.0, 1.0);
   public static final DoubleValue abyssalSecretsEldritchSpellPower = BUILDER.comment("Eldritch Spell Power || MULTIPLY_BASE || Default 0.15")
      .defineInRange("archive_of_abyssal_secrets.eldritch_spell_power", 0.15, 0.0, 1.0);
   public static final IntValue chroniclesFirelordSpellSlots = BUILDER.comment("Extra spell slots for Chronicles of the firelord || Default 10 + 2 built-in")
      .defineInRange("chronicles_of_the_firelord.extra_slots", 10, 3, Integer.MAX_VALUE);
   public static final DoubleValue chroniclesFirelordMaxMana = BUILDER.comment("Max Mana || ADDITION || Default 300.0")
      .defineInRange("chronicles_of_the_firelord.max_mana", 300.0, 50.0, 1000.0);
   public static final DoubleValue chroniclesFirelordFireSpellPower = BUILDER.comment("Fire Spell Power || MULTIPLY_BASE || Default 0.15")
      .defineInRange("chronicles_of_the_firelord.fire_spell_power", 0.15, 0.0, 1.0);
   public static final DoubleValue chroniclesFirelordEldritchSpellPower = BUILDER.comment("Eldritch Spell Power || MULTIPLY_BASE || Default 0.15")
      .defineInRange("chronicles_of_the_firelord.eldritch_spell_power", 0.15, 0.0, 1.0);
   public static final IntValue theAccusedCodexSpellSlots = BUILDER.comment("Extra spell slots for Shellbound || Default 11")
      .defineInRange("the_accused_codex.extra_slots", 11, 1, Integer.MAX_VALUE);
   public static final DoubleValue theAccusedCodexMaxMana = BUILDER.comment("Max Mana || ADDITION || Default 300.0")
      .defineInRange("the_accused_codex.max_mana", 300.0, 50.0, 1000.0);
   public static final DoubleValue theAccusedCodexCooldownReduction = BUILDER.comment("Cooldown Reduction || MULTIPLY_BASE || Default 0.10")
      .defineInRange("the_accused_codex.cooldown_reduction", 0.1, 0.0, 1.0);
   public static final DoubleValue theAccusedCodexIceSpellPower = BUILDER.comment("Ice Spell Power || MULTIPLY_BASE || Default 0.20")
      .defineInRange("the_accused_codex.ice_spell_power", 0.2, 0.0, 1.0);
   public static final IntValue shellboundSpellSlots = BUILDER.comment("Extra spell slots for Shellbound || Default 12")
      .defineInRange("shellbound.extra_slots", 12, 1, Integer.MAX_VALUE);
   public static final DoubleValue shellboundMaxMana = BUILDER.comment("Max Mana || ADDITION || Default 200.0")
      .defineInRange("shellbound.max_mana", 200.0, 50.0, 1000.0);
   public static final DoubleValue shellboundCooldownReduction = BUILDER.comment("Cooldown Reduction || MULTIPLY_BASE || Default 0.10")
      .defineInRange("shellbound.cooldown_reduction", 0.1, 0.0, 1.0);
   public static final DoubleValue shellboundNatureSpellPower = BUILDER.comment("Nature Spell Power || MULTIPLY_BASE || Default 0.10")
      .defineInRange("shellbound.nature_spell_power", 0.1, 0.0, 1.0);
   public static final ModConfigSpec SPEC = BUILDER.build();

   public SpellsConfig() {
      super();
   }

   private static boolean validateItemName(Object obj) {
      return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
   }

   @SubscribeEvent
   static void onLoad(ModConfigEvent event) {
   }
}
