package net.neoforged.neoforge.common.extensions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * NeoForge patches {@code LightningBolt} with a mutable damage value used by
 * {@code dealDamage}. On Fabric the damage is stored in a side map; a mixin on
 * {@code dealDamage} reads it back.
 */
public interface ILightningBoltExtension {
    Map<Entity, Float> CUSTOM_DAMAGE = new WeakHashMap<>();

    private LightningBolt self() {
        return (LightningBolt) (Object) this;
    }

    default void setDamage(float damage) {
        CUSTOM_DAMAGE.put(self(), damage);
    }

    default float getDamage() {
        return CUSTOM_DAMAGE.getOrDefault(self(), 5.0F);
    }
}
