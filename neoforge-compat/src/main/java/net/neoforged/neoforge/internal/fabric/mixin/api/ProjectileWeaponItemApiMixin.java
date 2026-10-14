package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.world.item.ProjectileWeaponItem} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.world.item.ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemApiMixin implements net.neoforged.neoforge.common.extensions.IProjectileWeaponItemExtension {
}
