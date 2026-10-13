package net.neoforged.neoforge.registries;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * NeoForge-style typed holder for blocks.
 */
public class DeferredBlock<T extends Block> extends DeferredHolder<Block, T> {
    public DeferredBlock(ResourceKey<?> registryKey, ResourceLocation location, Supplier<T> supplier) {
        super(registryKey, location, supplier);
    }

    /** NeoForge's {@code DeferredBlock.createBlock(ResourceLocation)} - holder without a registry entry yet. */
    public static <T extends Block> DeferredBlock<T> createBlock(ResourceLocation key) {
        return new DeferredBlock<>(
                net.minecraft.core.registries.Registries.BLOCK, key, () -> null);
    }
}
