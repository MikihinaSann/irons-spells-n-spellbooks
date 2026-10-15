package net.alshanex.tunes_n_tomes.registry;

import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TParticleRegistry {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, "tunes_n_tomes");
   public static final Supplier<SimpleParticleType> SLEEP_PARTICLE = PARTICLE_TYPES.register("sleeping", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> MUSICAL_NOTE_PARTICLE = PARTICLE_TYPES.register("musical_note", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> BLACK_NOTE_PARTICLE = PARTICLE_TYPES.register("black_note", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> VIBRATION_PARTICLE = PARTICLE_TYPES.register("vibration", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> DESTRUCTION_SYMPHONY_PARTICLE = PARTICLE_TYPES.register(
      "destruction_symphony", () -> new SimpleParticleType(false)
   );
   public static final Supplier<SimpleParticleType> HARP_EXPLOSION_PARTICLE = PARTICLE_TYPES.register("harp_explosion", () -> new SimpleParticleType(false));

   public static void register(IEventBus eventBus) {
      PARTICLE_TYPES.register(eventBus);
   }
}
