package com.github.L_Ender.lionfishapi.client.model.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class DynamicChain {
   private Vec3[] p;
   private Vec3[] v;
   private Vec3[] a;
   private Vec3[] F;
   private float[] m;
   private float[] d;
   private Vec3[] T;
   private Vec3[] r;
   private Vec3[] rv;
   private Vec3[] ra;
   private final Entity entity;
   private Vec3 prevP;
   private Vec3 prevV;
   private Vec3[] pOrig;
   private int prevUpdateTick;

   public DynamicChain(Entity entity) {
      this.entity = entity;
      this.p = new Vec3[0];
      this.v = new Vec3[0];
      this.a = new Vec3[0];
      this.F = new Vec3[0];
      this.m = new float[0];
      this.d = new float[0];
      this.T = new Vec3[0];
      this.ra = new Vec3[0];
      this.rv = new Vec3[0];
      this.r = new Vec3[0];
      this.pOrig = new Vec3[0];
      this.prevUpdateTick = -1;
   }

   public void updateBendConstraint(float gravityAmount, float stiffness, float stiffnessFalloff, float damping, int numUpdates, boolean useFloor) {
      Vec3[] prevPos = new Vec3[this.p.length];
      Vec3[] prevVel = new Vec3[this.v.length];

      for (int i = 0; i < this.p.length; i++) {
         prevPos[i] = this.p[i];
         prevVel[i] = this.v[i];
      }

      for (int j = 0; j < numUpdates; j++) {
         for (int i = 0; i < this.p.length - 1; i++) {
            if (i == 0) {
               Vec3 root = this.pOrig[i];
               this.p[i] = root;
               this.v[i] = this.p[i].subtract(prevPos[i]);
               this.a[i] = this.v[i].subtract(prevVel[i]);
            }

            Vec3 target = angleBetween(this.pOrig[i], this.pOrig[i + 1]);
            this.r[i] = angleBetween(this.p[i], this.p[i + 1]);
            this.T[i] = wrapAngles(this.r[i].subtract(target)).scale(-stiffness / Math.pow(i + 1, stiffnessFalloff));
            double down = Math.PI / 2;
            Vec3 gravityVec = wrapAngles(new Vec3(0.0, gravityAmount * this.d[i + 1] * this.m[i + 1] * Math.sin(down - this.r[i].y + down), 0.0));
            Vec3 floorVec = new Vec3(0.0, 1.0F * this.d[i + 1] * this.m[i + 1] * Math.sin((Math.PI / 2) - this.r[i].y + (Math.PI / 2)), 0.0);
            if (useFloor && this.entity.onGround() && this.p[i + 1].y < this.entity.getY()) {
               this.T[i] = this.T[i].subtract(floorVec);
            }

            this.T[i] = wrapAngles(this.T[i].add(gravityVec));
            this.ra[i] = this.T[i].scale(1.0F / (this.m[i + 1] * this.d[i + 1] * this.d[i + 1]));
            this.rv[i] = this.rv[i].add(this.ra[i].scale(1.0F / numUpdates)).scale(damping);
            this.rv[i] = wrapAngles(this.rv[i]);
            this.r[i] = this.r[i].add(this.rv[i].scale(1.0F / numUpdates));
            this.r[i] = wrapAngles(this.r[i]);
            this.p[i + 1] = fromPitchYaw((float)(this.r[i].y - (Math.PI / 2)), (float)(this.r[i].x - (Math.PI / 2))).scale(this.d[i + 1]).add(this.p[i]);
            this.v[i + 1] = this.p[i + 1].subtract(prevPos[i + 1]);
            this.a[i + 1] = this.v[i + 1].subtract(prevVel[i + 1]);
         }
      }
   }

   public void updateSpringConstraint(
      float gravityAmount, float dampAmount, float stiffness, float maxForce, boolean doAttract, float attractFalloff, int numUpdates
   ) {
      for (int j = 0; j < numUpdates; j++) {
         for (int i = 0; i < this.p.length; i++) {
            this.a[i] = this.F[i].scale(1.0F / this.m[i]);
            this.v[i] = this.v[i].add(this.a[i].scale(1.0F / numUpdates));
            this.p[i] = this.p[i].add(this.v[i].scale(1.0F / numUpdates));
            Vec3 disp;
            if (i == 0) {
               Vec3 root = this.pOrig[i];
               disp = this.p[i].subtract(root);
            } else {
               disp = this.p[i].subtract(this.p[i - 1]);
            }

            disp = disp.normalize().scale(disp.length() - this.d[i]);
            Vec3 damp = this.v[i].scale(dampAmount);
            Vec3 gravity = new Vec3(0.0, -gravityAmount, 0.0);
            Vec3 attract = this.pOrig[0].subtract(this.p[i]);
            this.F[i] = disp.scale(-stiffness * disp.length()).add(gravity.scale(this.m[i])).subtract(damp);
            if (i == 0 || doAttract) {
               this.F[i] = this.F[i].add(attract.scale(1.0F / (1.0F + i * i * attractFalloff)));
            }

            if (this.F[i].length() > maxForce) {
               this.F[i].normalize().scale(maxForce);
            }
         }
      }
   }

   public void setChain(AdvancedModelBox[] chainOrig, AdvancedModelBox[] chainDynamic) {
      this.p = new Vec3[chainOrig.length];
      this.v = new Vec3[chainOrig.length];
      this.a = new Vec3[chainOrig.length];
      this.F = new Vec3[chainOrig.length];
      this.m = new float[chainOrig.length];
      this.d = new float[chainOrig.length];
      this.T = new Vec3[chainOrig.length];
      this.r = new Vec3[chainOrig.length];
      this.rv = new Vec3[chainOrig.length];
      this.ra = new Vec3[chainOrig.length];
      this.pOrig = new Vec3[chainOrig.length];

      for (int i = 0; i < chainOrig.length; i++) {
         this.pOrig[i] = chainOrig[i].getWorldPos(this.entity, 0.0F);
         this.p[i] = this.pOrig[i];
         this.v[i] = new Vec3(0.0, 0.0, 0.0);
         this.a[i] = new Vec3(0.0, 0.0, 0.0);
         this.F[i] = new Vec3(0.0, 0.0, 0.0);
         this.T[i] = new Vec3(0.0, 0.0, 0.0);
         this.r[i] = new Vec3(0.0, 0.0, 0.0);
         this.rv[i] = new Vec3(0.0, 0.0, 0.0);
         this.ra[i] = new Vec3(0.0, 0.0, 0.0);
         this.m[i] = 0.5F + 0.5F / (i + 1);
         if (i > 0) {
            this.d[i] = (float)this.p[i].distanceTo(this.p[i - 1]);
         } else {
            this.d[i] = 1.0F;
         }

         chainOrig[i].setIsHidden(true);
      }

      for (int i = 0; i < chainOrig.length - 1; i++) {
         this.r[i] = angleBetween(this.p[i], this.p[i + 1]);
      }

      this.prevP = this.p[0];
      this.prevV = this.v[0];

      for (int i = 0; i < chainOrig.length; i++) {
         if (chainDynamic[i] == null) {
            chainDynamic[i] = new AdvancedModelBox(chainOrig[i]);
         }
      }
   }

   public void updateChain(
      float delta,
      AdvancedModelBox[] chainOrig,
      AdvancedModelBox[] chainDynamic,
      float gravityAmount,
      float stiffness,
      float stiffnessFalloff,
      float damping,
      int numUpdates,
      boolean useFloor
   ) {
      if (this.p.length != chainOrig.length || Double.isNaN(this.p[1].x)) {
         this.setChain(chainOrig, chainDynamic);
      }

      if (this.prevUpdateTick != this.entity.tickCount) {
         for (int i = 0; i < chainOrig.length; i++) {
            this.pOrig[i] = chainOrig[i].getWorldPos(this.entity, delta);
         }

         this.updateBendConstraint(gravityAmount, stiffness, stiffnessFalloff, damping, numUpdates, useFloor);
         this.prevUpdateTick = this.entity.tickCount;
      }

      if (chainDynamic != null) {
         if (Minecraft.getInstance().isPaused()) {
            delta = 0.5F;
         }

         for (int i = chainDynamic.length - 1; i >= 0; i--) {
            if (chainDynamic[i] == null) {
               return;
            }

            Vec3 renderPos = this.p[i].add(this.v[i].scale(delta)).add(this.a[i].scale(0.5 * delta * delta));
            chainDynamic[i].setWorldPos(this.entity, renderPos, delta);
            if (i < chainDynamic.length - 1) {
               Vec3 p1 = new Vec3(chainDynamic[i].rotationPointX, chainDynamic[i].rotationPointY, chainDynamic[i].rotationPointZ);
               Vec3 p2 = new Vec3(chainDynamic[i + 1].rotationPointX, chainDynamic[i + 1].rotationPointY, chainDynamic[i + 1].rotationPointZ);
               Vec3 diff = p2.subtract(p1);
               float yaw = (float)Math.atan2(diff.x, diff.z);
               float pitch = -((float)Math.asin(diff.y / diff.length()));
               chainDynamic[i].rotateAngleY = chainDynamic[i].defaultRotationY + yaw;
               chainDynamic[i].rotateAngleX = chainDynamic[i].defaultRotationZ + pitch;
               chainDynamic[i].rotateAngleZ = (float)this.r[i].z;
               Vec3 diffRotated = diff;
               diffRotated = diffRotated.yRot(yaw);
               diffRotated = diffRotated.xRot(pitch);
            }
         }
      }
   }

   public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, int alpha, AdvancedModelBox[] dynModelRenderers) {
      if (dynModelRenderers != null) {
         for (int i = 0; i < dynModelRenderers.length - 1; i++) {
            if (dynModelRenderers[i] == null) {
               return;
            }

            dynModelRenderers[i].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, alpha);
         }
      }
   }

   private static Vec3 fromPitchYaw(float pitch, float yaw) {
      float f = Mth.cos(-yaw - (float) Math.PI);
      float f1 = Mth.sin(-yaw - (float) Math.PI);
      float f2 = -Mth.cos(-pitch);
      float f3 = Mth.sin(-pitch);
      return new Vec3(f1 * f2, f3, f * f2);
   }

   private static Vec3 angleBetween(Vec3 p1, Vec3 p2) {
      float dz = (float)(p2.z - p1.z);
      float dx = (float)(p2.x - p1.x);
      float dy = (float)(p2.y - p1.y);
      float yaw = (float)Mth.atan2(dz, dx);
      float pitch = (float)Mth.atan2(Math.sqrt(dz * dz + dx * dx), dy);
      return wrapAngles(new Vec3(yaw, pitch, 0.0));
   }

   public static Vec3 toPitchYaw(Vec3 vector) {
      double f3 = vector.y;
      double pitch = -Math.asin(f3);
      double f2 = -Math.cos(pitch);
      double f1 = vector.x / f2;
      double yaw = -Math.asin(f1) + (Math.PI / 2);
      return wrapAngles(new Vec3(yaw, pitch, 0.0));
   }

   private static Vec3 toEuler(Vec3 axis, double angle) {
      double s = Math.sin(angle);
      double c = Math.cos(angle);
      double t = 1.0 - c;
      double yaw = 0.0;
      double pitch = 0.0;
      double roll = 0.0;
      double x = axis.x;
      double y = axis.y;
      double z = axis.z;
      if (x * y * t + z * s > 0.998) {
         yaw = 2.0 * Math.atan2(x * Math.sin(angle / 2.0), Math.cos(angle / 2.0));
         pitch = Math.PI / 2;
         roll = 0.0;
      } else if (x * y * t + z * s < -0.998) {
         yaw = -2.0 * Math.atan2(x * Math.sin(angle / 2.0), Math.cos(angle / 2.0));
         pitch = -Math.PI / 2;
         roll = 0.0;
      } else {
         yaw = Math.atan2(y * s - x * z * t, 1.0 - (y * y + z * z) * t);
         pitch = Math.asin(x * y * t + z * s);
         roll = Math.atan2(x * s - y * z * t, 1.0 - (x * x + z * z) * t);
      }

      return new Vec3(yaw, pitch, roll);
   }

   private static Vec3 wrapAngles(Vec3 r) {
      double x = r.x;
      double y = r.y;
      double z = r.z;

      while (x > Math.PI) {
         x -= Math.PI * 2;
      }

      while (x < -Math.PI) {
         x += Math.PI * 2;
      }

      while (y > Math.PI) {
         y -= Math.PI * 2;
      }

      while (y < -Math.PI) {
         y += Math.PI * 2;
      }

      while (z > Math.PI) {
         z -= Math.PI * 2;
      }

      while (z < -Math.PI) {
         z += Math.PI * 2;
      }

      return new Vec3(x, y, z);
   }

   private static Vec3 multiply(Vec3 u, Vec3 v, boolean preserveDir) {
      return preserveDir ? new Vec3(u.x * Math.abs(v.x), u.y * Math.abs(v.y), u.z * Math.abs(v.z)) : new Vec3(u.x * v.x, u.y * v.y, u.z * v.z);
   }
}
