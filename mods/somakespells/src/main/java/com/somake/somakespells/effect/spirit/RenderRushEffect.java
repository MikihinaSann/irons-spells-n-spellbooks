package com.somake.somakespells.effect.spirit;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.symmetry.RenderRushSpell;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class RenderRushEffect extends MagicMobEffect {
   public RenderRushEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      if (livingEntity.level() instanceof ServerLevel serverLevel) {
         float yaw = (float)Math.toRadians(-livingEntity.getYRot());
         float pitch = (float)Math.toRadians(-livingEntity.getXRot());
         serverLevel.sendParticles(
            new RingParticleOptions(yaw, pitch, 20, 86, 236, 204, 1.0F, 20.0F, false, 0),
            livingEntity.getX(),
            livingEntity.getY() + 1.0,
            livingEntity.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
      }

      List<Entity> list = livingEntity.level().getEntities(livingEntity, livingEntity.getBoundingBox().inflate(0.5, 0.5, 0.5));
      if (!list.isEmpty()) {
         for (Entity entity : list) {
            if (entity instanceof LivingEntity && entity != livingEntity && !entity.isAlliedTo(livingEntity)) {
               entity.hurt(((RenderRushSpell)ModSpells.RENDER_RUSH.get()).getDamageSource(livingEntity), 5.0F + amplifier);
            }
         }
      } else if (livingEntity.horizontalCollision) {
         return false;
      }

      livingEntity.fallDistance = 0.0F;
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return true;
   }

   public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
      super.onEffectAdded(pLivingEntity, pAmplifier);
   }

   public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
      super.onEffectRemoved(pLivingEntity, pAmplifier);
   }
}
