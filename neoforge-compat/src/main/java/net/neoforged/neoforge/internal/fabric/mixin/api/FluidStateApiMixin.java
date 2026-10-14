package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.world.level.material.FluidState} implements the extension listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.world.level.material.FluidState.class)
public interface FluidStateApiMixin extends net.neoforged.neoforge.common.extensions.IFluidStateExtension {
}
