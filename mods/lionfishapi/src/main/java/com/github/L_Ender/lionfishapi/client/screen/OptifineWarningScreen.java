package com.github.L_Ender.lionfishapi.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import org.jetbrains.annotations.Nullable;

public class OptifineWarningScreen extends Screen {
   private final Screen lastScreen;
   private int ticksUntilEnable = 160;
   private MultiLineLabel message = MultiLineLabel.EMPTY;
   private MultiLineLabel xenonsuggestions = MultiLineLabel.EMPTY;
   private MultiLineLabel oculussuggestions = MultiLineLabel.EMPTY;
   private static final Component text = Component.translatable("gui.lionfishapi.optifine.message");
   private static final MutableComponent xenonurl = Component.translatable("gui.lionfishapi.optifine.xenonsuggestions")
      .withStyle(
         style -> style.withColor(ChatFormatting.GREEN)
            .applyFormat(ChatFormatting.UNDERLINE)
            .withClickEvent(new ClickEvent(Action.OPEN_URL, "https://modrinth.com/mod/xenon-forge"))
      );
   private static final MutableComponent oculusurl = Component.translatable("gui.lionfishapi.optifine.oculussuggestions")
      .withStyle(
         style -> style.withColor(ChatFormatting.GREEN)
            .applyFormat(ChatFormatting.UNDERLINE)
            .withClickEvent(new ClickEvent(Action.OPEN_URL, "https://modrinth.com/mod/oculus"))
      );
   private Button exitButton;

   public OptifineWarningScreen(Screen screen) {
      super(Component.translatable("gui.lionfishapi.optifine.title"));
      this.lastScreen = screen;
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(new Component[]{super.getNarrationMessage(), text});
   }

   protected void init() {
      super.init();
      this.exitButton = (Button)this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_PROCEED, pressed -> Minecraft.getInstance().setScreen(this.lastScreen))
            .bounds(this.width / 2 - 75, this.height * 3 / 4, 150, 20)
            .build()
      );
      this.exitButton.active = true;
      this.message = MultiLineLabel.create(this.font, text, this.width - 25);
      this.xenonsuggestions = MultiLineLabel.create(this.font, xenonurl, this.width - 35);
      this.oculussuggestions = MultiLineLabel.create(this.font, oculusurl, this.width - 45);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(graphics, mouseX, mouseY, partialTicks);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 30, 16777215);
      this.message.renderCentered(graphics, this.width / 2, 70);
      this.xenonsuggestions.renderCentered(graphics, this.width / 2, 120);
      this.oculussuggestions.renderCentered(graphics, this.width / 2, 160);
      super.render(graphics, mouseX, mouseY, partialTicks);
      this.exitButton.render(graphics, mouseX, mouseY, partialTicks);
   }

   public void tick() {
      super.tick();
      if (--this.ticksUntilEnable <= 0) {
         this.exitButton.active = true;
      }
   }

   public boolean shouldCloseOnEsc() {
      return this.ticksUntilEnable <= 0;
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.lastScreen);
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (pMouseY > 120.0 && pMouseY < 130.0) {
         Style style = this.getClickedComponentStyleAt((int)pMouseX, xenonurl);
         if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == Action.OPEN_URL) {
            this.handleComponentClicked(style);
            return false;
         }
      }

      if (pMouseY > 160.0 && pMouseY < 170.0) {
         Style style = this.getClickedComponentStyleAt((int)pMouseX, oculusurl);
         if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == Action.OPEN_URL) {
            this.handleComponentClicked(style);
            return false;
         }
      }

      return super.mouseClicked(pMouseX, pMouseY, pButton);
   }

   @Nullable
   private Style getClickedComponentStyleAt(int xPos, MutableComponent url) {
      int wid = Minecraft.getInstance().font.width(url);
      int left = this.width / 2 - wid / 2;
      int right = this.width / 2 + wid / 2;
      return xPos >= left && xPos <= right ? Minecraft.getInstance().font.getSplitter().componentStyleAtWidth(url, xPos - left) : null;
   }
}
