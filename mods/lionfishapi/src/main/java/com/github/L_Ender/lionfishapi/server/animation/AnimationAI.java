package com.github.L_Ender.lionfishapi.server.animation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class AnimationAI<T extends Entity & IAnimatedEntity> extends Goal {
   protected T entity;

   public AnimationAI(T entity) {
      this.entity = entity;
   }

   public abstract Animation getAnimation();

   public boolean isAutomatic() {
      return false;
   }

   public boolean shouldAnimate() {
      return false;
   }

   public boolean canUse() {
      return this.isAutomatic() ? this.entity.getAnimation() == this.getAnimation() : this.shouldAnimate();
   }

   public void start() {
      if (!this.isAutomatic()) {
         AnimationHandler.INSTANCE.sendAnimationMessage(this.entity, this.getAnimation());
      }

      this.entity.setAnimationTick(0);
   }

   public boolean canContinueToUse() {
      return this.entity.getAnimationTick() < this.getAnimation().getDuration();
   }

   public void stop() {
      AnimationHandler.INSTANCE.sendAnimationMessage(this.entity, IAnimatedEntity.NO_ANIMATION);
   }
}
