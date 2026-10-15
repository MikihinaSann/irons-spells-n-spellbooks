package com.chadate.spellelemental.event;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.Entity;

public class CriticalHitTracker {
   private static final Map<Entity, Long> CRITICAL_HITS = new WeakHashMap<>();
   private static final long VALIDITY_DURATION = 50L;

   public static void markCritical(Entity target) {
      CRITICAL_HITS.put(target, System.currentTimeMillis());
   }

   public static boolean checkAndConsumeCritical(Entity target) {
      Long timestamp = CRITICAL_HITS.remove(target);
      if (timestamp == null) {
         return false;
      } else {
         long currentTime = System.currentTimeMillis();
         return currentTime - timestamp <= 50L;
      }
   }

   public static void cleanupExpired() {
      long currentTime = System.currentTimeMillis();
      CRITICAL_HITS.entrySet().removeIf(entry -> currentTime - entry.getValue() > 50L);
   }
}
