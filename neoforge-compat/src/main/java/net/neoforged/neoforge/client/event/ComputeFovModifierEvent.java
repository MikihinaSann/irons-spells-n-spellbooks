package net.neoforged.neoforge.client.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

/**
 * Fired when the client's fov modifier is computed (bow draw, effects) —
 * mirrors NeoForge's {@code ComputeFovModifierEvent}.
 */
public class ComputeFovModifierEvent extends Event {
    private final Player player;
    private final float fovModifier;
    private float newFovModifier;

    public ComputeFovModifierEvent(Player player, float fovModifier) {
        this.player = player;
        this.fovModifier = fovModifier;
        this.newFovModifier = fovModifier;
    }

    public Player getPlayer() {
        return player;
    }

    public float getFovModifier() {
        return fovModifier;
    }

    public void setNewFovModifier(float fovModifier) {
        this.newFovModifier = fovModifier;
    }

    public float getNewFovModifier() {
        return newFovModifier;
    }
}
