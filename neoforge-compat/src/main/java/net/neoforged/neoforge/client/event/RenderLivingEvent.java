package net.neoforged.neoforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class RenderLivingEvent<T extends LivingEntity, M> extends Event {
    private final T entity;
    private final LivingEntityRenderer<T, ?> renderer;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    public RenderLivingEvent(T entity, LivingEntityRenderer<T, ?> renderer, float partialTick, PoseStack poseStack,
                             MultiBufferSource multiBufferSource, int packedLight) {
        this.entity = entity;
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    public T getEntity() {
        return entity;
    }

    public LivingEntityRenderer<T, ?> getRenderer() {
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

    public static class Pre<T extends LivingEntity, M>
            extends RenderLivingEvent<T, M> implements ICancellableEvent {
        public Pre(T entity, LivingEntityRenderer<T, ?> renderer, float partialTick, PoseStack poseStack,
                   MultiBufferSource multiBufferSource, int packedLight) {
            super(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }

    public static class Post<T extends LivingEntity, M>
            extends RenderLivingEvent<T, M> {
        public Post(T entity, LivingEntityRenderer<T, ?> renderer, float partialTick, PoseStack poseStack,
                    MultiBufferSource multiBufferSource, int packedLight) {
            super(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }
}
