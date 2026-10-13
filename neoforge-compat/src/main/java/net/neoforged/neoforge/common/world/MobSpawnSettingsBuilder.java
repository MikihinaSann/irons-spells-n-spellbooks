package net.neoforged.neoforge.common.world;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.Builder;
import net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import org.jetbrains.annotations.Nullable;

public class MobSpawnSettingsBuilder extends Builder {
   private final Set<MobCategory> typesView = Collections.unmodifiableSet(this.spawners.keySet());
   private final Set<EntityType<?>> costView = Collections.unmodifiableSet(this.mobSpawnCosts.keySet());

   public MobSpawnSettingsBuilder(MobSpawnSettings orig) {
      orig.getSpawnerTypes().forEach(k -> {
         ((List)this.spawners.get(k)).clear();
         ((List)this.spawners.get(k)).addAll(orig.getMobs(k).unwrap());
      });
      orig.getEntityTypes().forEach(k -> this.mobSpawnCosts.put(k, orig.getMobSpawnCost(k)));
      this.creatureGenerationProbability = orig.getCreatureProbability();
   }

   public Set<MobCategory> getSpawnerTypes() {
      return this.typesView;
   }

   public List<SpawnerData> getSpawner(MobCategory type) {
      return (List<SpawnerData>)this.spawners.get(type);
   }

   public Set<EntityType<?>> getEntityTypes() {
      return this.costView;
   }

   @Nullable
   public MobSpawnCost getCost(EntityType<?> type) {
      return (MobSpawnCost)this.mobSpawnCosts.get(type);
   }

   public float getProbability() {
      return this.creatureGenerationProbability;
   }

   public MobSpawnSettingsBuilder disablePlayerSpawn() {
      return this;
   }

   public MobSpawnSettingsBuilder removeSpawnCost(EntityType<?>... entityTypes) {
      for (EntityType<?> entityType : entityTypes) {
         this.mobSpawnCosts.remove(entityType);
      }

      return this;
   }
}
