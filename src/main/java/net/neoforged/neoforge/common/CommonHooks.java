package net.neoforged.neoforge.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

/**
 * Static helpers NeoForge exposes for firing events / misc queries.
 */
public class CommonHooks {

    /**
     * Fires a block-break event; returns the event so callers can inspect the
     * cancellation state.
     */
    public static BlockEvent.BreakEvent fireBlockBreak(Level level, GameType gameModeForPlayer,
                                                       ServerPlayer player, BlockPos pos, BlockState state) {
        var event = new BlockEvent.BreakEvent(level, pos, state, player);
        return NeoForge.EVENT_BUS.post(event);
    }

    /**
     * Looting level for a killer. Vanilla consults attributes in 1.21.
     */
    public static int getLootingLevel(Entity target, Entity killer, net.minecraft.world.damagesource.DamageSource source) {
        return 0;
    }
}
