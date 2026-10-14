package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge (client): {@code net.minecraft.client.renderer.DimensionSpecialEffects} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.client.renderer.DimensionSpecialEffects.class)
public abstract class DimensionSpecialEffectsApiMixin implements net.neoforged.neoforge.client.extensions.IDimensionSpecialEffectsExtension {
}
