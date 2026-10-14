package com.gametechbc.traveloptics.init;

import com.gametechbc.traveloptics.entity.extended.ExtendedAbyssBlastEntity;
import com.gametechbc.traveloptics.entity.extended.ExtendedDeathLaserBeamEntity;
import com.gametechbc.traveloptics.entity.extended.ExtendedFlareBombEntity;
import com.gametechbc.traveloptics.entity.extended.ExtendedIgnisFireballEntity;
import com.gametechbc.traveloptics.entity.extended.ExtendedSandstormEntity;
import com.gametechbc.traveloptics.entity.extended.ExtendedWitherHowitzerEntity;
import com.gametechbc.traveloptics.entity.spells.AerialCollapseVisualEntity;
import com.gametechbc.traveloptics.entity.spells.AshenBreathProjectile;
import com.gametechbc.traveloptics.entity.spells.BlackoutAntiMagicField;
import com.gametechbc.traveloptics.entity.spells.BubbleSprayProjectile;
import com.gametechbc.traveloptics.entity.spells.EnsnareEntity;
import com.gametechbc.traveloptics.entity.spells.gyro_slash.GyroSlashProjectile;
import com.gametechbc.traveloptics.entity.spells.gyro_slash.GyroSlashVisual;
import com.gametechbc.traveloptics.entity.spells.psychic_bolt.PsychicBoltProjectile;
import com.gametechbc.traveloptics.entity.spells.reversal.ReversalEntity;
import com.gametechbc.traveloptics.entity.summons.SummonedAptrgangr;
import com.gametechbc.traveloptics.entity.summons.SummonedDraugr;
import com.gametechbc.traveloptics.entity.summons.SummonedEliteDraugr;
import com.gametechbc.traveloptics.entity.summons.SummonedEnderGolem;
import com.gametechbc.traveloptics.entity.summons.SummonedIgnitedBerserker;
import com.gametechbc.traveloptics.entity.summons.SummonedIgnitedRevenant;
import com.gametechbc.traveloptics.entity.summons.SummonedKobolediator;
import com.gametechbc.traveloptics.entity.summons.SummonedKoboleton;
import com.gametechbc.traveloptics.entity.summons.SummonedRoyalDraugr;
import com.gametechbc.traveloptics.entity.summons.SummonedTheProwler;
import com.gametechbc.traveloptics.entity.summons.SummonedTheWatcher;
import com.gametechbc.traveloptics.entity.summons.SummonedWadjet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TOEntities {
   private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "traveloptics");
   public static final DeferredHolder<EntityType<?>, EntityType<ReversalEntity>> REVERSAL = ENTITIES.register(
      "reversal",
      () -> Builder.<ReversalEntity>of(ReversalEntity::new, MobCategory.MISC)
         .sized(5.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("traveloptics", "reversal").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AshenBreathProjectile>> ASHEN_BREATH_PROJECTILE = ENTITIES.register(
      "ashen_breath",
      () -> Builder.<AshenBreathProjectile>of(AshenBreathProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("traveloptics", "ashen_breath").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BubbleSprayProjectile>> BUBBLE_SPRAY_PROJECTILE = ENTITIES.register(
      "bubble_spray",
      () -> Builder.<BubbleSprayProjectile>of(BubbleSprayProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("traveloptics", "bubble_spray").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BlackoutAntiMagicField>> BLACKOUT_ANTI_MAGIC_FIELD = ENTITIES.register(
      "blackout_anti_magic_field",
      () -> Builder.<BlackoutAntiMagicField>of(BlackoutAntiMagicField::new, MobCategory.MISC)
         .sized(4.0F, 1.2F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("traveloptics", "blackout_anti_magic_field").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<EnsnareEntity>> ENSNARE = ENTITIES.register(
      "ensnare",
      () -> Builder.<EnsnareEntity>of(EnsnareEntity::new, MobCategory.MISC)
         .sized(4.0F, 1.2F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("traveloptics", "ensnare").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AerialCollapseVisualEntity>> AERIAL_COLLAPSE_VISUAL_ENTITY = ENTITIES.register(
      "aerial_collapse_visual_entity",
      () -> Builder.<AerialCollapseVisualEntity>of(AerialCollapseVisualEntity::new, MobCategory.MISC)
         .sized(4.0F, 1.2F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("traveloptics", "aerial_collapse_visual_entity").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GyroSlashProjectile>> GYRO_SLASH_PROJECTILE = ENTITIES.register(
      "gyro_slash_projectile",
      () -> Builder.<GyroSlashProjectile>of(GyroSlashProjectile::new, MobCategory.MISC)
         .sized(2.0F, 0.5F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("traveloptics", "gyro_slash_projectile").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GyroSlashVisual>> GYRO_SLASH_VISUAL = ENTITIES.register(
      "gyro_slash_visual",
      () -> Builder.<GyroSlashVisual>of(GyroSlashVisual::new, MobCategory.MISC)
         .sized(5.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("traveloptics", "gyro_slash_visual").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PsychicBoltProjectile>> PSYCHIC_BOLT_PROJECTILE = ENTITIES.register(
      "psychic_bolt_projectile",
      () -> Builder.<PsychicBoltProjectile>of(PsychicBoltProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("traveloptics", "psychic_bolt_projectile").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ExtendedWitherHowitzerEntity>> EXTENDED_WITHER_HOWITZER = ENTITIES.register(
      "extended_wither_howitzer",
      () -> Builder.<ExtendedWitherHowitzerEntity>of(ExtendedWitherHowitzerEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .fireImmune()
         .setShouldReceiveVelocityUpdates(true)
         .setUpdateInterval(1)
         .build("traveloptics:extended_wither_howitzer")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ExtendedSandstormEntity>> EXTENDED_SANDSTORM = ENTITIES.register(
      "extended_sandstorm",
      () -> Builder.<ExtendedSandstormEntity>of(ExtendedSandstormEntity::new, MobCategory.MISC)
         .sized(2.5F, 4.5F)
         .fireImmune()
         .clientTrackingRange(10)
         .updateInterval(Integer.MAX_VALUE)
         .build("traveloptics:extended_sandstorm")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ExtendedAbyssBlastEntity>> EXTENDED_ABYSS_BLAST = ENTITIES.register(
      "extended_abyss_blast",
      () -> Builder.<ExtendedAbyssBlastEntity>of(ExtendedAbyssBlastEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).fireImmune().build("traveloptics:extended_abyss_blast")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ExtendedDeathLaserBeamEntity>> EXTENDED_DEATH_LASER_BEAM = ENTITIES.register(
      "extended_death_laser_beam",
      () -> Builder.<ExtendedDeathLaserBeamEntity>of(ExtendedDeathLaserBeamEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).fireImmune().build("traveloptics:extended_death_laser_beam")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ExtendedIgnisFireballEntity>> EXTENDED_IGNIS_FIREBALL = ENTITIES.register(
      "extended_ignis_fireball",
      () -> Builder.<ExtendedIgnisFireballEntity>of(ExtendedIgnisFireballEntity::new, MobCategory.MISC)
         .sized(0.6F, 0.6F)
         .setUpdateInterval(1)
         .setTrackingRange(20)
         .setShouldReceiveVelocityUpdates(true)
         .build("traveloptics:extended_ignis_fireball")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ExtendedFlareBombEntity>> EXTENDED_FLARE_BOMB = ENTITIES.register(
      "extended_flare_bomb",
      () -> Builder.<ExtendedFlareBombEntity>of(ExtendedFlareBombEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .fireImmune()
         .setShouldReceiveVelocityUpdates(true)
         .setUpdateInterval(1)
         .build("traveloptics:extended_flare_bomb")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedAptrgangr>> SUMMONED_APTRGANGR = ENTITIES.register(
      "summoned_aptrgangr",
      () -> Builder.<SummonedAptrgangr>of(SummonedAptrgangr::new, MobCategory.MONSTER).sized(2.4F, 4.0F).clientTrackingRange(8).build("traveloptics:summoned_aptrgangr")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedDraugr>> SUMMONED_DRAUGR = ENTITIES.register(
      "summoned_draugr",
      () -> Builder.<SummonedDraugr>of(SummonedDraugr::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(10).build("traveloptics:summoned_draugr")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedRoyalDraugr>> SUMMONED_ROYAL_DRAUGR = ENTITIES.register(
      "summoned_royal_draugr",
      () -> Builder.<SummonedRoyalDraugr>of(SummonedRoyalDraugr::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(10).build("traveloptics:summoned_royal_draugr")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedEliteDraugr>> SUMMONED_ELITE_DRAUGR = ENTITIES.register(
      "summoned_elite_draugr",
      () -> Builder.<SummonedEliteDraugr>of(SummonedEliteDraugr::new, MobCategory.MONSTER).sized(0.8F, 2.6F).clientTrackingRange(10).build("traveloptics:summoned_elite_draugr")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedKoboleton>> SUMMONED_KOBOLETON = ENTITIES.register(
      "summoned_koboleton",
      () -> Builder.<SummonedKoboleton>of(SummonedKoboleton::new, MobCategory.MONSTER)
         .sized(0.85F, 1.6F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("traveloptics", "summoned_koboleton").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedKobolediator>> SUMMONED_KOBOLEDIATOR = ENTITIES.register(
      "summoned_kobolediator",
      () -> Builder.<SummonedKobolediator>of(SummonedKobolediator::new, MobCategory.MONSTER).sized(2.4F, 4.4F).clientTrackingRange(8).build("traveloptics:summoned_kobolediator")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedWadjet>> SUMMONED_WADJET = ENTITIES.register(
      "summoned_wadjet",
      () -> Builder.<SummonedWadjet>of(SummonedWadjet::new, MobCategory.MONSTER).sized(0.85F, 3.4F).clientTrackingRange(8).build("traveloptics:summoned_wadjet")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedTheProwler>> SUMMONED_THE_PROWLER = ENTITIES.register(
      "summoned_the_prowler",
      () -> Builder.<SummonedTheProwler>of(SummonedTheProwler::new, MobCategory.MONSTER)
         .sized(2.5F, 2.75F)
         .fireImmune()
         .clientTrackingRange(8)
         .build("traveloptics:summoned_the_prowler")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedTheWatcher>> SUMMONED_THE_WATCHER = ENTITIES.register(
      "summoned_the_watcher",
      () -> Builder.<SummonedTheWatcher>of(SummonedTheWatcher::new, MobCategory.MONSTER).sized(0.85F, 0.85F).fireImmune().build("traveloptics:summoned_the_watcher")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedIgnitedRevenant>> SUMMONED_IGNITED_REVENANT = ENTITIES.register(
      "summoned_ignited_revenant",
      () -> Builder.<SummonedIgnitedRevenant>of(SummonedIgnitedRevenant::new, MobCategory.MONSTER).sized(1.6F, 2.8F).fireImmune().build("traveloptics:summoned_ignited_revenant")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedIgnitedBerserker>> SUMMONED_IGNITED_BERSERKER = ENTITIES.register(
      "summoned_ignited_berserker",
      () -> Builder.<SummonedIgnitedBerserker>of(SummonedIgnitedBerserker::new, MobCategory.MONSTER).sized(1.0F, 2.4F).fireImmune().build("traveloptics:summoned_ignited_berserker")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedEnderGolem>> SUMMONED_ENDER_GOLEM = ENTITIES.register(
      "summoned_ender_golem",
      () -> Builder.<SummonedEnderGolem>of(SummonedEnderGolem::new, MobCategory.MONSTER).sized(2.5F, 3.5F).fireImmune().build("traveloptics:summoned_ender_golem")
   );

   public TOEntities() {
      super();
   }

   public static void register(IEventBus eventBus) {
      ENTITIES.register(eventBus);
   }
}
