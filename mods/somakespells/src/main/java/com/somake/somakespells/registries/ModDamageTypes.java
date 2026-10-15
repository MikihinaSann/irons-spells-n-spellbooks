package com.somake.somakespells.registries;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
   public static final ResourceKey<DamageType> AQUA_MAGIC = register("aqua_magic");
   public static final ResourceKey<DamageType> FIRE_DAMAGE = register("fire_damage");
   public static final ResourceKey<DamageType> ICE_DAMAGE = register("ice_damage");
   public static final ResourceKey<DamageType> SYMMETRY_MAGIC = ResourceKey.create(Registries.DAMAGE_TYPE, MagicFromTheEastCompat.symmetrySchoolOrIce());

   public static ResourceKey<DamageType> register(String name) {
      return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("somakespells", name));
   }
}
