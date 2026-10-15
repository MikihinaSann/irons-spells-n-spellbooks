package com.somake.somakespells.registries;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.config.StartupFeatures;
import com.somake.somakespells.entity.AbyssalBurnEntity;
import com.somake.somakespells.entity.AbyssalOrbEntity;
import com.somake.somakespells.entity.AbyssalStrikeEntity;
import com.somake.somakespells.entity.ApocalypticBurstChargeEntity;
import com.somake.somakespells.entity.ApocalypticBurstEntity;
import com.somake.somakespells.entity.BellEntity;
import com.somake.somakespells.entity.BigWaveEntity;
import com.somake.somakespells.entity.BloodCrystalRootVisualEntity;
import com.somake.somakespells.entity.BloodboundBladeProjectileEntity;
import com.somake.somakespells.entity.BloodmarkMarkerEntity;
import com.somake.somakespells.entity.BloodmarkTraceEntity;
import com.somake.somakespells.entity.BloodmarkZoneEntity;
import com.somake.somakespells.entity.BloodyLegacyVisualEntity;
import com.somake.somakespells.entity.ConnectionChainVisualEntity;
import com.somake.somakespells.entity.ControlledWaterEntity;
import com.somake.somakespells.entity.CustodiaCaeliSigilEntity;
import com.somake.somakespells.entity.CustomSparkEntity;
import com.somake.somakespells.entity.DesertWrathControllerEntity;
import com.somake.somakespells.entity.ElementalBlazeEntity;
import com.somake.somakespells.entity.ElementalChargeBeamEntity;
import com.somake.somakespells.entity.EruptionEntity;
import com.somake.somakespells.entity.FireBlastVisualEntity;
import com.somake.somakespells.entity.FirestormVortexEntity;
import com.somake.somakespells.entity.FragmentedSwordRainEntity;
import com.somake.somakespells.entity.FuneralBloomBudEntity;
import com.somake.somakespells.entity.FuneralBloomControllerEntity;
import com.somake.somakespells.entity.FuneralBloomImpactEntity;
import com.somake.somakespells.entity.GeoGroundEntity;
import com.somake.somakespells.entity.GraveSigilControllerEntity;
import com.somake.somakespells.entity.IgnisShieldEntity;
import com.somake.somakespells.entity.IncineratorSlashEntity;
import com.somake.somakespells.entity.LightningBallProjectile;
import com.somake.somakespells.entity.LightningFieldEntity;
import com.somake.somakespells.entity.LightningLanceDanceProjectile;
import com.somake.somakespells.entity.MirrorStrikeVisualEntity;
import com.somake.somakespells.entity.PhantomBarrageProjectile;
import com.somake.somakespells.entity.ProcessionOfSoulsControllerEntity;
import com.somake.somakespells.entity.PumpkinBombEntity;
import com.somake.somakespells.entity.PumpkinZoneEntity;
import com.somake.somakespells.entity.RedEchoEntity;
import com.somake.somakespells.entity.RitualFireSigilEntity;
import com.somake.somakespells.entity.SacredPhoenixBlessingControllerEntity;
import com.somake.somakespells.entity.SeaSerpentJetEntity;
import com.somake.somakespells.entity.SomakeGhostEntity;
import com.somake.somakespells.entity.SoulBastionControllerEntity;
import com.somake.somakespells.entity.SoulBastionShieldEntity;
import com.somake.somakespells.entity.SoulGrabControllerEntity;
import com.somake.somakespells.entity.SoulLatchControllerEntity;
import com.somake.somakespells.entity.SoulLatchTetherEntity;
import com.somake.somakespells.entity.SoulReprisalDaggerEntity;
import com.somake.somakespells.entity.SoulfallJudgmentControllerEntity;
import com.somake.somakespells.entity.SoulfireOrbEntity;
import com.somake.somakespells.entity.SovereignArmoryPortalEntity;
import com.somake.somakespells.entity.SovereignArmoryProjectileEntity;
import com.somake.somakespells.entity.SpectralRondoControllerEntity;
import com.somake.somakespells.entity.SpellAnchorEntity;
import com.somake.somakespells.entity.SpellLightningSpearEntity;
import com.somake.somakespells.entity.SpellStormSerpentEntity;
import com.somake.somakespells.entity.SpellWaterSpearEntity;
import com.somake.somakespells.entity.SpiralOfRuinControllerEntity;
import com.somake.somakespells.entity.SpiralOfRuinProjectileEntity;
import com.somake.somakespells.entity.SummonedDrownedEntity;
import com.somake.somakespells.entity.ThrustLightningEntity;
import com.somake.somakespells.entity.WaterBallEntity;
import com.somake.somakespells.entity.WaterSplashEntity;
import com.somake.somakespells.entity.WingedRuinControllerEntity;
import com.somake.somakespells.entity.WitheredRoseVortexEntity;
import com.somake.somakespells.entity.spells.ResonantPulseProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
   public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "somakespells");
   private static final boolean ISS_LOADED = MagicFromTheEastCompat.isLoaded();
   private static final boolean LEGENDARY_MONSTERS_LOADED = LegendaryMonstersCompat.isLoaded();
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.AquaGhostEntity>> AQUA_GHOST = registerGhost(
      "aqua_ghost", SomakeGhostEntity.AquaGhostEntity::new
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.FireGhostEntity>> FIRE_GHOST = registerGhost(
      "fire_ghost", SomakeGhostEntity.FireGhostEntity::new
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.IceGhostEntity>> ICE_GHOST = registerGhost(
      "ice_ghost", SomakeGhostEntity.IceGhostEntity::new
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.LightningGhostEntity>> LIGHTNING_GHOST = registerGhost(
      "lightning_ghost", SomakeGhostEntity.LightningGhostEntity::new
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.HolyGhostEntity>> HOLY_GHOST = registerGhost(
      "holy_ghost", SomakeGhostEntity.HolyGhostEntity::new
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.EnderGhostEntity>> ENDER_GHOST = registerGhost(
      "ender_ghost", SomakeGhostEntity.EnderGhostEntity::new
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.BloodGhostEntity>> BLOOD_GHOST = registerGhost(
      "blood_ghost", SomakeGhostEntity.BloodGhostEntity::new
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.EvocationGhostEntity>> EVOCATION_GHOST = registerGhost(
      "evocation_ghost", SomakeGhostEntity.EvocationGhostEntity::new
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.NatureGhostEntity>> NATURE_GHOST = registerGhost(
      "nature_ghost", SomakeGhostEntity.NatureGhostEntity::new
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.EldritchGhostEntity>> ELDRITCH_GHOST = registerGhost(
      "eldritch_ghost", SomakeGhostEntity.EldritchGhostEntity::new
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.SpiritGhostEntity>> SPIRIT_GHOST = LEGENDARY_MONSTERS_LOADED
      ? registerGhost("spirit_ghost", SomakeGhostEntity.SpiritGhostEntity::new)
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.SymmetryGhostEntity>> SYMMETRY_GHOST = ISS_LOADED
      ? registerGhost("symmetry_ghost", SomakeGhostEntity.SymmetryGhostEntity::new)
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<LightningBallProjectile>> LIGHTNING_BALL_PROJECTILE = ENTITIES.register(
      "lightning_ball_projectile",
      () -> EntityType.Builder.<LightningBallProjectile>of(LightningBallProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(10)
         .build("lightning_ball_projectile")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<LightningLanceDanceProjectile>> LIGHTNING_LANCE_DANCE_PROJECTILE = ENTITIES.register(
      "lightning_lance_dance_projectile",
      () -> EntityType.Builder.<LightningLanceDanceProjectile>of(LightningLanceDanceProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .build("lightning_lance_dance_projectile")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<LightningFieldEntity>> LIGHTNING_FIELD = ENTITIES.register(
      "lightning_field",
      () -> EntityType.Builder.<LightningFieldEntity>of(LightningFieldEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(64).updateInterval(10).build("lightning_field")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CustodiaCaeliSigilEntity>> CUSTODIA_CAELI_SIGIL = ENTITIES.register(
      "custodia_caeli_sigil",
      () -> EntityType.Builder.<CustodiaCaeliSigilEntity>of(CustodiaCaeliSigilEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .build("custodia_caeli_sigil")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SovereignArmoryPortalEntity>> SOVEREIGN_ARMORY_PORTAL = ENTITIES.register(
      "sovereign_armory_portal",
      () -> EntityType.Builder.<SovereignArmoryPortalEntity>of(SovereignArmoryPortalEntity::new, MobCategory.MISC)
         .sized(2.4F, 2.4F)
         .clientTrackingRange(96)
         .updateInterval(1)
         .fireImmune()
         .noSave()
         .build("sovereign_armory_portal")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SovereignArmoryProjectileEntity>> SOVEREIGN_ARMORY_PROJECTILE = ENTITIES.register(
      "sovereign_armory_projectile",
      () -> EntityType.Builder.<SovereignArmoryProjectileEntity>of(SovereignArmoryProjectileEntity::new, MobCategory.MISC)
         .sized(0.45F, 0.45F)
         .clientTrackingRange(96)
         .updateInterval(1)
         .fireImmune()
         .noSave()
         .build("sovereign_armory_projectile")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ElementalChargeBeamEntity>> ELEMENTAL_CHARGE_BEAM = ENTITIES.register(
      "elemental_charge_beam",
      () -> EntityType.Builder.<ElementalChargeBeamEntity>of(ElementalChargeBeamEntity::new, MobCategory.MISC)
         .sized(1.5F, 14.0F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .build("elemental_charge_beam")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SacredPhoenixBlessingControllerEntity>> SACRED_PHOENIX_BLESSING_CONTROLLER = StartupFeatures.isSacredPhoenixBlessingEnabled()
      ? ENTITIES.register(
         "sacred_phoenix_blessing_controller",
         () -> EntityType.Builder.<SacredPhoenixBlessingControllerEntity>of(SacredPhoenixBlessingControllerEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(96)
            .updateInterval(1)
            .noSave()
            .build("sacred_phoenix_blessing_controller")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<ThrustLightningEntity>> THRUST_LIGHTNING_ENTITY = ENTITIES.register(
      "thrust_lightning_entity",
      () -> EntityType.Builder.<ThrustLightningEntity>of(ThrustLightningEntity::new, MobCategory.MISC)
         .sized(1.5F, 1.5F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .build("thrust_lightning_entity")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CustomSparkEntity>> CUSTOM_SPARK_ENTITY = ENTITIES.register(
      "custom_spark_entity",
      () -> EntityType.Builder.<CustomSparkEntity>of(CustomSparkEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(10).build("custom_spark_entity")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SpellWaterSpearEntity>> SPELL_WATER_SPEAR = ENTITIES.register(
      "spell_water_spear",
      () -> EntityType.Builder.<SpellWaterSpearEntity>of(SpellWaterSpearEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(5)
         .noSave()
         .build("spell_water_spear")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SpellLightningSpearEntity>> SPELL_LIGHTNING_SPEAR = ENTITIES.register(
      "spell_lightning_spear",
      () -> EntityType.Builder.<SpellLightningSpearEntity>of(SpellLightningSpearEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(5)
         .noSave()
         .build("spell_lightning_spear")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SpellAnchorEntity>> SPELL_ANCHOR = ENTITIES.register(
      "spell_anchor",
      () -> EntityType.Builder.<SpellAnchorEntity>of(SpellAnchorEntity::new, MobCategory.MISC).sized(1.5F, 1.5F).clientTrackingRange(64).updateInterval(20).build("spell_anchor")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SpellStormSerpentEntity>> SPELL_STORM_SERPENT = ENTITIES.register(
      "spell_storm_serpent",
      () -> EntityType.Builder.<SpellStormSerpentEntity>of(SpellStormSerpentEntity::new, MobCategory.MISC)
         .sized(2.0F, 9.0F)
         .clientTrackingRange(6)
         .updateInterval(2)
         .fireImmune()
         .build("spell_storm_serpent")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BigWaveEntity>> BIG_WAVE = ENTITIES.register(
      "big_wave",
      () -> EntityType.Builder.<BigWaveEntity>of(BigWaveEntity::new, MobCategory.MISC).sized(5.0F, 6.0F).clientTrackingRange(64).updateInterval(1).fireImmune().build("big_wave")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GeoGroundEntity>> GEO_GROUND = ENTITIES.register(
      "geo_ground", () -> EntityType.Builder.<GeoGroundEntity>of(GeoGroundEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(64).updateInterval(2).build("geo_ground")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<WaterSplashEntity>> WATER_SPLASH = ENTITIES.register(
      "water_splash",
      () -> EntityType.Builder.<WaterSplashEntity>of(WaterSplashEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(64).updateInterval(2).build("water_splash")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedDrownedEntity>> SUMMONED_DROWNED = ENTITIES.register(
      "summoned_drowned",
      () -> EntityType.Builder.<SummonedDrownedEntity>of(SummonedDrownedEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(64).updateInterval(3).build("summoned_drowned")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ElementalBlazeEntity.SoulBlazeEntity>> SOUL_BLAZE = ENTITIES.register(
      "soul_blaze",
      () -> Builder.of(ElementalBlazeEntity.SoulBlazeEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .build("soul_blaze")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PhantomBarrageProjectile>> PHANTOM_BARRAGE_PROJECTILE = ENTITIES.register(
      "phantom_barrage_projectile",
      () -> EntityType.Builder.<PhantomBarrageProjectile>of(PhantomBarrageProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(3)
         .build("phantom_barrage_projectile")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SoulGrabControllerEntity>> SOUL_GRAB_CONTROLLER = ENTITIES.register(
      "soul_grab_controller",
      () -> EntityType.Builder.<SoulGrabControllerEntity>of(SoulGrabControllerEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .updateInterval(3)
         .build("soul_grab_controller")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ProcessionOfSoulsControllerEntity>> PROCESSION_OF_SOULS_CONTROLLER = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "procession_of_souls_controller",
         () -> EntityType.Builder.<ProcessionOfSoulsControllerEntity>of(ProcessionOfSoulsControllerEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .noSave()
            .build("procession_of_souls_controller")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<GraveSigilControllerEntity>> GRAVE_SIGIL_CONTROLLER = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "grave_sigil_controller",
         () -> EntityType.Builder.<GraveSigilControllerEntity>of(GraveSigilControllerEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .noSave()
            .build("grave_sigil_controller")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<SoulBastionControllerEntity>> SOUL_BASTION_CONTROLLER = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "soul_bastion_controller",
         () -> EntityType.Builder.<SoulBastionControllerEntity>of(SoulBastionControllerEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .noSave()
            .build("soul_bastion_controller")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<SoulBastionShieldEntity>> SOUL_BASTION_SHIELD = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "soul_bastion_shield",
         () -> EntityType.Builder.<SoulBastionShieldEntity>of(SoulBastionShieldEntity::new, MobCategory.MISC)
            .sized(1.75F, 2.75F)
            .clientTrackingRange(64)
            .updateInterval(2)
            .noSave()
            .build("soul_bastion_shield")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<SoulLatchControllerEntity>> SOUL_LATCH_CONTROLLER = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "soul_latch_controller",
         () -> EntityType.Builder.<SoulLatchControllerEntity>of(SoulLatchControllerEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .noSave()
            .build("soul_latch_controller")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<SoulLatchTetherEntity>> SOUL_LATCH_TETHER = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "soul_latch_tether",
         () -> EntityType.Builder.<SoulLatchTetherEntity>of(SoulLatchTetherEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .noSave()
            .build("soul_latch_tether")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<SpiralOfRuinProjectileEntity>> SPIRAL_OF_RUIN_PROJECTILE = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "spiral_of_ruin_projectile",
         () -> EntityType.Builder.<SpiralOfRuinProjectileEntity>of(SpiralOfRuinProjectileEntity::new, MobCategory.MISC)
            .sized(1.0F, 1.0F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .noSave()
            .build("spiral_of_ruin_projectile")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<SpiralOfRuinControllerEntity>> SPIRAL_OF_RUIN_CONTROLLER = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "spiral_of_ruin_controller",
         () -> EntityType.Builder.<SpiralOfRuinControllerEntity>of(SpiralOfRuinControllerEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .noSave()
            .build("spiral_of_ruin_controller")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<SoulfallJudgmentControllerEntity>> SOULFALL_JUDGMENT_CONTROLLER = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "soulfall_judgment_controller",
         () -> EntityType.Builder.<SoulfallJudgmentControllerEntity>of(SoulfallJudgmentControllerEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .noSave()
            .build("soulfall_judgment_controller")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<SoulReprisalDaggerEntity>> SOUL_REPRISAL_DAGGER = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "soul_reprisal_dagger",
         () -> EntityType.Builder.<SoulReprisalDaggerEntity>of(SoulReprisalDaggerEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .noSave()
            .build("soul_reprisal_dagger")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<SpectralRondoControllerEntity>> SPECTRAL_RONDO_CONTROLLER = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "spectral_rondo_controller",
         () -> EntityType.Builder.<SpectralRondoControllerEntity>of(SpectralRondoControllerEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .noSave()
            .build("spectral_rondo_controller")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<WingedRuinControllerEntity>> WINGED_RUIN_CONTROLLER = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "winged_ruin_controller",
         () -> EntityType.Builder.<WingedRuinControllerEntity>of(WingedRuinControllerEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(96)
            .updateInterval(1)
            .noSave()
            .build("winged_ruin_controller")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<RedEchoEntity>> RED_ECHO = LEGENDARY_MONSTERS_LOADED
      ? ENTITIES.register(
         "red_echo",
         () -> EntityType.Builder.<RedEchoEntity>of(RedEchoEntity::new, MobCategory.MONSTER).sized(0.6F, 1.8F).clientTrackingRange(96).updateInterval(1).noSave().build("red_echo")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<DesertWrathControllerEntity>> DESERT_WRATH_CONTROLLER = ENTITIES.register(
      "desert_wrath_controller",
      () -> EntityType.Builder.<DesertWrathControllerEntity>of(DesertWrathControllerEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .updateInterval(3)
         .build("desert_wrath_controller")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IgnisShieldEntity>> IGNIS_SHIELD = ENTITIES.register(
      "ignis_shield",
      () -> EntityType.Builder.<IgnisShieldEntity>of(IgnisShieldEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(64).updateInterval(10).build("ignis_shield")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IncineratorSlashEntity>> INCINERATOR_SLASH = ENTITIES.register(
      "incinerator_slash",
      () -> EntityType.Builder.<IncineratorSlashEntity>of(IncineratorSlashEntity::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(64).updateInterval(3).build("incinerator_slash")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AbyssalBurnEntity>> ABYSSAL_BURN = ENTITIES.register(
      "abyssal_burn",
      () -> EntityType.Builder.<AbyssalBurnEntity>of(AbyssalBurnEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(64).updateInterval(3).build("abyssal_burn")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AbyssalStrikeEntity>> ABYSSAL_STRIKE = ENTITIES.register(
      "abyssal_strike",
      () -> EntityType.Builder.<AbyssalStrikeEntity>of(AbyssalStrikeEntity::new, MobCategory.MISC).sized(1.5F, 2.0F).clientTrackingRange(64).updateInterval(3).build("abyssal_strike")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SoulfireOrbEntity>> SOULFIRE_ORB = ENTITIES.register(
      "soulfire_orb",
      () -> EntityType.Builder.<SoulfireOrbEntity>of(SoulfireOrbEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(3).build("soulfire_orb")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AbyssalOrbEntity>> ABYSSAL_ORB = ENTITIES.register(
      "abyssal_orb", () -> EntityType.Builder.<AbyssalOrbEntity>of(AbyssalOrbEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(3).build("abyssal_orb")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ApocalypticBurstEntity>> APOCALYPTIC_BURST = ENTITIES.register(
      "apocalyptic_burst",
      () -> EntityType.Builder.<ApocalypticBurstEntity>of(ApocalypticBurstEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(64).updateInterval(3).build("apocalyptic_burst")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ApocalypticBurstChargeEntity>> APOCALYPTIC_BURST_CHARGE = ENTITIES.register(
      "apocalyptic_burst_charge",
      () -> EntityType.Builder.<ApocalypticBurstChargeEntity>of(ApocalypticBurstChargeEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .updateInterval(3)
         .build("apocalyptic_burst_charge")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PumpkinBombEntity>> PUMPKIN_BOMB = ENTITIES.register(
      "pumpkin_bomb",
      () -> EntityType.Builder.<PumpkinBombEntity>of(PumpkinBombEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(10).build("pumpkin_bomb")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PumpkinZoneEntity>> PUMPKIN_ZONE = ENTITIES.register(
      "pumpkin_zone",
      () -> EntityType.Builder.<PumpkinZoneEntity>of(PumpkinZoneEntity::new, MobCategory.MISC).sized(6.0F, 2.0F).clientTrackingRange(64).updateInterval(10).build("pumpkin_zone")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FireBlastVisualEntity>> FIRE_BLAST_VISUAL_ENTITY = ENTITIES.register(
      "fire_blast_visual_entity",
      () -> EntityType.Builder.<FireBlastVisualEntity>of(FireBlastVisualEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(10)
         .build("fire_blast_visual_entity")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FirestormVortexEntity>> FIRESTORM_VORTEX = ENTITIES.register(
      "firestorm_vortex",
      () -> EntityType.Builder.<FirestormVortexEntity>of(FirestormVortexEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(2)
         .noSave()
         .fireImmune()
         .build("firestorm_vortex")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<WitheredRoseVortexEntity>> WITHERED_ROSE_VORTEX = ENTITIES.register(
      "withered_rose_vortex",
      () -> EntityType.Builder.<WitheredRoseVortexEntity>of(WitheredRoseVortexEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(2)
         .noSave()
         .build("withered_rose_vortex")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FuneralBloomControllerEntity>> FUNERAL_BLOOM_CONTROLLER = ENTITIES.register(
      "funeral_bloom_controller",
      () -> EntityType.Builder.<FuneralBloomControllerEntity>of(FuneralBloomControllerEntity::new, MobCategory.MISC)
         .sized(0.2F, 0.2F)
         .clientTrackingRange(96)
         .updateInterval(1)
         .noSave()
         .build("funeral_bloom_controller")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FuneralBloomBudEntity>> FUNERAL_BLOOM_BUD = ENTITIES.register(
      "funeral_bloom_bud",
      () -> EntityType.Builder.<FuneralBloomBudEntity>of(FuneralBloomBudEntity::new, MobCategory.MISC)
         .sized(0.65F, 0.9F)
         .clientTrackingRange(96)
         .updateInterval(1)
         .noSave()
         .build("funeral_bloom_bud")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FuneralBloomImpactEntity>> FUNERAL_BLOOM_IMPACT = ENTITIES.register(
      "funeral_bloom_impact",
      () -> EntityType.Builder.<FuneralBloomImpactEntity>of(FuneralBloomImpactEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(96)
         .updateInterval(1)
         .noSave()
         .build("funeral_bloom_impact")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<EruptionEntity>> ERUPTION = ENTITIES.register(
      "eruption", () -> EntityType.Builder.<EruptionEntity>of(EruptionEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(64).updateInterval(10).build("eruption")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SeaSerpentJetEntity>> SEA_SERPENT_JET = ENTITIES.register(
      "sea_serpent_jet",
      () -> EntityType.Builder.<SeaSerpentJetEntity>of(SeaSerpentJetEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(64).updateInterval(2).build("sea_serpent_jet")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<WaterBallEntity>> WATER_BALL = ENTITIES.register(
      "water_ball", () -> EntityType.Builder.<WaterBallEntity>of(WaterBallEntity::new, MobCategory.MISC).sized(1.5F, 1.5F).clientTrackingRange(64).updateInterval(2).build("water_ball")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ControlledWaterEntity>> CONTROLLED_WATER = ENTITIES.register(
      "controlled_water",
      () -> EntityType.Builder.<ControlledWaterEntity>of(ControlledWaterEntity::new, MobCategory.MISC)
         .sized(0.75F, 0.75F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .build("controlled_water")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ConnectionChainVisualEntity>> CONNECTION_CHAIN_VISUAL = ENTITIES.register(
      "connection_chain_visual",
      () -> EntityType.Builder.<ConnectionChainVisualEntity>of(ConnectionChainVisualEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .build("connection_chain_visual")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RitualFireSigilEntity>> RITUAL_FIRE_SIGIL = ENTITIES.register(
      "ritual_fire_sigil",
      () -> EntityType.Builder.<RitualFireSigilEntity>of(RitualFireSigilEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .build("ritual_fire_sigil")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloodmarkTraceEntity>> BLOODMARK_TRACE = ENTITIES.register(
      "bloodmark_trace",
      () -> EntityType.Builder.<BloodmarkTraceEntity>of(BloodmarkTraceEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .build("bloodmark_trace")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloodmarkMarkerEntity>> BLOODMARK_MARKER = ENTITIES.register(
      "bloodmark_marker",
      () -> EntityType.Builder.<BloodmarkMarkerEntity>of(BloodmarkMarkerEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .build("bloodmark_marker")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloodmarkZoneEntity>> BLOODMARK_ZONE = ENTITIES.register(
      "bloodmark_zone",
      () -> EntityType.Builder.<BloodmarkZoneEntity>of(BloodmarkZoneEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(64).updateInterval(1).noSave().build("bloodmark_zone")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FragmentedSwordRainEntity>> FRAGMENTED_SWORD_RAIN = ENTITIES.register(
      "fragmented_sword_rain",
      () -> EntityType.Builder.<FragmentedSwordRainEntity>of(FragmentedSwordRainEntity::new, MobCategory.MISC)
         .sized(0.75F, 4.0F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .build("fragmented_sword_rain")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ResonantPulseProjectile>> RESONANT_PULSE_PROJECTILE = ENTITIES.register(
      "resonant_pulse_projectile",
      () -> EntityType.Builder.<ResonantPulseProjectile>of(ResonantPulseProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(3)
         .build("resonant_pulse_projectile")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MirrorStrikeVisualEntity>> MIRROR_STRIKE_VISUAL = ISS_LOADED
      ? ENTITIES.register(
         "mirror_strike_visual",
         () -> EntityType.Builder.<MirrorStrikeVisualEntity>of(MirrorStrikeVisualEntity::new, MobCategory.MISC)
            .sized(1.0F, 0.5F)
            .clientTrackingRange(64)
            .updateInterval(3)
            .build("mirror_strike_visual")
      )
      : null;
   public static final DeferredHolder<EntityType<?>, EntityType<BellEntity>> BELL = ENTITIES.register(
      "bell", () -> EntityType.Builder.<BellEntity>of(BellEntity::new, MobCategory.MISC).sized(1.2F, 1.8F).clientTrackingRange(64).updateInterval(3).build("bell")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloodyLegacyVisualEntity>> BLOODY_LEGACY_VISUAL = ENTITIES.register(
      "bloody_legacy_visual",
      () -> EntityType.Builder.<BloodyLegacyVisualEntity>of(BloodyLegacyVisualEntity::new, MobCategory.MISC)
         .sized(4.0F, 3.5F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .build("bloody_legacy_visual")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloodboundBladeProjectileEntity>> BLOODBOUND_BLADE_PROJECTILE = ENTITIES.register(
      "bloodbound_blade_projectile",
      () -> EntityType.Builder.<BloodboundBladeProjectileEntity>of(BloodboundBladeProjectileEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .build("bloodbound_blade_projectile")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BloodCrystalRootVisualEntity>> BLOOD_CRYSTAL_ROOT_VISUAL = ENTITIES.register(
      "blood_crystal_root_visual",
      () -> EntityType.Builder.<BloodCrystalRootVisualEntity>of(BloodCrystalRootVisualEntity::new, MobCategory.MISC)
         .sized(1.2F, 2.0F)
         .clientTrackingRange(96)
         .updateInterval(1)
         .noSave()
         .build("blood_crystal_root_visual")
   );

   private static <T extends SomakeGhostEntity> DeferredHolder<EntityType<?>, EntityType<T>> registerGhost(String id, EntityFactory<T> factory) {
      return ENTITIES.register(id, () -> Builder.of(factory, MobCategory.MONSTER).sized(0.7F, 1.95F).clientTrackingRange(64).updateInterval(3).build(id));
   }

   public static void register(IEventBus eventBus) {
      ENTITIES.register(eventBus);
   }
}
