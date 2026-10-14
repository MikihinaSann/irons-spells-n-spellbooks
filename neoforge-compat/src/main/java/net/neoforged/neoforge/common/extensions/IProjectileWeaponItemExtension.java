package net.neoforged.neoforge.common.extensions;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

/**
 * NeoForge's {@code ProjectileWeaponItem} extension — injected onto
 * {@code net.minecraft.world.item.ProjectileWeaponItem}. Adds the three-arg
 * {@code customArrow} hook NeoForge patches in.
 */
public interface IProjectileWeaponItemExtension {
    private ProjectileWeaponItem self() {
        return (ProjectileWeaponItem) this;
    }

    default AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        return arrow;
    }
}
