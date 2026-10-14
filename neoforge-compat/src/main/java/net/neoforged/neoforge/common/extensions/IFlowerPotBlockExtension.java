package net.neoforged.neoforge.common.extensions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;

import java.util.function.Supplier;

/**
 * NeoForge's {@code FlowerPotBlock.addPlant} — registers a potted variant for
 * a content block.
 */
public interface IFlowerPotBlockExtension {
    private FlowerPotBlock self() {
        return (FlowerPotBlock) (Object) this;
    }

    /**
     * Registers {@code plant} as the potted form of the content block with id
     * {@code fullPlant}.
     */
    default void addPlant(ResourceLocation fullPlant, Supplier<? extends Block> plant) {
        net.neoforged.neoforge.internal.fabric.FlowerPotBlocks.addPlant(self(), fullPlant, plant);
    }
}
