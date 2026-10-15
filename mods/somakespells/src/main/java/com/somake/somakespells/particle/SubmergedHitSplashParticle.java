package com.somake.somakespells.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SubmergedHitSplashParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final float baseSize;

   protected SubmergedHitSplashParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
      super(level, x, y, z, 0.0, 0.0, 0.0);
      this.sprites = sprites;
      this.lifetime = 7;
      this.gravity = 0.0F;
      this.hasPhysics = false;
      this.friction = 1.0F;
      this.baseSize = 0.72F + this.random.nextFloat() * 0.18F;
      this.quadSize = this.baseSize;
      this.roll = (this.random.nextFloat() - 0.5F) * 0.22F;
      this.oRoll = this.roll;
      this.setSpriteFromAge(sprites);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         float progress = (float)this.age / this.lifetime;
         this.quadSize = this.baseSize * Mth.lerp(progress, 0.82F, 1.16F);
         if (progress > 0.65F) {
            this.alpha = Mth.clamp((1.0F - progress) / 0.35F, 0.0F, 1.0F);
         }
      }
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      @Nullable
      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new SubmergedHitSplashParticle(level, x, y, z, this.sprites);
      }
   }
}
