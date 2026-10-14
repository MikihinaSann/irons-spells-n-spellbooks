package com.gametechbc.traveloptics.util;

import com.gametechbc.traveloptics.init.TOParticles;
import com.github.L_Ender.cataclysm.client.particle.Options.LightningParticleOptions;
import io.redspace.ironsspellbooks.particle.FogParticleOptions;
import io.redspace.ironsspellbooks.particle.SparkParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import org.joml.Vector3f;

public class TOParticleHelper {
   public static final ParticleOptions CURSED_BLAST = (ParticleOptions)TOParticles.CURSED_BLAST.get();
   public static final ParticleOptions ABYSS_SPIKE_PARTICLE = (ParticleOptions)TOParticles.ABYSS_SPIKE_PARTICLE.get();
   public static final ParticleOptions WATER_FOG = new FogParticleOptions(new Vector3f(0.2F, 0.592F, 0.82F), 2.0F);
   public static final ParticleOptions WATER_SPARKS = new SparkParticleOptions(new Vector3f(0.0F, 0.259F, 0.416F));
   public static final ParticleOptions WATER = (ParticleOptions)TOParticles.WATER_PARTICLE.get();
   public static final ParticleOptions WATER_BUBBLE = (ParticleOptions)TOParticles.WATER_BUBBLE_PARTICLE.get();
   public static final ParticleOptions WATER_DROP = (ParticleOptions)TOParticles.WATER_DROP_PARTICLE.get();
   public static final ParticleOptions ACID_DROP = (ParticleOptions)TOParticles.ACID_DROP_PARTICLE.get();
   public static final ParticleOptions BLUE_CORAL_BUBBLE = (ParticleOptions)TOParticles.BLUE_CORAL_BUBBLE_PARTICLE.get();
   public static final ParticleOptions PINK_CORAL_BUBBLE = (ParticleOptions)TOParticles.PINK_CORAL_BUBBLE_PARTICLE.get();
   public static final ParticleOptions RED_CORAL_BUBBLE = (ParticleOptions)TOParticles.RED_CORAL_BUBBLE_PARTICLE.get();
   public static final ParticleOptions YELLOW_CORAL_BUBBLE = (ParticleOptions)TOParticles.YELLOW_CORAL_BUBBLE_PARTICLE.get();
   public static final ParticleOptions BLUE_CORAL_SPARKS = new SparkParticleOptions(new Vector3f(0.2471F, 0.4235F, 0.898F));
   public static final ParticleOptions PINK_CORAL_SPARKS = new SparkParticleOptions(new Vector3f(0.8392F, 0.3294F, 0.6078F));
   public static final ParticleOptions RED_CORAL_SPARKS = new SparkParticleOptions(new Vector3f(0.6431F, 0.1333F, 0.1843F));
   public static final ParticleOptions YELLOW_CORAL_SPARKS = new SparkParticleOptions(new Vector3f(0.9294F, 0.9255F, 0.298F));
   public static final ParticleOptions PSYCHIC_SPARKS = new SparkParticleOptions(new Vector3f(0.1608F, 0.8745F, 0.9216F));
   public static final ParticleOptions PSYCHIC_CURRENT = new LightningParticleOptions(41, 223, 235);

   public TOParticleHelper() {
      super();
   }
}
