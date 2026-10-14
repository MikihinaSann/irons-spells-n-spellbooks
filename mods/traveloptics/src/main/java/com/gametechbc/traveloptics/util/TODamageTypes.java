package com.gametechbc.traveloptics.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class TODamageTypes {
   public static final ResourceKey<DamageType> NULLFLARE_BLAST = ResourceKey.create(
      Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "nullflare_blast")
   );
   public static final ResourceKey<DamageType> NULLFLARE_FIRE = ResourceKey.create(
      Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "nullflare_fire")
   );
   public static final ResourceKey<DamageType> NULLFLARE_ICE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "nullflare_ice"));
   public static final ResourceKey<DamageType> VOIDSTRIKE_REAPER_BONUS_DAMAGE = ResourceKey.create(
      Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "voidstrike_reaper_bonus_damage")
   );
   public static final ResourceKey<DamageType> REVERSAL = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "reversal"));
   public static final ResourceKey<DamageType> VOID_UPPERCUT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "void_uppercut"));
   public static final ResourceKey<DamageType> AERIAL_COLLAPSE = ResourceKey.create(
      Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "aerial_collapse")
   );
   public static final ResourceKey<DamageType> EXTENDED_WATER_BOLT = ResourceKey.create(
      Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "extended_water_bolt")
   );
   public static final ResourceKey<DamageType> BLACKOUT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "blackout"));
   public static final ResourceKey<DamageType> ASTEROID_IMPACT_CRATER = ResourceKey.create(
      Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "asteroid_impact_crater")
   );
   public static final ResourceKey<DamageType> AQUA_MAGIC = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "aqua_magic"));
   public static final ResourceKey<DamageType> LINGERING_STRAIN = ResourceKey.create(
      Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "lingering_strain")
   );
   public static final ResourceKey<DamageType> PRIMORDIAL_CREST = ResourceKey.create(
      Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "primordial_crest")
   );
   public static final ResourceKey<DamageType> TECTONIC_CREST = ResourceKey.create(
      Registries.DAMAGE_TYPE, new ResourceLocation("traveloptics", "tectonic_crest")
   );

   public TODamageTypes() {
      super();
   }
}
