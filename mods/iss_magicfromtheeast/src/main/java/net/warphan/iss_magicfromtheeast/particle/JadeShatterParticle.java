package net.warphan.iss_magicfromtheeast.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class JadeShatterParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   public JadeShatterParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.scale(this.random.nextFloat() * 1.75F + 1.0F);
      this.friction = (float)(this.friction - this.random.nextFloat() * 0.1);
      this.lifetime = 10 + (int)(Math.random() * 25.0);
      this.sprites = spriteSet;
      this.setSpriteFromAge(spriteSet);
      this.gravity = -0.01F;
   }

   public void tick() {
      super.tick();
      this.xd = this.xd + this.random.nextFloat() / 500.0F * (this.random.nextBoolean() ? 1 : -1);
      this.zd = this.zd + this.random.nextFloat() / 500.0F * (this.random.nextBoolean() ? 1 : -1);
      this.setSpriteFromAge(this.sprites);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   protected int getLightColor(float p_107249_) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
         return new JadeShatterParticle(level, x, y, z, this.sprites, dx, dy, dz);
      }
   }
}
