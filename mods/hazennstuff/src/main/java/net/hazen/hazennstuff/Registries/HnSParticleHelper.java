package net.hazen.hazennstuff.Registries;

import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.FogParticleOptions;
import io.redspace.ironsspellbooks.particle.SparkParticleOptions;
import net.hazen.hazennstuff.Particle.LeafParticle.LeafParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import org.joml.Vector3f;

public class HnSParticleHelper {
   public static final ParticleOptions LEAF_PARTICLE = new LeafParticleOptions(new Vector3f(0.23F, 0.78F, 0.08F));
   public static final ParticleOptions ICE_CLOUD = new FogParticleOptions(new Vector3f(0.9F, 1.0F, 0.91F), 1.0F);
   public static final ParticleOptions MUSHROOM_FOG = new FogParticleOptions(new Vector3f(0.036F, 0.27F, 0.72F), 0.7F);
   public static final ParticleOptions ICHOR_TRAIL = new SparkParticleOptions(new Vector3f(0.95F, 0.7F, 0.23F));
   public static final ParticleOptions NATURE_IMPACT = new BlastwaveParticleOptions(new Vector3f(0.95F, 0.7F, 0.23F), 1.0F);
   public static final ParticleOptions BLADE_PARTICLE = (ParticleOptions)HnSParticleRegistry.BLADE_PARTICLES.get();
   public static final ParticleOptions MUSHROOM_PARTICLE = (ParticleOptions)HnSParticleRegistry.MUSHROOM_PARTICLE.get();
   public static final ParticleOptions STAR_PARTICLE = (ParticleOptions)HnSParticleRegistry.STAR_PARTICLE.get();
   public static final ParticleOptions STAR_IMPACT_PARTICLE = (ParticleOptions)HnSParticleRegistry.STAR_IMPACT_PARTICLE.get();
   public static final ParticleOptions COSMIC_EMBER_PARTICLE = (ParticleOptions)HnSParticleRegistry.COSMIC_EMBER_PARTICLE.get();
   public static final ParticleOptions NIGHTS_EDGE_PARTICLE = (ParticleOptions)HnSParticleRegistry.NIGHTS_EDGE_PARTICLE.get();
   public static final ParticleOptions ROD_OF_DISCORD_PARTICLE = (ParticleOptions)HnSParticleRegistry.ROD_OF_DISCORD_PARTICLE.get();
}
