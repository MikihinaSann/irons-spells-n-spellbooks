package com.chadate.spellelemental.register;

import com.chadate.spellelemental.SpellElemental;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "spellelemental");
   public static final Supplier<SimpleParticleType> PHYSICAL_RESISTANCE_DOWN = PARTICLE_TYPES.register(
      "physical_resistance_down", () -> new SimpleParticleType(false)
   );

   public static void register(IEventBus modEventBus) {
      PARTICLE_TYPES.register(modEventBus);
      SpellElemental.LOGGER.info("已注册粒子类型");
   }
}
