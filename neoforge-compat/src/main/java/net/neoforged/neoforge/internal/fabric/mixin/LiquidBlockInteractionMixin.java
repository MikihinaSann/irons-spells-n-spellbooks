package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies {@link FluidInteractionRegistry} custom reactions before vanilla
 * fluid contact logic — mirrors NeoForge's {@code LiquidBlock} patch.
 */
@Mixin(LiquidBlock.class)
public abstract class LiquidBlockInteractionMixin {
    private static final Direction[] NEIGHBOR_DIRECTIONS = {
            Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.UP
    };

    @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true)
    private void neoforgeCompat$fluidInteraction(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        FluidState own = state.getFluidState();
        FluidType ownType = own.getFluidType();
        var interactions = FluidInteractionRegistry.getInteractions(ownType);
        if (interactions.isEmpty()) {
            return;
        }
        for (Direction dir : NEIGHBOR_DIRECTIONS) {
            BlockPos neighborPos = pos.relative(dir);
            FluidState neighbor = level.getFluidState(neighborPos);
            FluidType neighborType = neighbor.getFluidType();
            if (neighborType == ownType || neighbor.isEmpty()) {
                continue;
            }
            for (var info : interactions) {
                if (info.predicate().test(neighborType)) {
                    level.setBlockAndUpdate(pos, info.interactionResult().apply(neighbor));
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}
