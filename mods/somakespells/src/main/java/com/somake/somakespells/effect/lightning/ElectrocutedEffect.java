package com.somake.somakespells.effect.lightning;

import com.somake.somakespells.registries.ModAttributes;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class ElectrocutedEffect extends MagicMobEffect {
   public ElectrocutedEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
      this.addAttributeModifier(
         ModAttributes.AQUA_MAGIC_RESIST, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.electrocuted.aqua_resist"), -0.05, Operation.ADD_VALUE
      );
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 20 == 0;
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      float damage = (amplifier + 1) * 2;
      DamageSources.applyDamage(livingEntity, damage, livingEntity.damageSources().lightningBolt());
      if (livingEntity.level() instanceof ServerLevel serverLevel) {
         double radius = livingEntity.getBbWidth() * 0.5;
         double height = livingEntity.getBbHeight();

         for (int i = 0; i < 3; i++) {
            double x = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * radius * 2.0;
            double y = livingEntity.getY() + livingEntity.getRandom().nextDouble() * height;
            double z = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * radius * 2.0;
            serverLevel.sendParticles(ParticleHelper.ELECTRICITY, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
         }
      }

      return true;
   }
}
