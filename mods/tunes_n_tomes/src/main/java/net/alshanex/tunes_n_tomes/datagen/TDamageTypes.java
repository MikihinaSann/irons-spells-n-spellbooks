package net.alshanex.tunes_n_tomes.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class TDamageTypes {
   public static final ResourceKey<DamageType> MELODY_MAGIC = register("melody_magic");

   public static ResourceKey<DamageType> register(String name) {
      return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", name));
   }

   public static void bootstrap(BootstrapContext<DamageType> context) {
      context.register(MELODY_MAGIC, new DamageType(MELODY_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
   }
}
