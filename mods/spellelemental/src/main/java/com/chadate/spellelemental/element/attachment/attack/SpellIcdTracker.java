package com.chadate.spellelemental.element.attachment.attack;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public final class SpellIcdTracker {
   private static final Map<SpellIcdTracker.Key, SpellIcdTracker.State> STATES = new ConcurrentHashMap<>();

   private SpellIcdTracker() {
   }

   public static boolean allowAndRecord(Entity attacker, Entity target, ResourceLocation spellId, long currentTick, int step, int timeWindowTicks) {
      int attackerId = attacker == null ? -1 : attacker.getId();
      int targetId = target == null ? -1 : target.getId();
      SpellIcdTracker.Key key = new SpellIcdTracker.Key(attackerId, targetId, spellId);
      SpellIcdTracker.State state = STATES.computeIfAbsent(key, k -> new SpellIcdTracker.State());
      int hits = state.hitsSinceLastApply.incrementAndGet();
      int s = Math.max(1, step);
      boolean hitGate = (hits - 1) % s == 0;
      boolean timeGate = state.lastApplyTick == Long.MIN_VALUE || currentTick - state.lastApplyTick >= Math.max(0, timeWindowTicks);
      boolean allow = hitGate || timeGate;
      if (allow) {
         state.lastApplyTick = currentTick;
      }

      return allow;
   }

   private static final class Key {
      private final int attackerId;
      private final int targetId;
      private final String spellKey;

      Key(int attackerId, int targetId, ResourceLocation spellId) {
         this.attackerId = attackerId;
         this.targetId = targetId;
         this.spellKey = spellId == null ? "" : spellId.toString();
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return !(o instanceof SpellIcdTracker.Key key)
               ? false
               : this.attackerId == key.attackerId && this.targetId == key.targetId && Objects.equals(this.spellKey, key.spellKey);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.attackerId, this.targetId, this.spellKey);
      }
   }

   private static final class State {
      final AtomicInteger hitsSinceLastApply = new AtomicInteger(0);
      volatile long lastApplyTick = Long.MIN_VALUE;
   }
}
