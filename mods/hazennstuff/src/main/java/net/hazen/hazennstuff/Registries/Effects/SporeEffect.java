package net.hazen.hazennstuff.Registries.Effects;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.hazen.hazennstuff.Registries.HnSDamageTypes;
import net.hazen.hazennstuff.Registries.HnSParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SporeEffect extends MagicMobEffect implements ISyncedMobEffect {
   public SporeEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      DamageSource source = entity.damageSources().source(HnSDamageTypes.SPORES);
      entity.hurt(source, 1.0F + amplifier);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      int interval = 20 >> amplifier;
      return interval > 0 ? duration % interval == 0 : true;
   }

   public void clientTick(LivingEntity livingEntity, MobEffectInstance instance) {
      RandomSource random = livingEntity.getRandom();
      ParticleOptions particle = HnSParticleHelper.MUSHROOM_PARTICLE;

      for (int i = 0; i < 1; i++) {
         Vec3 motion = new Vec3(random.nextFloat() * 2.0F - 1.0F, random.nextFloat() * 2.0F - 1.0F, random.nextFloat() * 2.0F - 1.0F).scale(0.04F);
         livingEntity.level()
            .addParticle(particle, livingEntity.getRandomX(0.4F), livingEntity.getRandomY(), livingEntity.getRandomZ(0.4F), motion.x, motion.y, motion.z);
      }
   }
}
