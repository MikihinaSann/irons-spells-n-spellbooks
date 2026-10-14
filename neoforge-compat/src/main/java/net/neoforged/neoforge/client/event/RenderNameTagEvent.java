package net.neoforged.neoforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.EntityEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when an entity's name tag is rendered. Allows controlling whether the
 * name tag renders and replacing its content. Ported from NeoForge 21.1.
 */
public class RenderNameTagEvent extends EntityEvent {
    private final Component originalContent;
    private final EntityRenderer<?> entityRenderer;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;
    private final float partialTick;

    private Component content;
    private TriState canRender = TriState.DEFAULT;

    @ApiStatus.Internal
    public RenderNameTagEvent(Entity entity, Component content, EntityRenderer<?> entityRenderer, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int packedLight, float partialTick) {
        super(entity);
        this.originalContent = content;
        this.setContent(this.originalContent);
        this.entityRenderer = entityRenderer;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.partialTick = partialTick;
    }

    /**
     * Sets whether the name tag should render.
     */
    public void setCanRender(TriState canRender) {
        this.canRender = canRender;
    }

    /**
     * {@return the current render verdict}
     */
    public TriState canRender() {
        return canRender;
    }

    /**
     * Replaces the rendered content.
     */
    public void setContent(Component contents) {
        this.content = contents;
    }

    /**
     * {@return the content that will be rendered}
     */
    public Component getContent() {
        return this.content;
    }

    /**
     * {@return the original content passed to the renderer}
     */
    public Component getOriginalContent() {
        return this.originalContent;
    }

    /**
     * {@return the entity renderer rendering the name tag}
     */
    public EntityRenderer<?> getEntityRenderer() {
        return this.entityRenderer;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack() {
        return this.poseStack;
    }

    /**
     * {@return the multi-buffer source for rendering}
     */
    public MultiBufferSource getMultiBufferSource() {
        return this.multiBufferSource;
    }

    /**
     * {@return the packed light for the rendered text}
     */
    public int getPackedLight() {
        return this.packedLight;
    }

    /**
     * {@return the partial tick time}
     */
    public float getPartialTick() {
        return this.partialTick;
    }
}
