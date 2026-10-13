package net.neoforged.neoforge.client.event;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects HUD layer registrations; drained into FAPI's HudElementRegistry at
 * client init.
 */
public class RegisterGuiLayersEvent extends Event {
    private final List<Registration> registrations = new ArrayList<>();

    public void registerAboveAll(ResourceLocation id, LayeredDraw.Layer layer) {
        registrations.add(new Registration(id, layer, Order.ABOVE_ALL, null));
    }

    public void registerAbove(ResourceLocation existing, ResourceLocation id, LayeredDraw.Layer layer) {
        registrations.add(new Registration(id, layer, Order.ABOVE, existing));
    }

    public void registerBelow(ResourceLocation existing, ResourceLocation id, LayeredDraw.Layer layer) {
        registrations.add(new Registration(id, layer, Order.BELOW, existing));
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public enum Order {
        ABOVE_ALL, ABOVE, BELOW
    }

    public record Registration(ResourceLocation id, LayeredDraw.Layer layer,
                               Order order, ResourceLocation relativeTo) {
    }
}
