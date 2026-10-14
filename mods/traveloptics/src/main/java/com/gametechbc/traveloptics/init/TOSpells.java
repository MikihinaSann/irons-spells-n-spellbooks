package com.gametechbc.traveloptics.init;

import com.gametechbc.traveloptics.spells.blood.BloodHowlSpell;
import com.gametechbc.traveloptics.spells.eldritch.AbyssalBlastSpell;
import com.gametechbc.traveloptics.spells.eldritch.BlackoutSpell;
import com.gametechbc.traveloptics.spells.eldritch.PsychicBoltSpell;
import com.gametechbc.traveloptics.spells.eldritch.ReversalSpell;
import com.gametechbc.traveloptics.spells.eldritch.SpectralBlinkSpell;
import com.gametechbc.traveloptics.spells.ender.AstralSenseSpell;
import com.gametechbc.traveloptics.spells.ender.CursedMinefieldSpell;
import com.gametechbc.traveloptics.spells.ender.EternalSentinelSpell;
import com.gametechbc.traveloptics.spells.ender.OrbitalVoidSpell;
import com.gametechbc.traveloptics.spells.ender.VoidEruptionSpell;
import com.gametechbc.traveloptics.spells.ender.VortexPunchSpell;
import com.gametechbc.traveloptics.spells.evocation.AshenBreathSpell;
import com.gametechbc.traveloptics.spells.evocation.LingeringStrainSpell;
import com.gametechbc.traveloptics.spells.fire.BurningJudgmentSpell;
import com.gametechbc.traveloptics.spells.fire.GyroSlashSpell;
import com.gametechbc.traveloptics.spells.fire.IgnitedOnslaughtSpell;
import com.gametechbc.traveloptics.spells.fire.LavaBombSpell;
import com.gametechbc.traveloptics.spells.fire.MeteorStormSpell;
import com.gametechbc.traveloptics.spells.holy.NullflareSpell;
import com.gametechbc.traveloptics.spells.holy.SummonDesertDwellers;
import com.gametechbc.traveloptics.spells.holy.SwordOfTheAncientsSpell;
import com.gametechbc.traveloptics.spells.ice.AxeOfTheDoomedSpell;
import com.gametechbc.traveloptics.spells.ice.CursedBlastSpell;
import com.gametechbc.traveloptics.spells.ice.CursedRevenantsSpell;
import com.gametechbc.traveloptics.spells.ice.DespairSpell;
import com.gametechbc.traveloptics.spells.ice.HalberdHorizonSpell;
import com.gametechbc.traveloptics.spells.lightning.DeathLaserSpell;
import com.gametechbc.traveloptics.spells.lightning.EmPulse;
import com.gametechbc.traveloptics.spells.lightning.MechanizedPredatorSpell;
import com.gametechbc.traveloptics.spells.lightning.RapidLaserSpell;
import com.gametechbc.traveloptics.spells.nature.AerialCollapseSpell;
import com.gametechbc.traveloptics.spells.nature.SteleCascadeSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TOSpells {
   public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "traveloptics");
   public static final Supplier<AbstractSpell> BLOOD_HOWL_SPELL = registerSpell(new BloodHowlSpell());
   public static final Supplier<AbstractSpell> ABYSSAL_BLAST_SPELL = registerSpell(new AbyssalBlastSpell());
   public static final Supplier<AbstractSpell> BLACKOUT_SPELL = registerSpell(new BlackoutSpell());
   public static final Supplier<AbstractSpell> PSYCHIC_BOLT_SPELL = registerSpell(new PsychicBoltSpell());
   public static final Supplier<AbstractSpell> REVERSAL_SPELL = registerSpell(new ReversalSpell());
   public static final Supplier<AbstractSpell> SPECTRAL_BLINK = registerSpell(new SpectralBlinkSpell());
   public static final Supplier<AbstractSpell> ETERNAL_SENTINEL_SPELL = registerSpell(new EternalSentinelSpell());
   public static final Supplier<AbstractSpell> ORBITAL_VOID_SPELL = registerSpell(new OrbitalVoidSpell());
   public static final Supplier<AbstractSpell> CURSED_MINEFIELD_SPELL = registerSpell(new CursedMinefieldSpell());
   public static final Supplier<AbstractSpell> VOID_ERUPTION_SPELL = registerSpell(new VoidEruptionSpell());
   public static final Supplier<AbstractSpell> VORTEX_PUNCH_SPELL = registerSpell(new VortexPunchSpell());
   public static final Supplier<AbstractSpell> ASTRAL_SENSE_SPELL = registerSpell(new AstralSenseSpell());
   public static final Supplier<AbstractSpell> ASHEN_BREATH_SPELL = registerSpell(new AshenBreathSpell());
   public static final Supplier<AbstractSpell> LINGERING_STRAIN = registerSpell(new LingeringStrainSpell());
   public static final Supplier<AbstractSpell> IGNITED_ONSLAUGHT_SPELL = registerSpell(new IgnitedOnslaughtSpell());
   public static final Supplier<AbstractSpell> BURNING_JUDGEMENT_SPELL = registerSpell(new BurningJudgmentSpell());
   public static final Supplier<AbstractSpell> METEOR_STORM_SPELL = registerSpell(new MeteorStormSpell());
   public static final Supplier<AbstractSpell> LAVAL_BOMB_SPELL = registerSpell(new LavaBombSpell());
   public static final Supplier<AbstractSpell> GYRO_SLASH_SPELL = registerSpell(new GyroSlashSpell());
   public static final Supplier<AbstractSpell> NULLFLARE_SPELL = registerSpell(new NullflareSpell());
   public static final Supplier<AbstractSpell> SUMMON_DESERT_DWELLERS_SPELL = registerSpell(new SummonDesertDwellers());
   public static final Supplier<AbstractSpell> SWORD_OF_THE_ANCIENTS_SPELL = registerSpell(new SwordOfTheAncientsSpell());
   public static final Supplier<AbstractSpell> AXE_OF_THE_DOOMED_SPELL = registerSpell(new AxeOfTheDoomedSpell());
   public static final Supplier<AbstractSpell> CURSED_REVENANTS_SPELL = registerSpell(new CursedRevenantsSpell());
   public static final Supplier<AbstractSpell> DESPAIR_SPELL = registerSpell(new DespairSpell());
   public static final Supplier<AbstractSpell> HALBERD_HORIZON_SPELL = registerSpell(new HalberdHorizonSpell());
   public static final Supplier<AbstractSpell> CURSED_BLAST_SPELL = registerSpell(new CursedBlastSpell());
   public static final Supplier<AbstractSpell> MECHANIZED_PREDATORS_SPELL = registerSpell(new MechanizedPredatorSpell());
   public static final Supplier<AbstractSpell> RAPID_LASER_SPELL = registerSpell(new RapidLaserSpell());
   public static final Supplier<AbstractSpell> DEATH_LASER_SPELL = registerSpell(new DeathLaserSpell());
   public static final Supplier<AbstractSpell> EM_PULSE_SPELL = registerSpell(new EmPulse());
   public static final Supplier<AbstractSpell> AERIAL_COLLAPSE_SPELL = registerSpell(new AerialCollapseSpell());
   public static final Supplier<AbstractSpell> STELE_CASCADE_SPELL = registerSpell(new SteleCascadeSpell());

   public TOSpells() {
      super();
   }

   public static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
      return SPELLS.register(spell.getSpellName(), () -> spell);
   }

   public static void register(IEventBus eventBus) {
      SPELLS.register(eventBus);
   }
}
