package net.neoforged.neoforge.internal.fabric.mixin.api;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(FlowerPotBlock.class)
public interface FlowerPotBlockAccessor {

    @Accessor("POTTED_BY_CONTENT")
    static Map<Block, Block> getPottedByContent() {
        throw new AssertionError();
    }

    @Accessor("POTTED_BY_CONTENT")
    static void setPottedByContent(Map<Block, Block> map) {
        throw new AssertionError();
    }
}
