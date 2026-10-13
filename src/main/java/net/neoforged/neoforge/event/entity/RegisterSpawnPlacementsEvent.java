package net.neoforged.neoforge.event.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects spawn-placement registrations; drained to real
 * {@link SpawnPlacements#register} calls at bootstrap.
 */
public class RegisterSpawnPlacementsEvent extends Event {
    public enum Operation {
        AND,
        OR,
        REPLACE
    }

    private final List<Registration<?>> registrations = new ArrayList<>();

    public record Registration<T extends Mob>(EntityType<T> type, SpawnPlacementType placementType,
                                              Heightmap.Types heightmapType, SpawnPlacements.SpawnPredicate<? super T> predicate,
                                              Operation operation) {
    }

    public <T extends Mob> void register(EntityType<T> entityType, SpawnPlacementType placementType,
                                         Heightmap.Types heightmapType, SpawnPlacements.SpawnPredicate<? super T> predicate,
                                         Operation operation) {
        registrations.add(new Registration<>(entityType, placementType, heightmapType, predicate, operation));
    }

    public List<Registration<?>> getRegistrations() {
        return registrations;
    }
}
