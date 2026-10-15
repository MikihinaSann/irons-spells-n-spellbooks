package net.alshanex.familiarslib.registry;

import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FParticleRegistry {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, "familiarslib");
   public static final Supplier<SimpleParticleType> SLEEP_PARTICLE = PARTICLE_TYPES.register("sleeping", () -> new SimpleParticleType(false));

   public static void register(IEventBus eventBus) {
      PARTICLE_TYPES.register(eventBus);
   }
}
