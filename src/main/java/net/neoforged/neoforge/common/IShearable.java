package net.neoforged.neoforge.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Minimal stand-in for NeoForge's {@code IShearable}. Blocks implement it to opt into
 * shears-style drops; on Fabric the default no-op behavior is used.
 */
public interface IShearable {
    default boolean isShearable(@Nullable Player player, ItemStack item, Level level, BlockPos pos) {
        return true;
    }

    default List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level level, BlockPos pos) {
        return List.of();
    }

    default void spawnShearedDrop(Level level, BlockPos pos, ItemStack drop) {
        net.minecraft.world.entity.item.ItemEntity entity = new net.minecraft.world.entity.item.ItemEntity(
                level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
        level.addFreshEntity(entity);
    }
}
