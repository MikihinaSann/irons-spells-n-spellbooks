package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge (client): {@code net.minecraft.client.resources.model.BakedModel} extends the extension listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.client.resources.model.BakedModel.class)
public interface BakedModelApiMixin extends net.neoforged.neoforge.client.extensions.IBakedModelExtension {
}
