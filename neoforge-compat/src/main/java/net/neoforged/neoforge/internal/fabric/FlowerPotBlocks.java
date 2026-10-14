package net.neoforged.neoforge.internal.fabric;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.neoforge.internal.fabric.mixin.api.FlowerPotBlockAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Backing store for {@code FlowerPotBlock.addPlant}. */
public final class FlowerPotBlocks {
    private FlowerPotBlocks() {
    }

    public static void addPlant(FlowerPotBlock flowerPot, ResourceLocation contentId,
            Supplier<? extends Block> potted) {
        Block content = BuiltInRegistries.BLOCK.get(contentId);
        Map<Block, Block> map = FlowerPotBlockAccessor.getPottedByContent();
        if (!(map instanceof HashMap)) {
            map = new HashMap<>(map);
            FlowerPotBlockAccessor.setPottedByContent(map);
        }
        map.put(content, potted.get());
    }
}
