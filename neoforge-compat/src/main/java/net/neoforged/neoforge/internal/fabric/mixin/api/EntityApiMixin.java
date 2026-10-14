package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.world.entity.Entity} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class EntityApiMixin implements net.neoforged.neoforge.attachment.IAttachmentHolder, net.neoforged.neoforge.entity.IEntityExtension {
}
