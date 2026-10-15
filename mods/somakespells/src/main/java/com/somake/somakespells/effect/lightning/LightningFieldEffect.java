package com.somake.somakespells.effect.lightning;

import com.somake.somakespells.registries.ModMobEffects;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class LightningFieldEffect extends MagicMobEffect {
   public static final float RADIUS = 6.0F;

   public LightningFieldEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 10 == 0;
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      List<LivingEntity> targets = livingEntity.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            livingEntity.getBoundingBox().inflate(6.0),
            targetx -> targetx != livingEntity && targetx.distanceToSqr(livingEntity) < 36.0 && !targetx.isSpectator()
         );
      float damage = (amplifier + 1) * 2;

      for (LivingEntity target : targets) {
         DamageSources.applyDamage(target, damage, livingEntity.damageSources().lightningBolt());
         target.addEffect(new MobEffectInstance(ModMobEffects.LIGHTNING_STUN, 30, 0));
         if (livingEntity.level() instanceof ServerLevel serverLevel) {
            Vec3 start = livingEntity.getBoundingBox().getCenter();
            Vec3 end = target.getBoundingBox().getCenter();
            serverLevel.sendParticles(new ZapParticleOption(end), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
         }
      }

      return true;
   }
}
