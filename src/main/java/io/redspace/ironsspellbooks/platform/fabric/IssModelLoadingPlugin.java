package io.redspace.ironsspellbooks.platform.fabric;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.IronsSpellbooksFabric;
import io.redspace.ironsspellbooks.render.SeparateTransformsUnbakedModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Bridges two NeoForge model features onto Fabric's model loading API:
 *
 * <ul>
 *   <li>{@code neoforge:separate_transforms} item models, which vanilla silently
 *   drops (leaving invisible items). The loader json is detected during the
 *   reload prepare phase and wrapped by {@link SeparateTransformsUnbakedModel}.</li>
 *   <li>{@link ModelEvent.ModifyBakingResult}, dispatched once per top-level bake.
 *   Subscribers mutate a map keyed by {@link ModelResourceLocation} (e.g. wrapping
 *   the scroll inventory model); the map entry is read back as the effective model.</li>
 * </ul>
 *
 * <p>This FAPI version does not read the {@code fabric:model_loading} entrypoint —
 * registration is programmatic via {@link PreparableModelLoadingPlugin#register}.
 */
public class IssModelLoadingPlugin implements PreparableModelLoadingPlugin<Map<ResourceLocation, JsonObject>> {

    /**
     * Finds item model jsons using NeoForge's {@code separate_transforms} loader.
     * Runs during the resource-reload prepare phase where a real
     * {@link ResourceManager} is available.
     */
    public static CompletableFuture<Map<ResourceLocation, JsonObject>> scanSeparateTransforms(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, JsonObject> found = new HashMap<>();
            for (var entry : resourceManager.listResources("models/item", path -> path.getPath().endsWith(".json")).entrySet()) {
                try (var reader = entry.getValue().openAsReader()) {
                    var json = JsonParser.parseReader(reader).getAsJsonObject();
                    var loader = json.has("loader") ? json.get("loader").getAsString() : null;
                    if ("neoforge:separate_transforms".equals(loader)) {
                        ResourceLocation key = entry.getKey();
                        String p = key.getPath();
                        ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(
                                key.getNamespace(), p.substring("models/".length(), p.length() - ".json".length()));
                        found.put(modelId, json);
                    }
                } catch (Exception ignored) {
                }
            }
            return found;
        }, executor);
    }

    @Override
    public void onInitializeModelLoader(Map<ResourceLocation, JsonObject> separateTransforms, ModelLoadingPlugin.Context pluginContext) {
        IronsSpellbooks.LOGGER.info("ISS model plugin init: {} separate_transforms models", separateTransforms.size());

        pluginContext.modifyModelOnLoad().register((model, context) -> {
            var json = context.resourceId() != null ? separateTransforms.get(context.resourceId()) : null;
            if (json == null) {
                return model;
            }
            var parsed = SeparateTransformsUnbakedModel.parse(json);
            if (parsed == null) {
                IronsSpellbooks.LOGGER.warn("Failed to parse separate_transforms model {}", context.resourceId());
            }
            return parsed != null ? parsed : model;
        });

        pluginContext.modifyModelAfterBake().register((model, context) -> {
            ModelResourceLocation id = context.topLevelId();
            if (id == null) {
                var resId = context.resourceId();
                if (resId == null || !resId.getPath().startsWith("item/")) {
                    return model;
                }
                id = new ModelResourceLocation(resId.withPath(p -> p.substring("item/".length())), "inventory");
            }
            var map = new HashMap<ModelResourceLocation, BakedModel>();
            map.put(id, model);
            IronsSpellbooksFabric.MOD_BUS.post(new ModelEvent.ModifyBakingResult(map));
            return map.getOrDefault(id, model);
        });
    }
}
