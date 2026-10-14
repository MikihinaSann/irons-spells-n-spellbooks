package net.neoforged.neoforge.client.extensions.common;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.NeoForgeFluidTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Client-side fluid type extensions (textures, tint).
 */
public interface IClientFluidTypeExtensions {

    default ResourceLocation getStillTexture() {
        return null;
    }

    default ResourceLocation getFlowingTexture() {
        return null;
    }

    default ResourceLocation getStillTexture(FluidStack stack) {
        return getStillTexture();
    }

    default ResourceLocation getFlowingTexture(FluidStack stack) {
        return getFlowingTexture();
    }

    default ResourceLocation getStillTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return getStillTexture();
    }

    default ResourceLocation getFlowingTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return getFlowingTexture();
    }

    default int getTintColor() {
        return 0xFFFFFFFF;
    }

    default int getTintColor(FluidStack stack) {
        return getTintColor();
    }

    default int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return getTintColor();
    }

    @Nullable
    default ResourceLocation getOverlayTexture() {
        return null;
    }

    @Nullable
    default ResourceLocation getOverlayTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return getOverlayTexture();
    }

    @Nullable
    default ResourceLocation getRenderOverlayTexture(net.minecraft.client.Minecraft mc) {
        return null;
    }

    /** Lookup helper — resolves the extension for a fluid type. */
    static IClientFluidTypeExtensions of(net.neoforged.neoforge.fluids.FluidType type) {
        return net.neoforged.neoforge.client.extensions.common.ClientExtensionsRegistry.getFluidTypeExtension(type);
    }

    /** Lookup helper — resolves the extension for a {@link Fluid}. */
    static IClientFluidTypeExtensions of(Fluid fluid) {
        return of(NeoForgeFluidTypeRegistry.getTypeFor(fluid));
    }

    /**
     * {@return the client extensions for the fluid state's type}
     */
    static IClientFluidTypeExtensions of(net.minecraft.world.level.material.FluidState state) {
        return of(state.getType());
    }

    /** Lookup helper — resolves the extension for a {@link FluidStack}. */
    static IClientFluidTypeExtensions of(FluidStack stack) {
        return of(stack.getFluid());
    }
}
