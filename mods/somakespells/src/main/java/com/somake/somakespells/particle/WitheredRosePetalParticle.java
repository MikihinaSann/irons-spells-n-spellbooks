package com.somake.somakespells.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitheredRosePetalParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final float spin;

   protected WitheredRosePetalParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
      super(level, x, y, z, xd, yd, zd);
      this.sprites = sprites;
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.gravity = 0.0025F;
      this.friction = 0.98F;
      this.quadSize = 0.08F + this.random.nextFloat() * 0.06F;
      this.lifetime = 18 + this.random.nextInt(16);
      this.alpha = 0.9F;
      this.roll = this.random.nextFloat() * (float) (Math.PI * 2);
      this.spin = (this.random.nextFloat() - 0.5F) * 0.08F;
      this.setSpriteFromAge(sprites);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.oRoll = this.roll;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.roll = this.roll + this.spin;
         this.xd = this.xd + (this.random.nextDouble() - 0.5) * 0.0015;
         this.zd = this.zd + (this.random.nextDouble() - 0.5) * 0.0015;
         this.move(this.xd, this.yd, this.zd);
         this.yd = this.yd - this.gravity;
         this.xd = this.xd * this.friction;
         this.yd *= 0.98F;
         this.zd = this.zd * this.friction;
         this.alpha = 0.9F * (1.0F - (float)this.age / this.lifetime);
         this.setSpriteFromAge(this.sprites);
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public int getLightColor(float partialTick) {
      return 15728880;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
         return new WitheredRosePetalParticle(level, x, y, z, xd, yd, zd, this.sprites);
      }
   }
}
