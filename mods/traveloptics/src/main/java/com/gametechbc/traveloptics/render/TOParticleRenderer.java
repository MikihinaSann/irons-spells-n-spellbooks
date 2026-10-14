package com.gametechbc.traveloptics.render;

import com.gametechbc.traveloptics.init.TOParticles;
import com.gametechbc.traveloptics.particle.AbyssSpikeParticle;
import com.gametechbc.traveloptics.particle.AcidDropParticle;
import com.gametechbc.traveloptics.particle.CursedBlastParticle;
import com.gametechbc.traveloptics.particle.WaterBubbleParticle;
import com.gametechbc.traveloptics.particle.WaterDropParticle;
import com.gametechbc.traveloptics.particle.WaterParticle;
import com.gametechbc.traveloptics.particle.coral_bubble.BlueCoralBubbleParticle;
import com.gametechbc.traveloptics.particle.coral_bubble.PinkCoralBubbleParticle;
import com.gametechbc.traveloptics.particle.coral_bubble.RedCoralBubbleParticle;
import com.gametechbc.traveloptics.particle.coral_bubble.YellowCoralBubbleParticle;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(
   modid = "traveloptics",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class TOParticleRenderer {
   public TOParticleRenderer() {
      super();
   }

   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)TOParticles.CURSED_BLAST.get(), CursedBlastParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TOParticles.ABYSS_SPIKE_PARTICLE.get(), AbyssSpikeParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TOParticles.WATER_PARTICLE.get(), WaterParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TOParticles.WATER_BUBBLE_PARTICLE.get(), WaterBubbleParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TOParticles.WATER_DROP_PARTICLE.get(), WaterDropParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TOParticles.ACID_DROP_PARTICLE.get(), AcidDropParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TOParticles.BLUE_CORAL_BUBBLE_PARTICLE.get(), BlueCoralBubbleParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TOParticles.YELLOW_CORAL_BUBBLE_PARTICLE.get(), YellowCoralBubbleParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TOParticles.RED_CORAL_BUBBLE_PARTICLE.get(), RedCoralBubbleParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TOParticles.PINK_CORAL_BUBBLE_PARTICLE.get(), PinkCoralBubbleParticle.Provider::new);
   }
}
