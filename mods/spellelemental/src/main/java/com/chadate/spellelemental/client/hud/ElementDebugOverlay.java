package com.chadate.spellelemental.client.hud;

import com.chadate.spellelemental.client.network.custom.ClientPayloadHandler;
import com.chadate.spellelemental.network.ElementData;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "spellelemental",
   value = {Dist.CLIENT}
)
public class ElementDebugOverlay {
   @SubscribeEvent
   public static void onRenderGui(Post event) {
      if (ClientPayloadHandler.DebugState.enabled) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null && mc.level != null) {
            Entity target = mc.crosshairPickEntity;
            if (target != null) {
               PacketDistributor.sendToServer(new ElementData.ElementInspectRequest(target.getId()), new CustomPacketPayload[0]);
               if (ClientPayloadHandler.InspectCache.entityId == target.getId()) {
                  Map<String, Integer> snap = ClientPayloadHandler.InspectCache.data;
                  if (!snap.isEmpty()) {
                     GuiGraphics gg = event.getGuiGraphics();
                     int x = 6;
                     int y = 6;
                     gg.drawString(mc.font, "[Server Element Debug] EntityId: " + target.getId(), x, y, 16777215, false);
                     y += 10;

                     for (Entry<String, Integer> e : snap.entrySet()) {
                        String line = e.getKey() + ": " + e.getValue();
                        gg.drawString(mc.font, line, x, y, 8190976, false);
                        y += 10;
                     }
                  }
               }
            }
         }
      }
   }
}
