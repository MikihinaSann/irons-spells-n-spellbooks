package net.neoforged.neoforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when {@code ScreenEffectRenderer} draws a fullscreen overlay for a
 * block/fluid the camera is inside — mirrors NeoForge's
 * {@code RenderBlockScreenEffectEvent}.
 */
public class RenderBlockScreenEffectEvent extends Event implements ICancellableEvent {
    public enum OverlayType {
        FIRE, BLOCK, WATER, POWDER_SNOW
    }

    private final Player player;
    private final PoseStack poseStack;
    private final OverlayType overlayType;
    @Nullable
    private final BlockState blockState;
    @Nullable
    private final BlockPos blockPos;

    public RenderBlockScreenEffectEvent(Player player, PoseStack poseStack, OverlayType overlayType,
            @Nullable BlockState blockState, @Nullable BlockPos blockPos) {
        this.player = player;
        this.poseStack = poseStack;
        this.overlayType = overlayType;
        this.blockState = blockState;
        this.blockPos = blockPos;
    }

    public Player getPlayer() {
        return player;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public OverlayType getOverlayType() {
        return overlayType;
    }

    @Nullable
    public BlockState getBlockState() {
        return blockState;
    }

    @Nullable
    public BlockPos getBlockPos() {
        return blockPos;
    }
}
