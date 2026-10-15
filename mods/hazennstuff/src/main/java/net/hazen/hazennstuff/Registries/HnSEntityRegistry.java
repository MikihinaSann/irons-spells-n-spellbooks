package net.hazen.hazennstuff.Registries;

import net.hazen.hazennstuff.Entity.Mobs.Mobs.Blazes.CinderousFurnace.CinderousFurnace;
import net.hazen.hazennstuff.Entity.Mobs.Mobs.Blazes.TheInferno.TheInferno;
import net.hazen.hazennstuff.Entity.Mobs.Mobs.VoidWanderer.VoidWanderer;
import net.hazen.hazennstuff.Entity.Mobs.Summons.ReignOfTyros.Keeper.SummonKeeper;
import net.hazen.hazennstuff.Entity.Mobs.Summons.SummonTerraprisma.SummonedTerraprisma;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.BishopOfDeciet.BishopOfDeceitEntity;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.Electromancer.ElectromancerEntity;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.Necromancers.NamelessOne.NamelessOneEntity;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.Necromancers.Necromancer.NecromancerEntity;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.PiglinPyromancer.PiglinPyromancerEntity;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ReignOfTyros.Aegis.AegisEntity;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ReignOfTyros.Aptos.AptosEntity;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ReignOfTyros.Pyromus.PyromusEntity;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ServantsOfEnder.ServantsOfEnderEntity;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Good.Dryad.DryadEntity;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Good.TheRecluse.TheRecluseEntity;
import net.hazen.hazennstuff.Entity.Spells.Astral.CosmicBolt.CosmicBolt;
import net.hazen.hazennstuff.Entity.Spells.Astral.Evercomet.Evercomet;
import net.hazen.hazennstuff.Entity.Spells.Astral.ShootingStar.ShootingStar;
import net.hazen.hazennstuff.Entity.Spells.Blood.LifestealKnife.VampireKnife;
import net.hazen.hazennstuff.Entity.Spells.Eldritch.SoulSeeker.SoulSeeker;
import net.hazen.hazennstuff.Entity.Spells.Evocation.SpectralAxe.SpectralAxe;
import net.hazen.hazennstuff.Entity.Spells.Fire.BrimstoneHellblast.BrimstoneHellblast;
import net.hazen.hazennstuff.Entity.Spells.Fire.FireDaggers.FieryDaggerMagicProjectile;
import net.hazen.hazennstuff.Entity.Spells.Fire.ImmolatingObliterator.ScorchingSlash;
import net.hazen.hazennstuff.Entity.Spells.Holy.IchorStream.IchorStream;
import net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkA.IceChunkA;
import net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkB.IceChunkB;
import net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkC.IceChunkC;
import net.hazen.hazennstuff.Entity.Spells.Ice.IceArrow.IceArrow;
import net.hazen.hazennstuff.Entity.Spells.Ice.IceCloud.IceCloud;
import net.hazen.hazennstuff.Entity.Spells.Lightning.EnergyBurst.EnergyBurst;
import net.hazen.hazennstuff.Entity.Spells.Lightning.InstantLightningStrike.LightningStrike;
import net.hazen.hazennstuff.Entity.Spells.Nature.DeathSentence.DeathSentence;
import net.hazen.hazennstuff.Entity.Spells.Nature.SpiderLily.SpiderLily;
import net.hazen.hazennstuff.Entity.Spells.Nature.ThornChakram.ThornChakram;
import net.hazen.hazennstuff.Entity.Spells.Radiance.Syringe.Syringe;
import net.hazen.hazennstuff.Entity.Spells.Radiance.Terraprisma.Terraprisma;
import net.hazen.hazennstuff.Entity.Spells.Shadow.NightsEdgeAfterSlash.NightsEdgeAfterSlash;
import net.hazen.hazennstuff.Entity.Spells.Shadow.ShadowDagger.ShadowDagger;
import net.hazen.hazennstuff.Entity.Spells.Shadow.ShadowReaver.ShadowReaver;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HnSEntityRegistry {
   private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "hazennstuff");
   public static final DeferredHolder<EntityType<?>, EntityType<BrimstoneHellblast>> BRIMSTONE_HELLBLAST = ENTITIES.register(
      "brimstone_hellblast",
      () -> EntityType.Builder.<BrimstoneHellblast>of(BrimstoneHellblast::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(4)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "brimstone_hellblast").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<EnergyBurst>> ENERGY_BURST = ENTITIES.register(
      "energy_burst",
      () -> EntityType.Builder.<EnergyBurst>of(EnergyBurst::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(4)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "energy_burst").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Syringe>> SYRINGE = ENTITIES.register(
      "syringe",
      () -> EntityType.Builder.<Syringe>of(Syringe::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(4)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "syringe").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<VampireKnife>> LIFESTEAL_KNIFE = ENTITIES.register(
      "lifesteal_knife",
      () -> EntityType.Builder.<VampireKnife>of(VampireKnife::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(4)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "lifesteal_knife").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ThornChakram>> THORN_CHAKRAM = ENTITIES.register(
      "thorn_chakram",
      () -> EntityType.Builder.<ThornChakram>of(ThornChakram::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "thorn_chakram").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IceArrow>> ICE_ARROW = ENTITIES.register(
      "ice_arrow",
      () -> EntityType.Builder.<IceArrow>of(IceArrow::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "ice_arrow").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IceChunkA>> ICE_CHUNK_A = ENTITIES.register(
      "ice_chunk_a",
      () -> EntityType.Builder.<IceChunkA>of(IceChunkA::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "ice_chunk_a").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IceChunkB>> ICE_CHUNK_B = ENTITIES.register(
      "ice_chunk_b",
      () -> EntityType.Builder.<IceChunkB>of(IceChunkB::new, MobCategory.MISC)
         .sized(1.5F, 1.5F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "ice_chunk_b").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IceChunkC>> ICE_CHUNK_C = ENTITIES.register(
      "ice_chunk_c",
      () -> EntityType.Builder.<IceChunkC>of(IceChunkC::new, MobCategory.MISC)
         .sized(0.75F, 0.75F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "ice_chunk_c").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SpectralAxe>> SPECTRAL_AXE = ENTITIES.register(
      "spectral_axe",
      () -> EntityType.Builder.<SpectralAxe>of(SpectralAxe::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "spectral_axe").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SoulSeeker>> SOUL_SEEKER = ENTITIES.register(
      "soul_seeker",
      () -> EntityType.Builder.<SoulSeeker>of(SoulSeeker::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "soul_seeker").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SpiderLily>> SPIDER_LILY = ENTITIES.register(
      "spider_lily",
      () -> EntityType.Builder.<SpiderLily>of(SpiderLily::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "spider_lily").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DeathSentence>> DEATH_SENTENCE = ENTITIES.register(
      "death_sentence",
      () -> EntityType.Builder.<DeathSentence>of(DeathSentence::new, MobCategory.MISC)
         .sized(1.5F, 3.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "death_sentence").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<NightsEdgeAfterSlash>> NIGHTS_EDGE_AFTER_SLASH = ENTITIES.register(
      "nights_edge_after_slash",
      () -> EntityType.Builder.<NightsEdgeAfterSlash>of(NightsEdgeAfterSlash::new, MobCategory.MISC)
         .sized(4.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "nights_edge_after_slash").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ScorchingSlash>> SCORCHING_SLASH = ENTITIES.register(
      "scorching_slash",
      () -> EntityType.Builder.<ScorchingSlash>of(ScorchingSlash::new, MobCategory.MISC)
         .sized(4.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "scorching_slash").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ShootingStar>> SHOOTING_STAR = ENTITIES.register(
      "shooting_star",
      () -> EntityType.Builder.<ShootingStar>of(ShootingStar::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "shooting_star").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Terraprisma>> TERRAPRISMA_PROJECTILE = ENTITIES.register(
      "terraprisma_projectile",
      () -> EntityType.Builder.<Terraprisma>of(Terraprisma::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "terraprisma_projectile").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FieryDaggerMagicProjectile>> FIERY_DAGGER_MAGIC_PROJECTILE = ENTITIES.register(
      "fiery_dagger_magic_projectile",
      () -> EntityType.Builder.<FieryDaggerMagicProjectile>of(FieryDaggerMagicProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "fiery_dagger_magic_projectile").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ShadowDagger>> SHADOW_DAGGER = ENTITIES.register(
      "shadow_dagger",
      () -> EntityType.Builder.<ShadowDagger>of(ShadowDagger::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "shadow_dagger").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CosmicBolt>> COSMIC_BOLT = ENTITIES.register(
      "cosmic_bolt",
      () -> EntityType.Builder.<CosmicBolt>of(CosmicBolt::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "cosmic_bolt").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ShadowReaver>> SHADOW_REAVER = ENTITIES.register(
      "shadow_reaver",
      () -> EntityType.Builder.<ShadowReaver>of(ShadowReaver::new, MobCategory.MISC)
         .sized(0.75F, 0.75F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "shadow_reaver").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<Evercomet>> EVERCOMET = ENTITIES.register(
      "evercomet",
      () -> EntityType.Builder.<Evercomet>of(Evercomet::new, MobCategory.MISC)
         .sized(0.75F, 0.75F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "evercomet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ElectromancerEntity>> ELECTROMANCER = ENTITIES.register(
      "electromancer",
      () -> EntityType.Builder.<ElectromancerEntity>of(ElectromancerEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "electromancer").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AptosEntity>> APTOS = ENTITIES.register(
      "aptos",
      () -> EntityType.Builder.<AptosEntity>of(AptosEntity::new, MobCategory.MONSTER)
         .sized(0.85F, 2.3F)
         .clientTrackingRange(64)
         .eyeHeight(2.3F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "aptos").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PyromusEntity>> PYROMUS = ENTITIES.register(
      "pyromus",
      () -> EntityType.Builder.<PyromusEntity>of(PyromusEntity::new, MobCategory.MONSTER)
         .sized(0.85F, 2.3F)
         .clientTrackingRange(64)
         .eyeHeight(2.3F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "pyromus").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AegisEntity>> AEGIS = ENTITIES.register(
      "aegis",
      () -> EntityType.Builder.<AegisEntity>of(AegisEntity::new, MobCategory.MONSTER)
         .sized(0.85F, 2.3F)
         .clientTrackingRange(64)
         .eyeHeight(2.3F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "aegis").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TheRecluseEntity>> THE_RECLUSE = ENTITIES.register(
      "the_recluse",
      () -> EntityType.Builder.<TheRecluseEntity>of(TheRecluseEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 3.0F)
         .clientTrackingRange(64)
         .eyeHeight(3.0F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "the_recluse").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PiglinPyromancerEntity>> PIGLIN_PYROMANCER = ENTITIES.register(
      "piglin_pyromancer",
      () -> EntityType.Builder.<PiglinPyromancerEntity>of(PiglinPyromancerEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .eyeHeight(2.3F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "piglin_pyromancer").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ServantsOfEnderEntity>> SERVANT_OF_ENDER = ENTITIES.register(
      "servant_of_ender",
      () -> EntityType.Builder.<ServantsOfEnderEntity>of(ServantsOfEnderEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 3.0F)
         .clientTrackingRange(64)
         .eyeHeight(3.0F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "servant_of_ender").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BishopOfDeceitEntity>> BISHOP_OF_DECEIT = ENTITIES.register(
      "bishop_of_deceit",
      () -> EntityType.Builder.<BishopOfDeceitEntity>of(BishopOfDeceitEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .eyeHeight(3.0F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "bishop_of_deceit").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DryadEntity>> DRYAD = ENTITIES.register(
      "dryad",
      () -> EntityType.Builder.<DryadEntity>of(DryadEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .eyeHeight(3.0F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "dryad").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<NamelessOneEntity>> NAMELESS_ONE = ENTITIES.register(
      "nameless_one",
      () -> EntityType.Builder.<NamelessOneEntity>of(NamelessOneEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .eyeHeight(3.0F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "nameless_one").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<NecromancerEntity>> NECROMANCER = ENTITIES.register(
      "necromancer",
      () -> EntityType.Builder.<NecromancerEntity>of(NecromancerEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .eyeHeight(3.0F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "necromancer").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedTerraprisma>> TERRAPRISMA = ENTITIES.register(
      "terraprisma",
      () -> EntityType.Builder.<SummonedTerraprisma>of(SummonedTerraprisma::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .eyeHeight(1.6F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "terraprisma").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CinderousFurnace>> CINDEROUS_FURNACE = ENTITIES.register(
      "cinderous_furnace",
      () -> EntityType.Builder.<CinderousFurnace>of(CinderousFurnace::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(0.6F, 1.8F)
         .clientTrackingRange(8)
         .eyeHeight(1.6F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "cinderous_furnace").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TheInferno>> THE_INFERNO = ENTITIES.register(
      "the_inferno",
      () -> EntityType.Builder.<TheInferno>of(TheInferno::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(0.6F, 1.8F)
         .clientTrackingRange(8)
         .eyeHeight(1.6F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "the_inferno").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonKeeper>> SUMMON_KEEPER = ENTITIES.register(
      "summon_keeper",
      () -> EntityType.Builder.<SummonKeeper>of(SummonKeeper::new, MobCategory.MONSTER)
         .sized(0.85F, 2.3F)
         .clientTrackingRange(64)
         .eyeHeight(2.3F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "summon_keeper").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<VoidWanderer>> VOID_WANDERER = ENTITIES.register(
      "void_wanderer",
      () -> EntityType.Builder.<VoidWanderer>of(VoidWanderer::new, MobCategory.MONSTER)
         .sized(1.0F, 2.5F)
         .clientTrackingRange(64)
         .eyeHeight(2.3F)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "void_wanderer").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IceCloud>> ICE_CLOUD = ENTITIES.register(
      "ice_cloud",
      () -> EntityType.Builder.<IceCloud>of(IceCloud::new, MobCategory.MISC)
         .sized(4.0F, 1.2F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "ice_cloud").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IchorStream>> ICHOR_STREAM = ENTITIES.register(
      "ichor_stream",
      () -> EntityType.Builder.<IchorStream>of(IchorStream::new, MobCategory.MISC)
         .sized(1.2F, 1.2F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "ichor_stream").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<LightningStrike>> LIGHTNING_STRIKE = ENTITIES.register(
      "lightning_strike",
      () -> EntityType.Builder.<LightningStrike>of(LightningStrike::new, MobCategory.MISC)
         .sized(1.2F, 1.2F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("hazennstuff", "lightning_strike").toString())
   );

   public static void register(IEventBus eventBus) {
      ENTITIES.register(eventBus);
   }
}
