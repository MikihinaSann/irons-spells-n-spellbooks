package net.neoforged.neoforge.internal.fabric;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

/**
 * Bridges NeoForge's {@link IClientFluidTypeExtensions} (custom still/flowing
 * textures + tint per fluid type) into Fabric's fluid rendering pipeline.
 * Without this, fabric-rendering-fluids falls back to water sprites and every
 * custom fluid looks like plain water.
 */
public final class NeoForgeFluidRenderHandlers {

    public static void registerAll() {
        int[] count = {0};
        BuiltInRegistries.FLUID.forEach(fluid -> {
            IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid);
            if (ext == null || (ext.getStillTexture() == null && ext.getFlowingTexture() == null)) {
                return; // default extension — vanilla water lookup is fine
            }
            FluidRenderHandlerRegistry.INSTANCE.register(fluid, new FluidRenderHandler() {
                private final TextureAtlasSprite[] sprites = new TextureAtlasSprite[2];

                @Override
                public TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter view, BlockPos pos, FluidState state) {
                    ResourceLocation still = ext.getStillTexture(state, view, pos);
                    ResourceLocation flowing = ext.getFlowingTexture(state, view, pos);
                    TextureAtlas atlas = Minecraft.getInstance().getModelManager()
                            .getAtlas(TextureAtlas.LOCATION_BLOCKS);
                    return new TextureAtlasSprite[]{
                            still != null ? atlas.getSprite(still) : sprites[0],
                            flowing != null ? atlas.getSprite(flowing) : sprites[1],
                    };
                }

                @Override
                public int getFluidColor(BlockAndTintGetter view, BlockPos pos, FluidState state) {
                    return ext.getTintColor(state, view, pos);
                }

                @Override
                public void reloadTextures(TextureAtlas atlas) {
                    ResourceLocation still = ext.getStillTexture();
                    ResourceLocation flowing = ext.getFlowingTexture();
                    if (still != null) sprites[0] = atlas.getSprite(still);
                    if (flowing != null) sprites[1] = atlas.getSprite(flowing);
                }
            });
            count[0]++;
        });
        if (count[0] > 0) {
            NeoForgeCompat.LOGGER.info("Registered {} NeoForge fluid render handlers", count[0]);
        }
    }

    private NeoForgeFluidRenderHandlers() {
    }
}
