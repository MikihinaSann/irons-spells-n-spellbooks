package dev.shadowsoffire.placebo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.shadowsoffire.placebo.events.AnvilLandEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin {

    // onLand is inherited from the Fallable interface — name+descriptor (no
    // owner) so the remap resolves it via the AnvilBlock hierarchy.
    @Inject(at = @At("TAIL"), require = 1,
            method = "onLand(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/item/FallingBlockEntity;)V")
    public void onLand(Level level, BlockPos pos, BlockState newState, BlockState oldState, FallingBlockEntity entity, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new AnvilLandEvent(level, pos, newState, oldState, entity));
    }

}
