package net.acetheeldritchking.cataclysm_spellbooks.registries;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.function.Supplier;
import net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal.AbyssalBlastSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal.AbyssalPredatorSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal.AbyssalSlashSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal.DepthChargeSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal.DimensionalRiftSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal.TidalGrabSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal.VoidBeamSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.ender.GravitationPullSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.ender.GravityStormSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.ender.VoidRuneBulwarkSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.ender.VoidRuneSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.evocation.PilferSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.fire.AbyssFireballSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.fire.AshenBreathSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.fire.BonePierceSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.fire.BoneStormSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.fire.ConjureIgnitedReinforcement;
import net.acetheeldritchking.cataclysm_spellbooks.spells.fire.HellishBladeSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.fire.IncinerationSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.fire.InfernalStrikeSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.fire.TectonicTrembleSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.holy.ConjureKoboldiatorSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.holy.ConjureKoboletonSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.holy.ThothsWitnessSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.ice.ConjureThrallsSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.ice.CursedRushSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.ice.ForgoneRageSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.ice.MalevolentBattlefieldSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.nature.AmethystPunctureSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.nature.ConjureAmethystCrabSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.sand.DesertWindsSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.sand.MonolithCrashSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.sand.PharaohsWrathSpell;
import net.acetheeldritchking.cataclysm_spellbooks.spells.sand.SandstormSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpellRegistries {
   public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "cataclysm_spellbooks");
   public static final Supplier<AbstractSpell> VOID_BEAM = registerSpell(new VoidBeamSpell());
   public static final Supplier<AbstractSpell> ABYSSAL_BLAST = registerSpell(new AbyssalBlastSpell());
   public static final Supplier<AbstractSpell> DIMENSIONAL_RIFT = registerSpell(new DimensionalRiftSpell());
   public static final Supplier<AbstractSpell> DEPTH_CHARGE = registerSpell(new DepthChargeSpell());
   public static final Supplier<AbstractSpell> ABYSSAL_PREDATOR = registerSpell(new AbyssalPredatorSpell());
   public static final Supplier<AbstractSpell> ABYSSAL_SLASH = registerSpell(new AbyssalSlashSpell());
   public static final Supplier<AbstractSpell> TIDAL_GRAB = registerSpell(new TidalGrabSpell());
   public static final Supplier<AbstractSpell> VOID_RUNE = registerSpell(new VoidRuneSpell());
   public static final Supplier<AbstractSpell> VOID_BULWARK = registerSpell(new VoidRuneBulwarkSpell());
   public static final Supplier<AbstractSpell> GRAVITY_STORM = registerSpell(new GravityStormSpell());
   public static final Supplier<AbstractSpell> GRAVITATION_PULL = registerSpell(new GravitationPullSpell());
   public static final Supplier<AbstractSpell> PILFER = registerSpell(new PilferSpell());
   public static final Supplier<AbstractSpell> CONJURE_KOBOLDIATOR = registerSpell(new ConjureKoboldiatorSpell());
   public static final Supplier<AbstractSpell> CONJURE_KOBOLETON = registerSpell(new ConjureKoboletonSpell());
   public static final Supplier<AbstractSpell> THOTHS_WITNESS = registerSpell(new ThothsWitnessSpell());
   public static final Supplier<AbstractSpell> INCINERATION = registerSpell(new IncinerationSpell());
   public static final Supplier<AbstractSpell> INFERNAL_STRIKE = registerSpell(new InfernalStrikeSpell());
   public static final Supplier<AbstractSpell> CONJURE_IGNITED_REINFORCEMENT = registerSpell(new ConjureIgnitedReinforcement());
   public static final Supplier<AbstractSpell> HELLISH_BLADE = registerSpell(new HellishBladeSpell());
   public static final Supplier<AbstractSpell> BONE_STORM = registerSpell(new BoneStormSpell());
   public static final Supplier<AbstractSpell> BONE_PIERCE = registerSpell(new BonePierceSpell());
   public static final Supplier<AbstractSpell> ASHEN_BREATH = registerSpell(new AshenBreathSpell());
   public static final Supplier<AbstractSpell> ABYSS_FIREBALL = registerSpell(new AbyssFireballSpell());
   public static final Supplier<AbstractSpell> TECTONIC_TREMBLE = registerSpell(new TectonicTrembleSpell());
   public static final Supplier<AbstractSpell> MALEVOLENT_BATTLEFIELD = registerSpell(new MalevolentBattlefieldSpell());
   public static final Supplier<AbstractSpell> FORGONE_RAGE = registerSpell(new ForgoneRageSpell());
   public static final Supplier<AbstractSpell> CONJURE_THRALL = registerSpell(new ConjureThrallsSpell());
   public static final Supplier<AbstractSpell> CURSED_RUSH = registerSpell(new CursedRushSpell());
   public static final Supplier<AbstractSpell> SANDSTORM = registerSpell(new SandstormSpell());
   public static final Supplier<AbstractSpell> DESERT_WINDS = registerSpell(new DesertWindsSpell());
   public static final Supplier<AbstractSpell> MONOLITH_CRASH = registerSpell(new MonolithCrashSpell());
   public static final Supplier<AbstractSpell> AMETHYST_PUNCTURE = registerSpell(new AmethystPunctureSpell());
   public static final Supplier<AbstractSpell> CONJURE_AMETHYST_CRAB = registerSpell(new ConjureAmethystCrabSpell());
   public static final Supplier<AbstractSpell> PHARAOHS_WRATH = registerSpell(new PharaohsWrathSpell());

   public static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
      return SPELLS.register(spell.getSpellName(), () -> spell);
   }

   public static void register(IEventBus eventBus) {
      SPELLS.register(eventBus);
   }
}
