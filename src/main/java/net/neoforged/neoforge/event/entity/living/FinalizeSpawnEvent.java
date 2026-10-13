package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a mob finalizes spawn (post {@code finalizeSpawn}).
 */
public class FinalizeSpawnEvent extends LivingEvent implements ICancellableEvent {
    private final ServerLevelAccessor level;
    private final DifficultyInstance difficulty;
    private final MobSpawnType spawnType;
    @Nullable
    private final Object spawnData;

    public FinalizeSpawnEvent(Mob entity, ServerLevelAccessor level, DifficultyInstance difficulty,
                              MobSpawnType spawnType, @Nullable Object spawnData) {
        super(entity);
        this.level = level;
        this.difficulty = difficulty;
        this.spawnType = spawnType;
        this.spawnData = spawnData;
    }

    public FinalizeSpawnEvent(Mob entity, ServerLevelAccessor level, double x, double y, double z,
                              DifficultyInstance difficulty, MobSpawnType spawnType,
                              @Nullable Object spawnData) {
        this(entity, level, difficulty, spawnType, spawnData);
    }

    @Override
    public ServerLevelAccessor getLevel() {
        return level;
    }

    public DifficultyInstance getDifficulty() {
        return difficulty;
    }

    public MobSpawnType getSpawnType() {
        return spawnType;
    }

    @Nullable
    public Object getSpawnData() {
        return spawnData;
    }

    @Override
    public Mob getEntity() {
        return (Mob) super.getEntity();
    }
}
