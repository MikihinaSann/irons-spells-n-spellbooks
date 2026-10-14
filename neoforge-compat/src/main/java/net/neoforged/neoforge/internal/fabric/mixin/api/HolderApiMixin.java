package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.core.Holder} extends the extension listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.core.Holder.class)
public interface HolderApiMixin<T> extends net.neoforged.neoforge.common.extensions.IHolderExtension<T> {
}
