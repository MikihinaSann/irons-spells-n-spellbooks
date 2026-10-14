package com.github.L_Ender.lionfishapi.client.model.tools;

import com.github.L_Ender.lionfishapi.client.util.ClientUtils;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChainBuffer {
   private int yawTimer;
   private float yawVariation;
   private int pitchTimer;
   private float pitchVariation;
   private float prevYawVariation;
   private float prevPitchVariation;
   @Nullable
   protected Minecraft minecraft;

   public void resetRotations() {
      this.yawVariation = 0.0F;
      this.pitchVariation = 0.0F;
      this.prevYawVariation = 0.0F;
      this.prevPitchVariation = 0.0F;
   }

   public void calculateChainSwingBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, LivingEntity entity) {
      this.prevYawVariation = this.yawVariation;
      if (entity.yBodyRot != entity.yBodyRotO && Mth.abs(this.yawVariation) < maxAngle) {
         this.yawVariation = this.yawVariation + (entity.yBodyRotO - entity.yBodyRot) / divisor;
      }

      if (this.yawVariation > 0.7F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation -= angleDecrement;
            if (Mth.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = 0.0F;
               this.yawTimer = 0;
            }
         } else {
            this.yawTimer++;
         }
      } else if (this.yawVariation < -0.7F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation += angleDecrement;
            if (Mth.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = 0.0F;
               this.yawTimer = 0;
            }
         } else {
            this.yawTimer++;
         }
      }
   }

   public void calculateChainWaveBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, LivingEntity entity) {
      this.prevPitchVariation = this.pitchVariation;
      if (entity.getXRot() != entity.xRotO && Mth.abs(this.pitchVariation) < maxAngle) {
         this.pitchVariation = this.pitchVariation + (entity.xRotO - entity.getXRot()) / divisor;
      }

      if (this.pitchVariation > 0.7F * angleDecrement) {
         if (this.pitchTimer > bufferTime) {
            this.pitchVariation -= angleDecrement;
            if (Mth.abs(this.pitchVariation) < angleDecrement) {
               this.pitchVariation = 0.0F;
               this.pitchTimer = 0;
            }
         } else {
            this.pitchTimer++;
         }
      } else if (this.pitchVariation < -0.7F * angleDecrement) {
         if (this.pitchTimer > bufferTime) {
            this.pitchVariation += angleDecrement;
            if (Mth.abs(this.pitchVariation) < angleDecrement) {
               this.pitchVariation = 0.0F;
               this.pitchTimer = 0;
            }
         } else {
            this.pitchTimer++;
         }
      }
   }

   public void calculateChainSwingBuffer(float maxAngle, int bufferTime, float angleDecrement, LivingEntity entity) {
      this.calculateChainSwingBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
   }

   public void calculateChainWaveBuffer(float maxAngle, int bufferTime, float angleDecrement, LivingEntity entity) {
      this.calculateChainWaveBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
   }

   public void applyChainSwingBuffer(ModelPart... boxes) {
      float rotateAmount = (float) (Math.PI / 180.0)
         * ClientUtils.interpolate(this.prevYawVariation, this.yawVariation, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false))
         / boxes.length;

      for (ModelPart box : boxes) {
         box.yRot += rotateAmount;
      }
   }

   public void applyChainWaveBuffer(ModelPart... boxes) {
      float rotateAmount = (float) (Math.PI / 180.0)
         * ClientUtils.interpolate(this.prevPitchVariation, this.pitchVariation, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false))
         / boxes.length;

      for (ModelPart box : boxes) {
         box.xRot += rotateAmount;
      }
   }
}
