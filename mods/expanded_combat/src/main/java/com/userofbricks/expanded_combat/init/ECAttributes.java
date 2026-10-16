package com.userofbricks.expanded_combat.init;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.entity.attributes.RangedDamageAttribute;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ECAttributes {
   public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, "expanded_combat");
   public static final DeferredHolder<Attribute, RangedDamageAttribute> GAUNTLET_DMG_WITHOUT_WEAPON = ATTRIBUTES.register(
      "dmg_no_weapon", () -> new RangedDamageAttribute(ExpandedCombat.modLoc("dmg_no_weapon"), 0.0, 2048.0)
   );
   public static final DeferredHolder<Attribute, RangedDamageAttribute> HEAT_DMG = ATTRIBUTES.register(
      "heat_dmg", () -> new RangedDamageAttribute(ExpandedCombat.modLoc("heat_dmg"), 0.0, 2048.0)
   );
   public static final DeferredHolder<Attribute, RangedDamageAttribute> COLD_DMG = ATTRIBUTES.register(
      "cold_dmg", () -> new RangedDamageAttribute(ExpandedCombat.modLoc("cold_dmg"), 0.0, 2048.0)
   );
   public static final DeferredHolder<Attribute, RangedDamageAttribute> VOID_DMG = ATTRIBUTES.register(
      "void_dmg", () -> new RangedDamageAttribute(ExpandedCombat.modLoc("void_dmg"), 0.0, 2048.0)
   );
   public static final DeferredHolder<Attribute, RangedDamageAttribute> SOUL_DMG = ATTRIBUTES.register(
      "soul_dmg", () -> new RangedDamageAttribute(ExpandedCombat.modLoc("soul_dmg"), 0.0, 2048.0)
   );
}
