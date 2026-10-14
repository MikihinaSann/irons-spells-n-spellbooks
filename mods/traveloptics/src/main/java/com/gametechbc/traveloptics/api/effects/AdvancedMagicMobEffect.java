package com.gametechbc.traveloptics.api.effects;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;

public abstract class AdvancedMagicMobEffect extends MagicMobEffect {
   protected AdvancedMagicMobEffect(MobEffectCategory category, int color) {
      super(category, color);
      NeoForge.EVENT_BUS.register(this);
   }

   protected void onEffectAdded(LivingEntity entity, MobEffectInstance newEffectInstance, MobEffectInstance oldEffectInstance, Entity source) {
   }

   protected void onEffectRemoved(LivingEntity entity, MobEffectInstance effectInstance) {
   }

   protected void onEffectExpired(LivingEntity entity, MobEffectInstance effectInstance) {
   }

   protected void onEffectRemovedOrExpired(LivingEntity entity, MobEffectInstance effectInstance) {
   }

   @SubscribeEvent
   public void handleEffectAdded(Added event) {
      if (event.getEffectInstance().getEffect() == this) {
         this.onEffectAdded(event.getEntity(), event.getEffectInstance(), event.getOldEffectInstance(), event.getEffectSource());
      }
   }

   @SubscribeEvent
   public void handleEffectRemoved(Remove event) {
      if (event.getEffect() == this) {
         LivingEntity entity = event.getEntity();
         MobEffectInstance effectInstance = event.getEffectInstance();
         this.onEffectRemoved(entity, effectInstance);
         this.onEffectExpired(entity, effectInstance);
      }
   }

   @SubscribeEvent
   public void handleEffectExpired(Expired event) {
      if (event.getEffectInstance().getEffect() == this) {
         LivingEntity entity = event.getEntity();
         MobEffectInstance effectInstance = event.getEffectInstance();
         this.onEffectExpired(entity, effectInstance);
         this.onEffectExpired(entity, effectInstance);
      }
   }
}
