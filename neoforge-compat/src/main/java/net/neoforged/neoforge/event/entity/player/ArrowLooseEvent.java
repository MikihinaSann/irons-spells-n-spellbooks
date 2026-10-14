package net.neoforged.neoforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired when a player releases a drawn bow. Cancelling suppresses the shot;
 * the adjusted charge is otherwise used.
 */
public class ArrowLooseEvent extends PlayerEvent implements ICancellableEvent {
    private final ItemStack bow;
    private final Level level;
    private int charge;
    private final boolean hasAmmo;

    public ArrowLooseEvent(ItemStack bow, Level level, Player player, int charge, boolean hasAmmo) {
        super(player);
        this.bow = bow;
        this.level = level;
        this.charge = charge;
        this.hasAmmo = hasAmmo;
    }

    public ItemStack getBow() { return bow; }
    public Level getLevel() { return level; }
    public int getCharge() { return charge; }
    public void setCharge(int charge) { this.charge = charge; }
    public boolean hasAmmo() { return hasAmmo; }
}
