package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.world.level.block.state.BlockBehaviour} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.world.level.block.state.BlockBehaviour.class)
public abstract class BlockBehaviourApiMixin implements net.neoforged.neoforge.common.extensions.IBlockExtension {
}
