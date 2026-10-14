package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener.class)
public abstract class SimpleJsonResourceReloadListenerApiMixin implements net.neoforged.neoforge.common.conditions.IConditionalOpsProvider {
}
