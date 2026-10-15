package com.somake.somakespells.gui.upgrade_forge;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class UpgradeForgeScreen extends AbstractContainerScreen<UpgradeForgeMenu> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/gui/upgrade_forge.png");

   public UpgradeForgeScreen(UpgradeForgeMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.imageWidth = 256;
      this.imageHeight = 166;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      super.render(guiGraphics, mouseX, mouseY, partialTick);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
      guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && ((UpgradeForgeMenu)this.menu).canUpgrade()) {
         int resultX = this.leftPos + 134;
         int resultY = this.topPos + 32;
         if (mouseX >= resultX
            && mouseX < resultX + 16
            && mouseY >= resultY
            && mouseY < resultY + 16
            && this.minecraft.gameMode != null
            && this.minecraft.player != null
            && ((UpgradeForgeMenu)this.menu).clickMenuButton(this.minecraft.player, 0)) {
            this.minecraft.gameMode.handleInventoryButtonClick(((UpgradeForgeMenu)this.menu).containerId, 0);
            this.minecraft.player.playSound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.0F, 1.0F);
            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }
}
