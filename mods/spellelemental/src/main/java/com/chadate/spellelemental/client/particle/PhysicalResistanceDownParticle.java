package com.chadate.spellelemental.client.particle;

import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class PhysicalResistanceDownParticle extends TextureSheetParticle {
   protected PhysicalResistanceDownParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      super(level, x, y, z, xSpeed, ySpeed, zSpeed);
      this.lifetime = 40 + this.random.nextInt(20);
      this.gravity = 0.075F;
      this.friction = 0.98F;
      this.xd = (this.random.nextDouble() - 0.5) * 0.01;
      this.yd = -0.01 - this.random.nextDouble() * 0.02;
      this.zd = (this.random.nextDouble() - 0.5) * 0.01;
   }

   public void tick() {
      super.tick();
      if (this.age % 10 == 0) {
         this.xd = this.xd + (this.random.nextDouble() - 0.5) * 0.002;
         this.zd = this.zd + (this.random.nextDouble() - 0.5) * 0.002;
      }

      double maxHorizontalSpeed = 0.02;
      if (Math.abs(this.xd) > maxHorizontalSpeed) {
         this.xd = Math.signum(this.xd) * maxHorizontalSpeed;
      }

      if (Math.abs(this.zd) > maxHorizontalSpeed) {
         this.zd = Math.signum(this.zd) * maxHorizontalSpeed;
      }
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(
         @Nonnull SimpleParticleType type, @Nonnull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         PhysicalResistanceDownParticle particle = new PhysicalResistanceDownParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
         particle.pickSprite(this.sprites);
         return particle;
      }
   }
}
