package com.gametechbc.traveloptics.effects.nullflare;

import com.gametechbc.traveloptics.util.TODamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class NullflareFireEffect extends MobEffect {
   public NullflareFireEffect() {
      super(MobEffectCategory.HARMFUL, 16711680);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide() && entity.tickCount % 40 == 0) {
         Holder<DamageType> damageTypeHolder = entity.level()
            .registryAccess()
            .registryOrThrow(Registries.DAMAGE_TYPE)
            .getHolderOrThrow(TODamageTypes.NULLFLARE_FIRE);
         DamageSource damageSource = new DamageSource(damageTypeHolder);
         entity.hurt(damageSource, 1.0F + amplifier);
      }

      super.applyEffectTick(entity, amplifier);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
