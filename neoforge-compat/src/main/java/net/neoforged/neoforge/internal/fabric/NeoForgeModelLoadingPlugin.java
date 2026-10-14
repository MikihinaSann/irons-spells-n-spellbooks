package net.neoforged.neoforge.internal.fabric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.neoforged.neoforge.internal.fabric.NeoForgeCompat;
import net.neoforged.neoforge.internal.fabric.model.SeparateTransformsUnbakedModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
 * <p>This FAPI version does not read the {@code fabric:model_loading} entrypoint -
 * registration is programmatic via {@link PreparableModelLoadingPlugin#register}.
 */
public class NeoForgeModelLoadingPlugin implements PreparableModelLoadingPlugin<NeoForgeModelLoadingPlugin.ScanResult> {
    static final Logger LOGGER = LoggerFactory.getLogger("neoforge_compat");

    /** Result of the reload-prepare resource scan. */
    public record ScanResult(Map<ResourceLocation, JsonObject> separateTransforms,
                             Map<ResourceLocation, String> blockRenderTypes) {}

    /**
     * Finds item model jsons using NeoForge's {@code separate_transforms} loader
     * and block model jsons carrying a {@code render_type} hint, resolving the
     * hint to the owning block via its blockstate file. Runs during the
     * resource-reload prepare phase where a real {@link ResourceManager} is
     * available.
     */
    public static CompletableFuture<ScanResult> scanResources(ResourceManager resourceManager, Executor executor) {
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

            // NeoForge lets a block model carry "render_type": the chunk layer
            // the block renders in. Vanilla/Fabric ignores the key, leaving
            // cutout blocks solid — transparent texels show as raw RGB (white
            // faces on MCreator exports). Map each model's render_type through
            // its blockstate to a BlockRenderLayerMap entry.
            Map<ResourceLocation, String> blockRenderTypes = new HashMap<>();
            for (var entry : resourceManager.listResources("blockstates", path -> path.getPath().endsWith(".json")).entrySet()) {
                ResourceLocation key = entry.getKey();
                if ("minecraft".equals(key.getNamespace())) {
                    continue;
                }
                ResourceLocation blockId = ResourceLocation.fromNamespaceAndPath(
                        key.getNamespace(), key.getPath().substring("blockstates/".length(), key.getPath().length() - ".json".length()));
                String renderType = null;
                try (var reader = entry.getValue().openAsReader()) {
                    var bs = JsonParser.parseReader(reader).getAsJsonObject();
                    Set<ResourceLocation> modelIds = new java.util.LinkedHashSet<>();
                    collectModelRefs(bs, modelIds);
                    for (ResourceLocation modelId : modelIds) {
                        renderType = resolveRenderType(resourceManager, modelId, 0);
                        if (renderType != null) {
                            break;
                        }
                    }
                } catch (Exception ignored) {
                }
                if (renderType != null) {
                    blockRenderTypes.put(blockId, renderType);
                }
            }
            return new ScanResult(found, blockRenderTypes);
        }, executor);
    }

    /** Collects every {@code "model"} reference inside a blockstate json. */
    private static void collectModelRefs(com.google.gson.JsonElement el, Set<ResourceLocation> out) {
        if (el == null) {
            return;
        }
        if (el.isJsonObject()) {
            var obj = el.getAsJsonObject();
            var model = obj.get("model");
            if (model != null && model.isJsonPrimitive()) {
                try {
                    out.add(ResourceLocation.parse(model.getAsString()));
                } catch (Exception ignored) {
                }
            }
            obj.entrySet().forEach(e -> collectModelRefs(e.getValue(), out));
        } else if (el.isJsonArray()) {
            el.getAsJsonArray().forEach(child -> collectModelRefs(child, out));
        }
    }

    /** Reads {@code render_type} off a model json, walking up parents. */
    private static String resolveRenderType(ResourceManager resourceManager, ResourceLocation modelId, int depth) {
        if (depth > 8) {
            return null;
        }
        ResourceLocation file = ResourceLocation.fromNamespaceAndPath(
                modelId.getNamespace(), "models/" + modelId.getPath() + ".json");
        var resource = resourceManager.getResource(file);
        if (resource.isEmpty()) {
            return null;
        }
        try (var reader = resource.get().openAsReader()) {
            var json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("render_type")) {
                String rt = json.get("render_type").getAsString();
                return rt != null && !rt.isBlank() ? rt : null;
            }
            var parent = json.get("parent");
            if (parent != null && parent.isJsonPrimitive()) {
                return resolveRenderType(resourceManager, ResourceLocation.parse(parent.getAsString()), depth + 1);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static net.minecraft.client.renderer.RenderType parseRenderType(String name) {
        String path = name.contains(":") ? name.substring(name.indexOf(':') + 1) : name;
        return switch (path) {
            case "cutout" -> net.minecraft.client.renderer.RenderType.cutout();
            case "cutout_mipped" -> net.minecraft.client.renderer.RenderType.cutoutMipped();
            case "translucent" -> net.minecraft.client.renderer.RenderType.translucent();
            case "tripwire" -> net.minecraft.client.renderer.RenderType.tripwire();
            default -> null;
        };
    }

    @Override
    public void onInitializeModelLoader(ScanResult scan, ModelLoadingPlugin.Context pluginContext) {
        var separateTransforms = scan.separateTransforms();
        LOGGER.info("ISS model plugin init: {} separate_transforms models, {} render_type blocks",
                separateTransforms.size(), scan.blockRenderTypes().size());

        scan.blockRenderTypes().forEach((blockId, rtName) -> {
            net.minecraft.client.renderer.RenderType renderType = parseRenderType(rtName);
            if (renderType == null) {
                return;
            }
            var block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(blockId);
            if (block != null) {
                net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
            }
        });

        pluginContext.modifyModelOnLoad().register((model, context) -> {
            var json = context.resourceId() != null ? separateTransforms.get(context.resourceId()) : null;
            if (json == null) {
                return model;
            }
            var parsed = SeparateTransformsUnbakedModel.parse(json);
            if (parsed == null) {
                LOGGER.warn("Failed to parse separate_transforms model {}", context.resourceId());
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
            NeoForgeCompat.MOD_BUS.post(new ModelEvent.ModifyBakingResult(map));
            return map.getOrDefault(id, model);
        });
    }
}
