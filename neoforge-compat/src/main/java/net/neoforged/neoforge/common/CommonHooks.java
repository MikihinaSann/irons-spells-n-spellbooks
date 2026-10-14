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

    public static boolean onLivingDeath(net.minecraft.world.entity.LivingEntity entity,
            net.minecraft.world.damagesource.DamageSource src) {
        return NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.living.LivingDeathEvent(entity, src)).isCanceled();
    }

    public static void onLivingJump(net.minecraft.world.entity.LivingEntity entity) {
        NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent(entity));
    }

    /**
     * NeoForge: fires {@link LivingShieldBlockEvent} from inside
     * {@code LivingEntity.hurt}, replacing the vanilla shield block check.
     */
    public static net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent onDamageBlock(
            net.minecraft.world.entity.LivingEntity blocker,
            net.neoforged.neoforge.common.damagesource.DamageContainer container,
            boolean blocked) {
        var event = new net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent(blocker, container, blocked);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    /**
     * NeoForge: fires {@link LivingFallEvent} at the head of
     * {@code LivingEntity.causeFallDamage}; {@code null} cancels the fall.
     */
    public static float[] onLivingFall(
            net.minecraft.world.entity.LivingEntity entity, float distance, float damageMultiplier) {
        var event = new net.neoforged.neoforge.event.entity.living.LivingFallEvent(entity, distance, damageMultiplier);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return null;
        }
        return new float[] { event.getDistance(), event.getDamageMultiplier() };
    }

    public static void onEmptyClick(net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickEmpty(player, hand));
    }

    /** NeoForge: fires {@link LivingDropsEvent}; returns true when canceled. */
    public static boolean onLivingDrops(net.minecraft.world.entity.LivingEntity entity,
            net.minecraft.world.damagesource.DamageSource source,
            java.util.Collection<net.minecraft.world.entity.item.ItemEntity> drops,
            boolean recentlyHit) {
        return NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.living.LivingDropsEvent(
                entity, source, drops, 0, recentlyHit)).isCanceled();
    }

    /** NeoForge: fires {@link LivingKnockBackEvent}; returns the (possibly mutated) event. */
    public static net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent onLivingKnockBack(
            net.minecraft.world.entity.LivingEntity entity, float strength, double ratioX, double ratioZ) {
        var event = new net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent(entity, strength, ratioX, ratioZ);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    /**
     * NeoForge: fires {@link net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent};
     * returns the event-adjusted burn time for the stack.
     */
    public static int getBurnTime(net.minecraft.world.item.ItemStack stack,
            @org.jetbrains.annotations.Nullable net.minecraft.world.item.crafting.RecipeType<?> recipeType) {
        if (stack.isEmpty()) {
            return 0;
        }
        int burnTime = net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.getFuel()
                .getOrDefault(stack.getItem(), 0);
        var event = new net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent(stack, burnTime, recipeType);
        NeoForge.EVENT_BUS.post(event);
        return event.getBurnTime();
    }
}
