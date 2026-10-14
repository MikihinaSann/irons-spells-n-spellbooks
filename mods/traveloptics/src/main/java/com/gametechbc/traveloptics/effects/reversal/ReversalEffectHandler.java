package com.gametechbc.traveloptics.effects.reversal;

import com.gametechbc.traveloptics.init.TOEffects;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

@EventBusSubscriber(
   modid = "traveloptics"
)
public class ReversalEffectHandler {
   private static final Map<UUID, Float> storedDamageMap = new HashMap<>();
   private static final Map<UUID, Projectile> lastProjectileHitMap = new HashMap<>();

   public ReversalEffectHandler() {
      super();
   }

   @SubscribeEvent
   public static void onLivingDamagePre(Pre event) {
      LivingEntity entity = event.getEntity();
      if (entity.hasEffect(TOEffects.REVERSAL)) {
         float damage = event.getNewDamage();
         storeDamage(entity, damage);
         if (event.getSource().getDirectEntity() instanceof Projectile projectile) {
            storeLastProjectileHit(entity, projectile);
            entity.removeEffect(TOEffects.REVERSAL);
         }

         entity.setDeltaMovement(0.0, 0.0, 0.0);
         entity.hurtMarked = false;
         entity.setNoActionTime(10);
         event.setNewDamage(0.0F);
      }
   }

   @SubscribeEvent
   public static void onLivingKnockBack(LivingKnockBackEvent event) {
      LivingEntity entity = event.getEntity();
      if (entity.hasEffect(TOEffects.REVERSAL)) {
         event.setCanceled(true);
      }
   }

   public static void storeDamage(LivingEntity entity, float damage) {
      storedDamageMap.put(entity.getUUID(), storedDamageMap.getOrDefault(entity.getUUID(), 0.0F) + damage);
   }

   public static float getStoredDamage(LivingEntity entity) {
      return storedDamageMap.getOrDefault(entity.getUUID(), 0.0F);
   }

   public static void clearStoredDamage(LivingEntity entity) {
      storedDamageMap.remove(entity.getUUID());
   }

   public static void storeLastProjectileHit(LivingEntity entity, Projectile projectile) {
      lastProjectileHitMap.put(entity.getUUID(), projectile);
   }

   public static Projectile getLastProjectileHit(LivingEntity entity) {
      return lastProjectileHitMap.get(entity.getUUID());
   }

   public static void clearLastProjectileHit(LivingEntity entity) {
      lastProjectileHitMap.remove(entity.getUUID());
   }
}
