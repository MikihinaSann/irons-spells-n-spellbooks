package com.grandsigil.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

final class Restraint {
   private Restraint() {
   }

   static void pin(LivingEntity target) {
      Vec3 motion = target.getDeltaMovement();
      if (motion.x != 0.0 || motion.z != 0.0 || motion.y > 0.0) {
         target.setDeltaMovement(0.0, Math.min(motion.y, 0.0), 0.0);
      }

      target.setJumping(false);
      target.setSprinting(false);
      if (target instanceof Mob mob) {
         mob.getNavigation().stop();
      }
   }
}
