package top.theillusivec4.curios.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import top.theillusivec4.curios.api.SlotContext;

public class CurioChangeEvent extends Event {
    private final LivingEntity entity;
    private final SlotContext slotContext;
    private final ItemStack from;
    private final ItemStack to;

    public CurioChangeEvent(LivingEntity entity, SlotContext slotContext, ItemStack from, ItemStack to) {
        this.entity = entity;
        this.slotContext = slotContext;
        this.from = from;
        this.to = to;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public SlotContext getSlotContext() {
        return slotContext;
    }

    public ItemStack getFrom() {
        return from;
    }

    public ItemStack getTo() {
        return to;
    }
}
