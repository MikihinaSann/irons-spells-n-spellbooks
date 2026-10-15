package com.somake.somakespells.event;

import com.somake.somakespells.entity.ApocalypticBurstEntity;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.fire.ApocalypticBurstSpell;
import com.somake.somakespells.util.FireElementContext;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class DamnedDemomansHandler {
   @SubscribeEvent
   public static void onLivingTick(Pre event) {
      if (event.getEntity() instanceof LivingEntity entity && !entity.level().isClientSide) {
         MobEffectInstance effectInstance = entity.getEffect(ModMobEffects.DAMNED_DEMOMANS);
         if (effectInstance != null) {
            int duration = effectInstance.getDuration();
            int amplifier = effectInstance.getAmplifier();
            float maxHealth = entity.getMaxHealth();
            float currentHealth = entity.getHealth();
            boolean lowHealth = currentHealth > 0.0F && currentHealth / maxHealth <= 0.3F;
            boolean timeout = duration == 1;
            if (timeout) {
               explode(entity, amplifier);
            } else if (lowHealth && duration > 20) {
               explode(entity, amplifier);
               entity.removeEffect(ModMobEffects.DAMNED_DEMOMANS);
            }
         }
      }
   }

   private static void explode(LivingEntity entity, int amplifier) {
      Level level = entity.level();
      float damage = (amplifier + 1) * 5.0F;
      damage *= ((ApocalypticBurstSpell)ModSpells.APOCALYPTIC_BURST.get()).getEntityPowerMultiplier(entity);
      FireElementContext context = FireElementContext.resolveForSpell(entity, (AbstractSpell)ModSpells.APOCALYPTIC_BURST.get());
      damage = context.applySupportedDamage(damage, entity);
      level.playSound(
         null,
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         SoundEvents.GENERIC_EXPLODE,
         SoundSource.PLAYERS,
         4.0F,
         (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F
      );
      ApocalypticBurstEntity burst = new ApocalypticBurstEntity(level, entity, damage);
      burst.setMaxRadius(3.0F + amplifier);
      burst.setIsSoulFire(context.isSoulFire());
      burst.setIsInfernalFire(context.isInfernalFire());
      level.addFreshEntity(burst);
   }
}
