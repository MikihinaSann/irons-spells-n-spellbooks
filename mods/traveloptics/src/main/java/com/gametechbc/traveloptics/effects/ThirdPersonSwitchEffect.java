package com.gametechbc.traveloptics.effects;

import com.gametechbc.traveloptics.init.TOEffects;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class ThirdPersonSwitchEffect extends MobEffect {
   public ThirdPersonSwitchEffect() {
      super(MobEffectCategory.NEUTRAL, 0);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (FMLEnvironment.dist == Dist.CLIENT && entity.level().isClientSide() && entity == Minecraft.getInstance().player) {
         int duration = entity.getEffect(TOEffects.THIRD_PERSON_SWITCH).getDuration();
         if (duration > 2) {
            Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
         }

         if (duration <= 2) {
            Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
         }
      }

      super.applyEffectTick(entity, amplifier);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
