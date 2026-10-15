package com.somake.somakespells.registries;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.config.StartupFeatures;
import com.somake.somakespells.spells.aqua.HydroSlashSpell;
import com.somake.somakespells.spells.aqua.SeaSerpentJetSpell;
import com.somake.somakespells.spells.aqua.SeaSerpentSpell;
import com.somake.somakespells.spells.aqua.StormAuraSpell;
import com.somake.somakespells.spells.aqua.SubmergeSpell;
import com.somake.somakespells.spells.aqua.SummonDrownedSpell;
import com.somake.somakespells.spells.aqua.ThunderCloudSpell;
import com.somake.somakespells.spells.aqua.TidalDashSpell;
import com.somake.somakespells.spells.aqua.TidalGraspSpell;
import com.somake.somakespells.spells.aqua.TsunamiSpell;
import com.somake.somakespells.spells.aqua.WaterBallSpell;
import com.somake.somakespells.spells.aqua.WaterControlSpell;
import com.somake.somakespells.spells.aqua.WaterSpearSpell;
import com.somake.somakespells.spells.blood.BloodCutSpell;
import com.somake.somakespells.spells.blood.BloodRushSpell;
import com.somake.somakespells.spells.blood.BloodboundBladeSpell;
import com.somake.somakespells.spells.blood.BloodmarkSpell;
import com.somake.somakespells.spells.blood.BloodyLegacySpell;
import com.somake.somakespells.spells.blood.CursedConnectionSpell;
import com.somake.somakespells.spells.blood.FragmentedRequiemSpell;
import com.somake.somakespells.spells.blood.FuneralBloomSpell;
import com.somake.somakespells.spells.blood.RoseSecretSpell;
import com.somake.somakespells.spells.blood.WitheredRoseVortexSpell;
import com.somake.somakespells.spells.eldritch.EldritchGambitSpell;
import com.somake.somakespells.spells.ender.AbyssalOrbsSpell;
import com.somake.somakespells.spells.ender.EnderCorruptionSpell;
import com.somake.somakespells.spells.evocation.ChainConnectionSpell;
import com.somake.somakespells.spells.evocation.EvocationFortitudeSpell;
import com.somake.somakespells.spells.fire.AbyssalBurnSpell;
import com.somake.somakespells.spells.fire.ApocalypticBurstSpell;
import com.somake.somakespells.spells.fire.CombustionSpell;
import com.somake.somakespells.spells.fire.DamnedDemomansSpell;
import com.somake.somakespells.spells.fire.EruptionSpell;
import com.somake.somakespells.spells.fire.FireBlastSpell;
import com.somake.somakespells.spells.fire.FireOrbsSpell;
import com.somake.somakespells.spells.fire.FirestormVortexSpell;
import com.somake.somakespells.spells.fire.IgnisShieldSpell;
import com.somake.somakespells.spells.fire.IncineratorSlashSpell;
import com.somake.somakespells.spells.fire.PumpkinBombSpell;
import com.somake.somakespells.spells.fire.RitualFlameSpell;
import com.somake.somakespells.spells.geo.EarthboundSpell;
import com.somake.somakespells.spells.holy.BlessedConnectionSpell;
import com.somake.somakespells.spells.holy.BlessingSpell;
import com.somake.somakespells.spells.holy.CustodiaCaeliSpell;
import com.somake.somakespells.spells.holy.GuardianConnectionSpell;
import com.somake.somakespells.spells.holy.SacredPhoenixBlessingSpell;
import com.somake.somakespells.spells.holy.SovereignArmorySpell;
import com.somake.somakespells.spells.ice.PermafrostSpell;
import com.somake.somakespells.spells.lightning.LightningBallSpell;
import com.somake.somakespells.spells.lightning.LightningCutSpell;
import com.somake.somakespells.spells.lightning.LightningDanceSpell;
import com.somake.somakespells.spells.lightning.LightningFieldSpell;
import com.somake.somakespells.spells.lightning.LightningLashSpell;
import com.somake.somakespells.spells.lightning.LightningSparkSpell;
import com.somake.somakespells.spells.lightning.LightningSpearSpell;
import com.somake.somakespells.spells.lightning.LightningStrikeSpell;
import com.somake.somakespells.spells.lightning.LightningSwarmSpell;
import com.somake.somakespells.spells.nature.OvergrowthSpell;
import com.somake.somakespells.spells.sound.ComfortingLullabySpell;
import com.somake.somakespells.spells.sound.JingleBellSpell;
import com.somake.somakespells.spells.sound.RamTchumSpell;
import com.somake.somakespells.spells.sound.ResonantPulseSpell;
import com.somake.somakespells.spells.sound.ReverberationSpell;
import com.somake.somakespells.spells.sound.SlumberMelodySpell;
import com.somake.somakespells.spells.spirit.CrimsonReflectionSpell;
import com.somake.somakespells.spells.spirit.GraveSigilSpell;
import com.somake.somakespells.spells.spirit.ProcessionOfSoulsSpell;
import com.somake.somakespells.spells.spirit.SoulBastionSpell;
import com.somake.somakespells.spells.spirit.SoulLatchSpell;
import com.somake.somakespells.spells.spirit.SoulReprisalSpell;
import com.somake.somakespells.spells.spirit.SoulfallJudgmentSpell;
import com.somake.somakespells.spells.spirit.SpectralRondoSpell;
import com.somake.somakespells.spells.spirit.SpiralOfRuinSpell;
import com.somake.somakespells.spells.spirit.SpiritEmpowermentSpell;
import com.somake.somakespells.spells.spirit.WingedRuinSpell;
import com.somake.somakespells.spells.symmetry.AxeCleaveSpell;
import com.somake.somakespells.spells.symmetry.DesertWrathSpell;
import com.somake.somakespells.spells.symmetry.HalberdStrikeSpell;
import com.somake.somakespells.spells.symmetry.MirrorStrikeSpell;
import com.somake.somakespells.spells.symmetry.PhantomBarrageSpell;
import com.somake.somakespells.spells.symmetry.RenderRushSpell;
import com.somake.somakespells.spells.symmetry.SoulGrabSpell;
import com.somake.somakespells.spells.symmetry.SymmetryEmpowermentSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSpells {
   public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "somakespells");
   private static final boolean MOWZIE_LOADED = ModList.get().isLoaded("mowziesmobs");
   private static final boolean ISS_LOADED = MagicFromTheEastCompat.isLoaded();
   private static final boolean LEGENDARY_MONSTERS_LOADED = LegendaryMonstersCompat.isLoaded();
   private static final boolean SACRED_PHOENIX_BLESSING_ENABLED = StartupFeatures.isSacredPhoenixBlessingEnabled();
   public static final DeferredHolder<AbstractSpell, CombustionSpell> COMBUSTION = SPELLS.register("combustion", CombustionSpell::new);
   public static final DeferredHolder<AbstractSpell, RitualFlameSpell> RITUAL_FLAME = SPELLS.register("ritual_flame", RitualFlameSpell::new);
   public static final DeferredHolder<AbstractSpell, DamnedDemomansSpell> DAMNED_DEMOMANS = SPELLS.register("damned_demomans", DamnedDemomansSpell::new);
   public static final DeferredHolder<AbstractSpell, EruptionSpell> ERUPTION = SPELLS.register("eruption", EruptionSpell::new);
   public static final DeferredHolder<AbstractSpell, FireBlastSpell> FIRE_BLAST = SPELLS.register("fire_blast", FireBlastSpell::new);
   public static final DeferredHolder<AbstractSpell, FirestormVortexSpell> FIRESTORM_VORTEX = SPELLS.register("firestorm_vortex", FirestormVortexSpell::new);
   public static final DeferredHolder<AbstractSpell, PumpkinBombSpell> PUMPKIN_BOMB = SPELLS.register("pumpkin_bomb", PumpkinBombSpell::new);
   public static final DeferredHolder<AbstractSpell, IgnisShieldSpell> IGNIS_SHIELD = SPELLS.register("ignis_shield", IgnisShieldSpell::new);
   public static final DeferredHolder<AbstractSpell, IncineratorSlashSpell> INCINERATOR_SLASH = SPELLS.register("incinerator_slash", IncineratorSlashSpell::new);
   public static final DeferredHolder<AbstractSpell, AbyssalBurnSpell> ABYSSAL_BURN = SPELLS.register("abyssal_burn", AbyssalBurnSpell::new);
   public static final DeferredHolder<AbstractSpell, FireOrbsSpell> FIRE_ORBS = SPELLS.register("fire_orbs", FireOrbsSpell::new);
   public static final DeferredHolder<AbstractSpell, AbyssalOrbsSpell> ABYSSAL_ORBS = SPELLS.register("abyssal_orbs", AbyssalOrbsSpell::new);
   public static final DeferredHolder<AbstractSpell, ApocalypticBurstSpell> APOCALYPTIC_BURST = SPELLS.register("apocalyptic_burst", ApocalypticBurstSpell::new);
   public static final DeferredHolder<AbstractSpell, EarthboundSpell> EARTHBOUND = SPELLS.register("earthbound", EarthboundSpell::new);
   public static final DeferredHolder<AbstractSpell, SubmergeSpell> SUBMERGE = SPELLS.register("submerge", SubmergeSpell::new);
   public static final DeferredHolder<AbstractSpell, TidalGraspSpell> TIDAL_GRASP = SPELLS.register("tidal_grasp", TidalGraspSpell::new);
   public static final DeferredHolder<AbstractSpell, TsunamiSpell> TSUNAMI = SPELLS.register("tsunami", TsunamiSpell::new);
   public static final DeferredHolder<AbstractSpell, TidalDashSpell> TIDAL_DASH = SPELLS.register("tidal_dash", TidalDashSpell::new);
   public static final DeferredHolder<AbstractSpell, ThunderCloudSpell> THUNDER_CLOUD = SPELLS.register("thunder_cloud", ThunderCloudSpell::new);
   public static final DeferredHolder<AbstractSpell, WaterControlSpell> WATER_CONTROL = SPELLS.register("water_control", WaterControlSpell::new);
   public static final DeferredHolder<AbstractSpell, WaterSpearSpell> WATER_SPEAR = SPELLS.register("water_spear", WaterSpearSpell::new);
   public static final DeferredHolder<AbstractSpell, HydroSlashSpell> HYDRO_SLASH = SPELLS.register("hydro_slash", HydroSlashSpell::new);
   public static final DeferredHolder<AbstractSpell, SeaSerpentSpell> SEA_SERPENT = SPELLS.register("sea_serpent", SeaSerpentSpell::new);
   public static final DeferredHolder<AbstractSpell, SeaSerpentJetSpell> SEA_SERPENT_JET = SPELLS.register("sea_serpent_jet", SeaSerpentJetSpell::new);
   public static final DeferredHolder<AbstractSpell, StormAuraSpell> STORM_AURA = SPELLS.register("storm_aura", StormAuraSpell::new);
   public static final DeferredHolder<AbstractSpell, WaterBallSpell> WATER_BALL = SPELLS.register("water_ball", WaterBallSpell::new);
   public static final DeferredHolder<AbstractSpell, SummonDrownedSpell> SUMMON_DROWNED = SPELLS.register("summon_drowned", SummonDrownedSpell::new);
   public static final DeferredHolder<AbstractSpell, LightningSpearSpell> LIGHTNING_SPEAR = SPELLS.register("lightning_spear", LightningSpearSpell::new);
   public static final DeferredHolder<AbstractSpell, EnderCorruptionSpell> ENDER_CORRUPTION = SPELLS.register("ender_corruption", EnderCorruptionSpell::new);
   public static final DeferredHolder<AbstractSpell, PhantomBarrageSpell> PHANTOM_BARRAGE = SPELLS.register("phantom_barrage", PhantomBarrageSpell::new);
   public static final DeferredHolder<AbstractSpell, OvergrowthSpell> OVERGROWTH = SPELLS.register("overgrowth", OvergrowthSpell::new);
   public static final DeferredHolder<AbstractSpell, PermafrostSpell> PERMAFROST = SPELLS.register("permafrost", PermafrostSpell::new);
   public static final DeferredHolder<AbstractSpell, BlessingSpell> BLESSING = SPELLS.register("blessing", BlessingSpell::new);
   public static final DeferredHolder<AbstractSpell, CustodiaCaeliSpell> CUSTODIA_CAELI = SPELLS.register("custodia_caeli", CustodiaCaeliSpell::new);
   public static final DeferredHolder<AbstractSpell, SovereignArmorySpell> SOVEREIGN_ARMORY = SPELLS.register("sovereign_armory", SovereignArmorySpell::new);
   public static final DeferredHolder<AbstractSpell, SacredPhoenixBlessingSpell> SACRED_PHOENIX_BLESSING = SACRED_PHOENIX_BLESSING_ENABLED
      ? SPELLS.register("sacred_phoenix_blessing", SacredPhoenixBlessingSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, BlessedConnectionSpell> BLESSED_CONNECTION = MOWZIE_LOADED
      ? SPELLS.register("blessed_connection", BlessedConnectionSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, GuardianConnectionSpell> GUARDIAN_CONNECTION = MOWZIE_LOADED
      ? SPELLS.register("guardian_connection", GuardianConnectionSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, CursedConnectionSpell> CURSED_CONNECTION = MOWZIE_LOADED
      ? SPELLS.register("cursed_connection", CursedConnectionSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, BloodRushSpell> BLOOD_RUSH = SPELLS.register("blood_rush", BloodRushSpell::new);
   public static final DeferredHolder<AbstractSpell, BloodCutSpell> BLOOD_CUT = SPELLS.register("blood_cut", BloodCutSpell::new);
   public static final DeferredHolder<AbstractSpell, BloodyLegacySpell> BLOODY_LEGACY = SPELLS.register("bloody_legacy", BloodyLegacySpell::new);
   public static final DeferredHolder<AbstractSpell, BloodmarkSpell> BLOODMARK = SPELLS.register("bloodmark", BloodmarkSpell::new);
   public static final DeferredHolder<AbstractSpell, FragmentedRequiemSpell> FRAGMENTED_REQUIEM = SPELLS.register(
      "fragmented_requiem", FragmentedRequiemSpell::new
   );
   public static final DeferredHolder<AbstractSpell, RoseSecretSpell> ROSE_SECRET = SPELLS.register("rose_secret", RoseSecretSpell::new);
   public static final DeferredHolder<AbstractSpell, BloodboundBladeSpell> BLOODBOUND_BLADE = SPELLS.register("bloodbound_blade", BloodboundBladeSpell::new);
   public static final DeferredHolder<AbstractSpell, WitheredRoseVortexSpell> WITHERED_ROSE_VORTEX = SPELLS.register(
      "withered_rose_vortex", WitheredRoseVortexSpell::new
   );
   public static final DeferredHolder<AbstractSpell, FuneralBloomSpell> FUNERAL_BLOOM = SPELLS.register("funeral_bloom", FuneralBloomSpell::new);
   public static final DeferredHolder<AbstractSpell, EldritchGambitSpell> ELDRITCH_GAMBIT = SPELLS.register("eldritch_gambit", EldritchGambitSpell::new);
   public static final DeferredHolder<AbstractSpell, ChainConnectionSpell> CHAIN_CONNECTION = SPELLS.register("chain_connection", ChainConnectionSpell::new);
   public static final DeferredHolder<AbstractSpell, EvocationFortitudeSpell> EVOCATION_FORTITUDE = SPELLS.register(
      "evocation_fortitude", EvocationFortitudeSpell::new
   );
   public static final DeferredHolder<AbstractSpell, ReverberationSpell> REVERBERATION = SPELLS.register("reverberation", ReverberationSpell::new);
   public static final DeferredHolder<AbstractSpell, SlumberMelodySpell> SLUMBER_MELODY = SPELLS.register("slumber_melody", SlumberMelodySpell::new);
   public static final DeferredHolder<AbstractSpell, ResonantPulseSpell> RESONANT_PULSE = SPELLS.register("resonant_pulse", ResonantPulseSpell::new);
   public static final DeferredHolder<AbstractSpell, RamTchumSpell> RAM_TCHUM = SPELLS.register("ram_tchum", RamTchumSpell::new);
   public static final DeferredHolder<AbstractSpell, JingleBellSpell> JINGLE_BELL = SPELLS.register("jingle_bell", JingleBellSpell::new);
   public static final DeferredHolder<AbstractSpell, ComfortingLullabySpell> COMFORTING_LULLABY = SPELLS.register(
      "comforting_lullaby", ComfortingLullabySpell::new
   );
   public static final DeferredHolder<AbstractSpell, LightningBallSpell> LIGHTNING_BALL = SPELLS.register("lightning_ball", LightningBallSpell::new);
   public static final DeferredHolder<AbstractSpell, LightningDanceSpell> LIGHTNING_DANCE = SPELLS.register("lightning_dance", LightningDanceSpell::new);
   public static final DeferredHolder<AbstractSpell, LightningFieldSpell> LIGHTNING_FIELD = SPELLS.register("lightning_field", LightningFieldSpell::new);
   public static final DeferredHolder<AbstractSpell, LightningStrikeSpell> LIGHTNING_STRIKE = SPELLS.register("lightning_strike", LightningStrikeSpell::new);
   public static final DeferredHolder<AbstractSpell, LightningCutSpell> LIGHTNING_CUT = SPELLS.register("lightning_cut", LightningCutSpell::new);
   public static final DeferredHolder<AbstractSpell, LightningSparkSpell> LIGHTNING_SPARK = SPELLS.register("lightning_spark", LightningSparkSpell::new);
   public static final DeferredHolder<AbstractSpell, LightningSwarmSpell> LIGHTNING_SWARM = SPELLS.register("lightning_swarm", LightningSwarmSpell::new);
   public static final DeferredHolder<AbstractSpell, LightningLashSpell> LIGHTNING_LASH = SPELLS.register("lightning_lash", LightningLashSpell::new);
   public static final DeferredHolder<AbstractSpell, HalberdStrikeSpell> HALBERD_STRIKE = SPELLS.register("halberd_strike", HalberdStrikeSpell::new);
   public static final DeferredHolder<AbstractSpell, MirrorStrikeSpell> MIRROR_STRIKE = ISS_LOADED
      ? SPELLS.register("mirror_strike", MirrorStrikeSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, RenderRushSpell> RENDER_RUSH = SPELLS.register("render_rush", RenderRushSpell::new);
   public static final DeferredHolder<AbstractSpell, AxeCleaveSpell> AXE_CLEAVE = SPELLS.register("axe_cleave", AxeCleaveSpell::new);
   public static final DeferredHolder<AbstractSpell, DesertWrathSpell> DESERT_WRATH = SPELLS.register("desert_wrath", DesertWrathSpell::new);
   public static final DeferredHolder<AbstractSpell, SoulGrabSpell> SOUL_GRAB = SPELLS.register("soul_grab", SoulGrabSpell::new);
   public static final DeferredHolder<AbstractSpell, ProcessionOfSoulsSpell> PROCESSION_OF_SOULS = LEGENDARY_MONSTERS_LOADED
      ? SPELLS.register("procession_of_souls", ProcessionOfSoulsSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, GraveSigilSpell> GRAVE_SIGIL = LEGENDARY_MONSTERS_LOADED
      ? SPELLS.register("grave_sigil", GraveSigilSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, SoulBastionSpell> SOUL_BASTION = LEGENDARY_MONSTERS_LOADED
      ? SPELLS.register("soul_bastion", SoulBastionSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, SoulLatchSpell> SOUL_LATCH = LEGENDARY_MONSTERS_LOADED
      ? SPELLS.register("soul_latch", SoulLatchSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, SpiralOfRuinSpell> SPIRAL_OF_RUIN = LEGENDARY_MONSTERS_LOADED
      ? SPELLS.register("spiral_of_ruin", SpiralOfRuinSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, SoulfallJudgmentSpell> SOULFALL_JUDGMENT = LEGENDARY_MONSTERS_LOADED
      ? SPELLS.register("soulfall_judgment", SoulfallJudgmentSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, SoulReprisalSpell> SOUL_REPRISAL = LEGENDARY_MONSTERS_LOADED
      ? SPELLS.register("soul_reprisal", SoulReprisalSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, SpectralRondoSpell> SPECTRAL_RONDO = LEGENDARY_MONSTERS_LOADED
      ? SPELLS.register("spectral_rondo", SpectralRondoSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, WingedRuinSpell> WINGED_RUIN = LEGENDARY_MONSTERS_LOADED
      ? SPELLS.register("winged_ruin", WingedRuinSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, CrimsonReflectionSpell> CRIMSON_REFLECTION = LEGENDARY_MONSTERS_LOADED
      ? SPELLS.register("crimson_reflection", CrimsonReflectionSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, SpiritEmpowermentSpell> SPIRIT_EMPOWERMENT = LEGENDARY_MONSTERS_LOADED
      ? SPELLS.register("spirit_empowerment", SpiritEmpowermentSpell::new)
      : null;
   public static final DeferredHolder<AbstractSpell, SymmetryEmpowermentSpell> SYMMETRY_EMPOWERMENT = ISS_LOADED
      ? SPELLS.register("symmetry_empowerment", SymmetryEmpowermentSpell::new)
      : null;

   public static void register(IEventBus eventBus) {
      SPELLS.register(eventBus);
   }
}
