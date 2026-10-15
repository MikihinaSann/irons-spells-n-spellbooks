package com.chadate.spellelemental.client.particle;

import com.chadate.spellelemental.register.ModParticles;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(
   modid = "spellelemental",
   value = {Dist.CLIENT}
)
public class ModParticleProviders {
   @SubscribeEvent
   public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)ModParticles.PHYSICAL_RESISTANCE_DOWN.get(), PhysicalResistanceDownParticle.Provider::new);
   }
}
