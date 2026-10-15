package com.somake.somakespells.registries;

import com.somake.somakespells.entity.ElementalBlazeEntity;
import com.somake.somakespells.entity.SomakeGhostEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCompatEntityTypes {
   public static final DeferredRegister<EntityType<?>> GEO_ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "somakespells");
   public static final DeferredRegister<EntityType<?>> SOUND_ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "somakespells");
   public static final DeferredRegister<EntityType<?>> BORN_IN_CHAOS_ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "somakespells");
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.GeoGhostEntity>> GEO_GHOST = GEO_ENTITIES.register(
      "geo_ghost",
      () -> Builder.of(SomakeGhostEntity.GeoGhostEntity::new, MobCategory.MONSTER)
         .sized(0.7F, 1.95F)
         .clientTrackingRange(64)
         .updateInterval(3)
         .build("geo_ghost")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<SomakeGhostEntity.SoundGhostEntity>> SOUND_GHOST = SOUND_ENTITIES.register(
      "sound_ghost",
      () -> Builder.of(SomakeGhostEntity.SoundGhostEntity::new, MobCategory.MONSTER)
         .sized(0.7F, 1.95F)
         .clientTrackingRange(64)
         .updateInterval(3)
         .build("sound_ghost")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ElementalBlazeEntity.InfernalBlazeEntity>> INFERNAL_BLAZE = BORN_IN_CHAOS_ENTITIES.register(
      "infernal_blaze",
      () -> Builder.of(ElementalBlazeEntity.InfernalBlazeEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .build("infernal_blaze")
   );

   public static void registerGeo(IEventBus eventBus) {
      GEO_ENTITIES.register(eventBus);
   }

   public static void registerSound(IEventBus eventBus) {
      SOUND_ENTITIES.register(eventBus);
   }

   public static void registerBornInChaos(IEventBus eventBus) {
      BORN_IN_CHAOS_ENTITIES.register(eventBus);
   }
}
