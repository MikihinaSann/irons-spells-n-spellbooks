package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.world.item.ArrowItem} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.world.item.ArrowItem.class)
public abstract class ArrowItemApiMixin implements net.neoforged.neoforge.common.extensions.IArrowItemExtension {
}
