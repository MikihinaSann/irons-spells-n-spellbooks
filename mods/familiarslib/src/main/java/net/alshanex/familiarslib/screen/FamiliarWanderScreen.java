package net.alshanex.familiarslib.screen;

import net.alshanex.familiarslib.block.entity.AbstractFamiliarStorageBlockEntity;
import net.alshanex.familiarslib.network.SetStorageModePacket;
import net.alshanex.familiarslib.network.UpdateStorageSettingsPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public class FamiliarWanderScreen extends Screen {
   private static final int PANEL_WIDTH = 300;
   private static final int PANEL_HEIGHT = 200;
   private final BlockPos blockPos;
   private boolean canFamiliarsUseGoals = true;
   private int maxDistance = 25;
   private Button switchToStoreModeButton;
   private Button toggleGoalsButton;
   private FamiliarWanderScreen.DistanceSlider distanceSlider;
   private int panelX;
   private int panelY;

   public FamiliarWanderScreen(BlockPos blockPos) {
      super(Component.translatable("ui.familiarslib.familiar_wander_screen"));
      this.blockPos = blockPos;
   }

   protected void init() {
      super.init();
      this.panelX = (this.width - 300) / 2;
      this.panelY = (this.height - 200) / 2;
      this.loadStorageData();
      this.switchToStoreModeButton = Button.builder(
            Component.translatable("ui.familiarslib.familiar_storage_screen.switch_to_store"), button -> this.switchToStoreMode()
         )
         .pos(this.panelX + 50, this.panelY + 40)
         .size(200, 20)
         .build();
      this.toggleGoalsButton = Button.builder(this.getGoalsButtonText(), button -> this.toggleGoals())
         .pos(this.panelX + 50, this.panelY + 80)
         .size(200, 20)
         .build();
      this.distanceSlider = new FamiliarWanderScreen.DistanceSlider(this.panelX + 50, this.panelY + 160, 200, 20, this.maxDistance);
      this.addRenderableWidget(this.switchToStoreModeButton);
      this.addRenderableWidget(this.toggleGoalsButton);
      this.addRenderableWidget(this.distanceSlider);
   }

   private void loadStorageData() {
      if (this.minecraft != null && this.minecraft.level != null) {
         if (this.minecraft.level.getBlockEntity(this.blockPos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
            this.canFamiliarsUseGoals = storageEntity.canFamiliarsUseGoals();
            this.maxDistance = storageEntity.getMaxDistance();
         }
      }
   }

   private Component getGoalsButtonText() {
      return this.canFamiliarsUseGoals
         ? Component.translatable("ui.familiarslib.familiar_wander_screen.goals_enabled")
         : Component.translatable("ui.familiarslib.familiar_wander_screen.goals_disabled");
   }

   private void switchToStoreMode() {
      PacketDistributor.sendToServer(new SetStorageModePacket(this.blockPos, true), new CustomPacketPayload[0]);
      this.onClose();
   }

   private void toggleGoals() {
      this.canFamiliarsUseGoals = !this.canFamiliarsUseGoals;
      this.toggleGoalsButton.setMessage(this.getGoalsButtonText());
      PacketDistributor.sendToServer(new UpdateStorageSettingsPacket(this.blockPos, this.canFamiliarsUseGoals, this.maxDistance), new CustomPacketPayload[0]);
   }

   private void updateMaxDistance(int newDistance) {
      this.maxDistance = newDistance;
      PacketDistributor.sendToServer(new UpdateStorageSettingsPacket(this.blockPos, this.canFamiliarsUseGoals, this.maxDistance), new CustomPacketPayload[0]);
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      guiGraphics.fill(this.panelX - 2, this.panelY - 2, this.panelX + 300 + 2, this.panelY + 200 + 2, -11184811);
      guiGraphics.fill(this.panelX, this.panelY, this.panelX + 300, this.panelY + 200, -14540254);
      Component title = Component.translatable("ui.familiarslib.familiar_wander_screen.title");
      int titleWidth = this.font.width(title);
      guiGraphics.drawString(this.font, title, this.panelX + (300 - titleWidth) / 2, this.panelY + 10, 16777215);
      Component distanceLabel = Component.translatable("ui.familiarslib.familiar_wander_screen.max_distance", new Object[]{String.valueOf(this.maxDistance)});
      guiGraphics.drawString(this.font, distanceLabel, this.panelX + 90, this.panelY + 145, 16777215);
      super.render(guiGraphics, mouseX, mouseY, partialTick);
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      guiGraphics.fill(0, 0, this.width, this.height, -2013265920);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private class DistanceSlider extends AbstractSliderButton {
      private static final int MIN_DISTANCE = 3;
      private static final int MAX_DISTANCE = 25;

      public DistanceSlider(int x, int y, int width, int height, double initialValue) {
         super(x, y, width, height, Component.empty(), (initialValue - 3.0) / 22.0);
         this.updateMessage();
      }

      protected void updateMessage() {
         int currentDistance = (int)Math.round(3.0 + this.value * 22.0);
         this.setMessage(Component.literal(String.valueOf(currentDistance)));
      }

      protected void applyValue() {
         int newDistance = (int)Math.round(3.0 + this.value * 22.0);
         FamiliarWanderScreen.this.updateMaxDistance(newDistance);
      }
   }
}
