package io.redspace.ironslib.transmog.client;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CapeData {
   public double xCloakO;
   public double yCloakO;
   public double zCloakO;
   public double xCloak;
   public double yCloak;
   public double zCloak;
   public float bob;
   public float oBob;
   public int lastTick = -1;

   public void renderUpdate(@NotNull LivingEntity livingEntity) {
      if (this.lastTick != livingEntity.tickCount) {
         if (this.lastTick == -1) {
            for (int i = 0; i < 20; i++) {
               this.moveCloak(livingEntity);
            }
         }

         this.moveCloak(livingEntity);
         this.lastTick = livingEntity.tickCount;
      }
   }

   public Vec3 calculateRotationsDegrees(LivingEntity livingEntity, float partialTicks) {
      double d0 = Mth.lerp((double)partialTicks, this.xCloakO, this.xCloak) - Mth.lerp((double)partialTicks, livingEntity.xo, livingEntity.getX());
      double d1 = Mth.lerp((double)partialTicks, this.yCloakO, this.yCloak) - Mth.lerp((double)partialTicks, livingEntity.yo, livingEntity.getY());
      double d2 = Mth.lerp((double)partialTicks, this.zCloakO, this.zCloak) - Mth.lerp((double)partialTicks, livingEntity.zo, livingEntity.getZ());
      float f = Mth.rotLerp(partialTicks, livingEntity.yBodyRotO, livingEntity.yBodyRot);
      double d3 = (double)Mth.sin(f * (float) (Math.PI / 180.0));
      double d4 = (double)(-Mth.cos(f * (float) (Math.PI / 180.0)));
      float f1 = (float)d1 * 10.0F;
      f1 = Mth.clamp(f1, -6.0F, 32.0F);
      float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
      f2 = Mth.clamp(f2, 0.0F, 150.0F);
      float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
      f3 = Mth.clamp(f3, -20.0F, 20.0F);
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      float f4 = Mth.lerp(partialTicks, this.oBob, this.bob);
      f1 += Mth.sin(Mth.lerp(partialTicks, livingEntity.walkDistO, livingEntity.walkDist) * 6.0F) * 32.0F * f4;
      if (livingEntity.isCrouching()) {
         f1 += 25.0F;
      }

      return new Vec3((double)(f2 / 2.0F + f1), (double)(f3 / 2.0F), (double)(-f3 / 2.0F));
   }

   private void moveCloak(LivingEntity livingEntity) {
      this.oBob = this.bob;
      float f;
      if (livingEntity.onGround() && !livingEntity.isDeadOrDying()) {
         f = (float)Math.min(0.1, livingEntity.getDeltaMovement().horizontalDistance());
      } else {
         f = 0.0F;
      }

      this.bob = this.bob + (f - this.bob) * 0.4F;
      this.xCloakO = this.xCloak;
      this.yCloakO = this.yCloak;
      this.zCloakO = this.zCloak;
      double d0 = livingEntity.getX() - this.xCloak;
      double d1 = livingEntity.getY() - this.yCloak;
      double d2 = livingEntity.getZ() - this.zCloak;
      double d3 = 10.0;
      if (d0 > 10.0) {
         this.xCloak = livingEntity.getX();
         this.xCloakO = this.xCloak;
      }

      if (d2 > 10.0) {
         this.zCloak = livingEntity.getZ();
         this.zCloakO = this.zCloak;
      }

      if (d1 > 10.0) {
         this.yCloak = livingEntity.getY();
         this.yCloakO = this.yCloak;
      }

      if (d0 < -10.0) {
         this.xCloak = livingEntity.getX();
         this.xCloakO = this.xCloak;
      }

      if (d2 < -10.0) {
         this.zCloak = livingEntity.getZ();
         this.zCloakO = this.zCloak;
      }

      if (d1 < -10.0) {
         this.yCloak = livingEntity.getY();
         this.yCloakO = this.yCloak;
      }

      this.xCloak += d0 * 0.25;
      this.zCloak += d2 * 0.25;
      this.yCloak += d1 * 0.25;
   }
}
