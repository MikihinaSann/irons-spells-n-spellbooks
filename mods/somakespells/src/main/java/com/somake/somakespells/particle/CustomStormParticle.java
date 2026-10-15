package com.somake.somakespells.particle;

import com.github.L_Ender.cataclysm.client.particle.AbstractTrailParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class CustomStormParticle extends AbstractTrailParticle {
   private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/particle/amogus.png");
   private final int EntityId;
   private final float width;
   private final float height;
   private final float initialYRot;
   private final float rotateByAge;
   private final float expansion;
   private float currentWidth;

   public CustomStormParticle(
      ClientLevel world, double x, double y, double z, float r, float g, float b, float width, float height, int EntityId, float expansion
   ) {
      super(world, x, y, z, 0.0, 0.0, 0.0, r, g, b);
      this.EntityId = EntityId;
      this.gravity = 0.0F;
      this.lifetime = 20 + this.random.nextInt(20);
      this.initialYRot = this.random.nextFloat() * 360.0F;
      this.rotateByAge = (10.0F + this.random.nextFloat() * 10.0F) * (this.random.nextBoolean() ? -1.0F : 1.0F);
      this.width = width;
      this.height = height;
      this.expansion = expansion;
      this.currentWidth = width;
      Vec3 vec3 = this.getOrbitPosition();
      this.setPos(vec3.x, vec3.y, vec3.z);
      this.xo = vec3.x;
      this.yo = vec3.y;
      this.zo = vec3.z;
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
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
      super.tick();
      if (this.expansion > 0.0F) {
         this.currentWidth = this.currentWidth + this.expansion;
      }

      float fade = 1.0F - (float)this.age / this.lifetime;
      this.trailA = 1.0F * fade;
      Vec3 vec3 = this.getOrbitPosition();
      this.x = vec3.x;
      this.y = vec3.y;
      this.z = vec3.z;
      Entity from = this.getFromEntity();
      if (from == null) {
         this.remove();
      }
   }

   public int sampleCount() {
      return 4;
   }

   public int sampleStep() {
      return 1;
   }

   public float getTrailHeight() {
      return 0.5F;
   }

   public int getLightColor(float f) {
      return 240;
   }

   public ResourceLocation getTrailTexture() {
      return TRAIL_TEXTURE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<CustomStormParticleOptions> {
      public Particle createParticle(
         CustomStormParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new CustomStormParticle(level, x, y, z, data.r(), data.g(), data.b(), data.width(), data.height(), data.entityid(), data.expansion());
      }
   }
}
