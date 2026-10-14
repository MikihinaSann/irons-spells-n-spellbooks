package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge (client): {@code net.minecraft.client.KeyMapping} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.client.KeyMapping.class)
public abstract class KeyMappingApiMixin implements net.neoforged.neoforge.client.extensions.IKeyMappingExtension {
}
