package com.gametechbc.traveloptics.effects;

import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.util.TOParticleHelper;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.List;
import java.util.Random;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PsychicControlEffect extends MobEffect {
   private static final Random RANDOM = new Random();

   public PsychicControlEffect() {
      super(MobEffectCategory.HARMFUL, 2744299);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity instanceof Mob mob) {
         if (this.getEffectSource(entity) instanceof LivingEntity livingCaster) {
            if (mob.getTarget() == livingCaster) {
               mob.setTarget(null);
            }

            LivingEntity newTarget = this.findNewTarget(entity, livingCaster);
            if (newTarget != null) {
               mob.setTarget(newTarget);
            }
         }

         for (int i = 0; i < 5; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            MagicManager.spawnParticles(
               entity.level(),
               TOParticleHelper.PSYCHIC_CURRENT,
               entity.getX() + offsetX,
               entity.getY() + offsetY,
               entity.getZ() + offsetZ,
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 10 == 0;
   }

   private LivingEntity findNewTarget(LivingEntity affectedEntity, LivingEntity caster) {
      Level level = affectedEntity.level();
      double range = 12.0;
      List<LivingEntity> possibleTargets = level.getEntitiesOfClass(
         LivingEntity.class,
         caster.getBoundingBox().inflate(range),
         entity -> entity != caster && entity != affectedEntity && entity.isAlive() && !(entity instanceof Player)
      );
      return !possibleTargets.isEmpty() ? possibleTargets.get(RANDOM.nextInt(possibleTargets.size())) : null;
   }

   private Entity getEffectSource(LivingEntity entity) {
      return entity.getEffect(TOEffects.PSYCHIC_CONTROL) != null ? entity : null;
   }
}
