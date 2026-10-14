package com.gametechbc.traveloptics.init;

import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TOParticles {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, "traveloptics");
   public static final Supplier<SimpleParticleType> CURSED_BLAST = PARTICLE_TYPES.register("cursed_blast", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> ABYSS_SPIKE_PARTICLE = PARTICLE_TYPES.register("abyss_spike", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> WATER_PARTICLE = PARTICLE_TYPES.register("water", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> WATER_BUBBLE_PARTICLE = PARTICLE_TYPES.register("water_bubble", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> WATER_DROP_PARTICLE = PARTICLE_TYPES.register("water_drop", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> ACID_DROP_PARTICLE = PARTICLE_TYPES.register("acid_drop", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> BLUE_CORAL_BUBBLE_PARTICLE = PARTICLE_TYPES.register(
      "blue_coral_bubble", () -> new SimpleParticleType(false)
   );
   public static final Supplier<SimpleParticleType> PINK_CORAL_BUBBLE_PARTICLE = PARTICLE_TYPES.register(
      "pink_coral_bubble", () -> new SimpleParticleType(false)
   );
   public static final Supplier<SimpleParticleType> RED_CORAL_BUBBLE_PARTICLE = PARTICLE_TYPES.register("red_coral_bubble", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> YELLOW_CORAL_BUBBLE_PARTICLE = PARTICLE_TYPES.register(
      "yellow_coral_bubble", () -> new SimpleParticleType(false)
   );

   public TOParticles() {
      super();
   }

   public static void register(IEventBus eventBus) {
      PARTICLE_TYPES.register(eventBus);
   }
}
