package net.acetheeldritchking.cataclysm_spellbooks.effects.potion;

import com.github.L_Ender.cataclysm.init.ModParticle;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import java.util.Set;
import net.acetheeldritchking.cataclysm_spellbooks.CataclysmSpellbooks;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.common.EffectCure;

public class WrathfulPotionEffect extends MagicMobEffect {
   public static final float ATTACK_DAMAGE_PER_WRATH = 0.2F;

   public WrathfulPotionEffect() {
      super(MobEffectCategory.BENEFICIAL, 4583645);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, CataclysmSpellbooks.id("wrathful_effect"), 0.2F, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
      if (!pLivingEntity.level().isClientSide) {
         ServerLevel world = (ServerLevel)pLivingEntity.level();
         world.sendParticles(
            (SimpleParticleType)ModParticle.PHANTOM_WING_FLAME.get(),
            pLivingEntity.getX(),
            pLivingEntity.getY(0.5),
            pLivingEntity.getZ(),
            25,
            world.random.nextGaussian() * 0.007,
            world.random.nextGaussian() * 0.007,
            world.random.nextGaussian() * 0.007,
            0.05
         );
      }

      return super.applyEffectTick(pLivingEntity, pAmplifier);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 20 == 0;
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
   }
}
