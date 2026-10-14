package net.neoforged.neoforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.IPlantable;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge-style {@link BlockState} extensions — injected onto
 * {@code BlockBehaviour.BlockStateBase}.
 */
public interface IBlockStateExtension extends net.fabricmc.fabric.api.block.v1.FabricBlockState {
    private BlockState self() {
        return (BlockState) (Object) this;
    }

    private IBlockExtension blockExt() {
        return (IBlockExtension) self().getBlock();
    }

    @Nullable
    default BlockState getToolModifiedState(UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        return blockExt().getToolModifiedState(self(), context, itemAbility, simulate);
    }

    default boolean canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity) {
        return blockExt().canEntityDestroy(self(), level, pos, entity);
    }

    default boolean onDestroyedByPlayer(Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        return blockExt().onDestroyedByPlayer(self(), level, pos, player, willHarvest, fluid);
    }

    default boolean canHarvestBlock(BlockGetter level, BlockPos pos, Player player) {
        return blockExt().canHarvestBlock(self(), level, pos, player);
    }

    default boolean canDropFromExplosion(BlockGetter level, BlockPos pos, Explosion explosion) {
        return blockExt().canDropFromExplosion(self(), level, pos, explosion);
    }

    default float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion) {
        return blockExt().getExplosionResistance(self(), level, pos, explosion);
    }

    default ItemStack getCloneItemStack(HitResult target, LevelReader level, BlockPos pos, Player player) {
        return blockExt().getCloneItemStack(self(), target, level, pos, player);
    }

    default float getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return blockExt().getFriction(self(), level, pos, entity);
    }

    default int getLightEmission(BlockGetter level, BlockPos pos) {
        return blockExt().getLightEmission(self(), level, pos);
    }

    default boolean isLadder(LevelReader level, BlockPos pos, LivingEntity entity) {
        return blockExt().isLadder(self(), level, pos, entity);
    }

    default boolean isBurning(BlockGetter level, BlockPos pos) {
        return blockExt().isBurning(self(), level, pos);
    }

    default boolean isPortalFrame(BlockGetter level, BlockPos pos) {
        return blockExt().isPortalFrame(self(), level, pos);
    }

    default boolean isFoliage(BlockGetter level, BlockPos pos) {
        return blockExt().isFoliage(self(), level, pos);
    }

    default boolean isStickyBlock() {
        return blockExt().isStickyBlock(self());
    }

    default boolean canStickTo(BlockState other) {
        return blockExt().canStickTo(self(), other);
    }

    default boolean isConduitFrame(LevelReader level, BlockPos pos, BlockPos conduit) {
        return blockExt().isConduitFrame(self(), level, pos, conduit);
    }

    default boolean isScaffolding(LivingEntity entity) {
        return blockExt().isScaffolding(self(), entity.level(), entity.blockPosition(), entity);
    }

    default boolean makesOpenTrapdoorAboveClimbable(LevelReader level, BlockPos pos, BlockState trapdoorState) {
        return blockExt().makesOpenTrapdoorAboveClimbable(self(), level, pos, trapdoorState);
    }

    default boolean canRedstoneConnectTo(BlockGetter level, BlockPos pos, @Nullable Direction side) {
        return blockExt().canRedstoneConnectTo(self(), level, pos, side);
    }

    default boolean canBeHydrated(BlockGetter getter, BlockPos pos, FluidState fluidState, BlockPos fluidPos) {
        return blockExt().canBeHydrated(self(), getter, pos, fluidState, fluidPos);
    }

    default float getEnchantPowerBonus(LevelReader level, BlockPos pos) {
        return blockExt().getEnchantPowerBonus(self(), level, pos);
    }

    default boolean canSustainPlant(BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
        return blockExt().canSustainPlant(self(), level, pos, facing, plantable);
    }

    default boolean onTreeGrow(LevelReader level, java.util.function.BiConsumer<BlockPos, BlockState> placeFunction, net.minecraft.util.RandomSource randomSource, BlockPos pos, net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration config) {
        return blockExt().onTreeGrow(self(), level, placeFunction, randomSource, pos, config);
    }

    default boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir) {
        return blockExt().hidesNeighborFace(level, pos, self(), neighborState, dir);
    }

    default boolean supportsExternalFaceHiding() {
        return blockExt().supportsExternalFaceHiding(self());
    }

    default BlockState getAppearance(BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        return blockExt().getAppearance(self(), level, pos, side, queryState, queryPos);
    }

    default SoundType getSoundType(LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return blockExt().getSoundType(self(), level, pos, entity);
    }

    default void onCaughtFire(Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        blockExt().onCaughtFire(self(), level, pos, face, igniter);
    }

    default boolean ignitedByLava(BlockGetter level, BlockPos pos) {
        return blockExt().ignitedByLava(self(), level, pos);
    }

    default boolean shouldDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
        return blockExt().shouldDisplayFluidOverlay(self(), level, pos, fluidState);
    }

    default int getFireSpreadSpeed(BlockGetter level, BlockPos pos, Direction direction) {
        return blockExt().getFireSpreadSpeed(self(), level, pos, direction);
    }

    default int getFlammability(BlockGetter level, BlockPos pos, Direction direction) {
        return blockExt().getFlammability(self(), level, pos, direction);
    }

    default void onBlockExploded(Level level, BlockPos pos, Explosion explosion) {
        blockExt().onBlockExploded(self(), level, pos, explosion);
    }

    default boolean addLandingEffects(ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        blockExt().addLandingEffects(self(), level, pos, state2, entity, numberOfParticles);
        return true;
    }

    default boolean addRunningEffects(Level level, BlockPos pos, Entity entity) {
        return blockExt().addRunningEffects(self(), level, pos, entity);
    }

    default boolean isSlimeBlock() {
        return blockExt().isSlimeBlock(self());
    }

    @Nullable
    default PathType getBlockPathType(BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return blockExt().getBlockPathType(self(), level, pos, mob);
    }

    @Nullable
    default PathType getAdjacentBlockPathType(BlockGetter level, BlockPos pos, @Nullable Mob mob, PathType originalType) {
        return blockExt().getAdjacentBlockPathType(self(), level, pos, mob, originalType);
    }

    default Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return blockExt().getBeaconColorMultiplier(self(), level, pos, beaconPos);
    }

    default int getExpDrop(LevelAccessor level, BlockPos pos,
                           @Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity,
                           @Nullable Entity breaker, ItemStack tool) {
        return blockExt().getExpDrop(self(), level, pos, blockEntity, breaker, tool);
    }

    default void onNeighborChange(LevelReader level, BlockPos pos, BlockPos neighbor) {
        blockExt().onNeighborChange(self(), level, pos, neighbor);
    }

    default void onNeighborChange(LevelReader level, BlockPos pos, BlockPos neighbor, net.minecraft.world.level.block.entity.BlockEntity observer) {
        blockExt().onNeighborChange(self(), level, pos, neighbor, observer);
    }
}
