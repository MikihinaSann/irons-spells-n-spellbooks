package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class LivingEntityUseItemEvent extends LivingEvent {
    private ItemStack item;
    private int duration;
    private LivingEntity target;

    public LivingEntityUseItemEvent(LivingEntity entity, ItemStack item, int duration) {
        super(entity);
        this.item = item;
        this.duration = duration;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    /** The entity this item is being used on, if applicable. */
    public LivingEntity getTarget() {
        return target;
    }

    public static class Start extends LivingEntityUseItemEvent implements ICancellableEvent {
        public Start(LivingEntity entity, ItemStack item, int duration) {
            super(entity, item, duration);
        }
    }

    public static class Tick extends LivingEntityUseItemEvent implements ICancellableEvent {
        public Tick(LivingEntity entity, ItemStack item, int duration) {
            super(entity, item, duration);
        }
    }

    public static class Stop extends LivingEntityUseItemEvent implements ICancellableEvent {
        public Stop(LivingEntity entity, ItemStack item, int duration) {
            super(entity, item, duration);
        }
    }

    public static class Finish extends LivingEntityUseItemEvent {
        private ItemStack resultStack;

        public Finish(LivingEntity entity, ItemStack item, int duration, ItemStack result) {
            super(entity, item, duration);
            this.resultStack = result;
        }

        public ItemStack getResultStack() {
            return resultStack;
        }

        public void setResultStack(ItemStack resultStack) {
            this.resultStack = resultStack;
        }
    }
}
