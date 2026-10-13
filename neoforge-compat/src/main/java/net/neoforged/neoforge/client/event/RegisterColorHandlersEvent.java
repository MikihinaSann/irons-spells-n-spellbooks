package net.neoforged.neoforge.client.event;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Collects block/item color registrations; drained into FAPI's color registries
 * at client init.
 */
public abstract class RegisterColorHandlersEvent extends Event {

    public static class Block extends RegisterColorHandlersEvent {
        private final List<Runnable> registrations = new ArrayList<>();

        public void register(BlockColor color, Supplier<? extends net.minecraft.world.level.block.Block>[] blocks) {
            for (var supplier : blocks) {
                var block = supplier.get();
                registrations.add(() -> net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.register(color, block));
            }
        }

        public void register(BlockColor color, net.minecraft.world.level.block.Block... blocks) {
            registrations.add(() -> net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.register(color, blocks));
        }

        public void drain() {
            registrations.forEach(Runnable::run);
        }
    }

    public static class Item extends RegisterColorHandlersEvent {
        private final List<Runnable> registrations = new ArrayList<>();

        public void register(ItemColor color, Supplier<? extends net.minecraft.world.level.ItemLike>[] items) {
            for (var supplier : items) {
                var item = supplier.get();
                registrations.add(() -> net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register(color, item));
            }
        }

        public void register(ItemColor color, net.minecraft.world.level.ItemLike... items) {
            registrations.add(() -> net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register(color, items));
        }

        public void drain() {
            registrations.forEach(Runnable::run);
        }
    }
}
