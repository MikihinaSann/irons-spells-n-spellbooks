package net.neoforged.neoforge.common.extensions;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge-style {@link BlockEntity} extensions — injected onto
 * {@code net.minecraft.world.level.block.entity.BlockEntity}.
 */
public interface IBlockEntityExtension {
    private BlockEntity self() {
        return (BlockEntity) (Object) this;
    }

    /**
     * Called client-side when a {@link ClientboundBlockEntityDataPacket}
     * arrives for this block entity.
     */
    default void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        if (pkt.getTag() != null) {
            handleUpdateTag(pkt.getTag(), lookupProvider);
        }
    }

    /**
     * Handles a block entity data tag received from the server.
     */
    default void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        self().loadCustomOnly(tag, lookupProvider);
    }

    /**
     * NeoForge "persistent data" bag surviving level restarts; our port keeps
     * a per-instance tag on the block entity via the attachments bridge.
     */
    default CompoundTag getPersistentData() {
        return BlockEntityPersistentData.get(self());
    }

    /** Called when the block entity's chunk finishes loading. */
    default void onLoad() {
    }

    /** Called when the block entity's chunk unloads. */
    default void onChunkUnloaded() {
    }

    /** Invalidates any cached capability lookups for this block entity. */
    default void invalidateCapabilities() {
    }

    /** Client render-data invalidation — requests a re-render of the section. */
    default void requestModelDataUpdate() {
    }

    default ModelData getModelData() {
        return ModelData.EMPTY;
    }

    /**
     * Bounding box used by the chunk render dispatcher to decide whether this
     * block entity renderer is visible.
     */
    default AABB getRenderBoundingBox() {
        return new AABB(self().getBlockPos());
    }

    /**
     * Whether only a creative/op player may modify this block entity's saved
     * NBT (e.g. via debug stick / pick-block copy).
     */
    default boolean onlyOpCanSetNbt() {
        return false;
    }

    /**
     * AABB used for frustum culling of the block entity renderer.
     */
    default net.minecraft.world.phys.AABB getRenderBoundingBox(net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos());
    }
}
