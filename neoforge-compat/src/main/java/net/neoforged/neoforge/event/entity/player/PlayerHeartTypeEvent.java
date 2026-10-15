package net.neoforged.neoforge.event.entity.player;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.EntityEvent;

/**
 * Fired from {@code Gui.HeartType#forPlayer} to let mods override the heart
 * sprite used for a player (NeoForge: net.neoforged.neoforge.event.entity.player).
 */
public class PlayerHeartTypeEvent extends EntityEvent {
    private final Gui.HeartType originalType;
    private Gui.HeartType type;

    public PlayerHeartTypeEvent(Player player, Gui.HeartType originalType) {
        super(player);
        this.originalType = originalType;
        this.type = originalType;
    }

    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
    }

    public Gui.HeartType getOriginalType() {
        return originalType;
    }

    public Gui.HeartType getType() {
        return type;
    }

    public void setType(Gui.HeartType type) {
        this.type = type;
    }
}
