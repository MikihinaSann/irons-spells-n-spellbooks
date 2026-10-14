package com.gametechbc.traveloptics.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TOSounds {
   private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, "traveloptics");
   public static DeferredHolder<SoundEvent, SoundEvent> ELDRITCH_ABYSSAMORPH = registerSoundEvent("eldritch_abyssamorph");
   public static DeferredHolder<SoundEvent, SoundEvent> ANNIHILATION_CHARGE = registerSoundEvent("annihilation_charge");
   public static DeferredHolder<SoundEvent, SoundEvent> ORBITAL_VOID_PULSE = registerSoundEvent("orbital_void_pulse");
   public static DeferredHolder<SoundEvent, SoundEvent> ABYSSAL_STRIKE_TRIGGER = registerSoundEvent("abyssal_strike_trigger");
   public static DeferredHolder<SoundEvent, SoundEvent> AERIAL_COLLAPSE = registerSoundEvent("aerial_collapse");
   public static DeferredHolder<SoundEvent, SoundEvent> SPECTRAL_BLINK_FAILED = registerSoundEvent("spectral_blink");
   public static DeferredHolder<SoundEvent, SoundEvent> SPECTRAL_BLINK_CHARGE = registerSoundEvent("spectral_blink_charge");
   public static DeferredHolder<SoundEvent, SoundEvent> SPECTRAL_BLINK_SUCCESS = registerSoundEvent("spectral_blink_success");
   public static DeferredHolder<SoundEvent, SoundEvent> REFINED_ABYSSAL_BLAST_CHARGE = registerSoundEvent("refined_abyss_blast_charge");
   public static DeferredHolder<SoundEvent, SoundEvent> REFINED_ABYSSAL_BLAST_SHOOT = registerSoundEvent("refined_abyssal_blast_shoot");
   public static DeferredHolder<SoundEvent, SoundEvent> REFINED_DEATH_LASER_SHOOT = registerSoundEvent("refined_death_laser_shoot");
   public static DeferredHolder<SoundEvent, SoundEvent> MANIFESTATION_CHANGE = registerSoundEvent("manifestation");
   public static DeferredHolder<SoundEvent, SoundEvent> NULLFLARE = registerSoundEvent("nullflare");
   public static DeferredHolder<SoundEvent, SoundEvent> NULLFLARE_TWO = registerSoundEvent("nullflare_two");
   public static DeferredHolder<SoundEvent, SoundEvent> SPIRIT_POINTS_MAX = registerSoundEvent("spirit_points_max");
   public static DeferredHolder<SoundEvent, SoundEvent> REVERSAL_TRIGGER = registerSoundEvent("reversal_trigger");
   public static DeferredHolder<SoundEvent, SoundEvent> REVERSAL = registerSoundEvent("reversal");
   public static DeferredHolder<SoundEvent, SoundEvent> REFINED_REVENANT_BREATH = registerSoundEvent("refined_revenant_breath");
   public static DeferredHolder<SoundEvent, SoundEvent> BLAST_STAGE_ONE = registerSoundEvent("blast_stage_one");
   public static DeferredHolder<SoundEvent, SoundEvent> BLAST_STAGE_TWO = registerSoundEvent("blast_stage_two");
   public static DeferredHolder<SoundEvent, SoundEvent> BLAST_STAGE_THREE = registerSoundEvent("blast_stage_three");
   public static DeferredHolder<SoundEvent, SoundEvent> SPIRIT_POINTS_THRESHOLD_ONE = registerSoundEvent("spirit_threshold_one");
   public static DeferredHolder<SoundEvent, SoundEvent> SPIRIT_POINTS_THRESHOLD_TWO = registerSoundEvent("spirit_threshold_two");
   public static DeferredHolder<SoundEvent, SoundEvent> CURSED_BLAST_CHARGE = registerSoundEvent("cursed_blast_charge");
   public static DeferredHolder<SoundEvent, SoundEvent> NIGHTWARDEN_START = registerSoundEvent("nightwarden_start");
   public static DeferredHolder<SoundEvent, SoundEvent> NIGHTWARDEN_END = registerSoundEvent("nightwarden_end");
   public static DeferredHolder<SoundEvent, SoundEvent> EEK = registerSoundEvent("eek");
   public static DeferredHolder<SoundEvent, SoundEvent> ENSNARE_PREPARE = registerSoundEvent("ensnare_prepare");
   public static DeferredHolder<SoundEvent, SoundEvent> ENSNARE_TRIGGER = registerSoundEvent("ensnare_trigger");
   public static DeferredHolder<SoundEvent, SoundEvent> BANISH_CHARGE = registerSoundEvent("banish_charge");
   public static DeferredHolder<SoundEvent, SoundEvent> BANISH_CAST = registerSoundEvent("banish_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> LASER_SHOOT = registerSoundEvent("laser_shoot");
   public static DeferredHolder<SoundEvent, SoundEvent> LASER_CHARGE = registerSoundEvent("laser_charge");
   public static DeferredHolder<SoundEvent, SoundEvent> TECTONIC_RIFT_ERUPTION = registerSoundEvent("tectonic_rift_eruption");
   public static DeferredHolder<SoundEvent, SoundEvent> EXTINCTION_CAST = registerSoundEvent("extinction_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> EXTINCTION_FINISH = registerSoundEvent("extinction_finish");
   public static DeferredHolder<SoundEvent, SoundEvent> EXTINCTION_START = registerSoundEvent("extinction_start");
   public static DeferredHolder<SoundEvent, SoundEvent> REFINED_ABYSS_BLAST_PORTAL = registerSoundEvent("refined_abyss_blast_portal");
   public static DeferredHolder<SoundEvent, SoundEvent> ASTEROID_LOOP = registerSoundEvent("asteroid_loop");
   public static DeferredHolder<SoundEvent, SoundEvent> DEVASTATOR_CANON_TRANSFORM = registerSoundEvent("devastator_canon_transform");
   public static DeferredHolder<SoundEvent, SoundEvent> DEVASTATOR_CANON_TRANSFORM_OVER = registerSoundEvent("devastator_canon_transformation_over");
   public static DeferredHolder<SoundEvent, SoundEvent> DEVASTATOR_BLADE_TRANSFORM = registerSoundEvent("devastator_blade_transformation");
   public static DeferredHolder<SoundEvent, SoundEvent> DEVASTATOR_BLADE_TRANSFORM_OVER = registerSoundEvent("devastator_blade_transformation_over");
   public static DeferredHolder<SoundEvent, SoundEvent> AQUA_VORTEX_CAST = registerSoundEvent("aqua_vortex_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> AQUA_VORTEX_ACTIVE = registerSoundEvent("aqua_vortex_active");
   public static DeferredHolder<SoundEvent, SoundEvent> AQUA_VORTEX_END = registerSoundEvent("aqua_vortex_end");
   public static DeferredHolder<SoundEvent, SoundEvent> HYDROSHOT_IMPACT = registerSoundEvent("hydroshot_impact");
   public static DeferredHolder<SoundEvent, SoundEvent> AQUA_CAST_2 = registerSoundEvent("aqua_cast_2");
   public static DeferredHolder<SoundEvent, SoundEvent> AQUA_MISSILES_CAST = registerSoundEvent("aqua_missiles_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> RAINFALL_ACTIVE = registerSoundEvent("rainfall_active");
   public static DeferredHolder<SoundEvent, SoundEvent> ACID_RAIN_ACTIVE = registerSoundEvent("acid_rain_active");
   public static DeferredHolder<SoundEvent, SoundEvent> TIDAL_GRASP_SMACK = registerSoundEvent("tidal_grasp_smack");
   public static DeferredHolder<SoundEvent, SoundEvent> TIDAL_GRASP_PULL = registerSoundEvent("tidal_grasp_pull");

   public TOSounds() {
      super();
   }

   public static void register(IEventBus eventBus) {
      SOUND_EVENTS.register(eventBus);
   }

   private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
      return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("traveloptics", name)));
   }
}
