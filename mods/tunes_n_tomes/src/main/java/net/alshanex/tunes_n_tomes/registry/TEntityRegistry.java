package net.alshanex.tunes_n_tomes.registry;

import net.alshanex.tunes_n_tomes.entity.HarpExplosionEntity;
import net.alshanex.tunes_n_tomes.entity.aria_beam.AriaBeamEntity;
import net.alshanex.tunes_n_tomes.entity.blue_ball.BlueBallEffectEntity;
import net.alshanex.tunes_n_tomes.entity.blue_ball.BlueBallEntity;
import net.alshanex.tunes_n_tomes.entity.blue_ball.BlueBallHitEntity;
import net.alshanex.tunes_n_tomes.entity.blue_circle.BlueCircleEntity;
import net.alshanex.tunes_n_tomes.entity.celestial_chant.AngelEntity;
import net.alshanex.tunes_n_tomes.entity.celestial_chant.HealingCircleEntity;
import net.alshanex.tunes_n_tomes.entity.chord_blast.BlackNoteEntity;
import net.alshanex.tunes_n_tomes.entity.chord_blast.DefaultNoteEntity;
import net.alshanex.tunes_n_tomes.entity.fortissimo.FortissimoChargeEntity;
import net.alshanex.tunes_n_tomes.entity.fortissimo.FortissimoReleaseEntity;
import net.alshanex.tunes_n_tomes.entity.green_effect.GreenEffectEntity;
import net.alshanex.tunes_n_tomes.entity.hymn_of_hope.HopeBurstEntity;
import net.alshanex.tunes_n_tomes.entity.hymn_of_hope.HopeShieldEntity;
import net.alshanex.tunes_n_tomes.entity.red_arrow.RedArrowEntity;
import net.alshanex.tunes_n_tomes.entity.red_ball.RedBallEffectEntity;
import net.alshanex.tunes_n_tomes.entity.red_ball.RedBallEntity;
import net.alshanex.tunes_n_tomes.entity.red_ball.RedBallHitEntity;
import net.alshanex.tunes_n_tomes.entity.red_circle.RedCircleEntity;
import net.alshanex.tunes_n_tomes.entity.rhapsody.BlueBirdEntity;
import net.alshanex.tunes_n_tomes.entity.segno_marker.SegnoMarkerEntity;
import net.alshanex.tunes_n_tomes.entity.ult_strings.UltStrings1Entity;
import net.alshanex.tunes_n_tomes.entity.ult_strings.UltStrings2Entity;
import net.alshanex.tunes_n_tomes.entity.ult_strings.UltStrings3Entity;
import net.alshanex.tunes_n_tomes.entity.ult_strings.UltStrings4Entity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TEntityRegistry {
   private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "tunes_n_tomes");
   public static final DeferredHolder<EntityType<?>, EntityType<DefaultNoteEntity>> DEFAULT_NOTE = ENTITIES.register(
      "default_note",
      () -> Builder.<DefaultNoteEntity>of(DefaultNoteEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "default_note").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BlackNoteEntity>> BLACK_NOTE = ENTITIES.register(
      "black_note",
      () -> Builder.<BlackNoteEntity>of(BlackNoteEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "black_note").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<UltStrings1Entity>> ULT_STRINGS_1 = ENTITIES.register(
      "ult_strings_1",
      () -> Builder.<UltStrings1Entity>of(UltStrings1Entity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "ult_strings_1").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<UltStrings2Entity>> ULT_STRINGS_2 = ENTITIES.register(
      "ult_strings_2",
      () -> Builder.<UltStrings2Entity>of(UltStrings2Entity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "ult_strings_2").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<UltStrings3Entity>> ULT_STRINGS_3 = ENTITIES.register(
      "ult_strings_3",
      () -> Builder.<UltStrings3Entity>of(UltStrings3Entity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "ult_strings_3").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<UltStrings4Entity>> ULT_STRINGS_4 = ENTITIES.register(
      "ult_strings_4",
      () -> Builder.<UltStrings4Entity>of(UltStrings4Entity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "ult_strings_4").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HarpExplosionEntity>> HARP_EXPLOSION = ENTITIES.register(
      "harp_explosion",
      () -> Builder.<HarpExplosionEntity>of(HarpExplosionEntity::new, MobCategory.MISC)
         .sized(4.0F, 1.2F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "harp_explosion").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HealingCircleEntity>> HEALING_CIRCLE = ENTITIES.register(
      "healing_circle",
      () -> Builder.<HealingCircleEntity>of(HealingCircleEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "healing_circle").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AngelEntity>> ANGEL = ENTITIES.register(
      "angel",
      () -> Builder.<AngelEntity>of(AngelEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "angel").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BlueBirdEntity>> BLUE_BIRD = ENTITIES.register(
      "blue_bird",
      () -> Builder.<BlueBirdEntity>of(BlueBirdEntity::new, MobCategory.CREATURE)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "blue_bird").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SegnoMarkerEntity>> SEGNO_MARKER = ENTITIES.register(
      "segno_marker",
      () -> Builder.<SegnoMarkerEntity>of(SegnoMarkerEntity::new, MobCategory.MISC)
         .sized(1.0F, 2.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "segno_marker").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BlueBallEntity>> BLUE_BALL = ENTITIES.register(
      "blue_ball",
      () -> Builder.<BlueBallEntity>of(BlueBallEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "blue_ball").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BlueBallHitEntity>> BLUE_BALL_HIT = ENTITIES.register(
      "blue_ball_hit",
      () -> Builder.<BlueBallHitEntity>of(BlueBallHitEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "blue_ball_hit").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BlueBallEffectEntity>> BLUE_BALL_EFFECT = ENTITIES.register(
      "blue_ball_effect",
      () -> Builder.<BlueBallEffectEntity>of(BlueBallEffectEntity::new, MobCategory.MISC)
         .sized(1.0F, 2.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "blue_ball_effect").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RedBallEntity>> RED_BALL = ENTITIES.register(
      "red_ball",
      () -> Builder.<RedBallEntity>of(RedBallEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "red_ball").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RedBallHitEntity>> RED_BALL_HIT = ENTITIES.register(
      "red_ball_hit",
      () -> Builder.<RedBallHitEntity>of(RedBallHitEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "red_ball_hit").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RedBallEffectEntity>> RED_BALL_EFFECT = ENTITIES.register(
      "red_ball_effect",
      () -> Builder.<RedBallEffectEntity>of(RedBallEffectEntity::new, MobCategory.MISC)
         .sized(1.0F, 2.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "red_ball_effect").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HopeShieldEntity>> HOPE_SHIELD = ENTITIES.register(
      "hope_shield",
      () -> Builder.<HopeShieldEntity>of(HopeShieldEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "hope_shield").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HopeBurstEntity>> HOPE_BURST = ENTITIES.register(
      "hope_burst",
      () -> Builder.<HopeBurstEntity>of(HopeBurstEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "hope_burst").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BlueCircleEntity>> BLUE_CIRCLE = ENTITIES.register(
      "blue_circle",
      () -> Builder.<BlueCircleEntity>of(BlueCircleEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "blue_circle").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FortissimoChargeEntity>> FORTISSIMO_CHARGE = ENTITIES.register(
      "fortissimo_charge",
      () -> Builder.<FortissimoChargeEntity>of(FortissimoChargeEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "fortissimo_charge").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FortissimoReleaseEntity>> FORTISSIMO_RELEASE = ENTITIES.register(
      "fortissimo_release",
      () -> Builder.<FortissimoReleaseEntity>of(FortissimoReleaseEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "fortissimo_release").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<GreenEffectEntity>> GREEN_EFFECT = ENTITIES.register(
      "green_effect",
      () -> Builder.<GreenEffectEntity>of(GreenEffectEntity::new, MobCategory.MISC)
         .sized(1.0F, 2.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "green_effect").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AriaBeamEntity>> ARIA_BEAM = ENTITIES.register(
      "aria_beam",
      () -> Builder.<AriaBeamEntity>of(AriaBeamEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "aria_beam").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RedArrowEntity>> RED_ARROW = ENTITIES.register(
      "red_arrow",
      () -> Builder.<RedArrowEntity>of(RedArrowEntity::new, MobCategory.MISC)
         .sized(1.0F, 3.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "red_arrow").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<RedCircleEntity>> RED_CIRCLE = ENTITIES.register(
      "red_circle",
      () -> Builder.<RedCircleEntity>of(RedCircleEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "red_circle").toString())
   );

   public static void register(IEventBus eventBus) {
      ENTITIES.register(eventBus);
   }
}
