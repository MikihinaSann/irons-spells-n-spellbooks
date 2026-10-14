package net.neoforged.neoforge.event.entity.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Fired when a player starts using a bow. Setting the action overrides vanilla's
 * nock behavior.
 */
public class ArrowNockEvent extends PlayerEvent {
    private final ItemStack bow;
    private final InteractionHand hand;
    private final Level level;
    private final boolean hasAmmo;
    private InteractionResultHolder<ItemStack> action;

    public ArrowNockEvent(ItemStack bow, Level level, Player player, InteractionHand hand, boolean hasAmmo) {
        super(player);
        this.bow = bow;
        this.level = level;
        this.hand = hand;
        this.hasAmmo = hasAmmo;
    }

    public ItemStack getBow() { return bow; }
    public InteractionHand getHand() { return hand; }
    public Level getLevel() { return level; }
    public boolean hasAmmo() { return hasAmmo; }
    public InteractionResultHolder<ItemStack> getAction() { return action; }
    public void setAction(InteractionResultHolder<ItemStack> action) { this.action = action; }
}
