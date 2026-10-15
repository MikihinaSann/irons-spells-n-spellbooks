package net.alshanex.familiarslib.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class FDamageTypes {
   public static final ResourceKey<DamageType> SOUND_MAGIC = register("sound_magic");

   public static ResourceKey<DamageType> register(String name) {
      return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("familiarslib", name));
   }

   public static void bootstrap(BootstrapContext<DamageType> context) {
      context.register(SOUND_MAGIC, new DamageType(SOUND_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
   }
}
