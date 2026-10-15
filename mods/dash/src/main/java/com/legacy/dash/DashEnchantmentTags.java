package com.legacy.dash;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public interface DashEnchantmentTags {
   TagKey<Enchantment> DASHING_INCOMPATIBLE = tag("dashing_incompatible");

   static void init() {
   }

   private static TagKey<Enchantment> tag(String key) {
      return TagKey.create(Registries.ENCHANTMENT, DashMod.locate(key));
   }
}
