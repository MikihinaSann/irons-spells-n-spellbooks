package net.acetheeldritchking.discerning_the_eldritch.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class RiftSliceParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final boolean mirrored;

   public RiftSliceParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd) {
      super(level, x, y, z, xd, yd, zd);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.scale(this.random.nextFloat() * 1.75F + 1.0F);
      this.lifetime = 10 + (int)(Math.random() * 10.0);
      this.sprites = spriteSet;
      this.setSpriteFromAge(spriteSet);
      this.gravity = -0.01F;
      this.mirrored = this.random.nextBoolean();
   }

   protected float getU0() {
      return this.mirrored ? super.getU1() : super.getU0();
   }

   protected float getU1() {
      return this.mirrored ? super.getU0() : super.getU1();
   }

   public void tick() {
      super.tick();
      this.xd = this.xd + this.random.nextFloat() / 500.0F * (this.random.nextBoolean() ? 1 : -1);
      this.yd = this.yd + this.random.nextFloat() / 100.0F;
      this.zd = this.zd + this.random.nextFloat() / 500.0F * (this.random.nextBoolean() ? 1 : -1);
      this.setSpriteFromAge(this.sprites);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   protected int getLightColor(float partialTick) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      @Nullable
      public Particle createParticle(
         SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz
      ) {
         return new RiftSliceParticle(clientLevel, x, y, z, this.sprites, dx, dy, dz);
      }
   }
}
