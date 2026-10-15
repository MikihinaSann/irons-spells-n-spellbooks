package net.acetheeldritchking.cataclysm_spellbooks.effects.potion;

import com.github.L_Ender.cataclysm.client.particle.Options.StormParticleOptions;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import java.util.Set;
import net.acetheeldritchking.cataclysm_spellbooks.CataclysmSpellbooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.common.EffectCure;

public class KingsWrathPotionEffect extends MagicMobEffect {
   public static final float ATTACK_DAMAGE_PER_WRATH = 0.2F;
   public static final float SPELL_POWER_PER_WRATH = 0.05F;

   public KingsWrathPotionEffect() {
      super(MobEffectCategory.BENEFICIAL, 4583645);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, CataclysmSpellbooks.id("kings_wrath_effect"), 0.2F, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(AttributeRegistry.SPELL_POWER, CataclysmSpellbooks.id("kings_wrath_effect"), 0.05F, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (!entity.level().isClientSide) {
         ServerLevel world = (ServerLevel)entity.level();
         float random = 0.04F;
         float r = 0.89F + entity.getRandom().nextFloat() * random;
         float g = 0.85F + entity.getRandom().nextFloat() * random;
         float b = 0.69F + entity.getRandom().nextFloat() * random * 1.5F;
         MagicManager.spawnParticles(
            world,
            new StormParticleOptions(r, g, b, 2.75F + entity.getRandom().nextFloat() * 0.6F, 3.75F + entity.getRandom().nextFloat() * 0.6F, entity.getId()),
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            1.0,
            true
         );
         MagicManager.spawnParticles(
            world,
            new StormParticleOptions(r, g, b, 2.5F + entity.getRandom().nextFloat() * 0.45F, 3.0F + entity.getRandom().nextFloat() * 0.45F, entity.getId()),
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            1.0,
            true
         );
         MagicManager.spawnParticles(
            world,
            new StormParticleOptions(r, g, b, 2.25F + entity.getRandom().nextFloat() * 0.45F, 2.25F + entity.getRandom().nextFloat() * 0.45F, entity.getId()),
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            1.0,
            true
         );
         MagicManager.spawnParticles(
            world,
            new StormParticleOptions(r, g, b, 1.25F + entity.getRandom().nextFloat() * 0.45F, 1.25F + entity.getRandom().nextFloat() * 0.45F, entity.getId()),
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            1.0,
            true
         );
      }

      return super.applyEffectTick(entity, pAmplifier);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 2 == 0;
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
   }
}
