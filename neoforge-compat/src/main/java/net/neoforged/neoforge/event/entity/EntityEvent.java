package net.neoforged.neoforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

public abstract class EntityEvent extends Event {
    private final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public net.minecraft.world.level.LevelAccessor getLevel() {
        return entity.level();
    }
}
