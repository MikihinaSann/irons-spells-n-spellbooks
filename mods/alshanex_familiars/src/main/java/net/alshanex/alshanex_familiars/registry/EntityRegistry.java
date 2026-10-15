package net.alshanex.alshanex_familiars.registry;

import net.alshanex.alshanex_familiars.entity.ArchmagePetEntity;
import net.alshanex.alshanex_familiars.entity.BardPetEntity;
import net.alshanex.alshanex_familiars.entity.ClericPetEntity;
import net.alshanex.alshanex_familiars.entity.DragonWarriorPetEntity;
import net.alshanex.alshanex_familiars.entity.DruidPetEntity;
import net.alshanex.alshanex_familiars.entity.FrostlingPetEntity;
import net.alshanex.alshanex_familiars.entity.HunterPetEntity;
import net.alshanex.alshanex_familiars.entity.IllusionistPetEntity;
import net.alshanex.alshanex_familiars.entity.MagePetEntity;
import net.alshanex.alshanex_familiars.entity.NecromancerPetEntity;
import net.alshanex.alshanex_familiars.entity.PlaguePetEntity;
import net.alshanex.alshanex_familiars.entity.ScorcherPetEntity;
import net.alshanex.alshanex_familiars.entity.ShadowEntity;
import net.alshanex.alshanex_familiars.entity.SummonerPetEntity;
import net.alshanex.alshanex_familiars.entity.misc.BlueBirdEntity;
import net.alshanex.alshanex_familiars.entity.misc.ChorusFlowerEntity;
import net.alshanex.alshanex_familiars.entity.misc.DragonCircleEntity;
import net.alshanex.alshanex_familiars.entity.misc.DragonEggEntity;
import net.alshanex.alshanex_familiars.entity.misc.EndStoneEntity;
import net.alshanex.alshanex_familiars.entity.misc.FrozenEntity;
import net.alshanex.alshanex_familiars.entity.misc.HikenEntity;
import net.alshanex.alshanex_familiars.entity.misc.IllusionistDecoy;
import net.alshanex.alshanex_familiars.entity.misc.MemoryFragmentEntity;
import net.alshanex.alshanex_familiars.entity.misc.PurpurBricksEntity;
import net.alshanex.alshanex_familiars.entity.misc.PurpurPilarEntity;
import net.alshanex.alshanex_familiars.entity.misc.SoulEntity;
import net.alshanex.alshanex_familiars.entity.sound.AngelEntity;
import net.alshanex.alshanex_familiars.entity.sound.BlackNoteEntity;
import net.alshanex.alshanex_familiars.entity.sound.DefaultNoteEntity;
import net.alshanex.alshanex_familiars.entity.sound.HarpExplosionEntity;
import net.alshanex.alshanex_familiars.entity.sound.HealingCircleEntity;
import net.alshanex.alshanex_familiars.entity.sound.MusicBolt;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings1Entity;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings2Entity;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings3Entity;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings4Entity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistry {
   private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "alshanex_familiars");
   public static final DeferredHolder<EntityType<?>, EntityType<MagePetEntity>> MAGE_PET = ENTITIES.register(
      "mage_pet",
      () -> Builder.<MagePetEntity>of(MagePetEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "mage_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ArchmagePetEntity>> ARCHMAGE_PET = ENTITIES.register(
      "archmage_pet",
      () -> Builder.<ArchmagePetEntity>of(ArchmagePetEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "archmage_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonerPetEntity>> SUMMONER_PET = ENTITIES.register(
      "summoner_pet",
      () -> Builder.<SummonerPetEntity>of(SummonerPetEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "summoner_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<NecromancerPetEntity>> NECROMANCER_PET = ENTITIES.register(
      "necromancer_pet",
      () -> Builder.<NecromancerPetEntity>of(NecromancerPetEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "necromancer_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HunterPetEntity>> HUNTER_PET = ENTITIES.register(
      "hunter_pet",
      () -> Builder.<HunterPetEntity>of(HunterPetEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.25F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "hunter_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DruidPetEntity>> DRUID_PET = ENTITIES.register(
      "druid_pet",
      () -> Builder.<DruidPetEntity>of(DruidPetEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "druid_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IllusionistPetEntity>> ILLUSIONIST_PET = ENTITIES.register(
      "illusionist_pet",
      () -> Builder.<IllusionistPetEntity>of(IllusionistPetEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "illusionist_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ScorcherPetEntity>> SCORCHER_PET = ENTITIES.register(
      "scorcher_pet",
      () -> Builder.<ScorcherPetEntity>of(ScorcherPetEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "scorcher_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ClericPetEntity>> CLERIC_PET = ENTITIES.register(
      "cleric_pet",
      () -> Builder.<ClericPetEntity>of(ClericPetEntity::new, MobCategory.CREATURE)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "cleric_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PlaguePetEntity>> PLAGUE_PET = ENTITIES.register(
      "plague_pet",
      () -> Builder.<PlaguePetEntity>of(PlaguePetEntity::new, MobCategory.CREATURE)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "plague_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BardPetEntity>> BARD_PET = ENTITIES.register(
      "bard_pet",
      () -> Builder.<BardPetEntity>of(BardPetEntity::new, MobCategory.CREATURE)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "bard_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FrostlingPetEntity>> FROSTLING_PET = ENTITIES.register(
      "frostling_pet",
      () -> Builder.<FrostlingPetEntity>of(FrostlingPetEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "frostling_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DragonWarriorPetEntity>> DRAGON_WARRIOR_PET = ENTITIES.register(
      "dragon_warrior_pet",
      () -> Builder.<DragonWarriorPetEntity>of(DragonWarriorPetEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "dragon_warrior_pet").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ShadowEntity>> SHADOW_SUMMON = ENTITIES.register(
      "summoned_shadow",
      () -> Builder.<ShadowEntity>of(ShadowEntity::new, MobCategory.CREATURE)
         .sized(0.6F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "summoned_shadow").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<IllusionistDecoy>> DECOY = ENTITIES.register(
      "decoy",
      () -> Builder.<IllusionistDecoy>of(IllusionistDecoy::new, MobCategory.MISC)
         .sized(0.6F, 0.6F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "decoy").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DefaultNoteEntity>> DEFAULT_NOTE = ENTITIES.register(
      "default_note",
      () -> Builder.<DefaultNoteEntity>of(DefaultNoteEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "default_note").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BlackNoteEntity>> BLACK_NOTE = ENTITIES.register(
      "black_note",
      () -> Builder.<BlackNoteEntity>of(BlackNoteEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "black_note").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<UltStrings1Entity>> ULT_STRINGS_1 = ENTITIES.register(
      "ult_strings_1",
      () -> Builder.<UltStrings1Entity>of(UltStrings1Entity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "ult_strings_1").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<UltStrings2Entity>> ULT_STRINGS_2 = ENTITIES.register(
      "ult_strings_2",
      () -> Builder.<UltStrings2Entity>of(UltStrings2Entity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "ult_strings_2").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<UltStrings3Entity>> ULT_STRINGS_3 = ENTITIES.register(
      "ult_strings_3",
      () -> Builder.<UltStrings3Entity>of(UltStrings3Entity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "ult_strings_3").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<UltStrings4Entity>> ULT_STRINGS_4 = ENTITIES.register(
      "ult_strings_4",
      () -> Builder.<UltStrings4Entity>of(UltStrings4Entity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "ult_strings_4").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HarpExplosionEntity>> HARP_EXPLOSION = ENTITIES.register(
      "harp_explosion",
      () -> Builder.<HarpExplosionEntity>of(HarpExplosionEntity::new, MobCategory.MISC)
         .sized(4.0F, 1.2F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "harp_explosion").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HealingCircleEntity>> HEALING_CIRCLE = ENTITIES.register(
      "healing_circle",
      () -> Builder.<HealingCircleEntity>of(HealingCircleEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "healing_circle").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<AngelEntity>> ANGEL = ENTITIES.register(
      "angel",
      () -> Builder.<AngelEntity>of(AngelEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "angel").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BlueBirdEntity>> BLUE_BIRD = ENTITIES.register(
      "blue_bird",
      () -> Builder.<BlueBirdEntity>of(BlueBirdEntity::new, MobCategory.CREATURE)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "blue_bird").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MusicBolt>> MUSIC_BOLT = ENTITIES.register(
      "music_bolt",
      () -> Builder.<MusicBolt>of(MusicBolt::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "music_bolt").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<MemoryFragmentEntity>> MEMORY_FRAGMENT = ENTITIES.register(
      "memory_fragment",
      () -> Builder.<MemoryFragmentEntity>of(MemoryFragmentEntity::new, MobCategory.MISC)
         .sized(0.2F, 0.2F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "memory_fragment").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SoulEntity>> SOUL = ENTITIES.register(
      "soul_entity",
      () -> Builder.<SoulEntity>of(SoulEntity::new, MobCategory.MONSTER)
         .sized(0.5F, 1.0F)
         .clientTrackingRange(64)
         .build(new ResourceLocation("alshanex_familiars", "soul_entity").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<EndStoneEntity>> END_STONE_ENTITY = ENTITIES.register(
      "end_stone_entity",
      () -> Builder.<EndStoneEntity>of(EndStoneEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "end_stone_entity").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DragonEggEntity>> DRAGON_EGG_ENTITY = ENTITIES.register(
      "dragon_egg_entity",
      () -> Builder.<DragonEggEntity>of(DragonEggEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "dragon_egg_entity").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ChorusFlowerEntity>> CHORUS_FLOWER_ENTITY = ENTITIES.register(
      "chorus_flower_entity",
      () -> Builder.<ChorusFlowerEntity>of(ChorusFlowerEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "chorus_flower_entity").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PurpurBricksEntity>> PURPUR_BRICKS_ENTITY = ENTITIES.register(
      "purpur_bricks_entity",
      () -> Builder.<PurpurBricksEntity>of(PurpurBricksEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "purpur_bricks_entity").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<PurpurPilarEntity>> PURPUR_PILAR_ENTITY = ENTITIES.register(
      "purpur_pilar_entity",
      () -> Builder.<PurpurPilarEntity>of(PurpurPilarEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "purpur_pilar_entity").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<DragonCircleEntity>> DRAGON_CIRCLE = ENTITIES.register(
      "dragon_circle",
      () -> Builder.<DragonCircleEntity>of(DragonCircleEntity::new, MobCategory.MISC)
         .sized(4.0F, 1.2F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "dragon_circle").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<FrozenEntity>> FROZEN_ENTITY = ENTITIES.register(
      "frozen_entity",
      () -> Builder.<FrozenEntity>of(FrozenEntity::new, MobCategory.CREATURE)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "frozen_entity").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HikenEntity>> HIKEN_ENTITY = ENTITIES.register(
      "hiken",
      () -> Builder.<HikenEntity>of(HikenEntity::new, MobCategory.MISC)
         .sized(2.0F, 2.0F)
         .clientTrackingRange(4)
         .build(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "hiken").toString())
   );

   public static void register(IEventBus eventBus) {
      ENTITIES.register(eventBus);
   }
}
