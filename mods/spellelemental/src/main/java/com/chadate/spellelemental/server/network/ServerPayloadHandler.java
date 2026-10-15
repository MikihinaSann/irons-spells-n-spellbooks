package com.chadate.spellelemental.server.network;

import com.chadate.spellelemental.data.ElementContainerAttachment;
import com.chadate.spellelemental.data.SpellAttachments;
import com.chadate.spellelemental.network.ElementData;
import java.util.Map;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ServerPayloadHandler {
   private ServerPayloadHandler() {
   }

   public static void handleInspectRequest(ElementData.ElementInspectRequest payload, IPayloadContext context) {
      context.enqueueWork(
         () -> {
            if (context.player().level().getEntity(payload.entityId()) instanceof LivingEntity living) {
               ElementContainerAttachment container = (ElementContainerAttachment)living.getData(SpellAttachments.ELEMENTS_CONTAINER);
               Map<String, Integer> snap = container.snapshot();
               String[] keys = snap.keySet().toArray(new String[0]);
               int[] values = new int[keys.length];

               for (int i = 0; i < keys.length; i++) {
                  values[i] = snap.get(keys[i]);
               }

               PacketDistributor.sendToPlayer(
                  (ServerPlayer)context.player(), new ElementData.ElementInspectResponse(living.getId(), keys, values), new CustomPacketPayload[0]
               );
            }
         }
      );
   }
}
