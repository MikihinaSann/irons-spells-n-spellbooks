package net.neoforged.neoforge.event.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.Event;

import java.util.HashMap;
import java.util.Map;

public class EntityAttributeCreationEvent extends Event {
    private final Map<EntityType<? extends LivingEntity>, AttributeSupplier> map = new HashMap<>();

    public void put(EntityType<? extends LivingEntity> type, AttributeSupplier builder) {
        map.put(type, builder);
    }

    public Map<EntityType<? extends LivingEntity>, AttributeSupplier> getMap() {
        return map;
    }
}
