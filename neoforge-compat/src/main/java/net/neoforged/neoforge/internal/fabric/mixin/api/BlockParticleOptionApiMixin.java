package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.core.particles.BlockParticleOption} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.core.particles.BlockParticleOption.class)
public abstract class BlockParticleOptionApiMixin implements net.neoforged.neoforge.common.extensions.IBlockParticleOptionExtension {
}
