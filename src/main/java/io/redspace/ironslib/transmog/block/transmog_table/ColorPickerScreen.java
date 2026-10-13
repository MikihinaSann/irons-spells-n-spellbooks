package io.redspace.ironslib.transmog.block.transmog_table;

import io.redspace.ironslib.IronsLib;
import java.awt.Color;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ColorPickerScreen extends Screen {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_lib", "textures/gui/color_picker.png");
   private static final int PICKER_X = 8;
   private static final int PICKER_Y = 24;
   private static final int PICKER_WIDTH = 100;
   private static final int PICKER_HEIGHT = 64;
   private static final int VALUE_X = 114;
   private static final int VALUE_Y = 24;
   private static final int VALUE_WIDTH = 8;
   private static final int VALUE_HEIGHT = 64;
   float x;
   float y;
   float v;
   int leftPos;
   int topPos;
   public int imageWidth = 130;
   public int imageHeight = 96;
   int currentColor;
   int initialColor;
   final Runnable closeCallback;
   final Consumer<Integer> setColor;
   EditBox hexTextInput;
   boolean isMouseSelectingColor;
   boolean isMouseSelectingValue;

   protected ColorPickerScreen(Runnable closeCallback, Consumer<Integer> setColor, int initialColor) {
      super(Component.empty());
      this.setColor = setColor;
      this.closeCallback = closeCallback;
      this.currentColor = initialColor & 16777215;
      this.initialColor = this.currentColor;
      this.setPickerFromColor(this.initialColor);
      String hex = "#" + Integer.toHexString(this.currentColor);
      this.hexTextInput = new EditBox(Minecraft.getInstance().font, 7, 7, 52, 12, Component.literal(hex));
      this.hexTextInput.setMaxLength(7);
      this.hexTextInput.setValue(hex);
   }

   public void init() {
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      this.hexTextInput.setX(this.leftPos + 7);
      this.hexTextInput.setY(this.topPos + 7);
   }

   public void setPos(int x, int y) {
      this.leftPos = x;
      this.topPos = y;
      this.hexTextInput.setX(this.leftPos + 7);
      this.hexTextInput.setY(this.topPos + 7);
   }

   public void onClose() {
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void resetColor() {
      this.setColor(this.initialColor);
      this.setTextHex(this.initialColor);
      this.setPickerFromColor(this.initialColor);
   }

   public void setColor(int color) {
      this.currentColor = color;
      this.setColor.accept(color);
   }

   public void setTextHex(int color) {
      String hex = "#" + Integer.toHexString(color);
      this.hexTextInput.setValue(hex);
   }

   public void onTextInputChanged() {
      if (!this.hexTextInput.getValue().startsWith("#")) {
         this.hexTextInput.setValue("#" + this.hexTextInput.getValue());
      }

      String hexValue = this.hexTextInput.getValue().substring(1);
      boolean isValidHex = hexValue.matches("^[0-9a-fA-F]+$");
      if (isValidHex) {
         this.hexTextInput.setTextColor(-1);
         this.setColor(hexValue.isEmpty() ? 0 : Integer.parseInt(hexValue, 16));
         this.setPickerFromColor(this.currentColor);
      } else {
         this.hexTextInput.setTextColor(ChatFormatting.RED.getColor());
      }
   }

   private void updateValueFromMouse(double mouseY) {
      this.v = (float)((int)Math.clamp(mouseY - (double)this.topPos - 24.0, 0.0, 64.0));
      this.setColor(this.colorFromPicker(this.x, this.y, this.v));
      this.setTextHex(this.currentColor);
   }

   private void updateColorFromMouse(double mouseX, double mouseY) {
      this.x = (float)((int)Math.clamp(mouseX - (double)this.leftPos - 8.0, 0.0, 99.0));
      this.y = (float)((int)Math.clamp(mouseY - (double)this.topPos - 24.0, 0.0, 63.0));
      this.setColor(this.colorFromPicker(this.x, this.y, this.v));
      this.setTextHex(this.currentColor);
   }

   public int colorFromPicker(float x, float y, float v) {
      float hue = x / 100.0F;
      float saturation = 1.0F - y / 64.0F;
      float brightness = 1.0F - v / 64.0F;
      return Color.getHSBColor(hue, saturation, brightness).getRGB() & 16777215;
   }

   public void setPickerFromColor(int color) {
      io.redspace.ironslib.util.Color colorUnpacked = new io.redspace.ironslib.util.Color(color);
      float[] hsb = Color.RGBtoHSB(colorUnpacked.red(), colorUnpacked.green(), colorUnpacked.blue(), null);
      float hue = hsb[0];
      float saturation = hsb[1];
      float brightness = hsb[2];
      this.x = hue * 100.0F;
      this.y = (1.0F - saturation) * 64.0F;
      this.v = (1.0F - brightness) * 64.0F;
   }

   public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.render(guiGraphics, mouseX, mouseY, partialTick);
      this.hexTextInput.render(guiGraphics, mouseX, mouseY, partialTick);
      guiGraphics.blitSprite(
         IronsLib.id("color_picker/color_picker_target"), (int)((float)(this.leftPos + 8) + this.x) - 5, (int)((float)(this.topPos + 24) + this.y) - 5, 10, 10
      );
      guiGraphics.blitSprite(IronsLib.id("color_picker/value_picker"), this.leftPos + 114 - 2, (int)((float)(this.topPos - 4 + 24) + this.v), 8, 8);
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(0.0F, 0.0F, 500.0F);
      this.drawPreviewBox(guiGraphics);
      guiGraphics.pose().popPose();
   }

   private void drawPreviewBox(GuiGraphics guiGraphics) {
      int width = 20;
      int height = 10;
      int x = this.leftPos + 88;
      int y = this.topPos + 8;
      io.redspace.ironslib.util.Color color = new io.redspace.ironslib.util.Color(this.currentColor);
      guiGraphics.setColor((float)color.red() / 255.0F, (float)color.green() / 255.0F, (float)color.blue() / 255.0F, 1.0F);
      guiGraphics.blitSprite(IronsLib.id("color_picker/white"), x, y, width, height);
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.isMouseSelectingColor = false;
      this.isMouseSelectingValue = false;
      if (this.hexTextInput.mouseClicked(mouseX, mouseY, button)) {
         this.hexTextInput.setFocused(true);
         return true;
      } else {
         this.hexTextInput.setFocused(false);
         if (mouseX > (double)(this.leftPos + 64)
            && mouseY > (double)(this.topPos + 8)
            && mouseX < (double)(this.leftPos + 64 + 10)
            && mouseY < (double)(this.topPos + 8 + 10)) {
            this.resetColor();
            return true;
         } else if (mouseX >= (double)(this.leftPos + 8)
            && mouseX <= (double)(this.leftPos + 8 + 100)
            && mouseY >= (double)(this.topPos + 24)
            && mouseY <= (double)(this.topPos + 24 + 64)) {
            this.isMouseSelectingColor = true;
            this.updateColorFromMouse(mouseX, mouseY);
            return true;
         } else if (mouseX >= (double)(this.leftPos + 114)
            && mouseX <= (double)(this.leftPos + 114 + 8)
            && mouseY >= (double)(this.topPos + 24)
            && mouseY <= (double)(this.topPos + 24 + 64)) {
            this.isMouseSelectingValue = true;
            this.updateValueFromMouse(mouseY);
            return true;
         } else {
            return super.mouseClicked(mouseX, mouseY, button);
         }
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      if (this.isMouseSelectingColor) {
         this.updateColorFromMouse(mouseX, mouseY);
         return true;
      } else if (this.isMouseSelectingValue) {
         this.updateValueFromMouse(mouseY);
         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.hexTextInput.keyPressed(keyCode, scanCode, modifiers)) {
         this.onTextInputChanged();
         return true;
      } else {
         return this.hexTextInput.isFocused() && this.hexTextInput.isVisible() && keyCode != 256 ? true : super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public boolean charTyped(char codePoint, int modifiers) {
      if (this.hexTextInput.isFocused() && this.hexTextInput.charTyped(codePoint, modifiers)) {
         this.onTextInputChanged();
         return true;
      } else {
         return super.charTyped(codePoint, modifiers);
      }
   }
}
