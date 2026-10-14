package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.world.item.TooltipFlag} extends the extension listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.world.item.TooltipFlag.class)
public interface TooltipFlagApiMixin extends net.neoforged.neoforge.common.extensions.ITooltipFlagExtension {
}
