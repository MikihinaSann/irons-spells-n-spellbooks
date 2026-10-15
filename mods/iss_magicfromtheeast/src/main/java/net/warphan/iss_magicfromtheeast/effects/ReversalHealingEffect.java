package net.warphan.iss_magicfromtheeast.effects;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.warphan.iss_magicfromtheeast.datagen.MFTEDamageTypeTagGenerator;
import net.warphan.iss_magicfromtheeast.registries.MFTEEffectRegistries;

@EventBusSubscriber
public class ReversalHealingEffect extends MagicMobEffect {
   public ReversalHealingEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   @SubscribeEvent
   public static void healWhenBeingHit(Pre event) {
      LivingEntity entity = event.getEntity();
      DamageSource damageSource = event.getSource();
      float damageAmount = event.getOriginalDamage();
      Level level = entity.level;
      if (!entity.level.isClientSide
         && !damageSource.is(MFTEDamageTypeTagGenerator.BYPASS_REVERSAL_HEALING)
         && !damageSource.is(DamageTypeTags.IS_FALL)
         && !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         MobEffectInstance effect = entity.getEffect(MFTEEffectRegistries.REVERSAL_HEALING);
         if (effect != null) {
            event.setNewDamage(0.0F);
            entity.heal(damageAmount * healingAmplifier(effect.getAmplifier()));
            Vec3 vec = entity.getBoundingBox().getCenter();
            if (!level.isClientSide) {
               MagicManager.spawnParticles(level, ParticleTypes.SCRAPE, vec.x, vec.y, vec.z, 10, 1.2, 0.8, 1.2, 0.4, true);
            }
         }
      }
   }

   public static float healingAmplifier(int amplifier) {
      return (amplifier + 1) * 0.1F;
   }
}
