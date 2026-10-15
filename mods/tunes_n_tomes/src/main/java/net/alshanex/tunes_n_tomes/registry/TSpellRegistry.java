package net.alshanex.tunes_n_tomes.registry;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.function.Supplier;
import net.alshanex.tunes_n_tomes.spells.CelestialChantSpell;
import net.alshanex.tunes_n_tomes.spells.ChordBlastSpell;
import net.alshanex.tunes_n_tomes.spells.ClamorNoteSpell;
import net.alshanex.tunes_n_tomes.spells.CrescendoSpell;
import net.alshanex.tunes_n_tomes.spells.DalSegnoSpell;
import net.alshanex.tunes_n_tomes.spells.EncoreSpell;
import net.alshanex.tunes_n_tomes.spells.FortissimoSpell;
import net.alshanex.tunes_n_tomes.spells.GrandFinaleSpell;
import net.alshanex.tunes_n_tomes.spells.HarmonicAriaSpell;
import net.alshanex.tunes_n_tomes.spells.HymnOfHopeSpell;
import net.alshanex.tunes_n_tomes.spells.PiercingSoloSpell;
import net.alshanex.tunes_n_tomes.spells.RhapsodySpell;
import net.alshanex.tunes_n_tomes.spells.SerenadeSpell;
import net.alshanex.tunes_n_tomes.spells.SlumberNoteSpell;
import net.alshanex.tunes_n_tomes.spells.SonataSpell;
import net.alshanex.tunes_n_tomes.spells.SwiftMelodySpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TSpellRegistry {
   public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "tunes_n_tomes");
   public static final Supplier<AbstractSpell> CHORD_BLAST_SPELL = registerSpell(new ChordBlastSpell());
   public static final Supplier<AbstractSpell> CRESCENDO_SPELL = registerSpell(new CrescendoSpell());
   public static final Supplier<AbstractSpell> GRAND_FINALE_SPELL = registerSpell(new GrandFinaleSpell());
   public static final Supplier<AbstractSpell> CELESTIAL_CHANT_SPELL = registerSpell(new CelestialChantSpell());
   public static final Supplier<AbstractSpell> RHAPSODY_SPELL = registerSpell(new RhapsodySpell());
   public static final Supplier<AbstractSpell> SONATA_SPELL = registerSpell(new SonataSpell());
   public static final Supplier<AbstractSpell> SERENADE_SPELL = registerSpell(new SerenadeSpell());
   public static final Supplier<AbstractSpell> DAL_SEGNO_SPELL = registerSpell(new DalSegnoSpell());
   public static final Supplier<AbstractSpell> ENCORE_SPELL = registerSpell(new EncoreSpell());
   public static final Supplier<AbstractSpell> FORTISSIMO_SPELL = registerSpell(new FortissimoSpell());
   public static final Supplier<AbstractSpell> SWIFT_MELODY_SPELL = registerSpell(new SwiftMelodySpell());
   public static final Supplier<AbstractSpell> HYMN_OF_HOPE_SPELL = registerSpell(new HymnOfHopeSpell());
   public static final Supplier<AbstractSpell> SLUMBER_NOTE_SPELL = registerSpell(new SlumberNoteSpell());
   public static final Supplier<AbstractSpell> CLAMOR_NOTE_SPELL = registerSpell(new ClamorNoteSpell());
   public static final Supplier<AbstractSpell> HARMONIC_ARIA_SPELL = registerSpell(new HarmonicAriaSpell());
   public static final Supplier<AbstractSpell> PIERCING_SOLO_SPELL = registerSpell(new PiercingSoloSpell());

   public static void register(IEventBus eventBus) {
      SPELLS.register(eventBus);
   }

   private static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
      return SPELLS.register(spell.getSpellName(), () -> spell);
   }
}
