package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge (client): {@code net.minecraft.client.renderer.blockentity.BlockEntityRenderer} extends the extension listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.client.renderer.blockentity.BlockEntityRenderer.class)
public interface BlockEntityRendererApiMixin<T extends net.minecraft.world.level.block.entity.BlockEntity> extends net.neoforged.neoforge.client.extensions.common.IBlockEntityRendererExtension<T> {
}
