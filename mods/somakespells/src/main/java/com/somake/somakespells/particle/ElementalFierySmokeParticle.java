package com.somake.somakespells.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElementalFierySmokeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final boolean mirrored;

   public ElementalFierySmokeParticle(
      ClientLevel level,
      double xCoord,
      double yCoord,
      double zCoord,
      SpriteSet spriteSet,
      double xd,
      double yd,
      double zd,
      float r,
      float g,
      float b,
      float scaleMultiplier
   ) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.scale((this.random.nextFloat() * 2.5F + 1.5F) * scaleMultiplier);
      this.lifetime = 10 + this.random.nextInt(30);
      this.sprites = spriteSet;
      this.setSpriteFromAge(spriteSet);
      this.gravity = -0.0025F;
      this.mirrored = this.random.nextBoolean();
      this.applyCompensatedColor(r, g, b);
   }

   private void applyCompensatedColor(float r, float g, float b) {
      if (b > g * 1.25F && b > r * 1.8F) {
         float compensatedRed = r * 0.18F;
         float compensatedGreen = g * 1.45F + 0.08F;
         float compensatedBlue = b * 2.35F;
         this.setColor(compensatedRed, compensatedGreen, compensatedBlue);
      } else if (g > r * 1.2F && g >= b) {
         float compensatedRed = r * 0.9F;
         float compensatedGreen = g * 1.08F;
         float compensatedBlue = b * 0.95F;
         this.setColor(compensatedRed, compensatedGreen, compensatedBlue);
      } else {
         this.setColor(r, g, b);
      }
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.alpha = Mth.clampedLerp(1.0F, 0.0F, (this.age - this.lifetime + 8) / 8.0F);
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         this.xd = this.xd + this.random.nextFloat() / 500.0F * (this.random.nextBoolean() ? 1 : -1);
         this.yd = this.yd + (this.random.nextFloat() / 100.0F - this.gravity);
         this.zd = this.zd + this.random.nextFloat() / 500.0F * (this.random.nextBoolean() ? 1 : -1);
         this.setSpriteFromAge(this.sprites);
         this.scale(1.023F);
      }
   }

   protected float getU0() {
      return this.mirrored ? super.getU1() : super.getU0();
   }

   protected float getU1() {
      return this.mirrored ? super.getU0() : super.getU1();
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public int getLightColor(float partialTick) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<ElementalFierySmokeParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(
         ElementalFierySmokeParticleOptions options, ClientLevel level, double x, double y, double z, double dx, double dy, double dz
      ) {
         return new ElementalFierySmokeParticle(level, x, y, z, this.sprites, dx, dy, dz, options.r(), options.g(), options.b(), options.scale());
      }
   }
}
