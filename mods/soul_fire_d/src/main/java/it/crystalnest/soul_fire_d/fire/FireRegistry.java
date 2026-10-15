package it.crystalnest.soul_fire_d.fire;

import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.FireRegistrar;
import it.crystalnest.prometheus.api.Fire.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class FireRegistry {
   @Internal
   public static final ResourceLocation SOUL_FIRE_TYPE = FireManager.SOUL_FIRE_TYPE;

   private FireRegistry() {
   }

   public static void register() {
   }

   static {
      FireManager.registerFire(
         FireManager.fireBuilder(SOUL_FIRE_TYPE)
            .setDefaultComponents()
            .setComponent(Component.FLAME_PARTICLE, BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.SOUL_FIRE_FLAME))
            .setLight(10)
            .setDamage(2.0F)
            .build()
      );
      FireRegistrar.registerFireCharge(SOUL_FIRE_TYPE);
   }
}
