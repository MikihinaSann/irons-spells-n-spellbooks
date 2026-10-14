package net.neoforged.neoforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge-style {@link Block} extensions — injected onto
 * {@code net.minecraft.world.level.block.state.BlockBehaviour} via Loom
 * {@code injected_interfaces}, so both {@code Block} and state-base code can
 * reach them.
 */
public interface IBlockExtension extends net.fabricmc.fabric.api.block.v1.FabricBlock {
    private BlockBehaviour self() {
        return (BlockBehaviour) this;
    }

    /**
     * State modified by a tool action (strip, till, douse...), or null if not
     * applicable.
     */
    @Nullable
    default BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        return null;
    }

    @Nullable
    default PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return null;
    }

    @Nullable
    default PathType getAdjacentBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, PathType originalType) {
        return null;
    }

    default boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return state.getDestroySpeed(level, pos) >= 0;
    }

    default boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player,
                                        boolean willHarvest, FluidState fluid) {
        return level.removeBlock(pos, !fluid.isEmpty());
    }

    default boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest) {
        return onDestroyedByPlayer(state, level, pos, player, willHarvest, level.getFluidState(pos));
    }

    default boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return !state.requiresCorrectToolForDrops() || player.hasCorrectToolForDrops(state);
    }

    default boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return true;
    }

    default float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return self().explosionResistance;
    }

    default ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return state.getBlock().getCloneItemStack(level, pos, state);
    }

    default float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return self().friction;
    }

    default int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getLightEmission();
    }

    default boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return state.is(net.minecraft.tags.BlockTags.CLIMBABLE);
    }

    default boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(net.minecraft.tags.BlockTags.FIRE);
    }

    default boolean isPortalFrame(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    default boolean isFoliage(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    default boolean isStickyBlock(BlockState state) {
        return state.is(net.minecraft.world.level.block.Blocks.SLIME_BLOCK)
                || state.is(net.minecraft.world.level.block.Blocks.HONEY_BLOCK);
    }

    default boolean canStickTo(BlockState state, BlockState other) {
        return state.getBlock() == other.getBlock();
    }

    default boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
        return false;
    }

    default boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return state.is(net.minecraft.world.level.block.Blocks.SCAFFOLDING);
    }

    default boolean makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState) {
        return false;
    }

    default boolean canRedstoneConnectTo(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction side) {
        return state.isSignalSource();
    }

    default boolean canBeHydrated(BlockState state, BlockGetter getter, BlockPos pos, FluidState fluidState, BlockPos fluidPos) {
        return state.canBeReplaced(fluidState.getType());
    }

    default float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return 0;
    }

    default boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
        return false;
    }

    default boolean onTreeGrow(BlockState state, LevelReader level, java.util.function.BiConsumer<BlockPos, BlockState> placeFunction, net.minecraft.util.RandomSource randomSource, BlockPos pos, net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration config) {
        return true;
    }

    default boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        return false;
    }

    default boolean supportsExternalFaceHiding(BlockState state) {
        return false;
    }

    default BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side,
                                     @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        return state;
    }

    default SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return state.getSoundType();
    }

    default void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
    }

    default boolean ignitedByLava(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    default boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
        return fluidState.is(net.minecraft.tags.FluidTags.WATER);
    }

    default int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    default int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    default void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
    }

    default void addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
    }

    default boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        return false;
    }

    default boolean isSlimeBlock(BlockState state) {
        return state.is(net.minecraft.world.level.block.Blocks.SLIME_BLOCK);
    }

    default net.minecraft.world.level.material.PushReaction getPistonPushReaction(BlockState state) {
        return state.getPistonPushReaction();
    }

    default Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return null;
    }

    default int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos,
                           @Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity,
                           @Nullable Entity breaker, ItemStack tool) {
        return 0;
    }

    default void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
    }

    default void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor, BlockEntity observer) {
        onNeighborChange(state, level, pos, neighbor);
    }
}
