package net.acetheeldritchking.discerning_the_eldritch.registries;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.function.Supplier;
import net.acetheeldritchking.discerning_the_eldritch.spells.blood.VeinRipperSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.eldritch.AbracadabraSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.eldritch.ConjureForsakenAidSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.eldritch.ConjureGaolerSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.eldritch.EsotericEdgeSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.eldritch.EsotericStrikeSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.eldritch.MendFleshSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.eldritch.OtherworldlyPresenceSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.eldritch.RiftWalkerSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.eldritch.SilenceSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.evocation.BoogieWoogieSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.evocation.GuardiansGazeSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.fire.SoulSetAblazeSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.fire.SoulSliceSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.holy.ExorcismSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.ice.CrystallineCarverSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.ice.GlacialEdgeSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.ritual.BladesOfRancorSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.ritual.CallAscendedOneSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.ritual.LibrasJudgementSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.ritual.RavenousRevenantSpell;
import net.acetheeldritchking.discerning_the_eldritch.spells.ritual.ZealousHarbingerSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpellRegistries {
   public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "discerning_the_eldritch");
   public static final Supplier<AbstractSpell> SILENCE = registerSpell(new SilenceSpell());
   public static final Supplier<AbstractSpell> CONJURE_FORSAKE_AID = registerSpell(new ConjureForsakenAidSpell());
   public static final Supplier<AbstractSpell> ESOTERIC_EDGE = registerSpell(new EsotericEdgeSpell());
   public static final Supplier<AbstractSpell> CONJURE_GAOLER = registerSpell(new ConjureGaolerSpell());
   public static final Supplier<AbstractSpell> OTHERWORLDLY_PRESENCE = registerSpell(new OtherworldlyPresenceSpell());
   public static final Supplier<AbstractSpell> MEND_FLESH = registerSpell(new MendFleshSpell());
   public static final Supplier<AbstractSpell> RIFT_WALKER = registerSpell(new RiftWalkerSpell());
   public static final Supplier<AbstractSpell> ABRACADABRA = registerSpell(new AbracadabraSpell());
   public static final Supplier<AbstractSpell> ESOTERIC_STRIKE = registerSpell(new EsotericStrikeSpell());
   public static final Supplier<AbstractSpell> VEIN_RIPPER = registerSpell(new VeinRipperSpell());
   public static final Supplier<AbstractSpell> BOOGIE_WOOGIE = registerSpell(new BoogieWoogieSpell());
   public static final Supplier<AbstractSpell> GUARDIANS_GAZE = registerSpell(new GuardiansGazeSpell());
   public static final Supplier<AbstractSpell> SOUL_SLICE = registerSpell(new SoulSliceSpell());
   public static final Supplier<AbstractSpell> SOUL_SET_ABLAZE = registerSpell(new SoulSetAblazeSpell());
   public static final Supplier<AbstractSpell> EXORCISM = registerSpell(new ExorcismSpell());
   public static final Supplier<AbstractSpell> GLACIAL_EDGE = registerSpell(new GlacialEdgeSpell());
   public static final Supplier<AbstractSpell> CRYSTALLINE_CARVER = registerSpell(new CrystallineCarverSpell());
   public static final Supplier<AbstractSpell> CALL_ASCENDED_ONE = registerSpell(new CallAscendedOneSpell());
   public static final Supplier<AbstractSpell> BLADES_OF_RANCOR = registerSpell(new BladesOfRancorSpell());
   public static final Supplier<AbstractSpell> ZEALOUS_HARBINGER = registerSpell(new ZealousHarbingerSpell());
   public static final Supplier<AbstractSpell> RAVENOUS_REVENANT = registerSpell(new RavenousRevenantSpell());
   public static final Supplier<AbstractSpell> LIBRAS_JUDGEMENT = registerSpell(new LibrasJudgementSpell());

   public static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
      return SPELLS.register(spell.getSpellName(), () -> spell);
   }

   public static void register(IEventBus eventBus) {
      SPELLS.register(eventBus);
   }
}
