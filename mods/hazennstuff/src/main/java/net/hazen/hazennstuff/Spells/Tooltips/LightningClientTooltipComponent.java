package net.hazen.hazennstuff.Spells.Tooltips;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix4f;

public class LightningClientTooltipComponent implements ClientTooltipComponent {
   private final Component text;

   public LightningClientTooltipComponent(LightningClientTooltipComponent.LightningTooltipData data) {
      this.text = data.text();
   }

   public int getHeight() {
      return 10;
   }

   public int getWidth(Font font) {
      return font.width(this.text);
   }

   public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
      int width = this.getWidth(font);
      int height = this.getHeight();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(0.0F, 0.0F, 500.0F);
      guiGraphics.fill(x - 2, y - 2, x + width + 2, y + height + 2, -65536);
      guiGraphics.fill(x, y, x + width, y + height, -1);
      guiGraphics.pose().popPose();
   }

   public void renderText(Font font, int x, int y, Matrix4f matrix, BufferSource buffer) {
      font.drawInBatch(this.text, x, y, -1, true, matrix, buffer, DisplayMode.NORMAL, 0, 15728880);
   }

   public record LightningTooltipData(Component text) implements TooltipComponent {
   }
}
