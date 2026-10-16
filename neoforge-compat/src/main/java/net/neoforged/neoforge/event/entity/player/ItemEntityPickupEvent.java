package net.neoforged.neoforge.event.entity.player;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired when a player touches an item entity. {@link Pre} fires before the
 * vanilla pickup logic and may be canceled to skip the pickup entirely.
 */
public abstract class ItemEntityPickupEvent extends PlayerEvent {
    private final ItemEntity itemEntity;

    public ItemEntityPickupEvent(Player player, ItemEntity itemEntity) {
        super(player);
        this.itemEntity = itemEntity;
    }

    public Player getPlayer() {
        return getEntity();
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    /** Before pickup logic runs; cancel to prevent the pickup. */
    public static class Pre extends ItemEntityPickupEvent implements ICancellableEvent {
        private boolean canceled;

        public Pre(Player player, ItemEntity itemEntity) {
            super(player, itemEntity);
        }

        @Override
        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }

        @Override
        public boolean isCanceled() {
            return canceled;
        }
    }

    /** After an item entity has been picked up. */
    public static class Post extends ItemEntityPickupEvent {
        public Post(Player player, ItemEntity itemEntity) {
            super(player, itemEntity);
        }
    }
}
