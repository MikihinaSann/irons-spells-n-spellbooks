package net.acetheeldritchking.cataclysm_spellbooks.registries;

import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedAmethystCrab;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedAptrgangr;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedDraugur;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedEliteDraugur;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedIgnitedBerserker;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedIgnitedRevenant;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedKoboldiator;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedKoboleton;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedPhantomRemnant;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedRoyalDraugur;
import net.acetheeldritchking.cataclysm_spellbooks.entity.spells.blazing_aoe.BlazingAoE;
import net.acetheeldritchking.cataclysm_spellbooks.entity.spells.hellish_blade.HellishBladeProjectile;
import net.acetheeldritchking.cataclysm_spellbooks.entity.spells.infernal_blade.InfernalBladeProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CSEntityRegistry {
   private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "cataclysm_spellbooks");
   public static final DeferredHolder<EntityType<?>, EntityType<InfernalBladeProjectile>> INFERNAL_BLADE_PROJECTILE = ENTITIES.register(
      "infernal_blade",
      () -> EntityType.Builder.<InfernalBladeProjectile>of(InfernalBladeProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "infernal_blade").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HellishBladeProjectile>> HELLISH_BLADE_PROJECTILE = ENTITIES.register(
      "hellish_blade",
      () -> EntityType.Builder.<HellishBladeProjectile>of(HellishBladeProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "hellish_blade").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<BlazingAoE>> BLAZING_AOE_ENTITY = ENTITIES.register(
      "blazing_aoe",
      () -> EntityType.Builder.<BlazingAoE>of(BlazingAoE::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "blazing_aoe").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedIgnitedRevenant>> SUMMONED_IGNITED_REVENANT = ENTITIES.register(
      "summoned_ignited_revenant",
      () -> EntityType.Builder.<SummonedIgnitedRevenant>of(SummonedIgnitedRevenant::new, MobCategory.MONSTER)
         .sized(1.0F, 3.0F)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "summoned_ignited_revenant").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedIgnitedBerserker>> SUMMONED_IGNITED_BERSERKER = ENTITIES.register(
      "summoned_ignited_berserker",
      () -> EntityType.Builder.<SummonedIgnitedBerserker>of(SummonedIgnitedBerserker::new, MobCategory.MONSTER)
         .sized(1.0F, 3.0F)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "summoned_ignited_berserker").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedKoboldiator>> SUMMONED_KOBOLDIATOR = ENTITIES.register(
      "summoned_koboldiator",
      () -> EntityType.Builder.<SummonedKoboldiator>of(SummonedKoboldiator::new, MobCategory.MONSTER)
         .sized(2.5F, 4.5F)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "summoned_koboldiator").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedKoboleton>> SUMMONED_KOBOLETON = ENTITIES.register(
      "summoned_koboleton",
      () -> EntityType.Builder.<SummonedKoboleton>of(SummonedKoboleton::new, MobCategory.MONSTER)
         .sized(1.0F, 2.0F)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "summoned_koboleton").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedDraugur>> SUMMONED_DRAUGUR = ENTITIES.register(
      "summoned_draugur",
      () -> EntityType.Builder.<SummonedDraugur>of(SummonedDraugur::new, MobCategory.MONSTER)
         .sized(1.0F, 2.0F)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "summoned_draugur").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedRoyalDraugur>> SUMMONED_ROYAL_DRAUGUR = ENTITIES.register(
      "summoned_royal_draugur",
      () -> EntityType.Builder.<SummonedRoyalDraugur>of(SummonedRoyalDraugur::new, MobCategory.MONSTER)
         .sized(1.0F, 2.0F)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "summoned_royal_draugur").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedEliteDraugur>> SUMMONED_ELITE_DRAUGUR = ENTITIES.register(
      "summoned_elite_draugur",
      () -> EntityType.Builder.<SummonedEliteDraugur>of(SummonedEliteDraugur::new, MobCategory.MONSTER)
         .sized(1.0F, 3.0F)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "summoned_elite_draugur").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedAptrgangr>> SUMMONED_APTRGANGR = ENTITIES.register(
      "summoned_aptrgangr",
      () -> EntityType.Builder.<SummonedAptrgangr>of(SummonedAptrgangr::new, MobCategory.MONSTER)
         .sized(1.0F, 3.0F)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "summoned_aptrgangr").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedAmethystCrab>> SUMMONED_AMETHYST_CRAB = ENTITIES.register(
      "summoned_amethyst_crab",
      () -> EntityType.Builder.<SummonedAmethystCrab>of(SummonedAmethystCrab::new, MobCategory.MONSTER)
         .sized(3.0F, 2.5F)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "summoned_amethyst_crab").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SummonedPhantomRemnant>> PHANTOM_ANCIENT_REMNANT = ENTITIES.register(
      "phantom_ancient_remnant",
      () -> EntityType.Builder.<SummonedPhantomRemnant>of(SummonedPhantomRemnant::new, MobCategory.MONSTER)
         .sized(5.0F, 5.0F)
         .build(ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "phantom_ancient_remnant").toString())
   );

   public static void register(IEventBus eventBus) {
      ENTITIES.register(eventBus);
   }
}
