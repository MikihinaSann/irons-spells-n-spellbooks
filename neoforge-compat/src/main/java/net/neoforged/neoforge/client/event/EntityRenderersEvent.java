package net.neoforged.neoforge.client.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.Event;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class EntityRenderersEvent extends Event {

    /** Register entity/block-entity renderers. Drained at client init into FAPI's factories. */
    public static class RegisterRenderers extends EntityRenderersEvent {
        private final Map<EntityType<?>, EntityRendererProvider<?>> entityRenderers = new java.util.HashMap<>();
        private final Map<BlockEntityType<?>, BlockEntityRendererProvider<?>> blockEntityRenderers = new java.util.HashMap<>();

        public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> type, EntityRendererProvider<T> provider) {
            entityRenderers.put(type, provider);
        }

        public <T extends Entity> void registerEntityRenderer(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> provider) {
            registerEntityRenderer(type.get(), provider);
        }

        public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> provider) {
            blockEntityRenderers.put(type, provider);
        }

        public <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<? extends BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> provider) {
            registerBlockEntityRenderer(type.get(), provider);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public void drain() {
            entityRenderers.forEach((type, provider) ->
                    net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register((EntityType) type, (EntityRendererProvider) provider));
            blockEntityRenderers.forEach((type, provider) ->
                    net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register((BlockEntityType) type, (BlockEntityRendererProvider) provider));
        }
    }

    /** Register layer definitions. Drained at client init into FAPI's layer registry. */
    public static class RegisterLayerDefinitions extends EntityRenderersEvent {
        private final Map<ModelLayerLocation, Supplier<LayerDefinition>> layers = new java.util.HashMap<>();

        public void registerLayerDefinition(ModelLayerLocation location, Supplier<LayerDefinition> supplier) {
            layers.put(location, supplier);
        }

        public Map<ModelLayerLocation, Supplier<LayerDefinition>> getLayers() {
            return layers;
        }
    }

    /** Add extra layers to existing renderers. Matches NeoForge's real AddLayers API. */
    public static class AddLayers extends EntityRenderersEvent {
        private final Map<EntityType<?>, net.minecraft.client.renderer.entity.EntityRenderer<?>> renderers;
        private final Map<net.minecraft.client.resources.PlayerSkin.Model, net.minecraft.client.renderer.entity.EntityRenderer<? extends Player>> skins;
        private final net.minecraft.client.renderer.entity.EntityRendererProvider.Context context;

        public AddLayers(Map<EntityType<?>, net.minecraft.client.renderer.entity.EntityRenderer<?>> renderers,
                         Map<net.minecraft.client.resources.PlayerSkin.Model, net.minecraft.client.renderer.entity.EntityRenderer<? extends Player>> skins,
                         net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) {
            this.renderers = renderers;
            this.skins = skins;
            this.context = context;
        }

        public Map<EntityType<?>, net.minecraft.client.renderer.entity.EntityRenderer<?>> getRenderers() {
            return renderers;
        }

        public java.util.Set<EntityType<?>> getEntityTypes() {
            return renderers.keySet();
        }

        @SuppressWarnings("unchecked")
        public <T extends Entity, R extends net.minecraft.client.renderer.entity.EntityRenderer<T>> R getRenderer(
                EntityType<? extends T> type) {
            return (R) renderers.get(type);
        }

        public java.util.Set<net.minecraft.client.resources.PlayerSkin.Model> getSkins() {
            return skins.keySet();
        }

        @SuppressWarnings("unchecked")
        public <R extends net.minecraft.client.renderer.entity.EntityRenderer<? extends Player>> R getSkin(
                net.minecraft.client.resources.PlayerSkin.Model skin) {
            return (R) skins.get(skin);
        }

        public net.minecraft.client.model.geom.EntityModelSet getEntityModels() {
            return context.getModelSet();
        }

        public net.minecraft.client.renderer.entity.EntityRendererProvider.Context getContext() {
            return context;
        }
    }

    /**
     * Fired when the skull renderers map is built — allows registering custom
     * {@link net.minecraft.world.level.block.SkullBlock.Type} models.
     * NeoForge posts this from {@code SkullBlockRenderer.createSkullRenderers}.
     */
    public static class CreateSkullModels extends EntityRenderersEvent {
        private final java.util.Map<net.minecraft.world.level.block.SkullBlock.Type,
                net.minecraft.client.model.SkullModelBase> models = new java.util.LinkedHashMap<>();
        private final net.minecraft.client.model.geom.EntityModelSet entityModelSet;

        @org.jetbrains.annotations.ApiStatus.Internal
        public CreateSkullModels(net.minecraft.client.model.geom.EntityModelSet entityModelSet) {
            this.entityModelSet = entityModelSet;
        }

        public void registerSkullModel(net.minecraft.world.level.block.SkullBlock.Type type,
                net.minecraft.client.model.SkullModelBase model) {
            this.models.put(type, model);
        }

        public net.minecraft.client.model.geom.EntityModelSet getEntityModelSet() {
            return entityModelSet;
        }

        @org.jetbrains.annotations.ApiStatus.Internal
        public java.util.Map<net.minecraft.world.level.block.SkullBlock.Type,
                net.minecraft.client.model.SkullModelBase> getModels() {
            return java.util.Collections.unmodifiableMap(models);
        }
    }
}
