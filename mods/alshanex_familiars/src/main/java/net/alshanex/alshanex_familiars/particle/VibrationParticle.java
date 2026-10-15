package net.alshanex.alshanex_familiars.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class VibrationParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   public VibrationParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.quadSize *= 2.0F;
      this.scale(2.0F);
      this.lifetime = 15 + (int)(Math.random() * 10.0);
      this.sprites = spriteSet;
      this.gravity = 0.0F;
      this.setSpriteFromAge(spriteSet);
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
      float lifeProgress = (float)this.age / this.lifetime;
      this.quadSize = 1.0F + lifeProgress * 5.0F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   protected int getLightColor(float pPartialTick) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
         return new VibrationParticle(level, x, y, z, this.sprites, dx, dy, dz);
      }
   }
}
