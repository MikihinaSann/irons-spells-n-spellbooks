package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.ICancellableEvent;

import java.util.Collection;

public class LivingDropsEvent extends LivingEvent implements ICancellableEvent {
    private final DamageSource source;
    private final Collection<ItemEntity> drops;
    private final int lootingLevel;
    private final boolean recentlyHit;
    private final DamageSource originalSource;

    public LivingDropsEvent(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops,
                            int lootingLevel, boolean recentlyHit) {
        super(entity);
        this.source = source;
        this.originalSource = source;
        this.drops = drops;
        this.lootingLevel = lootingLevel;
        this.recentlyHit = recentlyHit;
    }

    public DamageSource getSource() {
        return source;
    }

    public DamageSource getOriginalSource() {
        return originalSource;
    }

    public Collection<ItemEntity> getDrops() {
        return drops;
    }

    public int getLootingLevel() {
        return lootingLevel;
    }

    public boolean isRecentlyHit() {
        return recentlyHit;
    }
}
