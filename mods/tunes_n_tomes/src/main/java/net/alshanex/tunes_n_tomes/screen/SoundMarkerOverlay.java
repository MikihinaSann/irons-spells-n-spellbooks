package net.alshanex.tunes_n_tomes.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.alshanex.tunes_n_tomes.data.ClientSoundMarkerData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SoundMarkerOverlay implements Layer {
   private static final ResourceLocation SEGNO_ICON = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/gui/segno_icon.png");
   private static final int X_OFFSET = 10;
   private static final int Y_OFFSET_FROM_BOTTOM = 30;

   public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.level != null) {
         if (!mc.options.hideGui) {
            int currentMarkers = ClientSoundMarkerData.getCurrentMarkers();
            int maxMarkers = ClientSoundMarkerData.getMaxMarkers();
            if (currentMarkers > 0) {
               int screenHeight = mc.getWindow().getGuiScaledHeight();
               int x = 10;
               int y = screenHeight - 30;
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               guiGraphics.blit(SEGNO_ICON, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
               int textColor = this.getTextColor(currentMarkers, maxMarkers);
               Component markerText = Component.literal(currentMarkers + "/" + maxMarkers);
               guiGraphics.drawString(mc.font, markerText, x + 20, y + 4, textColor, true);
               RenderSystem.disableBlend();
            }
         }
      }
   }

   private int getTextColor(int current, int max) {
      if (current >= max) {
         return 16777045;
      } else {
         return current >= max - 1 ? 16777215 : 5635925;
      }
   }
}
