package net.neoforged.neoforge.event.level;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.bus.api.Event;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Lets mods modify a level's custom spawners list. Collected at world-load and
 * applied by the platform bridge.
 */
public class ModifyCustomSpawnersEvent extends Event {
    private final ServerLevelAccessor level;
    private final List<CustomSpawner> spawners;

    public ModifyCustomSpawnersEvent(ServerLevelAccessor level, List<CustomSpawner> spawners) {
        this.level = level;
        this.spawners = spawners;
    }

    public ServerLevelAccessor getLevel() {
        return level;
    }

    public List<CustomSpawner> getCustomSpawners() {
        return spawners;
    }

    public void addCustomSpawner(CustomSpawner spawner) {
        spawners.add(spawner);
    }

    public boolean removeCustomSpawner(CustomSpawner spawner) {
        return spawners.remove(spawner);
    }

    public <T extends CustomSpawner> void modifyCustomSpawner(Class<T> type, Consumer<T> consumer) {
        for (CustomSpawner spawner : spawners) {
            if (type.isInstance(spawner)) {
                consumer.accept(type.cast(spawner));
            }
        }
    }
}
