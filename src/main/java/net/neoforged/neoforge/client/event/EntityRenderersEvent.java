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

    /** Add extra layers to existing renderers. */
    public static class AddLayers extends EntityRenderersEvent {
        private final Map<EntityType<?>, net.minecraft.client.renderer.entity.EntityRenderer<?>> renderers;
        private final Map<net.minecraft.client.resources.PlayerSkin.Model, net.minecraft.client.renderer.entity.player.PlayerRenderer> skins;

        public AddLayers(Map<EntityType<?>, net.minecraft.client.renderer.entity.EntityRenderer<?>> renderers,
                         Map<net.minecraft.client.resources.PlayerSkin.Model, net.minecraft.client.renderer.entity.player.PlayerRenderer> skins) {
            this.renderers = renderers;
            this.skins = skins;
        }

        public Map<EntityType<?>, net.minecraft.client.renderer.entity.EntityRenderer<?>> getRenderers() {
            return renderers;
        }

        public java.util.Set<EntityType<?>> getEntityTypes() {
            return renderers.keySet();
        }

        @SuppressWarnings("unchecked")
        public <T extends Entity> net.minecraft.client.renderer.entity.EntityRenderer<T> getRenderer(EntityType<T> type) {
            return (net.minecraft.client.renderer.entity.EntityRenderer<T>) renderers.get(type);
        }

        public Map<net.minecraft.client.resources.PlayerSkin.Model, net.minecraft.client.renderer.entity.player.PlayerRenderer> getSkins() {
            return skins;
        }

        public net.minecraft.client.renderer.entity.EntityRenderer<? extends Player> getSkin(net.minecraft.client.resources.PlayerSkin.Model skin) {
            return skins.get(skin);
        }

        public net.minecraft.client.renderer.entity.player.PlayerRenderer getPlayer(net.minecraft.client.resources.PlayerSkin.Model skin) {
            return skins.get(skin);
        }
    }
}
