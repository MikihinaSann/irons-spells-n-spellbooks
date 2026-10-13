package com.grandsigil.registry;

import com.grandsigil.spell.BlackCoffinSpell;
import com.grandsigil.spell.ColosseumSpell;
import com.grandsigil.spell.CrimsonThreadsSpell;
import com.grandsigil.spell.DimensionCutSpell;
import com.grandsigil.spell.FullBloomSpell;
import com.grandsigil.spell.GlaciationSpell;
import com.grandsigil.spell.GrandSigilSpell;
import com.grandsigil.spell.HeavenGateSpell;
import com.grandsigil.spell.IceAgeSpell;
import com.grandsigil.spell.PhoenixSpell;
import com.grandsigil.spell.ReckoningSpell;
import com.grandsigil.spell.SkybreakSpell;
import com.grandsigil.spell.SunfallSpell;
import com.grandsigil.spell.TheGazeSpell;
import com.grandsigil.spell.ThousandBladesSpell;
import com.grandsigil.spell.ThunderDragonSpell;
import com.grandsigil.spell.VoidgripSpell;
import com.grandsigil.spell.WorldTreeSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GsSpells {
   private static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "grandsigil");
   public static final Supplier<AbstractSpell> GRAND_SIGIL = SPELLS.register("grand_sigil", GrandSigilSpell::new);
   public static final Supplier<AbstractSpell> SUNFALL = SPELLS.register("sunfall", SunfallSpell::new);
   public static final Supplier<AbstractSpell> SKYBREAK = SPELLS.register("skybreak", SkybreakSpell::new);
   public static final Supplier<AbstractSpell> WORLD_TREE = SPELLS.register("world_tree", WorldTreeSpell::new);
   public static final Supplier<AbstractSpell> HEAVEN_GATE = SPELLS.register("heaven_gate", HeavenGateSpell::new);
   public static final Supplier<AbstractSpell> GLACIATION = SPELLS.register("glaciation", GlaciationSpell::new);
   public static final Supplier<AbstractSpell> THE_GAZE = SPELLS.register("the_gaze", TheGazeSpell::new);
   public static final Supplier<AbstractSpell> COLOSSEUM = SPELLS.register("colosseum", ColosseumSpell::new);
   public static final Supplier<AbstractSpell> VOIDGRIP = SPELLS.register("voidgrip", VoidgripSpell::new);
   public static final Supplier<AbstractSpell> RECKONING = SPELLS.register("reckoning", ReckoningSpell::new);
   public static final Supplier<AbstractSpell> BLACK_COFFIN = SPELLS.register("black_coffin", BlackCoffinSpell::new);
   public static final Supplier<AbstractSpell> THOUSAND_BLADES = SPELLS.register("thousand_blades", ThousandBladesSpell::new);
   public static final Supplier<AbstractSpell> PHOENIX = SPELLS.register("phoenix", PhoenixSpell::new);
   public static final Supplier<AbstractSpell> THUNDER_DRAGON = SPELLS.register("thunder_dragon", ThunderDragonSpell::new);
   public static final Supplier<AbstractSpell> FULL_BLOOM = SPELLS.register("full_bloom", FullBloomSpell::new);
   public static final Supplier<AbstractSpell> ICE_AGE = SPELLS.register("ice_age", IceAgeSpell::new);
   public static final Supplier<AbstractSpell> CRIMSON_THREADS = SPELLS.register("crimson_threads", CrimsonThreadsSpell::new);
   public static final Supplier<AbstractSpell> DIMENSION_CUT = SPELLS.register("dimension_cut", DimensionCutSpell::new);

   public static void register(IEventBus modBus) {
      SPELLS.register(modBus);
   }

   private GsSpells() {
   }
}
