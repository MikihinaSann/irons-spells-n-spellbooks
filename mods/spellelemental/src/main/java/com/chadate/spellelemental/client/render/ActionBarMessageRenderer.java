package com.chadate.spellelemental.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent.Post;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "spellelemental"
)
public class ActionBarMessageRenderer {
   private static Component currentMessage = null;
   private static long messageStartTime = 0L;
   private static final long MESSAGE_DURATION = 3000L;

   public static void setMessage(Component message) {
      currentMessage = message;
      messageStartTime = System.currentTimeMillis();
   }

   public static void clearMessage() {
      currentMessage = null;
      messageStartTime = 0L;
   }

   @SubscribeEvent
   public static void onRenderGui(Post event) {
      if (currentMessage != null) {
         long currentTime = System.currentTimeMillis();
         if (currentTime - messageStartTime > 3000L) {
            clearMessage();
         } else {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null && minecraft.screen == null) {
               GuiGraphics guiGraphics = event.getGuiGraphics();
               int screenWidth = minecraft.getWindow().getGuiScaledWidth();
               int screenHeight = minecraft.getWindow().getGuiScaledHeight();
               int textWidth = minecraft.font.width(currentMessage);
               int x = (screenWidth - textWidth) / 2;
               int y = screenHeight - 69;
               float alpha = calculateAlpha(currentTime - messageStartTime);
               int color = (int)(255.0F * alpha) << 24 | 16777215;
               guiGraphics.drawString(minecraft.font, currentMessage, x, y, color);
            }
         }
      }
   }

   private static float calculateAlpha(long elapsedTime) {
      float fadeInTime = 300.0F;
      float fadeOutTime = 500.0F;
      float totalTime = 3000.0F;
      if ((float)elapsedTime < fadeInTime) {
         return (float)elapsedTime / fadeInTime;
      } else {
         return (float)elapsedTime > totalTime - fadeOutTime ? (totalTime - (float)elapsedTime) / fadeOutTime : 1.0F;
      }
   }
}
