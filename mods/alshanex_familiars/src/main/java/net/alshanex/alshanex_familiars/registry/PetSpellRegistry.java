package net.alshanex.alshanex_familiars.registry;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.function.Supplier;
import net.alshanex.alshanex_familiars.spells.AngelSpell;
import net.alshanex.alshanex_familiars.spells.BirdsSpell;
import net.alshanex.alshanex_familiars.spells.DefaultNoteSpell;
import net.alshanex.alshanex_familiars.spells.EndMayhemSpell;
import net.alshanex.alshanex_familiars.spells.ExplosionMelodySpell;
import net.alshanex.alshanex_familiars.spells.HikenSpell;
import net.alshanex.alshanex_familiars.spells.IceAgeSpell;
import net.alshanex.alshanex_familiars.spells.IceChamberSpell;
import net.alshanex.alshanex_familiars.spells.LullabySpell;
import net.alshanex.alshanex_familiars.spells.MegidoSpell;
import net.alshanex.alshanex_familiars.spells.MusicBoltSpell;
import net.alshanex.alshanex_familiars.spells.ShadowSummonSpell;
import net.alshanex.alshanex_familiars.spells.SonataSpell;
import net.alshanex.alshanex_familiars.spells.VibrationSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PetSpellRegistry {
   public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "alshanex_familiars");
   public static final Supplier<AbstractSpell> SUMMON_SHADOW = registerSpell(new ShadowSummonSpell());
   public static final Supplier<AbstractSpell> DEFAULT_NOTE_SPELL = registerSpell(new DefaultNoteSpell());
   public static final Supplier<AbstractSpell> VIBRATION_SPELL = registerSpell(new VibrationSpell());
   public static final Supplier<AbstractSpell> EXPLOSION_MELODY = registerSpell(new ExplosionMelodySpell());
   public static final Supplier<AbstractSpell> ANGEL_SPELL = registerSpell(new AngelSpell());
   public static final Supplier<AbstractSpell> BIRDS_SPELL = registerSpell(new BirdsSpell());
   public static final Supplier<AbstractSpell> MUSIC_BOLT = registerSpell(new MusicBoltSpell());
   public static final Supplier<AbstractSpell> SONATA = registerSpell(new SonataSpell());
   public static final Supplier<AbstractSpell> LULLABY = registerSpell(new LullabySpell());
   public static final Supplier<AbstractSpell> ICE_AGE = registerSpell(new IceAgeSpell());
   public static final Supplier<AbstractSpell> ICE_CHAMBER = registerSpell(new IceChamberSpell());
   public static final Supplier<AbstractSpell> MEGIDO = registerSpell(new MegidoSpell());
   public static final Supplier<AbstractSpell> HIKEN = registerSpell(new HikenSpell());
   public static final Supplier<AbstractSpell> MAYHEM = registerSpell(new EndMayhemSpell());

   public static void register(IEventBus eventBus) {
      SPELLS.register(eventBus);
   }

   private static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
      return SPELLS.register(spell.getSpellName(), () -> spell);
   }
}
