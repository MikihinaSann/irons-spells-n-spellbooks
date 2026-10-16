package com.userofbricks.expanded_combat.init;

import com.userofbricks.expanded_combat.ExpandedCombat;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ECEnchantments {
   public static final ResourceKey<Enchantment> KNOCKBACK_RESISTANCE = ResourceKey.create(Registries.ENCHANTMENT, ExpandedCombat.modLoc("knockback_resistance"));
   public static final ResourceKey<Enchantment> BLOCKING = ResourceKey.create(Registries.ENCHANTMENT, ExpandedCombat.modLoc("blocking"));
   public static final ResourceKey<Enchantment> AGILITY = ResourceKey.create(Registries.ENCHANTMENT, ExpandedCombat.modLoc("agility"));
   public static final ResourceKey<Enchantment> GROUND_SLAM = ResourceKey.create(Registries.ENCHANTMENT, ExpandedCombat.modLoc("ground_slam"));
}
