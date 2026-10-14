package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge (client): {@code net.minecraft.client.gui.screens.inventory.AbstractContainerScreen} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.client.gui.screens.inventory.AbstractContainerScreen.class)
public abstract class AbstractContainerScreenApiMixin implements net.neoforged.neoforge.client.extensions.common.IScreenExtension {
}
