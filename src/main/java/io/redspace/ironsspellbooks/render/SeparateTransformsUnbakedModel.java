package io.redspace.ironsspellbooks.render;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Unbaked counterpart of {@link SeparateTransformsModel}. Bakes the base model
 * plus each inline perspective model through the shared bakery.
 */
public class SeparateTransformsUnbakedModel implements UnbakedModel {
    private final UnbakedModel base;
    private final Map<ItemDisplayContext, UnbakedModel> perspectives;

    public SeparateTransformsUnbakedModel(UnbakedModel base, Map<ItemDisplayContext, UnbakedModel> perspectives) {
        this.base = base;
        this.perspectives = perspectives;
    }

    /** Parses NeoForge's separate_transforms json. Returns null on malformed input. */
    @Nullable
    public static SeparateTransformsUnbakedModel parse(com.google.gson.JsonObject json) {
        var baseElem = json.get("base");
        if (baseElem == null || !baseElem.isJsonObject()) {
            return null;
        }
        UnbakedModel base = BlockModel.fromString(baseElem.toString());
        Map<ItemDisplayContext, UnbakedModel> perspectives = new LinkedHashMap<>();
        var persp = json.getAsJsonObject("perspectives");
        if (persp != null) {
            for (var entry : persp.entrySet()) {
                ItemDisplayContext ctx = contextFor(entry.getKey());
                if (ctx != null && entry.getValue().isJsonObject()) {
                    perspectives.put(ctx, BlockModel.fromString(entry.getValue().toString()));
                }
            }
        }
        return new SeparateTransformsUnbakedModel(base, perspectives);
    }

    @Nullable
    private static ItemDisplayContext contextFor(String name) {
        return switch (name) {
            case "none" -> ItemDisplayContext.NONE;
            case "thirdperson_lefthand" -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            case "thirdperson_righthand" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            case "firstperson_lefthand" -> ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            case "firstperson_righthand" -> ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
            case "head" -> ItemDisplayContext.HEAD;
            case "gui" -> ItemDisplayContext.GUI;
            case "ground" -> ItemDisplayContext.GROUND;
            case "fixed" -> ItemDisplayContext.FIXED;
            default -> null;
        };
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        var deps = new java.util.ArrayList<>(base.getDependencies());
        perspectives.values().forEach(m -> deps.addAll(m.getDependencies()));
        return deps;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> resolver) {
        base.resolveParents(resolver);
        perspectives.values().forEach(m -> m.resolveParents(resolver));
    }

    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();

    /** Mirrors ModelBakery's special case: item/generated models get their quads from the texture layers. */
    @Nullable
    private static BakedModel bakePart(UnbakedModel model, ModelBaker baker, Function<Material, TextureAtlasSprite> textureGetter, ModelState modelState) {
        if (model instanceof BlockModel blockModel && blockModel.getRootModel() == ModelBakery.GENERATION_MARKER) {
            return ITEM_MODEL_GENERATOR.generateBlockModel(textureGetter, blockModel)
                    .bake(baker, blockModel, textureGetter, modelState, false);
        }
        return model.bake(baker, textureGetter, modelState);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> textureGetter, ModelState modelState) {
        BakedModel bakedBase = bakePart(base, baker, textureGetter, modelState);
        Map<ItemDisplayContext, BakedModel> baked = new LinkedHashMap<>();
        perspectives.forEach((ctx, model) -> {
            BakedModel b = bakePart(model, baker, textureGetter, modelState);
            if (b != null) {
                baked.put(ctx, b);
            }
        });
        return new SeparateTransformsModel(bakedBase, baked);
    }
}
