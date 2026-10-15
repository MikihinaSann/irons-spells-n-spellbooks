package com.somake.somakespells.particle;

import com.somake.somakespells.entity.WitheredRoseVortexEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class WitheredRoseVortexParticle extends TextureSheetParticle {
   private final int entityId;
   private final float radius;
   private final float baseHeight;
   private final float angularVelocity;
   private final float ascent;
   private final float jitter;
   private final int strand;
   private final SpriteSet sprites;
   private final float phaseOffset;
   private final float verticalNoise;
   private final float baseSize;
   private final float spin;

   protected WitheredRoseVortexParticle(ClientLevel level, double x, double y, double z, WitheredRoseVortexParticleOptions options, SpriteSet sprites) {
      super(level, x, y, z);
      this.entityId = options.entityId();
      this.radius = options.radius() * (1.0F + (this.random.nextFloat() - 0.5F) * 0.2F * options.jitter());
      this.baseHeight = options.height() + (this.random.nextFloat() - 0.5F) * 0.22F * options.jitter();
      this.angularVelocity = options.angularVelocity() * (1.0F + (this.random.nextFloat() - 0.5F) * 0.18F * options.jitter());
      this.ascent = options.ascent() * (1.0F + (this.random.nextFloat() - 0.5F) * 0.2F * options.jitter());
      this.jitter = options.jitter();
      this.strand = Math.floorMod(options.strand(), 3);
      this.sprites = sprites;
      this.phaseOffset = this.strand * 120.0F + this.random.nextFloat() * 24.0F;
      this.verticalNoise = this.random.nextFloat() * (float) (Math.PI * 2);
      this.gravity = 0.0F;
      this.friction = 1.0F;
      this.hasPhysics = false;
      this.lifetime = Math.max(12, Mth.ceil((24 + this.random.nextInt(12)) * options.lifeScale()));
      this.baseSize = options.scale() * (0.78F + this.random.nextFloat() * 0.34F);
      this.quadSize = this.baseSize;
      this.roll = this.random.nextFloat() * (float) (Math.PI * 2);
      this.spin = (this.random.nextFloat() - 0.5F) * 0.28F;
      this.alpha = 0.94F;
      if (options.crimson()) {
         float tint = this.random.nextFloat();
         this.rCol = Mth.lerp(tint, 0.48F, 0.82F);
         this.gCol = Mth.lerp(tint, 0.035F, 0.12F);
         this.bCol = Mth.lerp(tint, 0.08F, 0.19F);
      } else {
         this.rCol = 1.0F;
         this.gCol = 1.0F;
         this.bCol = 1.0F;
      }

      this.pickSprite(sprites);
      Vec3 position = this.orbitPosition(0.0F);
      this.setPos(position.x, position.y, position.z);
      this.xo = position.x;
      this.yo = position.y;
      this.zo = position.z;
   }

   private Entity anchor() {
      return this.entityId < 0 ? null : this.level.getEntity(this.entityId);
   }

   private Vec3 orbitPosition(float partialAge) {
      Entity anchor = this.anchor();
      if (anchor == null) {
         return new Vec3(this.x, this.y, this.z);
      } else {
         float localAge = this.age + partialAge;
         float progress = Mth.clamp(localAge / this.lifetime, 0.0F, 1.0F);
         double height = this.baseHeight + localAge * this.ascent;
         double coneProgress = Mth.clamp((float)(height / 6.4), 0.0F, 1.0F);
         double coneRadius = this.radius * Mth.lerp((float)coneProgress, 1.08F, 0.24F);
         coneRadius += Math.sin(this.verticalNoise + localAge * 0.34F) * 0.12F * this.jitter;
         coneRadius *= 0.88 + 0.12 * Math.sin(progress * Math.PI);
         float retreat = retreatProgress(anchor, partialAge);
         float retreatEase = retreat * retreat;
         coneRadius *= Mth.lerp(retreat, 1.0F, 0.04F);
         height = Mth.lerp(retreatEase, (float)height, 0.08F + this.baseHeight * 0.04F);
         float angle = this.phaseOffset + localAge * this.angularVelocity + (float)height * 19.0F;
         double y = height + Math.sin(this.verticalNoise + localAge * 0.22F) * 0.08F * this.jitter;
         return anchor.position().add(new Vec3(0.0, y, Math.max(0.08, coneRadius)).yRot((float)Math.toRadians(angle)));
      }
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.oRoll = this.roll;
      Entity anchor = this.anchor();
      if (anchor instanceof WitheredRoseVortexEntity vortex && vortex.tickCount >= vortex.getTotalLifetime()) {
         int remainingRetreatTicks = Math.max(1, vortex.getTotalLifetime() + 16 - vortex.tickCount + 1);
         this.lifetime = Math.max(this.lifetime, this.age + remainingRetreatTicks);
      }

      if (this.age++ < this.lifetime && anchor != null) {
         Vec3 position = this.orbitPosition(0.0F);
         this.setPos(position.x, position.y, position.z);
         this.roll = this.roll + this.spin;
         this.setSpriteFromAge(this.sprites);
         float fadeIn = Mth.clamp(this.age / 4.0F, 0.0F, 1.0F);
         float fadeOut = Mth.clamp((this.lifetime - this.age) / 8.0F, 0.0F, 1.0F);
         float retreat = retreatProgress(anchor, 0.0F);
         float retreatFade = 1.0F - retreat;
         this.alpha = 0.94F * Math.min(fadeIn, fadeOut) * retreatFade;
         this.quadSize = this.baseSize * (0.9F + 0.1F * fadeOut) * Mth.lerp(retreat, 1.0F, 0.42F);
         if (retreat >= 0.995F) {
            this.remove();
         }
      } else {
         this.remove();
      }
   }

   private static float retreatProgress(Entity anchor, float partialTick) {
      return anchor instanceof WitheredRoseVortexEntity vortex ? vortex.getPetalRetreatProgress(partialTick) : 0.0F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   protected int getLightColor(float partialTick) {
      int packed = super.getLightColor(partialTick);
      int block = Math.max(packed & 65535, 160);
      int sky = Math.max(packed >> 16 & 65535, 96);
      return block | sky << 16;
   }

   public static class Provider implements ParticleProvider<WitheredRoseVortexParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(
         WitheredRoseVortexParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new WitheredRoseVortexParticle(level, x, y, z, options, this.sprites);
      }
   }
}
