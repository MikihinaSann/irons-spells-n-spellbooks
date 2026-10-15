package com.somake.somakespells.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class FireVortexParticle extends TextureSheetParticle {
   private final int entityId;
   private final float radius;
   private final float baseHeight;
   private final float angularVelocity;
   private final float ascent;
   private final SpriteSet sprites;
   private final float spinOffset;
   private final float verticalNoise;
   private final float jitter;
   private final int elementMode;

   protected FireVortexParticle(ClientLevel level, double x, double y, double z, FireVortexParticleOptions options, SpriteSet sprites) {
      super(level, x, y, z);
      this.entityId = options.entityid();
      this.jitter = options.jitter();
      this.radius = options.radius() * (1.0F + (this.random.nextFloat() - 0.5F) * 0.3F * this.jitter);
      this.baseHeight = options.height() + (this.random.nextFloat() - 0.5F) * 0.35F * this.jitter;
      this.angularVelocity = options.angularVelocity() * (1.0F + (this.random.nextFloat() - 0.5F) * 0.4F * this.jitter);
      this.ascent = options.ascent() * (1.0F + (this.random.nextFloat() - 0.5F) * 0.35F * this.jitter);
      this.sprites = sprites;
      this.elementMode = options.elementMode();
      this.spinOffset = this.random.nextFloat() * 360.0F;
      this.verticalNoise = this.random.nextFloat() * (float) (Math.PI * 2);
      this.gravity = 0.0F;
      this.friction = 1.0F;
      this.lifetime = Math.max(8, Mth.ceil((18 + this.random.nextInt(10)) * options.lifeScale()));
      this.quadSize = options.scale() * (0.72F + this.random.nextFloat() * (0.3F + 0.18F * this.jitter));
      this.hasPhysics = false;
      if (this.elementMode != 1 && this.elementMode != 2) {
         this.rCol = 1.0F;
         this.gCol = 0.45F + this.random.nextFloat() * 0.25F;
         this.bCol = 0.05F + this.random.nextFloat() * 0.08F;
      } else {
         this.rCol = 1.0F;
         this.gCol = 1.0F;
         this.bCol = 1.0F;
      }

      this.alpha = 0.88F;
      this.pickSprite(sprites);
      Vec3 pos = this.getOrbitPosition(0.0F);
      this.setPos(pos.x, pos.y, pos.z);
      this.xo = pos.x;
      this.yo = pos.y;
      this.zo = pos.z;
   }

   private Entity getAnchor() {
      return this.entityId == -1 ? null : this.level.getEntity(this.entityId);
   }

   private Vec3 getOrbitPosition(float partialAge) {
      Entity anchor = this.getAnchor();
      if (anchor == null) {
         return new Vec3(this.x, this.y, this.z);
      } else {
         float age = this.age + partialAge;
         float progress = age / this.lifetime;
         double taper = Mth.clamp(1.05F - progress * 0.8F, 0.2F, 1.1F);
         double noisyRadius = Math.max(0.12, this.radius * taper + Math.sin(this.verticalNoise + age * 0.32F) * 0.14F * this.jitter);
         double yOffset = this.baseHeight + age * this.ascent + Math.sin(this.verticalNoise + age * 0.18F) * 0.18F * this.jitter;
         float angle = this.spinOffset + age * this.angularVelocity;
         return anchor.position().add(new Vec3(0.0, yOffset, noisyRadius).yRot((float)Math.toRadians(angle)));
      }
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ < this.lifetime && this.getAnchor() != null) {
         Vec3 pos = this.getOrbitPosition(0.0F);
         this.setPos(pos.x, pos.y, pos.z);
         this.setSpriteFromAge(this.sprites);
         float fade = 1.0F - (float)this.age / this.lifetime;
         this.alpha = 0.2F + fade * 0.68F;
         this.quadSize *= 0.985F;
      } else {
         this.remove();
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   protected int getLightColor(float partialTick) {
      return 240;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<FireVortexParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(
         FireVortexParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new FireVortexParticle(level, x, y, z, options, this.sprites);
      }
   }
}
