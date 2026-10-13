package net.neoforged.neoforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.ICancellableEvent;

public class EntityMountEvent extends EntityEvent implements ICancellableEvent {
    private final Entity entityMounting;
    private final Entity entityBeingMounted;
    private final Entity entityMountingOn;
    private final boolean isMounting;
    private final Level level;

    public EntityMountEvent(Entity entityMounting, Entity entityBeingMounted, Entity entityMountingOn, boolean isMounting) {
        super(entityMounting);
        this.entityMounting = entityMounting;
        this.entityBeingMounted = entityBeingMounted;
        this.entityMountingOn = entityMountingOn;
        this.isMounting = isMounting;
        this.level = entityMounting.level();
    }

    public Entity getEntityMounting() {
        return entityMounting;
    }

    public Entity getEntityBeingMounted() {
        return entityBeingMounted;
    }

    public Entity getEntityMountingOn() {
        return entityMountingOn;
    }

    public Entity getEntityBeingMountedOn() {
        return entityMountingOn;
    }

    public boolean isMounting() {
        return isMounting;
    }

    public boolean isDismounting() {
        return !isMounting;
    }

    public Level getLevel() {
        return level;
    }
}
