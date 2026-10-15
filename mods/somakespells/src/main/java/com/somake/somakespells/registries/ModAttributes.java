package com.somake.somakespells.registries;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.RedEchoEntity;
import com.somake.somakespells.entity.SomakeGhostEntity;
import io.redspace.ironsspellbooks.api.attribute.MagicPercentAttribute;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.MOD
)
public class ModAttributes {
   public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, "somakespells");
   public static final DeferredRegister<Attribute> BORN_IN_CHAOS_ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, "somakespells");
   public static final DeferredHolder<Attribute, Attribute> AQUA_SPELL_POWER = ATTRIBUTES.register(
      "aqua_spell_power", () -> new MagicPercentAttribute("attribute.somakespells.aqua_spell_power", 1.0, -100.0, 100.0).setSyncable(true)
   );
   public static final DeferredHolder<Attribute, Attribute> AQUA_MAGIC_RESIST = ATTRIBUTES.register(
      "aqua_magic_resist", () -> new MagicPercentAttribute("attribute.somakespells.aqua_magic_resist", 1.0, -100.0, 100.0).setSyncable(true)
   );
   public static final DeferredHolder<Attribute, Attribute> SOUL_FIRE_SPELL_POWER = ATTRIBUTES.register(
      "soul_fire_spell_power", () -> new MagicPercentAttribute("attribute.somakespells.soul_fire_spell_power", 1.0, -100.0, 100.0).setSyncable(true)
   );
   public static final DeferredHolder<Attribute, Attribute> FIRE_DAMAGE = ATTRIBUTES.register(
      "fire_damage", () -> new RangedAttribute("attribute.somakespells.fire_damage", 0.0, 0.0, 1000.0).setSyncable(true)
   );
   public static final DeferredHolder<Attribute, Attribute> ICE_DAMAGE = ATTRIBUTES.register(
      "ice_damage", () -> new RangedAttribute("attribute.somakespells.ice_damage", 0.0, 0.0, 1000.0).setSyncable(true)
   );
   public static final DeferredHolder<Attribute, Attribute> INFERNAL_FIRE_SPELL_POWER = BORN_IN_CHAOS_ATTRIBUTES.register(
      "infernal_fire_spell_power", () -> new MagicPercentAttribute("attribute.somakespells.infernal_fire_spell_power", 1.0, -100.0, 100.0).setSyncable(true)
   );

   public static void register(IEventBus eventBus) {
      ATTRIBUTES.register(eventBus);
   }

   public static void registerBornInChaos(IEventBus eventBus) {
      BORN_IN_CHAOS_ATTRIBUTES.register(eventBus);
   }

   @SubscribeEvent
   public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
      event.add(EntityType.PLAYER, AQUA_SPELL_POWER);
      event.add(EntityType.PLAYER, AQUA_MAGIC_RESIST);
      event.add(EntityType.PLAYER, SOUL_FIRE_SPELL_POWER);
      event.add(EntityType.PLAYER, FIRE_DAMAGE);
      event.add(EntityType.PLAYER, ICE_DAMAGE);
      if (BornInChaosCompat.isLoaded()) {
         event.add(EntityType.PLAYER, INFERNAL_FIRE_SPELL_POWER);
      }
   }

   @SubscribeEvent
   public static void registerAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)ModEntityTypes.SUMMONED_DROWNED.get(), Drowned.createAttributes().build());
      event.put((EntityType)ModEntityTypes.SOUL_BLAZE.get(), Blaze.createAttributes().build());
      if (LegendaryMonstersCompat.isLoaded()) {
         event.put((EntityType)ModEntityTypes.RED_ECHO.get(), RedEchoEntity.prepareAttributes().build());
      }

      if (BornInChaosCompat.isLoaded()) {
         event.put((EntityType)ModCompatEntityTypes.INFERNAL_BLAZE.get(), Blaze.createAttributes().build());
      }

      event.put((EntityType)ModEntityTypes.AQUA_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      event.put((EntityType)ModEntityTypes.FIRE_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      event.put((EntityType)ModEntityTypes.ICE_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      event.put((EntityType)ModEntityTypes.LIGHTNING_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      event.put((EntityType)ModEntityTypes.HOLY_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      event.put((EntityType)ModEntityTypes.ENDER_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      event.put((EntityType)ModEntityTypes.BLOOD_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      event.put((EntityType)ModEntityTypes.EVOCATION_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      event.put((EntityType)ModEntityTypes.NATURE_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      event.put((EntityType)ModEntityTypes.ELDRITCH_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      if (LegendaryMonstersCompat.isLoaded()) {
         event.put((EntityType)ModEntityTypes.SPIRIT_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      }

      if (MagicFromTheEastCompat.isLoaded()) {
         event.put((EntityType)ModEntityTypes.SYMMETRY_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      }

      if (ModList.get().isLoaded("gtbcs_geomancy_plus")) {
         event.put((EntityType)ModCompatEntityTypes.GEO_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      }

      if (ModList.get().isLoaded("tunes_n_tomes")) {
         event.put((EntityType)ModCompatEntityTypes.SOUND_GHOST.get(), SomakeGhostEntity.prepareAttributes().build());
      }
   }

   @SubscribeEvent
   public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)ModEntityTypes.FIRE_GHOST.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         ModAttributes::checkFireGhostSpawnRules,
         Operation.REPLACE
      );
   }

   private static boolean checkFireGhostSpawnRules(
      EntityType<? extends SomakeGhostEntity.FireGhostEntity> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random
   ) {
      return level.getDifficulty() != Difficulty.PEACEFUL && Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
   }
}
