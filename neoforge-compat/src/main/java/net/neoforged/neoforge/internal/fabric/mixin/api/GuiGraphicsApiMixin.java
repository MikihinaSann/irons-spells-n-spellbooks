package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.client.gui.GuiGraphics} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.client.gui.GuiGraphics.class)
public abstract class GuiGraphicsApiMixin implements net.neoforged.neoforge.client.extensions.IGuiGraphicsExtension {
}
