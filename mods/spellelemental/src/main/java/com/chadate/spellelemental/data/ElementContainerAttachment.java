package com.chadate.spellelemental.data;

import java.util.HashMap;
import java.util.Map;

public class ElementContainerAttachment {
   private final Map<String, Integer> elementIdToValue = new HashMap<>();
   private final Map<String, Long> lastAppliedGameTime = new HashMap<>();
   private final Map<String, Integer> lastAttackerIds = new HashMap<>();

   public int getValue(String elementId) {
      return elementId == null ? 0 : this.elementIdToValue.getOrDefault(elementId.toLowerCase(), 0);
   }

   public void setValue(String elementId, int value) {
      if (elementId != null) {
         String key = elementId.toLowerCase();
         if (value <= 0) {
            this.elementIdToValue.remove(key);
         } else {
            this.elementIdToValue.put(key, value);
         }
      }
   }

   public void markApplied(String elementId, long gameTime) {
      if (elementId != null) {
         this.lastAppliedGameTime.put(elementId.toLowerCase(), gameTime);
      }
   }

   public void markAppliedWithAttacker(String elementId, long gameTime, int attackerId) {
      if (elementId != null) {
         String key = elementId.toLowerCase();
         this.lastAppliedGameTime.put(key, gameTime);
         this.lastAttackerIds.put(key, attackerId);
      }
   }

   public long getLastApplied(String elementId) {
      return elementId == null ? 0L : this.lastAppliedGameTime.getOrDefault(elementId.toLowerCase(), 0L);
   }

   public int getLastAttackerId(String elementId) {
      return elementId == null ? -1 : this.lastAttackerIds.getOrDefault(elementId.toLowerCase(), -1);
   }

   public void remove(String elementId) {
      if (elementId != null) {
         String key = elementId.toLowerCase();
         this.elementIdToValue.remove(key);
      }
   }

   public Map<String, Integer> snapshot() {
      return new HashMap<>(this.elementIdToValue);
   }

   public Map<String, Long> snapshotLastApplied() {
      return new HashMap<>(this.lastAppliedGameTime);
   }
}
