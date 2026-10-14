package net.neoforged.neoforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before the player's arm is rendered in first person. Canceling skips
 * the vanilla arm render. Ported from NeoForge 21.1.
 */
public class RenderArmEvent extends Event implements ICancellableEvent {
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;
    private final AbstractClientPlayer player;
    private final HumanoidArm arm;

    @ApiStatus.Internal
    public RenderArmEvent(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, AbstractClientPlayer player,
            HumanoidArm arm) {
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.player = player;
        this.arm = arm;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack() {
        return poseStack;
    }

    /**
     * {@return the multi-buffer source for rendering}
     */
    public MultiBufferSource getMultiBufferSource() {
        return multiBufferSource;
    }

    /**
     * {@return the packed light value}
     */
    public int getPackedLight() {
        return packedLight;
    }

    /**
     * {@return the player being rendered}
     */
    public AbstractClientPlayer getPlayer() {
        return player;
    }

    /**
     * {@return which arm is being rendered}
     */
    public HumanoidArm getArm() {
        return arm;
    }
}
