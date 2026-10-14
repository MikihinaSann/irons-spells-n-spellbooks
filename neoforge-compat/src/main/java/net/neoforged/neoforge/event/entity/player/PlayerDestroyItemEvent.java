package net.neoforged.neoforge.event.entity.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge's PlayerDestroyItemEvent — fired when an item runs out of
 * durability and breaks.
 */
public class PlayerDestroyItemEvent extends PlayerEvent {
    private final ItemStack original;
    private final InteractionHand hand;

    public PlayerDestroyItemEvent(Player player, ItemStack original, @Nullable InteractionHand hand) {
        super(player);
        this.original = original;
        this.hand = hand;
    }

    /** The broken item. */
    public ItemStack getOriginal() {
        return original;
    }

    /** The hand that held the item, or null if it broke elsewhere. */
    @Nullable
    public InteractionHand getHand() {
        return hand;
    }
}
