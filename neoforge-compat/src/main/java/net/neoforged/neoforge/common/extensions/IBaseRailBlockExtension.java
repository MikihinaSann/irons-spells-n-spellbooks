package net.neoforged.neoforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

/**
 * NeoForge's {@code BaseRailBlock} extensions.
 */
public interface IBaseRailBlockExtension {
    private BaseRailBlock self() {
        return (BaseRailBlock) (Object) this;
    }

    /** The rail shape at the position — may be overridden by custom rails. */
    default RailShape getRailDirection(BlockState state, Level level, BlockPos pos, @org.jetbrains.annotations.Nullable AbstractMinecart cart) {
        return state.getValue(self().getShapeProperty());
    }

    /** Called when a minecart passes over this rail. */
    default void onMinecartPass(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
    }

    /** NeoForge: maximum minecart speed on this rail. */
    default float getRailMaxSpeed(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        return 0.4F;
    }
}
