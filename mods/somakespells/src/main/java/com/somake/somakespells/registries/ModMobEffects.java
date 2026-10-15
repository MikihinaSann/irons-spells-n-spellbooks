package com.somake.somakespells.registries;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.compat.TunesCompat;
import com.somake.somakespells.config.StartupFeatures;
import com.somake.somakespells.effect.aqua.SubmergedEffect;
import com.somake.somakespells.effect.aqua.WetEffect;
import com.somake.somakespells.effect.blood.BloodCutEffect;
import com.somake.somakespells.effect.blood.BloodRushEffect;
import com.somake.somakespells.effect.blood.CrimsonRootEffect;
import com.somake.somakespells.effect.blood.CursedConnectionEffect;
import com.somake.somakespells.effect.blood.RoseSecretEffect;
import com.somake.somakespells.effect.eldritch.EldritchGambitEffect;
import com.somake.somakespells.effect.ender.EnderCorruptionEffect;
import com.somake.somakespells.effect.evocation.EvocationFortitudeEffect;
import com.somake.somakespells.effect.fire.BurningEffect;
import com.somake.somakespells.effect.fire.CombustionedEffect;
import com.somake.somakespells.effect.fire.DamnedDemomansEffect;
import com.somake.somakespells.effect.fire.InfernalFireEffect;
import com.somake.somakespells.effect.fire.SoulFireEffect;
import com.somake.somakespells.effect.geo.EarthboundEffect;
import com.somake.somakespells.effect.holy.BlessedConnectionEffect;
import com.somake.somakespells.effect.holy.BlessedEffect;
import com.somake.somakespells.effect.holy.CustodiaCaeliBlessingEffect;
import com.somake.somakespells.effect.holy.GuardianConnectionEffect;
import com.somake.somakespells.effect.holy.SacredPhoenixExhaustionEffect;
import com.somake.somakespells.effect.ice.PermafrostEffect;
import com.somake.somakespells.effect.lightning.ElectrocutedEffect;
import com.somake.somakespells.effect.lightning.LightningFieldEffect;
import com.somake.somakespells.effect.lightning.LightningStunEffect;
import com.somake.somakespells.effect.nature.OvergrownEffect;
import com.somake.somakespells.effect.sound.ReverberationEffect;
import com.somake.somakespells.effect.spirit.RedSoulEffect;
import com.somake.somakespells.effect.spirit.RenderRushEffect;
import com.somake.somakespells.effect.spirit.SpectralRondoEffect;
import com.somake.somakespells.effect.spirit.SpiritEmpowermentEffect;
import com.somake.somakespells.effect.spirit.WingedRuinEffect;
import com.somake.somakespells.effect.symmetry.SymmetryEmpowermentEffect;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMobEffects {
   public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, "somakespells");
   public static final DeferredRegister<MobEffect> BORN_IN_CHAOS_MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, "somakespells");
   private static final boolean MOWZIE_LOADED = ModList.get().isLoaded("mowziesmobs");
   private static final boolean ISS_LOADED = MagicFromTheEastCompat.isLoaded();
   private static final boolean LEGENDARY_MONSTERS_LOADED = LegendaryMonstersCompat.isLoaded();
   public static final DeferredHolder<MobEffect, MobEffect> COMBUSTIONED = MOB_EFFECTS.register(
      "combustioned",
      () -> new CombustionedEffect(MobEffectCategory.BENEFICIAL, 16729344)
         .addAttributeModifier(
            AttributeRegistry.FIRE_SPELL_POWER,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_combustioned"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> SOUL_COMBUSTIONED = MOB_EFFECTS.register(
      "soul_combustioned",
      () -> new CombustionedEffect(MobEffectCategory.BENEFICIAL, 65535)
         .addAttributeModifier(
            ModAttributes.SOUL_FIRE_SPELL_POWER,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_soul_combustioned"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> INFERNAL_COMBUSTIONED = BORN_IN_CHAOS_MOB_EFFECTS.register(
      "infernal_combustioned",
      () -> new CombustionedEffect(MobEffectCategory.BENEFICIAL, 3407667)
         .addAttributeModifier(
            ModAttributes.INFERNAL_FIRE_SPELL_POWER,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_infernal_combustioned"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> SUBMERGED = MOB_EFFECTS.register(
      "submerged",
      () -> new SubmergedEffect(MobEffectCategory.BENEFICIAL, 65535)
         .addAttributeModifier(
            ModAttributes.AQUA_SPELL_POWER, ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_submerged"), 0.05F, Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            NeoForgeMod.SWIM_SPEED,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_submerged_swim_speed"),
            0.5,
            Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            Attributes.WATER_MOVEMENT_EFFICIENCY,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_submerged_water_efficiency"),
            0.5,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> OVERGROWN = MOB_EFFECTS.register(
      "overgrown",
      () -> new OvergrownEffect(MobEffectCategory.BENEFICIAL, 65280)
         .addAttributeModifier(
            AttributeRegistry.NATURE_SPELL_POWER,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_overgrown"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> PERMAFROST = MOB_EFFECTS.register(
      "permafrost",
      () -> new PermafrostEffect(MobEffectCategory.BENEFICIAL, 65535)
         .addAttributeModifier(
            AttributeRegistry.ICE_SPELL_POWER,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_permafrost"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> BLESSED = MOB_EFFECTS.register(
      "blessed",
      () -> new BlessedEffect(MobEffectCategory.BENEFICIAL, 16766720)
         .addAttributeModifier(
            AttributeRegistry.HOLY_SPELL_POWER,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_blessed"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> CUSTODIA_CAELI_BLESSING = MOB_EFFECTS.register(
      "custodia_caeli_blessing", () -> new CustodiaCaeliBlessingEffect(MobEffectCategory.BENEFICIAL, 16773800)
   );
   public static final DeferredHolder<MobEffect, MobEffect> SACRED_PHOENIX_EXHAUSTION = StartupFeatures.isSacredPhoenixBlessingEnabled()
      ? MOB_EFFECTS.register("sacred_phoenix_exhaustion", () -> new SacredPhoenixExhaustionEffect(MobEffectCategory.HARMFUL, 13910811))
      : null;
   public static final DeferredHolder<MobEffect, MobEffect> BLESSED_CONNECTION = MOWZIE_LOADED
      ? MOB_EFFECTS.register("blessed_connection", () -> new BlessedConnectionEffect(MobEffectCategory.BENEFICIAL, 16774335))
      : null;
   public static final DeferredHolder<MobEffect, MobEffect> GUARDIAN_CONNECTION = MOWZIE_LOADED
      ? MOB_EFFECTS.register("guardian_connection", () -> new GuardianConnectionEffect(MobEffectCategory.BENEFICIAL, 16241496))
      : null;
   public static final DeferredHolder<MobEffect, MobEffect> CURSED_CONNECTION = MOWZIE_LOADED
      ? MOB_EFFECTS.register("cursed_connection", () -> new CursedConnectionEffect(MobEffectCategory.HARMFUL, 9109504))
      : null;
   public static final DeferredHolder<MobEffect, MobEffect> BLOOD_RUSH = MOB_EFFECTS.register(
      "blood_rush",
      () -> new BloodRushEffect(MobEffectCategory.BENEFICIAL, 9109504)
         .addAttributeModifier(
            AttributeRegistry.BLOOD_SPELL_POWER,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_blood_rush"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> BLOOD_CUT = MOB_EFFECTS.register(
      "blood_cut", () -> new BloodCutEffect(MobEffectCategory.BENEFICIAL, 9109504)
   );
   public static final DeferredHolder<MobEffect, MobEffect> ROSE_SECRET = MOB_EFFECTS.register(
      "rose_secret", () -> new RoseSecretEffect(MobEffectCategory.BENEFICIAL, 8001330)
   );
   public static final DeferredHolder<MobEffect, MobEffect> CRIMSON_ROOT = MOB_EFFECTS.register(
      "crimson_root", () -> new CrimsonRootEffect(MobEffectCategory.HARMFUL, 10358821)
   );
   public static final DeferredHolder<MobEffect, MobEffect> ENDER_CORRUPTION = MOB_EFFECTS.register(
      "ender_corruption",
      () -> new EnderCorruptionEffect(MobEffectCategory.BENEFICIAL, 8388736)
         .addAttributeModifier(
            AttributeRegistry.ENDER_SPELL_POWER,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_ender_corruption"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> SPIRIT_EMPOWERMENT = LEGENDARY_MONSTERS_LOADED
      ? MOB_EFFECTS.register(
         "spirit_empowerment",
         () -> {
            SpiritEmpowermentEffect effect = new SpiritEmpowermentEffect(MobEffectCategory.BENEFICIAL, 65535);
            effect.addAttributeModifier(
               MagicFromTheEastCompat.getSpiritSpellPowerAttributeOrEvocation(),
               ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_spirit_empowerment"),
               0.05F,
               Operation.ADD_MULTIPLIED_TOTAL
            );
            return effect;
         }
      )
      : null;
   public static final DeferredHolder<MobEffect, MobEffect> SYMMETRY_EMPOWERMENT = ISS_LOADED
      ? MOB_EFFECTS.register(
         "symmetry_empowerment",
         () -> {
            SymmetryEmpowermentEffect effect = new SymmetryEmpowermentEffect(MobEffectCategory.BENEFICIAL, 16776960);
            Optional<Reference<Attribute>> attr = MagicFromTheEastCompat.getSymmetrySpellPowerAttribute();
            if (attr.isPresent()) {
               effect.addAttributeModifier(
                  (Holder)attr.get(),
                  ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_symmetry_empowerment"),
                  0.05F,
                  Operation.ADD_MULTIPLIED_TOTAL
               );
            }

            return effect;
         }
      )
      : null;
   public static final DeferredHolder<MobEffect, MobEffect> ELDRITCH_GAMBIT = MOB_EFFECTS.register(
      "eldritch_gambit",
      () -> new EldritchGambitEffect(MobEffectCategory.BENEFICIAL, 4915330)
         .addAttributeModifier(
            AttributeRegistry.ELDRITCH_SPELL_POWER,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_eldritch_gambit"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> EVOCATION_FORTITUDE = MOB_EFFECTS.register(
      "evocation_fortitude",
      () -> new EvocationFortitudeEffect(MobEffectCategory.BENEFICIAL, 8421504)
         .addAttributeModifier(
            AttributeRegistry.EVOCATION_SPELL_POWER,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_evocation_fortitude"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            AttributeRegistry.SUMMON_DAMAGE,
            ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_evocation_fortitude_summon_damage"),
            0.05F,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> REVERBERATION = MOB_EFFECTS.register(
      "reverberation",
      () -> {
         ReverberationEffect effect = new ReverberationEffect(MobEffectCategory.BENEFICIAL, 12632256);
         Optional<Reference<Attribute>> attr = TunesCompat.getMelodySpellPowerAttribute();
         if (attr.isPresent()) {
            effect.addAttributeModifier(
               (Holder)attr.get(), ResourceLocation.fromNamespaceAndPath("somakespells", "mobeffect_reverberation"), 0.05F, Operation.ADD_MULTIPLIED_TOTAL
            );
         }

         return effect;
      }
   );
   public static final DeferredHolder<MobEffect, MobEffect> EARTHBOUND = MOB_EFFECTS.register(
      "earthbound", () -> new EarthboundEffect(MobEffectCategory.BENEFICIAL, 9127187)
   );
   public static final DeferredHolder<MobEffect, MobEffect> LIGHTNING_STUN = MOB_EFFECTS.register(
      "lightning_stun", () -> new LightningStunEffect(MobEffectCategory.HARMFUL, 16766720)
   );
   public static final DeferredHolder<MobEffect, MobEffect> LIGHTNING_FIELD = MOB_EFFECTS.register(
      "lightning_field", () -> new LightningFieldEffect(MobEffectCategory.BENEFICIAL, 49151)
   );
   public static final DeferredHolder<MobEffect, MobEffect> ELECTROCUTED = MOB_EFFECTS.register(
      "electrocuted", () -> new ElectrocutedEffect(MobEffectCategory.HARMFUL, 52945)
   );
   public static final DeferredHolder<MobEffect, MobEffect> RENDER_RUSH = MOB_EFFECTS.register(
      "render_rush", () -> new RenderRushEffect(MobEffectCategory.BENEFICIAL, 8842444)
   );
   public static final DeferredHolder<MobEffect, MobEffect> SPECTRAL_RONDO = LEGENDARY_MONSTERS_LOADED
      ? MOB_EFFECTS.register("spectral_rondo", () -> new SpectralRondoEffect(MobEffectCategory.BENEFICIAL, 59084))
      : null;
   public static final DeferredHolder<MobEffect, MobEffect> WINGED_RUIN = LEGENDARY_MONSTERS_LOADED
      ? MOB_EFFECTS.register("winged_ruin", () -> new WingedRuinEffect(MobEffectCategory.BENEFICIAL, 14686245))
      : null;
   public static final DeferredHolder<MobEffect, MobEffect> RED_SOUL = LEGENDARY_MONSTERS_LOADED
      ? MOB_EFFECTS.register("red_soul", () -> new RedSoulEffect(MobEffectCategory.BENEFICIAL, 12785694))
      : null;
   public static final DeferredHolder<MobEffect, MobEffect> WET = MOB_EFFECTS.register("wet", () -> new WetEffect(MobEffectCategory.NEUTRAL, 255));
   public static final DeferredHolder<MobEffect, MobEffect> BURNING = MOB_EFFECTS.register(
      "burning", () -> new BurningEffect(MobEffectCategory.HARMFUL, 16747520)
   );
   public static final DeferredHolder<MobEffect, MobEffect> SOUL_FIRE = MOB_EFFECTS.register(
      "soul_fire", () -> new SoulFireEffect(MobEffectCategory.HARMFUL, 65535)
   );
   public static final DeferredHolder<MobEffect, MobEffect> INFERNAL_FIRE = BORN_IN_CHAOS_MOB_EFFECTS.register(
      "infernal_fire", () -> new InfernalFireEffect(MobEffectCategory.HARMFUL, 7844643)
   );
   public static final DeferredHolder<MobEffect, MobEffect> DAMNED_DEMOMANS = MOB_EFFECTS.register(
      "damned_demomans", () -> new DamnedDemomansEffect(MobEffectCategory.BENEFICIAL, 16729344)
   );

   public static void register(IEventBus eventBus) {
      MOB_EFFECTS.register(eventBus);
   }

   public static void registerBornInChaos(IEventBus eventBus) {
      BORN_IN_CHAOS_MOB_EFFECTS.register(eventBus);
   }

   public static MobEffect infernalCombustionedOr(MobEffect fallback) {
      return BornInChaosCompat.isLoaded() ? (MobEffect)INFERNAL_COMBUSTIONED.get() : fallback;
   }

   public static MobEffect infernalFireOr(MobEffect fallback) {
      return BornInChaosCompat.isLoaded() ? (MobEffect)INFERNAL_FIRE.get() : fallback;
   }
}
