package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge (client): {@code net.minecraft.client.renderer.block.BlockRenderDispatcher} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.client.renderer.block.BlockRenderDispatcher.class)
public abstract class BlockRenderDispatcherApiMixin implements net.neoforged.neoforge.client.extensions.IBlockRenderDispatcherExtension {
}
