package net.neoforged.neoforge.event.entity.player;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemTooltipEvent extends Event {
    private final ItemStack itemStack;
    private final List<Entity> entityList;
    private final List<Component> toolTip;
    private final TooltipFlag flags;
    private final Player entity;

    public ItemTooltipEvent(ItemStack itemStack, @Nullable Player entity, List<Component> toolTip, TooltipFlag flags) {
        this.itemStack = itemStack;
        this.entity = entity;
        this.entityList = entity == null ? List.of() : List.of(entity);
        this.toolTip = toolTip;
        this.flags = flags;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<Component> getToolTip() {
        return toolTip;
    }

    public TooltipFlag getFlags() {
        return flags;
    }

    @Nullable
    public Player getEntity() {
        return entity;
    }
}
