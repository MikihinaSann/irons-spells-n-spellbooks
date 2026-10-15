package com.chadate.spellelemental.event.element;

import com.chadate.spellelemental.data.ElementContainerAttachment;
import com.chadate.spellelemental.data.SpellAttachments;
import com.chadate.spellelemental.network.ElementData;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ElementDecaySystem {
   private static final Set<LivingEntity> TRACKED = Collections.newSetFromMap(new WeakHashMap<>());

   private ElementDecaySystem() {
   }

   public static void track(LivingEntity entity) {
      if (entity != null && !entity.level().isClientSide()) {
         TRACKED.add(entity);
      }
   }

   public static void elementDecay(Post event) {
      event.getServer().overworld();
      if (!TRACKED.isEmpty()) {
         TRACKED.removeIf(entityx -> entityx == null || !entityx.isAlive() || entityx.level().isClientSide());

         for (LivingEntity entity : TRACKED) {
            ElementContainerAttachment container = (ElementContainerAttachment)entity.getData(SpellAttachments.ELEMENTS_CONTAINER);
            Map<String, Integer> snap = container.snapshot();
            if (!snap.isEmpty()) {
               for (Entry<String, Integer> e : snap.entrySet()) {
                  String key = e.getKey();
                  int value = e.getValue();
                  if (value > 0) {
                     container.setValue(key, --value);
                     PacketDistributor.sendToAllPlayers(new ElementData(entity.getId(), key, value), new CustomPacketPayload[0]);
                  }
               }
            }
         }
      }
   }
}
