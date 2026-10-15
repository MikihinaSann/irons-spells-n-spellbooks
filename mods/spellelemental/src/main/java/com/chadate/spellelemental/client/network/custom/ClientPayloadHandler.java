package com.chadate.spellelemental.client.network.custom;

import com.chadate.spellelemental.data.ElementContainerAttachment;
import com.chadate.spellelemental.data.SpellAttachments;
import com.chadate.spellelemental.network.ElementData;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
   public static void handleDataOnNetwork(ElementData data, IPayloadContext context) {
      String element = data.elementMessage;
      int entityId = data.entityIdMessage;
      int elementDuration = data.durationMessage;
      context.enqueueWork(() -> {
         Entity entity = context.player().level().getEntity(entityId);
         if (entity != null) {
            ElementContainerAttachment container = (ElementContainerAttachment)entity.getData(SpellAttachments.ELEMENTS_CONTAINER);
            if (elementDuration == 0) {
               container.remove(element);
               ClientPayloadHandler.DisplayCache.remove(entityId, element);
            } else {
               container.setValue(element, elementDuration);
               long now = context.player().level().getGameTime();
               ClientPayloadHandler.DisplayCache.update(entityId, element, elementDuration, now);
            }
         }
      });
   }

   public static void handleSnapshotOnNetwork(ElementData.ElementSnapshot snapshot, IPayloadContext context) {
      context.enqueueWork(() -> {
         Entity entity = context.player().level().getEntity(snapshot.entityId);
         if (entity != null) {
            ElementContainerAttachment container = (ElementContainerAttachment)entity.getData(SpellAttachments.ELEMENTS_CONTAINER);
            container.snapshot().keySet().forEach(container::remove);
            ClientPayloadHandler.DisplayCache.clearEntity(snapshot.entityId);
            long now = context.player().level().getGameTime();

            for (int i = 0; i < snapshot.keys.length; i++) {
               int v = snapshot.values[i];
               if (v > 0) {
                  container.setValue(snapshot.keys[i], v);
                  ClientPayloadHandler.DisplayCache.update(snapshot.entityId, snapshot.keys[i], v, now);
               } else {
                  container.remove(snapshot.keys[i]);
               }
            }
         }
      });
   }

   public static void handleDebugToggleOnNetwork(ElementData.ElementDebugToggle payload, IPayloadContext context) {
      context.enqueueWork(() -> ClientPayloadHandler.DebugState.enabled = payload.enabled());
   }

   public static void handleInspectResponseOnNetwork(ElementData.ElementInspectResponse resp, IPayloadContext context) {
      context.enqueueWork(() -> {
         ClientPayloadHandler.InspectCache.entityId = resp.entityId;
         ClientPayloadHandler.InspectCache.data.clear();

         for (int i = 0; i < resp.keys.length; i++) {
            ClientPayloadHandler.InspectCache.data.put(resp.keys[i], resp.values[i]);
         }

         ClientPayloadHandler.InspectCache.lastUpdateMs = System.currentTimeMillis();
      });
   }

   public static final class DebugState {
      public static boolean enabled = false;
   }

   public static final class DisplayCache {
      private static final Map<Integer, Map<String, ClientPayloadHandler.DisplayCache.Status>> CACHE = new HashMap<>();

      public static void update(int entityId, String key, int duration, long gameTime) {
         CACHE.computeIfAbsent(entityId, k -> new HashMap<>()).put(key, new ClientPayloadHandler.DisplayCache.Status(duration, gameTime));
      }

      public static void remove(int entityId, String key) {
         Map<String, ClientPayloadHandler.DisplayCache.Status> m = CACHE.get(entityId);
         if (m != null) {
            m.remove(key);
         }
      }

      public static void clearEntity(int entityId) {
         CACHE.remove(entityId);
      }

      public static int predictRemaining(int entityId, String key) {
         Map<String, ClientPayloadHandler.DisplayCache.Status> m = CACHE.get(entityId);
         if (m == null) {
            return -1;
         } else {
            ClientPayloadHandler.DisplayCache.Status s = m.get(key);
            if (s == null) {
               return -1;
            } else {
               long now = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : s.atGameTime;
               long elapsed = Math.max(0L, now - s.atGameTime);
               long remain = s.startDuration - elapsed;
               return (int)Math.max(0L, remain);
            }
         }
      }

      private record Status(int startDuration, long atGameTime) {
      }
   }

   public static final class InspectCache {
      public static int entityId = -1;
      public static final Map<String, Integer> data = new HashMap<>();
      public static long lastUpdateMs = 0L;
   }
}
