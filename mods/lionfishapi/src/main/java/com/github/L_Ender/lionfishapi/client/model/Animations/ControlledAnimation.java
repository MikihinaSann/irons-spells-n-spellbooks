package com.github.L_Ender.lionfishapi.client.model.Animations;

import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControlledAnimation {
   private int timer = 0;
   private int duration;
   private int timerChange;

   public ControlledAnimation(int d) {
      this.duration = d;
   }

   public void setDuration(int d) {
      this.timer = 0;
      this.duration = d;
   }

   public int getTimer() {
      return this.timer;
   }

   public void setTimer(int time) {
      this.timer = time;
      if (this.timer > this.duration) {
         this.timer = this.duration;
      } else if (this.timer < 0) {
         this.timer = 0;
      }
   }

   public void resetTimer() {
      this.timer = 0;
   }

   public void increaseTimer() {
      if (this.timer < this.duration) {
         this.timer++;
         this.timerChange = 1;
      }
   }

   public boolean canIncreaseTimer() {
      return this.timer < this.duration;
   }

   public void increaseTimer(int time) {
      int newTime = this.timer + time;
      if (newTime <= this.duration && newTime >= 0) {
         this.timer = newTime;
      } else {
         this.timer = newTime < 0 ? 0 : this.duration;
      }
   }

   public void decreaseTimer() {
      if (this.timer > 0.0) {
         this.timer--;
         this.timerChange = -1;
      }
   }

   public boolean canDecreaseTimer() {
      return this.timer > 0.0;
   }

   public void decreaseTimer(int time) {
      if (this.timer - time > 0.0) {
         this.timer -= time;
      } else {
         this.timer = 0;
      }
   }

   public float getAnimationFraction() {
      return (float)this.timer / this.duration;
   }

   public float getAnimationProgressSmooth() {
      if (this.timer > 0.0) {
         return this.timer < this.duration ? (float)(1.0 / (1.0 + Math.exp(4.0 - 8.0 * this.getAnimationFraction()))) : 1.0F;
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
      return this.timerChange == -1
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
      double x = this.getAnimationFraction();
      double xi = Math.pow(x, n);
      return (float)(xi / (xi + Math.pow(1.0 - x, n)));
   }

   public float getAnimationProgressArcTan() {
      return (float)(0.5 + 0.4980651F * Math.atan(3.14159265359 * (this.getAnimationFraction() - 0.5)));
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
