package com.somake.somakespells.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class OrbitingWaterParticle extends TextureSheetParticle {
   private final int EntityId;
   private final float width;
   private final float height;
   private final float initialYRot;
   private final float rotateByAge;
   private final float expansion;
   private float currentWidth;

   public OrbitingWaterParticle(ClientLevel world, double x, double y, double z, float width, float height, int EntityId, SpriteSet sprites, float expansion) {
      super(world, x, y, z);
      this.EntityId = EntityId;
      this.gravity = 0.0F;
      this.lifetime = 20 + this.random.nextInt(20);
      this.initialYRot = this.random.nextFloat() * 360.0F;
      this.rotateByAge = (10.0F + this.random.nextFloat() * 10.0F) * (this.random.nextBoolean() ? -1.0F : 1.0F);
      this.width = width;
      this.height = height;
      this.expansion = expansion;
      this.currentWidth = width;
      this.pickSprite(sprites);
      Vec3 vec3 = this.getOrbitPosition();
      this.setPos(vec3.x, vec3.y, vec3.z);
      this.xo = vec3.x;
      this.yo = vec3.y;
      this.zo = vec3.z;
   }

   public Vec3 getEntityPosition() {
      Entity from = this.getFromEntity();
      return from != null ? from.position() : new Vec3(this.x, this.y, this.z);
   }

   public Entity getFromEntity() {
      return this.EntityId == -1 ? null : this.level.getEntity(this.EntityId);
   }

   public Vec3 getOrbitPosition() {
      Vec3 dinoPos = this.getEntityPosition();
      double yOffset = Math.sin(this.age * 0.15) * 0.5;
      Vec3 vec3 = new Vec3(0.0, this.height + yOffset, this.currentWidth).yRot((float)Math.toRadians(this.initialYRot + this.rotateByAge * this.age));
      return dinoPos.add(vec3);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.expansion > 0.0F) {
         this.currentWidth = this.currentWidth + this.expansion;
      }

      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         Vec3 vec3 = this.getOrbitPosition();
         this.setPos(vec3.x, vec3.y, vec3.z);
         Entity from = this.getFromEntity();
         if (from == null) {
            this.remove();
         }
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<OrbitingWaterParticleOptions> {
      private final SpriteSet sprites;

      public Factory(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(
         OrbitingWaterParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new OrbitingWaterParticle(level, x, y, z, data.width(), data.height(), data.entityid(), this.sprites, data.expansion());
      }
   }
}
