package net.neoforged.neoforge.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.neoforge.event.entity.EntityEvent;

/**
 * Fired when an item entity is about to expire (age >= 6000). Listeners may
 * extend its life via {@link #addExtraLife(int)} — the item's age is pushed
 * back by that many ticks instead of discarding.
 */
public class ItemExpireEvent extends EntityEvent {
    private final ItemEntity entity;
    private int extraLife;

    public ItemExpireEvent(ItemEntity entity, int extraLife) {
        super(entity);
        this.entity = entity;
        this.extraLife = extraLife;
    }

    @Override
    public ItemEntity getEntity() {
        return entity;
    }

    public int getExtraLife() {
        return extraLife;
    }

    public void addExtraLife(int life) {
        this.extraLife += life;
    }
}
