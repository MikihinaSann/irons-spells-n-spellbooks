package net.alshanex.tunes_n_tomes.util;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationController.AnimationStateHandler;

public class ClampedAnimationController<T extends GeoAnimatable> extends AnimationController<T> {
   private final double loopStartTick;
   private final double loopEndTick;
   private boolean hasPlayedIntro = false;

   public ClampedAnimationController(
      T animatable, String name, int transitionTickTime, double loopStartSeconds, double loopEndSeconds, AnimationStateHandler<T> handler
   ) {
      super(animatable, name, transitionTickTime, handler);
      this.loopStartTick = loopStartSeconds * 20.0;
      this.loopEndTick = loopEndSeconds * 20.0;
   }

   protected double adjustTick(double tick) {
      double adjusted = super.adjustTick(tick);
      if (adjusted >= this.loopEndTick) {
         this.hasPlayedIntro = true;
      }

      if (this.hasPlayedIntro && adjusted >= this.loopEndTick) {
         double loopDuration = this.loopEndTick - this.loopStartTick;
         adjusted = this.loopStartTick + (adjusted - this.loopStartTick) % loopDuration;
      }

      return adjusted;
   }
}
