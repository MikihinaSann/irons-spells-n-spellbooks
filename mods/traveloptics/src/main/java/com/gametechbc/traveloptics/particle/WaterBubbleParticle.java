package com.gametechbc.traveloptics.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class WaterBubbleParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   public WaterBubbleParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.quadSize *= 1.0F;
      this.scale(3.0F);
      this.lifetime = 10 + (int)(Math.random() * 5.0);
      this.sprites = spriteSet;
      this.gravity = 0.35F;
      this.setSpriteFromAge(spriteSet);
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         super();
         this.sprites = spriteSet;
      }

      public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
         return new WaterBubbleParticle(level, x, y, z, this.sprites, dx, dy, dz);
      }
   }
}
