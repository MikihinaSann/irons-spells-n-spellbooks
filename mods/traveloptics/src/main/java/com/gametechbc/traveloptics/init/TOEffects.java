package com.gametechbc.traveloptics.init;

import com.gametechbc.traveloptics.effects.AerialCollapseEffect;
import com.gametechbc.traveloptics.effects.AerialCollapseHelperEffect;
import com.gametechbc.traveloptics.effects.AstralPathRevealEffect;
import com.gametechbc.traveloptics.effects.AstralSenseEffect;
import com.gametechbc.traveloptics.effects.AstralSenseTreasureEffect;
import com.gametechbc.traveloptics.effects.BlackoutEffect;
import com.gametechbc.traveloptics.effects.FlareVacuumEffect;
import com.gametechbc.traveloptics.effects.MeteorStormEffect;
import com.gametechbc.traveloptics.effects.OrbitalVoidEffect;
import com.gametechbc.traveloptics.effects.OverloadedEffect;
import com.gametechbc.traveloptics.effects.PsychicControlEffect;
import com.gametechbc.traveloptics.effects.SpectralBlinkEffect;
import com.gametechbc.traveloptics.effects.ThirdPersonSwitchEffect;
import com.gametechbc.traveloptics.effects.VortexPunchEffect;
import com.gametechbc.traveloptics.effects.casting.CastingEffect;
import com.gametechbc.traveloptics.effects.frozen_sight.FrozenSightEffect;
import com.gametechbc.traveloptics.effects.lingering_strain.LingeringStrainEffect;
import com.gametechbc.traveloptics.effects.nullflare.NullflareFireEffect;
import com.gametechbc.traveloptics.effects.nullflare.NullflareIceEffect;
import com.gametechbc.traveloptics.effects.reversal.ReversalEffect;
import io.redspace.ironsspellbooks.effect.SummonTimer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TOEffects {
   public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, "traveloptics");
   public static final DeferredHolder<MobEffect, MobEffect> BLACKOUT = MOB_EFFECTS.register("blackout", BlackoutEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> ORBITAL_VOID_EFFECT = MOB_EFFECTS.register("orbital_void", OrbitalVoidEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> OVERLOADED_EFFECT = MOB_EFFECTS.register("overloaded", OverloadedEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> FROZEN_SIGHT_EFFECT = MOB_EFFECTS.register("frozen_sight", FrozenSightEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> METEOR_STORM_EFFECT = MOB_EFFECTS.register("meteor_storm", MeteorStormEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> CASTING_EFFECT = MOB_EFFECTS.register("casting", CastingEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> FLARE_VACUUM_EFFECT = MOB_EFFECTS.register("flare_vacuum", FlareVacuumEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> VORTEX_PUNCH = MOB_EFFECTS.register("vortex_punch", VortexPunchEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> THIRD_PERSON_SWITCH = MOB_EFFECTS.register("third_person_switch", ThirdPersonSwitchEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> AERIAL_COLLAPSE = MOB_EFFECTS.register("aerial_collapse", AerialCollapseEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> AERIAL_COLLAPSE_HELPER = MOB_EFFECTS.register(
      "aerial_collapse_helper", AerialCollapseHelperEffect::new
   );
   public static final DeferredHolder<MobEffect, MobEffect> PSYCHIC_CONTROL = MOB_EFFECTS.register("psychic_control", PsychicControlEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> REVERSAL = MOB_EFFECTS.register("reversal", ReversalEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> SPECTRAL_BLINK = MOB_EFFECTS.register("spectral_blink", SpectralBlinkEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> ASTRAL_PATH_REVEAL = MOB_EFFECTS.register("astral_path_reveal", AstralPathRevealEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> ASTRAL_SENSE = MOB_EFFECTS.register("astral_sense", AstralSenseEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> ASTRAL_SENSE_TREASURE = MOB_EFFECTS.register(
      "astral_sense_treasure", AstralSenseTreasureEffect::new
   );
   public static final DeferredHolder<MobEffect, MobEffect> LINGERING_STRAIN = MOB_EFFECTS.register("lingering_strain", LingeringStrainEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> NULLFLARE_ICE = MOB_EFFECTS.register("nullflare_ice", NullflareIceEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> NULLFLARE_FIRE = MOB_EFFECTS.register("nullflare_fire", NullflareFireEffect::new);
   public static final DeferredHolder<MobEffect, SummonTimer> DESERT_DWELLER_TIMER = MOB_EFFECTS.register(
      "desert_dweller_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> KOBOLEDIATOR_TIMER = MOB_EFFECTS.register(
      "kobolediator_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> MAGNETRON_TIMER = MOB_EFFECTS.register(
      "magnetron_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> MECHANIZED_PREDATOR_TIMER = MOB_EFFECTS.register(
      "mechanized_predator_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> GUM_WORM_TIMER = MOB_EFFECTS.register(
      "gum_worm_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> FORSAKEN_TIMER = MOB_EFFECTS.register(
      "forsaken_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> PRIMAL_PACK_TIMER = MOB_EFFECTS.register(
      "primal_pack_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> ATLATITAN_TIMER = MOB_EFFECTS.register(
      "polar_bear_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> VESPER_TIMER = MOB_EFFECTS.register(
      "vesper_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> IGNITED_ONSLAUGHT_TIMER = MOB_EFFECTS.register(
      "ignited_onslaught_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> ENDER_GOLEM_TIMER = MOB_EFFECTS.register(
      "ender_golem_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> CURSED_REVENANTS_TIMER = MOB_EFFECTS.register(
      "cursed_revenants_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> APTRGANGR_TIMER = MOB_EFFECTS.register(
      "aptrgangr_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );
   public static final DeferredHolder<MobEffect, SummonTimer> HULLBREAKER_TIMER = MOB_EFFECTS.register(
      "hullbreaker_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 12495141)
   );

   public TOEffects() {
      super();
   }

   public static void register(IEventBus eventBus) {
      MOB_EFFECTS.register(eventBus);
   }
}
