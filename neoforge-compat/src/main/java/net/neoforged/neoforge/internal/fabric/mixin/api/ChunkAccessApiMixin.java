package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.world.level.chunk.ChunkAccess} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.world.level.chunk.ChunkAccess.class)
public abstract class ChunkAccessApiMixin implements net.neoforged.neoforge.attachment.IAttachmentHolder {
}
