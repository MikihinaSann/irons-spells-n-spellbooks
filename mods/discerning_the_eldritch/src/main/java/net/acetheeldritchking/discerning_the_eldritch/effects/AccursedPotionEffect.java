package net.acetheeldritchking.discerning_the_eldritch.effects;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.acetheeldritchking.discerning_the_eldritch.particle.DTEParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class AccursedPotionEffect extends MobEffect {
   public AccursedPotionEffect() {
      super(MobEffectCategory.HARMFUL, 11749198);
      this.addAttributeModifier(AttributeRegistry.SPELL_RESIST, DiscerningTheEldritch.id("accursed_potion_effect"), -0.15F, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(Attributes.ARMOR, DiscerningTheEldritch.id("accursed_potion_effect"), -5.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, DiscerningTheEldritch.id("accursed_potion_effect"), -0.25, Operation.ADD_MULTIPLIED_BASE);
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      if (!livingEntity.level().isClientSide) {
         ServerLevel world = (ServerLevel)livingEntity.level();
         world.sendParticles(
            DTEParticleHelper.MALIGNANT_SOUL,
            livingEntity.getX(),
            livingEntity.getY(0.5),
            livingEntity.getZ(),
            1,
            world.random.nextGaussian() * 0.007,
            world.random.nextGaussian() * 0.007,
            world.random.nextGaussian() * 0.007,
            0.02
         );
         world.sendParticles(
            DTEParticleHelper.MALIGNANT_FLAME,
            livingEntity.getX(),
            livingEntity.getY(0.5),
            livingEntity.getZ(),
            2,
            world.random.nextGaussian() * 0.007,
            world.random.nextGaussian() * 0.007,
            world.random.nextGaussian() * 0.007,
            0.05
         );
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 20 == 0;
   }
}
