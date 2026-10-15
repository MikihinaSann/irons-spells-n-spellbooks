package net.alshanex.familiarslib.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarStorageBlockEntity;
import net.alshanex.familiarslib.data.PlayerFamiliarData;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.network.MoveFamiliarPacket;
import net.alshanex.familiarslib.network.SetStorageModePacket;
import net.alshanex.familiarslib.registry.AttachmentRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FamiliarStorageScreen extends Screen {
   private static final int BASE_PANEL_WIDTH = 200;
   private static final int BASE_PANEL_HEIGHT = 320;
   private static final int BASE_BUTTON_PANEL_WIDTH = 140;
   private static final int BASE_FAMILIAR_ITEM_HEIGHT = 60;
   private static final int BASE_SCROLL_SPEED = 20;
   private static final int BASE_PADDING = 20;
   private int PANEL_WIDTH;
   private int PANEL_HEIGHT;
   private int BUTTON_PANEL_WIDTH;
   private int FAMILIAR_ITEM_HEIGHT;
   private int SCROLL_SPEED;
   private int PADDING;
   private final BlockPos blockPos;
   private final List<FamiliarStorageScreen.FamiliarEntry> storedFamiliars = new ArrayList<>();
   private final List<FamiliarStorageScreen.FamiliarEntry> playerFamiliars = new ArrayList<>();
   private UUID selectedStoredFamiliar = null;
   private UUID selectedPlayerFamiliar = null;
   private int storedScrollOffset = 0;
   private int playerScrollOffset = 0;
   private int storedMaxScroll = 0;
   private int playerMaxScroll = 0;
   private float animationTime = 0.0F;
   private int storedPanelX;
   private int buttonPanelX;
   private int playerPanelX;
   private int panelY;
   private Button storeButton;
   private Button retrieveButton;
   private Button switchToWanderModeButton;

   public FamiliarStorageScreen(BlockPos blockPos) {
      super(Component.translatable("ui.familiarslib.familiar_storage_screen"));
      this.blockPos = blockPos;
   }

   protected void init() {
      super.init();
      double scale = Math.min(this.width / 1000.0, this.height / 600.0);
      scale = Math.max(0.7, Math.min(1.5, scale));
      this.PANEL_WIDTH = (int)(200.0 * scale);
      this.PANEL_HEIGHT = (int)(320.0 * scale);
      this.BUTTON_PANEL_WIDTH = (int)(140.0 * scale);
      this.FAMILIAR_ITEM_HEIGHT = (int)(60.0 * scale);
      this.SCROLL_SPEED = (int)(20.0 * scale);
      this.PADDING = (int)(20.0 * scale);
      int totalWidth = this.PANEL_WIDTH * 2 + this.BUTTON_PANEL_WIDTH + this.PADDING * 2;
      int startX = (this.width - totalWidth) / 2;
      this.storedPanelX = startX;
      this.buttonPanelX = startX + this.PANEL_WIDTH + this.PADDING;
      this.playerPanelX = startX + this.PANEL_WIDTH + this.BUTTON_PANEL_WIDTH + this.PADDING * 2;
      this.panelY = (this.height - this.PANEL_HEIGHT) / 2 + 30;
      int buttonWidth = Math.max(120, (int)(120.0 * scale));
      int buttonHeight = Math.max(20, (int)(20.0 * scale));
      int smallButtonHeight = Math.max(18, (int)(18.0 * scale));
      this.switchToWanderModeButton = Button.builder(
            Component.translatable("ui.familiarslib.familiar_storage_screen.switch_to_wander"), button -> this.switchToWanderMode()
         )
         .pos(this.buttonPanelX + (this.BUTTON_PANEL_WIDTH - buttonWidth) / 2, this.panelY)
         .size(buttonWidth, buttonHeight)
         .build();
      int screenCenterY = this.height / 2;
      int moveButtonWidth = Math.max(80, (int)(80.0 * scale));
      this.storeButton = Button.builder(Component.literal("←"), button -> this.moveToStorage())
         .pos(this.buttonPanelX + (this.BUTTON_PANEL_WIDTH - moveButtonWidth) / 2, screenCenterY - 30)
         .size(moveButtonWidth, smallButtonHeight)
         .build();
      this.retrieveButton = Button.builder(Component.literal("→"), button -> this.moveToPlayer())
         .pos(this.buttonPanelX + (this.BUTTON_PANEL_WIDTH - moveButtonWidth) / 2, screenCenterY + 30)
         .size(moveButtonWidth, smallButtonHeight)
         .build();
      this.addRenderableWidget(this.switchToWanderModeButton);
      this.addRenderableWidget(this.storeButton);
      this.addRenderableWidget(this.retrieveButton);
      this.loadFamiliarData();
      this.updateButtonStates();
   }

   private void switchToWanderMode() {
      PacketDistributor.sendToServer(new SetStorageModePacket(this.blockPos, false), new CustomPacketPayload[0]);
      this.onClose();
   }

   private void loadFamiliarData() {
      this.storedFamiliars.clear();
      this.playerFamiliars.clear();
      if (this.minecraft != null && this.minecraft.player != null) {
         if (this.minecraft.level.getBlockEntity(this.blockPos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
            Map<UUID, CompoundTag> stored = storageEntity.getStoredFamiliars();

            for (Entry<UUID, CompoundTag> entry : stored.entrySet()) {
               UUID id = entry.getKey();
               CompoundTag nbt = entry.getValue();
               FamiliarStorageScreen.FamiliarEntry familiarEntry = this.createFamiliarEntry(id, nbt);
               if (familiarEntry != null) {
                  this.storedFamiliars.add(familiarEntry);
               }
            }
         }

         PlayerFamiliarData familiarData = (PlayerFamiliarData)this.minecraft.player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         Map<UUID, CompoundTag> playerFams = familiarData.getAllFamiliars();

         for (Entry<UUID, CompoundTag> entryx : playerFams.entrySet()) {
            UUID id = entryx.getKey();
            CompoundTag nbt = entryx.getValue();
            FamiliarStorageScreen.FamiliarEntry familiarEntry = this.createFamiliarEntry(id, nbt);
            if (familiarEntry != null) {
               this.playerFamiliars.add(familiarEntry);
            }
         }

         this.calculateMaxScroll();
      }
   }

   private FamiliarStorageScreen.FamiliarEntry createFamiliarEntry(UUID id, CompoundTag nbt) {
      if (this.minecraft != null && this.minecraft.level != null) {
         String entityTypeString = nbt.getString("id");
         EntityType<?> entityType = (EntityType<?>)EntityType.byString(entityTypeString).orElse(null);
         if (entityType != null && entityType.create(this.minecraft.level) instanceof AbstractSpellCastingPet familiar) {
            familiar.load(nbt);
            familiar.setUUID(id);
            String displayName = familiar.hasCustomName() ? familiar.getCustomName().getString() : familiar.getType().getDescription().getString();
            return new FamiliarStorageScreen.FamiliarEntry(id, familiar, displayName);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void calculateMaxScroll() {
      int visibleItems = this.PANEL_HEIGHT / this.FAMILIAR_ITEM_HEIGHT;
      this.storedMaxScroll = Math.max(0, (this.storedFamiliars.size() - visibleItems) * this.FAMILIAR_ITEM_HEIGHT);
      this.playerMaxScroll = Math.max(0, (this.playerFamiliars.size() - visibleItems) * this.FAMILIAR_ITEM_HEIGHT);
   }

   public void reloadFamiliarData() {
      this.selectedStoredFamiliar = null;
      this.selectedPlayerFamiliar = null;
      this.loadFamiliarData();
      this.updateButtonStates();
      this.storedScrollOffset = Math.max(0, Math.min(this.storedScrollOffset, this.storedMaxScroll));
      this.playerScrollOffset = Math.max(0, Math.min(this.playerScrollOffset, this.playerMaxScroll));
   }

   private void updateButtonStates() {
      if (this.minecraft.level.getBlockEntity(this.blockPos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)this.minecraft.player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         this.storeButton.active = this.selectedPlayerFamiliar != null && storageEntity.canStoreFamiliar();
         this.retrieveButton.active = this.selectedStoredFamiliar != null && familiarData.canTameMoreFamiliars();
      }
   }

   private void moveToStorage() {
      if (this.selectedPlayerFamiliar != null) {
         PacketDistributor.sendToServer(new MoveFamiliarPacket(this.blockPos, this.selectedPlayerFamiliar, true), new CustomPacketPayload[0]);
         this.selectedPlayerFamiliar = null;
      }
   }

   private void moveToPlayer() {
      if (this.selectedStoredFamiliar != null) {
         PacketDistributor.sendToServer(new MoveFamiliarPacket(this.blockPos, this.selectedStoredFamiliar, false), new CustomPacketPayload[0]);
         this.selectedStoredFamiliar = null;
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.animationTime += partialTick;
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      if (this.minecraft.level.getBlockEntity(this.blockPos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         Component modeTitle = Component.translatable("ui.familiarslib.familiar_storage_screen.store_mode_title");
         int titleWidth = this.font.width(modeTitle);
         guiGraphics.drawString(this.font, modeTitle, (this.width - titleWidth) / 2, this.panelY - 50, 65280);
         this.renderTitle(
            guiGraphics,
            Component.translatable(
               "ui.familiarslib.familiar_storage_screen.storage_familiars",
               new Object[]{storageEntity.getStoredFamiliarCount(), storageEntity.getMaxStoredFamiliars()}
            ),
            this.storedPanelX,
            this.panelY - 20
         );
         this.renderTitle(guiGraphics, Component.translatable("ui.familiarslib.familiar_storage_screen.player_familiars"), this.playerPanelX, this.panelY - 20);
      }

      this.renderFamiliarPanel(
         guiGraphics, this.storedFamiliars, this.storedPanelX, this.panelY, this.storedScrollOffset, this.selectedStoredFamiliar, mouseX, mouseY, partialTick
      );
      this.renderFamiliarPanel(
         guiGraphics, this.playerFamiliars, this.playerPanelX, this.panelY, this.playerScrollOffset, this.selectedPlayerFamiliar, mouseX, mouseY, partialTick
      );
      super.render(guiGraphics, mouseX, mouseY, partialTick);
   }

   private void renderTitle(GuiGraphics guiGraphics, Component title, int x, int y) {
      int titleWidth = this.font.width(title);
      guiGraphics.drawString(this.font, title, x + (this.PANEL_WIDTH - titleWidth) / 2, y, 16777215);
   }

   private void renderFamiliarPanel(
      GuiGraphics guiGraphics,
      List<FamiliarStorageScreen.FamiliarEntry> familiars,
      int panelX,
      int panelY,
      int scrollOffset,
      UUID selectedId,
      int mouseX,
      int mouseY,
      float partialTick
   ) {
      guiGraphics.fill(panelX - 2, panelY - 2, panelX + this.PANEL_WIDTH + 2, panelY + this.PANEL_HEIGHT + 2, -11184811);
      guiGraphics.fill(panelX, panelY, panelX + this.PANEL_WIDTH, panelY + this.PANEL_HEIGHT, -14540254);
      this.enableScissor(panelX, panelY, panelX + this.PANEL_WIDTH, panelY + this.PANEL_HEIGHT);
      int currentY = panelY - scrollOffset;

      for (FamiliarStorageScreen.FamiliarEntry entry : familiars) {
         if (currentY + this.FAMILIAR_ITEM_HEIGHT > panelY && currentY < panelY + this.PANEL_HEIGHT) {
            this.renderFamiliarItem(guiGraphics, entry, panelX, currentY, selectedId, mouseX, mouseY, partialTick);
         }

         currentY += this.FAMILIAR_ITEM_HEIGHT;
      }

      this.disableScissor();
   }

   private void renderFamiliarItem(
      GuiGraphics guiGraphics, FamiliarStorageScreen.FamiliarEntry entry, int x, int y, UUID selectedId, int mouseX, int mouseY, float partialTick
   ) {
      boolean isSelected = entry.id.equals(selectedId);
      boolean isHovered = mouseX >= x && mouseX < x + this.PANEL_WIDTH && mouseY >= y && mouseY < y + this.FAMILIAR_ITEM_HEIGHT;
      if (isSelected) {
         guiGraphics.fill(x, y, x + this.PANEL_WIDTH, y + this.FAMILIAR_ITEM_HEIGHT, 1140915968);
      } else if (isHovered) {
         guiGraphics.fill(x, y, x + this.PANEL_WIDTH, y + this.FAMILIAR_ITEM_HEIGHT, 1157627903);
      }

      float entityScale = Math.max(15.0F, this.FAMILIAR_ITEM_HEIGHT * 0.4F);
      int entityX = x + (int)(this.FAMILIAR_ITEM_HEIGHT * 0.5F);
      int entityY = y + (int)(this.FAMILIAR_ITEM_HEIGHT * 0.7F);
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      poseStack.translate(entityX, entityY, 50.0F);
      poseStack.scale(entityScale, entityScale, entityScale);
      Quaternionf rotation = new Quaternionf().rotateY((float)Math.toRadians(45.0));
      poseStack.mulPose(rotation);
      this.renderEntity(guiGraphics, entry.familiar, 0.0F, 0.0F, 0.0F);
      poseStack.popPose();
      Component nameComponent = Component.literal(entry.displayName);
      int textX = x + (int)(this.FAMILIAR_ITEM_HEIGHT * 1.1F);
      int textY = y + this.FAMILIAR_ITEM_HEIGHT / 2 - 9 / 2;
      guiGraphics.drawString(this.font, nameComponent, textX, textY, 16777215);
   }

   private void renderEntity(GuiGraphics guiGraphics, LivingEntity entity, float x, float y, float z) {
      if (entity != null) {
         PoseStack poseStack = guiGraphics.pose();
         EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
         poseStack.pushPose();
         poseStack.translate(x, y, z);
         poseStack.scale(1.0F, -1.0F, 1.0F);
         float rotationY = this.animationTime * 0.02F % (float) (Math.PI * 2);
         Quaternionf rotation = new Quaternionf().rotateY(rotationY);
         poseStack.mulPose(rotation);
         Quaternionf tiltRotation = new Quaternionf().rotateX((float)Math.toRadians(-10.0));
         poseStack.mulPose(tiltRotation);
         RenderSystem.setShaderLights(new Vector3f(0.2F, 1.0F, -1.0F), new Vector3f(-0.2F, -1.0F, 0.0F));
         RenderSystem.disableCull();

         try {
            BufferSource bufferSource = guiGraphics.bufferSource();
            entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 0.0F, poseStack, bufferSource, 15728880);
            guiGraphics.flush();
         } catch (Exception var14) {
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            Component errorText = Component.literal("Error");
            int textWidth = this.font.width(errorText);
            guiGraphics.drawString(this.font, errorText, (int)x - textWidth / 2, (int)y, 16733525);
         }

         RenderSystem.enableCull();
         poseStack.popPose();
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0) {
         if (mouseX >= this.storedPanelX && mouseX < this.storedPanelX + this.PANEL_WIDTH && mouseY >= this.panelY && mouseY < this.panelY + this.PANEL_HEIGHT) {
            int relativeY = (int)(mouseY - this.panelY + this.storedScrollOffset);
            int itemIndex = relativeY / this.FAMILIAR_ITEM_HEIGHT;
            if (itemIndex >= 0 && itemIndex < this.storedFamiliars.size()) {
               this.selectedStoredFamiliar = this.storedFamiliars.get(itemIndex).id;
               this.selectedPlayerFamiliar = null;
               this.updateButtonStates();
               return true;
            }
         }

         if (mouseX >= this.playerPanelX && mouseX < this.playerPanelX + this.PANEL_WIDTH && mouseY >= this.panelY && mouseY < this.panelY + this.PANEL_HEIGHT) {
            int relativeY = (int)(mouseY - this.panelY + this.playerScrollOffset);
            int itemIndex = relativeY / this.FAMILIAR_ITEM_HEIGHT;
            if (itemIndex >= 0 && itemIndex < this.playerFamiliars.size()) {
               this.selectedPlayerFamiliar = this.playerFamiliars.get(itemIndex).id;
               this.selectedStoredFamiliar = null;
               this.updateButtonStates();
               return true;
            }
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (mouseX >= this.storedPanelX && mouseX < this.storedPanelX + this.PANEL_WIDTH && mouseY >= this.panelY && mouseY < this.panelY + this.PANEL_HEIGHT) {
         this.storedScrollOffset = this.storedScrollOffset - (int)(scrollY * this.SCROLL_SPEED);
         this.storedScrollOffset = Math.max(0, Math.min(this.storedScrollOffset, this.storedMaxScroll));
         return true;
      } else if (mouseX >= this.playerPanelX
         && mouseX < this.playerPanelX + this.PANEL_WIDTH
         && mouseY >= this.panelY
         && mouseY < this.panelY + this.PANEL_HEIGHT) {
         this.playerScrollOffset = this.playerScrollOffset - (int)(scrollY * this.SCROLL_SPEED);
         this.playerScrollOffset = Math.max(0, Math.min(this.playerScrollOffset, this.playerMaxScroll));
         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.onClose();
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      guiGraphics.fill(0, 0, this.width, this.height, -2013265920);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void enableScissor(int x1, int y1, int x2, int y2) {
      double scale = this.minecraft.getWindow().getGuiScale();
      RenderSystem.enableScissor(
         (int)(x1 * scale), (int)(this.minecraft.getWindow().getHeight() - y2 * scale), (int)((x2 - x1) * scale), (int)((y2 - y1) * scale)
      );
   }

   private void disableScissor() {
      RenderSystem.disableScissor();
   }

   private static class FamiliarEntry {
      final UUID id;
      final AbstractSpellCastingPet familiar;
      final String displayName;

      FamiliarEntry(UUID id, AbstractSpellCastingPet familiar, String displayName) {
         this.id = id;
         this.familiar = familiar;
         this.displayName = displayName;
      }
   }
}
