package net.neoforged.neoforge.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class ItemTossEvent extends EntityEvent implements ICancellableEvent {
    private final Player player;

    public ItemTossEvent(ItemEntity entityItem, Player player) {
        super(entityItem);
        this.player = player;
    }

    @Override
    public ItemEntity getEntity() {
        return (ItemEntity) super.getEntity();
    }

    public Player getPlayer() {
        return player;
    }
}
