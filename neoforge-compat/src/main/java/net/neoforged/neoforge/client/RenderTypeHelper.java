package net.neoforged.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * Helpers translating chunk/block render types to their entity/item
 * equivalents. Ported from NeoForge 21.1.
 */
public final class RenderTypeHelper {
    /**
     * {@return the item/entity equivalent of the given chunk render type}
     */
    public static RenderType getEntityRenderType(RenderType chunkRenderType, boolean cull) {
        if (chunkRenderType != RenderType.translucent())
            return Sheets.cutoutBlockSheet();
        return cull || !Minecraft.useShaderTransparency() ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
    }

    /**
     * {@return the moving-block equivalent of the given render type}
     */
    public static RenderType getMovingBlockRenderType(RenderType renderType) {
        if (renderType == RenderType.translucent())
            return RenderType.translucentMovingBlock();
        return renderType;
    }

    /**
     * {@return a fallback render type for item model rendering}
     */
    public static RenderType getFallbackItemRenderType(ItemStack stack, BakedModel model, boolean cull) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            var renderTypes = model.getRenderTypes(blockItem.getBlock().defaultBlockState(), RandomSource.create(42), ModelData.EMPTY);
            if (renderTypes.contains(RenderType.translucent()))
                return getEntityRenderType(RenderType.translucent(), cull);
            return Sheets.cutoutBlockSheet();
        }
        return cull ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
    }

    private RenderTypeHelper() {}
}
