package io.redspace.ironsspellbooks.platform.fabric;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Thread-local collector that captures {@link ItemEntity}s spawned while a
 * {@link LivingEntity#dropAllDeathLoot} call is in flight, so the resulting
 * collection can be exposed to {@code LivingDropsEvent} subscribers.
 */
public final class DeathDropCapture {
    private DeathDropCapture() {
    }

    public record Capture(LivingEntity owner, List<ItemEntity> drops) {
    }

    private static final ThreadLocal<Capture> CURRENT = new ThreadLocal<>();

    public static void begin(LivingEntity owner) {
        CURRENT.set(new Capture(owner, new ArrayList<>()));
    }

    public static Capture end() {
        Capture capture = CURRENT.get();
        CURRENT.remove();
        return capture;
    }

    public static void capture(Entity spawner, ItemEntity itemEntity) {
        Capture capture = CURRENT.get();
        if (capture != null && itemEntity != null && capture.owner() == spawner) {
            capture.drops().add(itemEntity);
        }
    }
}
