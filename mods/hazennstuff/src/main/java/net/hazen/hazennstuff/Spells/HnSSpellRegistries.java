package net.hazen.hazennstuff.Spells;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.function.Supplier;
import net.hazen.hazennstuff.Spells.Schools.Cosmic.CosmicBoltSpell;
import net.hazen.hazennstuff.Spells.Schools.Cosmic.EvercometBarrageSpell;
import net.hazen.hazennstuff.Spells.Schools.Cosmic.ShootingStarSpell;
import net.hazen.hazennstuff.Spells.Schools.Eldritch.SoulSeekersSpell;
import net.hazen.hazennstuff.Spells.Schools.Evocation.ParrySpell;
import net.hazen.hazennstuff.Spells.Schools.Evocation.SpectralAxeSpell;
import net.hazen.hazennstuff.Spells.Schools.Fire.BrimstoneHellblastSpell;
import net.hazen.hazennstuff.Spells.Schools.Fire.CinderousStepSpell;
import net.hazen.hazennstuff.Spells.Schools.Fire.FieryDaggerSpell;
import net.hazen.hazennstuff.Spells.Schools.Fire.ScorchingSlashSpell;
import net.hazen.hazennstuff.Spells.Schools.Holy.GoldenShowerSpell;
import net.hazen.hazennstuff.Spells.Schools.Ice.HailstormSpell;
import net.hazen.hazennstuff.Spells.Schools.Ice.IceArrowSpell;
import net.hazen.hazennstuff.Spells.Schools.Lightning.EnergyBurstSpell;
import net.hazen.hazennstuff.Spells.Schools.Lightning.IonicSlashSpell;
import net.hazen.hazennstuff.Spells.Schools.Nature.CounterspellSpiderLilySpell;
import net.hazen.hazennstuff.Spells.Schools.Nature.DeathSentenceSpell;
import net.hazen.hazennstuff.Spells.Schools.Nature.ShardSwordSpell;
import net.hazen.hazennstuff.Spells.Schools.Nature.ThornChakramSpell;
import net.hazen.hazennstuff.Spells.Schools.Radiance.CallForthTerraprismaSpell;
import net.hazen.hazennstuff.Spells.Schools.Radiance.PrismaticShiftSpell;
import net.hazen.hazennstuff.Spells.Schools.Radiance.SyringeBarrageSpell;
import net.hazen.hazennstuff.Spells.Schools.Radiance.TerraprismicBarrageSpell;
import net.hazen.hazennstuff.Spells.Schools.Shadow.NightsEdgeStrikeSpell;
import net.hazen.hazennstuff.Spells.Schools.Shadow.ShadowReaverSpell;
import net.hazen.hazennstuff.Spells.Schools.Shadow.UmbrashiftBarrageSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HnSSpellRegistries {
   public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "hazennstuff");
   public static final Supplier<AbstractSpell> BRIMSTONE_HELLBLAST = registerSpell(new BrimstoneHellblastSpell());
   public static final Supplier<AbstractSpell> CINDEROUS_STEP = registerSpell(new CinderousStepSpell());
   public static final Supplier<AbstractSpell> SCORCHING_SLASH = registerSpell(new ScorchingSlashSpell());
   public static final Supplier<AbstractSpell> FIERY_DAGGER = registerSpell(new FieryDaggerSpell());
   public static final Supplier<AbstractSpell> ICE_ARROW = registerSpell(new IceArrowSpell());
   public static final Supplier<AbstractSpell> HAILSTORM = registerSpell(new HailstormSpell());
   public static final Supplier<AbstractSpell> ENERGY_BURST = registerSpell(new EnergyBurstSpell());
   public static final Supplier<AbstractSpell> IONIC_SLASH = registerSpell(new IonicSlashSpell());
   public static final Supplier<AbstractSpell> THORN_CHAKRAM = registerSpell(new ThornChakramSpell());
   public static final Supplier<AbstractSpell> COUNTERSPELL_SPIDER_LILY = registerSpell(new CounterspellSpiderLilySpell());
   public static final Supplier<AbstractSpell> SHARD_SWORD = registerSpell(new ShardSwordSpell());
   public static final Supplier<AbstractSpell> DEATH_SENTENCE = registerSpell(new DeathSentenceSpell());
   public static final Supplier<AbstractSpell> SPECTRAL_AXE = registerSpell(new SpectralAxeSpell());
   public static final Supplier<AbstractSpell> PARRY = registerSpell(new ParrySpell());
   public static final Supplier<AbstractSpell> GOLDEN_SHOWER = registerSpell(new GoldenShowerSpell());
   public static final Supplier<AbstractSpell> SYRINGE_BARRAGE = registerSpell(new SyringeBarrageSpell());
   public static final Supplier<AbstractSpell> TERRAPRISMIC_BARRAGE = registerSpell(new TerraprismicBarrageSpell());
   public static final Supplier<AbstractSpell> CALL_FORTH_TERRAPRISMA = registerSpell(new CallForthTerraprismaSpell());
   public static final Supplier<AbstractSpell> PRISMATIC_SHIFT = registerSpell(new PrismaticShiftSpell());
   public static final Supplier<AbstractSpell> NIGHTS_EDGE_STRIKE = registerSpell(new NightsEdgeStrikeSpell());
   public static final Supplier<AbstractSpell> UMBRASHIFT_BARRAGE = registerSpell(new UmbrashiftBarrageSpell());
   public static final Supplier<AbstractSpell> SHADOW_REAVER = registerSpell(new ShadowReaverSpell());
   public static final Supplier<AbstractSpell> SOUL_SEEKERS = registerSpell(new SoulSeekersSpell());
   public static final Supplier<AbstractSpell> SHOOTING_STAR = registerSpell(new ShootingStarSpell());
   public static final Supplier<AbstractSpell> COSMIC_BOLT = registerSpell(new CosmicBoltSpell());
   public static final Supplier<AbstractSpell> EVERCOMET_BARRAGE = registerSpell(new EvercometBarrageSpell());

   public static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
      return SPELLS.register(spell.getSpellName(), () -> spell);
   }

   public static void register(IEventBus eventBus) {
      SPELLS.register(eventBus);
   }
}
