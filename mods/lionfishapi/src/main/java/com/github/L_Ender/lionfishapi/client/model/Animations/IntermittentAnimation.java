package com.github.L_Ender.lionfishapi.client.model.Animations;

import com.github.L_Ender.lionfishapi.server.entity.IIntermittentEntity;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IntermittentAnimation<T extends Entity & IIntermittentEntity> {
   private final Random rand = new Random();
   private final T entity;
   private int timeRunning;
   private int duration;
   private boolean isRunning;
   private int runDirection;
   private int timeIdle;
   private final int minIdleTime;
   private final int startProbability;
   private final boolean isOperator;
   private byte id;

   public IntermittentAnimation(T entity, int duration, int intervalDuration, int startPropbability, boolean isOperator) {
      this.entity = entity;
      this.duration = duration;
      this.minIdleTime = intervalDuration;
      this.startProbability = startPropbability;
      this.isOperator = isOperator;
      this.timeRunning = 0;
      this.isRunning = false;
      this.runDirection = -1;
   }

   public void setID(byte id) {
      this.id = id;
   }

   public void setDuration(int duration) {
      this.timeRunning = 0;
      this.duration = duration;
   }

   public int getTimeRunning() {
      return this.timeRunning;
   }

   public void setTimeRunning(int timeRunning) {
      this.timeRunning = timeRunning;
      if (this.timeRunning > this.duration) {
         this.timeRunning = this.duration;
      } else if (this.timeRunning < 0) {
         this.timeRunning = 0;
      }
   }

   public void resetTimeRunning() {
      this.timeRunning = 0;
   }

   public void update() {
      if (this.isRunning) {
         if (this.timeRunning < this.duration && this.timeRunning > 0) {
            this.timeRunning = this.timeRunning + this.runDirection;
         } else {
            if (this.timeRunning >= this.duration) {
               this.timeRunning = this.duration;
            } else if (this.timeRunning <= 0) {
               this.timeRunning = 0;
            }

            this.timeIdle = 0;
            this.isRunning = false;
         }
      } else if (this.isOperator) {
         if (this.timeIdle < this.minIdleTime) {
            this.timeIdle++;
         } else if (this.rand.nextInt(this.startProbability) == 0) {
            this.start();
            this.entity.level().broadcastEntityEvent(this.entity, (byte)(this.entity.getOffsetEntityState() + this.id));
         }
      }
   }

   public void start() {
      this.runDirection = -this.runDirection;
      this.timeRunning = this.timeRunning + this.runDirection;
      this.isRunning = true;
   }

   public void stop() {
      if (this.timeRunning > 0) {
         this.timeRunning--;
      } else {
         this.timeRunning = 0;
         this.isRunning = false;
         this.timeIdle = 0;
         this.runDirection = 1;
      }
   }

   public void stop(int timeDelta) {
      if (this.timeRunning - timeDelta > 0) {
         this.timeRunning -= timeDelta;
      } else {
         this.timeRunning = 0;
         this.isRunning = false;
         this.timeIdle = 0;
         this.runDirection = 1;
      }
   }

   public float getAnimationFraction() {
      return (float)this.timeRunning / this.duration;
   }

   public float getAnimationProgressSmooth() {
      if (this.timeRunning > 0) {
         return this.timeRunning < this.duration ? (float)(1.0 / (1.0 + Math.exp(4.0 - 8.0 * this.getAnimationFraction()))) : 1.0F;
      } else {
         return 0.0F;
      }
   }

   public float getAnimationProgressSteep() {
      return (float)(1.0 / (1.0 + Math.exp(6.0 - 12.0 * this.getAnimationFraction())));
   }

   public float getAnimationProgressSin() {
      return Mth.sin((float) (Math.PI / 2) * this.getAnimationFraction());
   }

   public float getAnimationProgressSinSqrt() {
      float result = Mth.sin((float) (Math.PI / 2) * this.getAnimationFraction());
      return result * result;
   }

   public float getAnimationProgressSinToTen() {
      return (float)Math.pow(Mth.sin((float) (Math.PI / 2) * this.getAnimationFraction()), 10.0);
   }

   public float getAnimationProgressSinToTenWithoutReturn() {
      return this.runDirection == -1
         ? Mth.sin((float) (Math.PI / 2) * this.getAnimationFraction()) * Mth.sin((float) (Math.PI / 2) * this.getAnimationFraction())
         : (float)Math.pow(Mth.sin((float) (Math.PI / 2) * this.getAnimationFraction()), 10.0);
   }

   public float getAnimationProgressSinPowerOf(int i) {
      return (float)Math.pow(Mth.sin((float) (Math.PI / 2) * this.getAnimationFraction()), i);
   }

   public float getAnimationProgressPoly2() {
      float x = this.getAnimationFraction();
      float x2 = x * x;
      return x2 / (x2 + (1.0F - x) * (1.0F - x));
   }

   public float getAnimationProgressPoly3() {
      float x = this.getAnimationFraction();
      float x3 = x * x * x;
      return x3 / (x3 + (1.0F - x) * (1.0F - x) * (1.0F - x));
   }

   public float getAnimationProgressPolyN(int n) {
      double x = this.timeRunning / this.duration;
      double xi = Math.pow(x, n);
      return (float)(xi / (xi + Math.pow(1.0 - x, n)));
   }

   public float getAnimationProgressArcTan() {
      return (float)(0.5 + 0.4980651F * Math.atan(3.14159265359 * (this.timeRunning / this.duration - 0.5)));
   }

   public float getAnimationProgressTemporary() {
      float x = (float) (Math.PI * 2) * this.getAnimationFraction();
      return 0.5F - 0.5F * Mth.cos(x + Mth.sin(x));
   }

   public float getAnimationProgressTemporaryFS() {
      float x = (float) Math.PI * this.getAnimationFraction();
      return Mth.sin(x + Mth.sin(x));
   }

   public float getAnimationProgressTemporaryInvesed() {
      float x = (float) (Math.PI * 2) * this.getAnimationFraction();
      return 0.5F + 0.5F * Mth.cos(x + Mth.sin(x));
   }
}
