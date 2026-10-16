package com.userofbricks.expanded_combat.api.weapon_type;

import com.userofbricks.expanded_combat.config.WeaponTypeConfig;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record WeaponType(@NotNull String name, @NotNull ResourceLocation id, boolean potionDippable, boolean isBlockWeapon, WeaponTypeConfig config) {
}
