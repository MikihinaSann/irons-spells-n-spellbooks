package net.acetheeldritchking.discerning_the_eldritch.effects;

import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

@EventBusSubscriber
public class ScorchedSoulPotionEffect extends MobEffect {
   public ScorchedSoulPotionEffect() {
      super(MobEffectCategory.HARMFUL, 2598645);
   }

   @SubscribeEvent
   public static void livingDamageEventPre(Pre event) {
      LivingEntity entity = event.getEntity();
      DamageSource source = event.getSource();
      Entity attacker = event.getSource().getEntity();
      if (attacker != null
         && attacker.getWeaponItem() != null
         && attacker.getWeaponItem().is((Item)ItemRegistries.SOUL_FIRE_SCYTHE.get())
         && (source.is(ISSDamageTypes.FIRE_MAGIC) || entity.isOnFire())
         && entity.hasEffect(DTEPotionEffectRegistry.SCORCHED_SOUL_EFFECT)) {
         float baseDamage = event.getOriginalDamage();
         float newDamage = baseDamage * 1.5F;
         event.setNewDamage(newDamage);
      }
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      if (!livingEntity.level().isClientSide()) {
         ServerLevel world = (ServerLevel)livingEntity.level();
         world.sendParticles(
            ParticleTypes.SOUL_FIRE_FLAME,
            livingEntity.getX(),
            livingEntity.getY(0.5),
            livingEntity.getZ(),
            15,
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
