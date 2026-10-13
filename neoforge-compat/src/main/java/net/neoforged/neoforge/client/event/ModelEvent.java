package net.neoforged.neoforge.client.event;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public abstract class ModelEvent extends Event {

    /** Modify a model's baked geometry/materials. */
    public static class ModifyBakingResult extends ModelEvent {
        private final Map<ModelResourceLocation, BakedModel> models;

        public ModifyBakingResult(Map<ModelResourceLocation, BakedModel> models) {
            this.models = models;
        }

        public Map<ModelResourceLocation, BakedModel> getModels() {
            return models;
        }

        @Nullable
        public net.minecraft.client.resources.model.ModelBakery getModelBakery() {
            return null;
        }

        @Nullable
        public BakedModel getModel(ModelResourceLocation location) {
            return models.get(location);
        }
    }

    /** Register custom model loaders / additional models. */
    public static class RegisterAdditional extends ModelEvent {
        private final java.util.Set<net.minecraft.client.resources.model.ModelResourceLocation> models = new java.util.HashSet<>();

        public void register(ModelResourceLocation location) {
            models.add(location);
        }

        public void register(ResourceLocation location) {
            models.add(new net.minecraft.client.resources.model.ModelResourceLocation(location, "standalone"));
        }

        public java.util.Set<ModelResourceLocation> getModels() {
            return models;
        }
    }

    /** Register baking-time model modifiers. */
    public static class RegisterGeometryLoaders extends ModelEvent {
        private final Map<ResourceLocation, IGeometryLoader<?>> loaders = new java.util.HashMap<>();

        public void register(ResourceLocation location, IGeometryLoader<?> loader) {
            loaders.put(location, loader);
        }
    }

    /** Fired when the model manager is refreshed (e.g. resource reload). */
    public static class BakingCompleted extends ModelEvent {
        private final Map<ModelResourceLocation, BakedModel> models;

        public BakingCompleted(Map<ModelResourceLocation, BakedModel> models) {
            this.models = models;
        }

        public Map<ModelResourceLocation, BakedModel> getModels() {
            return models;
        }
    }

    public interface IGeometryLoader<T extends UnbakedModel> {
        T read(net.minecraft.client.renderer.block.model.BlockModelDefinition.Context context) throws java.io.IOException;
    }
}
