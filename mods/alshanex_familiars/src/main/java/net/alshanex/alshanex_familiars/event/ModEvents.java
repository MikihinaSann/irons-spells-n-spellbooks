package net.alshanex.alshanex_familiars.event;

import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.alshanex_familiars.util.AlshanexFamiliarsTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;

@EventBusSubscriber(
   modid = "alshanex_familiars",
   bus = Bus.MOD
)
public class ModEvents {
   @SubscribeEvent
   public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)EntityRegistry.MAGE_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, level, spawnType, pos, random) -> checkCustomSpawnRules(
            entityType, level, spawnType, pos, random, AlshanexFamiliarsTags.MAGE_SPAWNABLE_ON, Mob::checkMobSpawnRules
         ),
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.ARCHMAGE_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, level, spawnType, pos, random) -> checkCustomSpawnRules(
            entityType, level, spawnType, pos, random, AlshanexFamiliarsTags.ARCHMAGE_SPAWNABLE_ON, Mob::checkMobSpawnRules
         ),
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.SUMMONER_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, level, spawnType, pos, random) -> checkCustomSpawnRules(
            entityType, level, spawnType, pos, random, AlshanexFamiliarsTags.SUMMONER_SPAWNABLE_ON, Mob::checkMobSpawnRules
         ),
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.NECROMANCER_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, level, spawnType, pos, random) -> checkCustomSpawnRules(
            entityType, level, spawnType, pos, random, AlshanexFamiliarsTags.NECROMANCER_SPAWNABLE_ON, Mob::checkMobSpawnRules
         ),
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.HUNTER_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, level, spawnType, pos, random) -> checkCustomSpawnRules(
            entityType, level, spawnType, pos, random, AlshanexFamiliarsTags.HUNTER_SPAWNABLE_ON, Mob::checkMobSpawnRules
         ),
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.DRUID_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, level, spawnType, pos, random) -> checkCustomSpawnRules(
            entityType, level, spawnType, pos, random, AlshanexFamiliarsTags.DRUID_SPAWNABLE_ON, Mob::checkMobSpawnRules
         ),
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.ILLUSIONIST_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         Mob::checkMobSpawnRules,
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.SCORCHER_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, level, spawnType, pos, random) -> checkCustomSpawnRules(
            entityType, level, spawnType, pos, random, AlshanexFamiliarsTags.SCORCHER_SPAWNABLE_ON, Mob::checkMobSpawnRules
         ),
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.FROSTLING_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (entityType, level, spawnType, pos, random) -> checkCustomSpawnRules(
            entityType, level, spawnType, pos, random, AlshanexFamiliarsTags.FROSTLING_SPAWNABLE_ON, Mob::checkMobSpawnRules
         ),
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.CLERIC_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         Mob::checkMobSpawnRules,
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.PLAGUE_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         Mob::checkMobSpawnRules,
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.BARD_PET.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.DRAGON_WARRIOR_PET.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         Mob::checkMobSpawnRules,
         Operation.REPLACE
      );
      event.register(
         (EntityType)EntityRegistry.SOUL.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, Operation.REPLACE
      );
   }

   private static <T extends Mob> boolean checkCustomSpawnRules(
      EntityType<T> entityType,
      ServerLevelAccessor level,
      MobSpawnType spawnType,
      BlockPos pos,
      RandomSource random,
      TagKey<Block> allowedBlocks,
      SpawnPredicate<T> originalSpawnRule
   ) {
      BlockPos belowPos = pos.below();
      BlockState belowState = level.getBlockState(belowPos);
      return !belowState.is(allowedBlocks) ? false : originalSpawnRule.test(entityType, level, spawnType, pos, random);
   }
}
