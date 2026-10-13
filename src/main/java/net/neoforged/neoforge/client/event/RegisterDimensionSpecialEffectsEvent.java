package net.neoforged.neoforge.client.event;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterDimensionSpecialEffectsEvent extends Event {
    public static final Map<ResourceLocation, DimensionSpecialEffects> EFFECTS = new ConcurrentHashMap<>();

    public void register(ResourceLocation id, DimensionSpecialEffects effects) {
        EFFECTS.put(id, effects);
    }
}
