package com.userofbricks.expanded_combat.init;

import net.minecraft.client.particle.SuspendedTownParticle.Provider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   modid = "expanded_combat",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ECParticles {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_OPTIONS = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "expanded_combat");
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PURIFIED_GAS = PARTICLE_OPTIONS.register(
      "pure_gas", () -> new SimpleParticleType(false)
   );

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void registerToParticleEngine(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)PURIFIED_GAS.get(), Provider::new);
   }
}
