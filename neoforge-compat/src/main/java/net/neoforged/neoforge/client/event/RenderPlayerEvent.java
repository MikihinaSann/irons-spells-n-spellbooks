package net.neoforged.neoforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/** NeoForge RenderPlayerEvent - Pre is cancellable, Post fires after render. */
public abstract class RenderPlayerEvent extends Event {
    private final AbstractClientPlayer player;
    private final PlayerRenderer renderer;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    protected RenderPlayerEvent(AbstractClientPlayer player, PlayerRenderer renderer, float partialTick,
            PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        this.player = player;
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    public AbstractClientPlayer getEntity() {
        return player;
    }

    public PlayerRenderer getRenderer() {
        return renderer;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return multiBufferSource;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public static class Pre extends RenderPlayerEvent implements ICancellableEvent {
        public Pre(AbstractClientPlayer player, PlayerRenderer renderer, float partialTick,
                PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
            super(player, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }

    public static class Post extends RenderPlayerEvent {
        public Post(AbstractClientPlayer player, PlayerRenderer renderer, float partialTick,
                PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
            super(player, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }
}
