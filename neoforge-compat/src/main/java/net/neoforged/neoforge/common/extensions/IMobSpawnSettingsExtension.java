package net.neoforged.neoforge.common.extensions;

import java.util.Set;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

/**
 * NeoForge's MobSpawnSettings extensions — exposes the spawn tables patched
 * onto MobSpawnSettings so {@code MobSpawnSettingsBuilder} can copy them.
 */
public interface IMobSpawnSettingsExtension {

    default Set<MobCategory> getSpawnerTypes() {
        return ((MobSpawnSettings) (Object) this).spawners.keySet();
    }

    default Set<EntityType<?>> getEntityTypes() {
        return ((MobSpawnSettings) (Object) this).mobSpawnCosts.keySet();
    }

    default MobSpawnSettings.MobSpawnCost getMobSpawnCost(EntityType<?> type) {
        return ((MobSpawnSettings) (Object) this).mobSpawnCosts.get(type);
    }

    default float getCreatureProbability() {
        return ((MobSpawnSettings) (Object) this).creatureGenerationProbability;
    }
}
