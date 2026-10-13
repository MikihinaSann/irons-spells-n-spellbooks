package net.neoforged.neoforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class EntityMobGriefingEvent extends EntityEvent {
    private final LevelAccessor level;

    public EntityMobGriefingEvent(LevelAccessor level, Entity entity) {
        super(entity);
        this.level = level;
    }

    public LevelAccessor getLevel() {
        return level;
    }
}
