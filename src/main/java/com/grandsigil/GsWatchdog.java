package com.grandsigil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GsWatchdog {
   private static final long BUDGET = 40000000L;
   private static final long COOLDOWN = 5000000000L;
   private static final Map<String, Long> LAST = new ConcurrentHashMap<>();

   private GsWatchdog() {
   }

   public static long start() {
      return System.nanoTime();
   }

   public static void end(String subject, long started) {
      long spent = System.nanoTime() - started;
      if (spent >= 40000000L) {
         long now = System.nanoTime();
         Long last = LAST.get(subject);
         if (last == null || now - last >= 5000000000L) {
            LAST.put(subject, now);
            GrandSigil.LOGGER.warn("STALL {} took {} ms on the {}", new Object[]{subject, spent / 1000000L, Thread.currentThread().getName()});
         }
      }
   }
}
