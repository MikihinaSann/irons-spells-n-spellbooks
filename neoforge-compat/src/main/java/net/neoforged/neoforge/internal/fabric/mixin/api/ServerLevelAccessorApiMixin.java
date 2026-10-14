package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.world.level.ServerLevelAccessor} extends the extension listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.world.level.ServerLevelAccessor.class)
public interface ServerLevelAccessorApiMixin extends net.neoforged.neoforge.common.extensions.ILevelAccessorExtension {
}
