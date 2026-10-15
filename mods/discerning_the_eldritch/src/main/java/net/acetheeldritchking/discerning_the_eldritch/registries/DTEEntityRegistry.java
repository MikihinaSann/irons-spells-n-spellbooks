package net.acetheeldritchking.discerning_the_eldritch.registries;

import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicAcolyteEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicCrusaderEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicSummonerEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicTraitorEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistCaptainEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistMageEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistWitchEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.ascended_one.AscendedOneBoss;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.ascended_one.AscendedOneCultistEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.minibosses.blood_matriarch.BloodMatriarchEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.eldritch_caster.TheApostleEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.electromancer.ElectromancerEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.gaoler.GaolerEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.sightless_maw.SightlessMawEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.untold_behemoth.UntoldBehemothEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.blade_of_rancor.BladeOfRancorProjectile;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.cataclysm_blade_projectile.CataclysmBladeBigProjectile;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.cataclysm_blade_projectile.CataclysmBladeSmallProjectile;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.crystal_carve.CrystalCarveEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.esoteric_edge.EsotericEdge;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.esoteric_strike.EsotericStrike;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.glacial_edge.GlacialEdge;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.gore_bile.GoreBileAoE;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.mourning_star.MourningStarProjectile;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.ravenous_jaw.RavenousJawEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.razor_blade.RazorBlade;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.rift_walker.UnstableRiftEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.ritual_burn.RitualBurnAoE;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.soul_eruption.SoulEruptionAoe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DTEEntityRegistry {
   private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "discerning_the_eldritch");
   public static final DeferredHolder<EntityType<?>, EntityType<EsotericEdge>> ESOTERIC_EDGE = ENTITIES.register(
      "esoteric_edge",
      () -> EntityType.Builder.<EsotericEdge>of(EsotericEdge::new, MobCategory.MISC)
         .sized(5.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "esoteric_edge").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ApothicSummonerEntity>> APOTHIC_SUMMONER = ENTITIES.register(
      "apothic_summoner",
      () -> EntityType.Builder.<ApothicSummonerEntity>of(ApothicSummonerEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "apothic_summoner").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ApothicCrusaderEntity>> APOTHIC_CRUSADER = ENTITIES.register(
      "apothic_crusader",
      () -> EntityType.Builder.<ApothicCrusaderEntity>of(ApothicCrusaderEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "apothic_crusader").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ApothicAcolyteEntity>> APOTHIC_ACOLYTE = ENTITIES.register(
      "apothic_acolyte",
      () -> EntityType.Builder.<ApothicAcolyteEntity>of(ApothicAcolyteEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "apothic_acolyte").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SightlessMawEntity>> SIGHTLESS_MAW = ENTITIES.register(
      "sightless_maw",
      () -> EntityType.Builder.<SightlessMawEntity>of(SightlessMawEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "sightless_maw").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<UntoldBehemothEntity>> UNTOLD_BEHEMOTH = ENTITIES.register(
      "untold_behemoth",
      () -> EntityType.Builder.<UntoldBehemothEntity>of(UntoldBehemothEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "untold_behemoth").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<TheApostleEntity>> APOSTLE_ENTITY = ENTITIES.register(
      "apostle",
      () -> EntityType.Builder.<TheApostleEntity>of(TheApostleEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "apostle").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GaolerEntity>> GAOLER_ENTITY = ENTITIES.register(
      "gaoler",
      () -> EntityType.Builder.<GaolerEntity>of(GaolerEntity::new, MobCategory.MONSTER)
         .sized(3.5F, 5.0F)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "gaoler").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AscendedOneBoss>> ASCENDED_ONE = ENTITIES.register(
      "ascended_one",
      () -> EntityType.Builder.<AscendedOneBoss>of(AscendedOneBoss::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "ascended_one").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AscendedOneCultistEntity>> ASCENDED_ONE_CULTIST = ENTITIES.register(
      "ascended_one_cultist",
      () -> EntityType.Builder.<AscendedOneCultistEntity>of(AscendedOneCultistEntity::new, MobCategory.MISC)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "ascended_one_cultist").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GlacialEdge>> GLACIAL_EDGE = ENTITIES.register(
      "glacial_edge",
      () -> EntityType.Builder.<GlacialEdge>of(GlacialEdge::new, MobCategory.MISC)
         .sized(5.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "glacial_edge").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<UnstableRiftEntity>> UNSTABLE_RIFT = ENTITIES.register(
      "unstable_rift",
      () -> EntityType.Builder.<UnstableRiftEntity>of(UnstableRiftEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "unstable_rift").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<EsotericStrike>> ESOTERIC_STRIKE = ENTITIES.register(
      "esoteric_strike",
      () -> EntityType.Builder.<EsotericStrike>of(EsotericStrike::new, MobCategory.MISC)
         .sized(2.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "esoteric_strike").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ApothicTraitorEntity>> APOTHIC_TRAITOR = ENTITIES.register(
      "apothic_traitor",
      () -> EntityType.Builder.<ApothicTraitorEntity>of(ApothicTraitorEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "apothic_traitor").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CrystalCarveEntity>> CRYSTAL_CARVE = ENTITIES.register(
      "crystal_carve",
      () -> EntityType.Builder.<CrystalCarveEntity>of(CrystalCarveEntity::new, MobCategory.MISC)
         .sized(5.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "crystal_carve").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RazorBlade>> RAZOR_BLADE = ENTITIES.register(
      "razor_blade",
      () -> EntityType.Builder.<RazorBlade>of(RazorBlade::new, MobCategory.MISC)
         .sized(1.0F, 2.0F)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "razor_blade").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloodCultistCaptainEntity>> BLOOD_CULTIST_CAPTAIN = ENTITIES.register(
      "blood_cultist_captain",
      () -> EntityType.Builder.<BloodCultistCaptainEntity>of(BloodCultistCaptainEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "blood_cultist_captain").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloodCultistMageEntity>> BLOOD_CULTIST_MAGE = ENTITIES.register(
      "blood_cultist_mage",
      () -> EntityType.Builder.<BloodCultistMageEntity>of(BloodCultistMageEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "blood_cultist_mage").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloodCultistWitchEntity>> BLOOD_CULTIST_WITCH = ENTITIES.register(
      "blood_cultist_witch",
      () -> EntityType.Builder.<BloodCultistWitchEntity>of(BloodCultistWitchEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "blood_cultist_witch").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloodMatriarchEntity>> BLOOD_MATRIARCH = ENTITIES.register(
      "blood_matriarch",
      () -> EntityType.Builder.<BloodMatriarchEntity>of(BloodMatriarchEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "blood_matriarch").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BladeOfRancorProjectile>> BLADE_OF_RANCOR = ENTITIES.register(
      "blade_of_rancor",
      () -> EntityType.Builder.<BladeOfRancorProjectile>of(BladeOfRancorProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "blade_of_rancor").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GoreBileAoE>> GORE_BILE_AOE = ENTITIES.register(
      "gore_bile_aoe",
      () -> EntityType.Builder.<GoreBileAoE>of(GoreBileAoE::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "gore_bile_aoe").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CataclysmBladeSmallProjectile>> CATACLYSM_BLADE_SMALL = ENTITIES.register(
      "cataclysm_blade_small",
      () -> EntityType.Builder.<CataclysmBladeSmallProjectile>of(CataclysmBladeSmallProjectile::new, MobCategory.MISC)
         .sized(2.0F, 2.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "cataclysm_blade_small").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CataclysmBladeBigProjectile>> CATACLYSM_BLADE_BIG = ENTITIES.register(
      "cataclysm_blade_big",
      () -> EntityType.Builder.<CataclysmBladeBigProjectile>of(CataclysmBladeBigProjectile::new, MobCategory.MISC)
         .sized(2.0F, 2.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "cataclysm_blade_big").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RavenousJawEntity>> RAVENOUS_JAW = ENTITIES.register(
      "ravenous_jaw",
      () -> EntityType.Builder.<RavenousJawEntity>of(RavenousJawEntity::new, MobCategory.MISC)
         .sized(2.0F, 2.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "ravenous_jaw").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ElectromancerEntity>> ELECTROMANCER_MAGE = ENTITIES.register(
      "electromancer",
      () -> EntityType.Builder.<ElectromancerEntity>of(ElectromancerEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "electromancer").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SoulEruptionAoe>> SOUL_FIRE_ERUPTION_AOE = ENTITIES.register(
      "soul_fire_eruption",
      () -> EntityType.Builder.<SoulEruptionAoe>of(SoulEruptionAoe::new, MobCategory.MISC)
         .sized(4.0F, 0.8F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "soul_fire_eruption").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MourningStarProjectile>> MOURNING_STAR_PROJECTILE = ENTITIES.register(
      "mourning_star_projectile",
      () -> EntityType.Builder.<MourningStarProjectile>of(MourningStarProjectile::new, MobCategory.MISC)
         .sized(3.5F, 3.5F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "mourning_star_projectile").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RitualBurnAoE>> RITUAL_BURN_AOE = ENTITIES.register(
      "ritual_burn_aoe",
      () -> EntityType.Builder.<RitualBurnAoE>of(RitualBurnAoE::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "ritual_burn_aoe").toString())
   );

   public static void register(IEventBus eventBus) {
      ENTITIES.register(eventBus);
   }
}
