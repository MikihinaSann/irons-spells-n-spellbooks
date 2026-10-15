package net.warphan.iss_magicfromtheeast.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class MFTEDamageTypeTagGenerator {
   public static final TagKey<DamageType> SYMMETRY_MAGIC = create("symmetry_magic");
   public static final TagKey<DamageType> SPIRIT_MAGIC = create("spirit_magic");
   public static final TagKey<DamageType> DUNE_MAGIC = create("dune_magic");
   public static final TagKey<DamageType> SOUL_HURTING = create("is_soul");
   public static final TagKey<DamageType> BYPASS_REVERSAL_HEALING = create("bypass_reversal_healing");

   private static TagKey<DamageType> create(String name) {
      return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", name));
   }
}
