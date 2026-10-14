package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.world.entity.EntityType$Builder} implements the extension listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.world.entity.EntityType.Builder.class)
public interface EntityTypeBuilderApiMixin<T extends net.minecraft.world.entity.Entity>
        extends net.neoforged.neoforge.common.extensions.IEntityTypeBuilderExtension<T> {
}
