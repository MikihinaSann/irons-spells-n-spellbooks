package net.alshanex.familiarslib.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.data.PlayerFamiliarData;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.network.ReleaseFamiliarPacket;
import net.alshanex.familiarslib.network.SelectFamiliarPacket;
import net.alshanex.familiarslib.registry.AttachmentRegistry;
import net.alshanex.familiarslib.util.consumables.ConsumableUtils;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FamiliarSelectionScreen extends Screen {
   private static final int PANEL_WIDTH = 200;
   private static final int PANEL_HEIGHT = 300;
   private static final int FAMILIAR_ITEM_HEIGHT = 80;
   private static final int SCROLL_SPEED = 20;
   private static final ResourceLocation HEART_CONTAINER = ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/heart/container.png");
   private static final ResourceLocation HEART_FULL = ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/heart/full.png");
   private static final ResourceLocation ARMOR_FULL = ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/armor_full.png");
   private static final ResourceLocation RED_MUSHROOM = ResourceLocation.withDefaultNamespace("textures/block/red_mushroom.png");
   private final List<FamiliarSelectionScreen.FamiliarEntry> familiarEntries = new ArrayList<>();
   private UUID selectedFamiliarId;
   private int scrollOffset = 0;
   private int maxScroll = 0;
   private float animationTime = 0.0F;
   private int leftPanelX;
   private int rightPanelX;
   private int panelY;
   private Button releaseButton;

   public FamiliarSelectionScreen() {
      super(Component.translatable("ui.familiarslib.familiar_selection_screen"));
   }

   protected void init() {
      super.init();
      this.loadFamiliarData();
      this.leftPanelX = (this.width - 400 - 20) / 2;
      this.rightPanelX = this.leftPanelX + 200 + 20;
      this.panelY = (this.height - 300) / 2 + 30;
      int buttonWidth = 80;
      int buttonHeight = 20;
      int buttonX = this.leftPanelX + (200 - buttonWidth) / 2;
      int buttonY = this.panelY + 300 - 85;
      this.releaseButton = Button.builder(Component.translatable("ui.familiarslib.release_familiar"), this::onReleaseButtonPressed)
         .bounds(buttonX, buttonY, buttonWidth, buttonHeight)
         .build();
      this.addRenderableWidget(this.releaseButton);
      this.updateReleaseButtonVisibility();
   }

   private void loadFamiliarData() {
      this.familiarEntries.clear();
      if (this.minecraft != null && this.minecraft.player != null) {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)this.minecraft.player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         this.selectedFamiliarId = familiarData.getSelectedFamiliarId();
         Map<UUID, CompoundTag> familiars = familiarData.getAllFamiliars();

         for (Entry<UUID, CompoundTag> entry : familiars.entrySet()) {
            UUID id = entry.getKey();
            CompoundTag nbt = entry.getValue();
            String entityTypeString = nbt.getString("id");
            EntityType<?> entityType = (EntityType<?>)EntityType.byString(entityTypeString).orElse(null);
            if (entityType != null && entityType.create(this.minecraft.level) instanceof AbstractSpellCastingPet familiar) {
               familiar.load(nbt);
               familiar.setUUID(id);
               String displayName = familiar.hasCustomName() ? familiar.getCustomName().getString() : familiar.getType().getDescription().getString();
               float baseMaxHealth = nbt.contains("baseMaxHealth") ? nbt.getFloat("baseMaxHealth") : familiar.getBaseMaxHealth();
               FamiliarConsumableSystem.ConsumableData consumableData = FamiliarConsumableSystem.loadConsumableDataFromNBT(nbt);
               if (!nbt.contains("consumableData")) {
                  int legacyArmor = nbt.getInt("armorStacks");
                  int legacyEnraged = nbt.getInt("enragedStacks");
                  int legacyHealth = nbt.getInt("healthStacks");
                  boolean legacyBlocking = nbt.getBoolean("canBlock");
                  if (legacyArmor > 0 || legacyEnraged > 0 || legacyHealth > 0 || legacyBlocking) {
                     if (legacyHealth > 0) {
                        int healthPercentage = legacyHealth * 10;
                        healthPercentage = Math.min(healthPercentage, FamiliarConsumableSystem.ConsumableType.HEALTH.getMaxLimit());
                        consumableData.setValue(FamiliarConsumableSystem.ConsumableType.HEALTH, healthPercentage);
                     }

                     if (legacyArmor > 0) {
                        int armorPoints = Math.min(legacyArmor, FamiliarConsumableSystem.ConsumableType.ARMOR.getMaxLimit());
                        consumableData.setValue(FamiliarConsumableSystem.ConsumableType.ARMOR, armorPoints);
                     }

                     if (legacyEnraged > 0) {
                        int enragedStacks = Math.min(legacyEnraged, FamiliarConsumableSystem.ConsumableType.ENRAGED.getMaxLimit());
                        consumableData.setValue(FamiliarConsumableSystem.ConsumableType.ENRAGED, enragedStacks);
                     }

                     if (legacyBlocking) {
                        consumableData.setValue(FamiliarConsumableSystem.ConsumableType.BLOCKING, 1);
                     }

                     FamiliarConsumableSystem.saveConsumableDataToNBT(consumableData, nbt);
                     familiarData.addTamedFamiliar(id, nbt);
                  }
               }

               float displayHealth = nbt.getFloat("currentHealth");
               if (displayHealth <= 0.0F && nbt.contains("healthPercentage")) {
                  float healthPercentage = nbt.getFloat("healthPercentage");
                  float maxHealthWithModifiers = ConsumableUtils.calculateMaxHealthWithModifiers(consumableData, baseMaxHealth);
                  displayHealth = healthPercentage * maxHealthWithModifiers;
               }

               if (displayHealth <= 0.0F) {
                  displayHealth = baseMaxHealth;
               }

               int armor = consumableData.getValue(FamiliarConsumableSystem.ConsumableType.ARMOR);
               int enraged = consumableData.getValue(FamiliarConsumableSystem.ConsumableType.ENRAGED);
               boolean canBlock = consumableData.getValue(FamiliarConsumableSystem.ConsumableType.BLOCKING) > 0;
               this.familiarEntries.add(new FamiliarSelectionScreen.FamiliarEntry(id, familiar, displayName, displayHealth, armor, enraged, canBlock));
               float maxHealthWithModifiers = ConsumableUtils.calculateMaxHealthWithModifiers(consumableData, baseMaxHealth);
               FamiliarsLib.LOGGER
                  .debug(
                     "FamiliarSelection: Loaded familiar {} - Health: {}/{}, Armor: {}, Enraged: {}, Blocking: {}",
                     new Object[]{id, displayHealth, maxHealthWithModifiers, armor, enraged, canBlock}
                  );
            }
         }

         int visibleItems = 3;
         this.maxScroll = Math.max(0, (this.familiarEntries.size() - visibleItems) * 80);
      }
   }

   private void updateReleaseButtonVisibility() {
      if (this.releaseButton != null) {
         this.releaseButton.visible = this.selectedFamiliarId != null && !this.familiarEntries.isEmpty();
      }
   }

   private void onReleaseButtonPressed(Button button) {
      if (this.selectedFamiliarId != null) {
         FamiliarSelectionScreen.FamiliarEntry selectedEntry = this.getSelectedEntry();
         String familiarName = selectedEntry != null ? selectedEntry.displayName : "Unknown";
         Component title = Component.translatable("ui.familiarslib.confirm_release");
         Component message = Component.translatable("ui.familiarslib.confirm_release_message", new Object[]{familiarName});
         this.minecraft.setScreen(new FamiliarSelectionScreen.ConfirmReleaseScreen(this, title, message, confirmed -> {
            if (confirmed) {
               PacketDistributor.sendToServer(new ReleaseFamiliarPacket(this.selectedFamiliarId), new CustomPacketPayload[0]);
            }
         }));
      }
   }

   public void reloadFamiliarData() {
      try {
         FamiliarsLib.LOGGER.debug("=== FamiliarSelectionScreen.reloadFamiliarData() START ===");
         UUID previousSelected = this.selectedFamiliarId;
         int previousCount = this.familiarEntries.size();
         FamiliarsLib.LOGGER.debug("Previous state - Count: {}, Selected: {}", previousCount, previousSelected);
         this.familiarEntries.clear();
         if (this.minecraft == null || this.minecraft.player == null) {
            FamiliarsLib.LOGGER.warn("Minecraft or player is null, cannot reload familiar data");
            return;
         }

         PlayerFamiliarData familiarData = (PlayerFamiliarData)this.minecraft.player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         if (familiarData == null) {
            FamiliarsLib.LOGGER.warn("FamiliarData is null");
            return;
         }

         Map<UUID, CompoundTag> familiars = familiarData.getAllFamiliars();
         UUID currentSelected = familiarData.getSelectedFamiliarId();
         FamiliarsLib.LOGGER.debug("Current data - Familiar count: {}, Selected: {}", familiars.size(), currentSelected);
         if (familiars.isEmpty()) {
            FamiliarsLib.LOGGER.debug("No familiars found, closing screen");
            this.onClose();
            return;
         }

         for (Entry<UUID, CompoundTag> entry : familiars.entrySet()) {
            UUID id = entry.getKey();
            CompoundTag nbt = entry.getValue();
            FamiliarsLib.LOGGER.debug("Processing familiar: {} with saved health: {}", id, nbt.getFloat("currentHealth"));
            String entityTypeString = nbt.getString("id");
            EntityType<?> entityType = (EntityType<?>)EntityType.byString(entityTypeString).orElse(null);
            if (entityType != null) {
               if (entityType.create(this.minecraft.level) instanceof AbstractSpellCastingPet familiar) {
                  familiar.load(nbt);
                  familiar.setUUID(id);
                  String displayName = familiar.hasCustomName() ? familiar.getCustomName().getString() : familiar.getType().getDescription().getString();
                  float baseMaxHealth = nbt.contains("baseMaxHealth") ? nbt.getFloat("baseMaxHealth") : familiar.getBaseMaxHealth();
                  FamiliarConsumableSystem.ConsumableData consumableData = FamiliarConsumableSystem.loadConsumableDataFromNBT(nbt);
                  if (!nbt.contains("consumableData")) {
                     int legacyArmor = nbt.getInt("armorStacks");
                     int legacyEnraged = nbt.getInt("enragedStacks");
                     int legacyHealth = nbt.getInt("healthStacks");
                     boolean legacyBlocking = nbt.getBoolean("canBlock");
                     if (legacyArmor > 0 || legacyEnraged > 0 || legacyHealth > 0 || legacyBlocking) {
                        if (legacyHealth > 0) {
                           int healthPercentage = legacyHealth * 10;
                           healthPercentage = Math.min(healthPercentage, FamiliarConsumableSystem.ConsumableType.HEALTH.getMaxLimit());
                           consumableData.setValue(FamiliarConsumableSystem.ConsumableType.HEALTH, healthPercentage);
                        }

                        if (legacyArmor > 0) {
                           int armorPoints = Math.min(legacyArmor, FamiliarConsumableSystem.ConsumableType.ARMOR.getMaxLimit());
                           consumableData.setValue(FamiliarConsumableSystem.ConsumableType.ARMOR, armorPoints);
                        }

                        if (legacyEnraged > 0) {
                           int enragedStacks = Math.min(legacyEnraged, FamiliarConsumableSystem.ConsumableType.ENRAGED.getMaxLimit());
                           consumableData.setValue(FamiliarConsumableSystem.ConsumableType.ENRAGED, enragedStacks);
                        }

                        if (legacyBlocking) {
                           consumableData.setValue(FamiliarConsumableSystem.ConsumableType.BLOCKING, 1);
                        }

                        FamiliarConsumableSystem.saveConsumableDataToNBT(consumableData, nbt);
                        familiarData.addTamedFamiliar(id, nbt);
                     }
                  }

                  float displayHealth = nbt.getFloat("currentHealth");
                  if (displayHealth <= 0.0F && nbt.contains("healthPercentage")) {
                     float healthPercentage = nbt.getFloat("healthPercentage");
                     float maxHealthWithModifiers = ConsumableUtils.calculateMaxHealthWithModifiers(consumableData, baseMaxHealth);
                     displayHealth = healthPercentage * maxHealthWithModifiers;
                  }

                  if (displayHealth <= 0.0F) {
                     displayHealth = baseMaxHealth;
                  }

                  int armor = consumableData.getValue(FamiliarConsumableSystem.ConsumableType.ARMOR);
                  int enraged = consumableData.getValue(FamiliarConsumableSystem.ConsumableType.ENRAGED);
                  boolean canBlock = consumableData.getValue(FamiliarConsumableSystem.ConsumableType.BLOCKING) > 0;
                  this.familiarEntries.add(new FamiliarSelectionScreen.FamiliarEntry(id, familiar, displayName, displayHealth, armor, enraged, canBlock));
                  float maxHealthWithModifiers = ConsumableUtils.calculateMaxHealthWithModifiers(consumableData, baseMaxHealth);
                  FamiliarsLib.LOGGER
                     .debug(
                        "Reloaded familiar {} - Health: {}/{}, Armor: {}, Enraged: {}, Blocking: {}",
                        new Object[]{id, displayHealth, maxHealthWithModifiers, armor, enraged, canBlock}
                     );
               }
            } else {
               FamiliarsLib.LOGGER.warn("Unknown entity type for familiar {}: {}", id, entityTypeString);
            }
         }

         this.selectedFamiliarId = currentSelected;
         if (this.selectedFamiliarId != null) {
            boolean foundSelected = this.familiarEntries.stream().anyMatch(entryx -> entryx.id.equals(this.selectedFamiliarId));
            if (!foundSelected) {
               FamiliarsLib.LOGGER.warn("Selected familiar {} not found in entries, selecting first available", this.selectedFamiliarId);
               if (!this.familiarEntries.isEmpty()) {
                  this.selectedFamiliarId = this.familiarEntries.get(0).id;
               } else {
                  this.selectedFamiliarId = null;
               }
            }
         }

         int visibleItems = 3;
         this.maxScroll = Math.max(0, (this.familiarEntries.size() - visibleItems) * 80);
         this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, this.maxScroll));
         this.updateReleaseButtonVisibility();
         FamiliarsLib.LOGGER
            .debug(
               "Reload completed - New count: {}, New selected: {}, Max scroll: {}",
               new Object[]{this.familiarEntries.size(), this.selectedFamiliarId, this.maxScroll}
            );
         FamiliarsLib.LOGGER.debug("=== FamiliarSelectionScreen.reloadFamiliarData() END ===");
      } catch (Exception var23) {
         FamiliarsLib.LOGGER.error("Error reloading familiar data in FamiliarSelectionScreen: ", var23);

         try {
            this.onClose();
         } catch (Exception var22) {
            FamiliarsLib.LOGGER.error("Error closing screen after reload failure: ", var22);
         }
      }
   }

   private Set<UUID> getAllSummonedFamiliarIds() {
      if (this.minecraft != null && this.minecraft.player != null) {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)this.minecraft.player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         return familiarData.getSummonedFamiliarIds();
      } else {
         return new HashSet<>();
      }
   }

   private boolean isFamiliarSummoned(UUID familiarId) {
      if (this.minecraft != null && this.minecraft.player != null) {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)this.minecraft.player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         return familiarData.isFamiliarSummoned(familiarId);
      } else {
         return false;
      }
   }

   private void drawHeartIcon(GuiGraphics guiGraphics, int x, int y) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      guiGraphics.blit(HEART_CONTAINER, x, y - 1, 0.0F, 0.0F, 9, 9, 9, 9);
      guiGraphics.blit(HEART_FULL, x, y - 1, 0.0F, 0.0F, 9, 9, 9, 9);
      RenderSystem.disableBlend();
   }

   private void drawArmorIcon(GuiGraphics guiGraphics, int x, int y) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      guiGraphics.blit(ARMOR_FULL, x, y, 0.0F, 0.0F, 9, 9, 9, 9);
      RenderSystem.disableBlend();
   }

   private void drawItemIcon(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      guiGraphics.blit(texture, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
      RenderSystem.disableBlend();
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.animationTime += partialTick;
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      this.renderSelectedFamiliar(guiGraphics, mouseX, mouseY, partialTick);
      this.renderFamiliarList(guiGraphics, mouseX, mouseY, partialTick);
      super.render(guiGraphics, mouseX, mouseY, partialTick);
   }

   private void renderSelectedFamiliar(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      FamiliarSelectionScreen.FamiliarEntry selectedEntry = this.getSelectedEntry();
      if (selectedEntry != null) {
         PoseStack poseStack = guiGraphics.pose();
         if (this.isFamiliarSummoned(selectedEntry.id)) {
            Component statusComponent = Component.translatable("ui.familiarslib.familiar_summoned");
            int statusWidth = this.font.width(statusComponent);
            guiGraphics.drawString(this.font, statusComponent, this.leftPanelX + (200 - statusWidth) / 2, this.panelY + 20, 16777045);
         }

         poseStack.pushPose();
         poseStack.translate(this.leftPanelX + 100, this.panelY + 100, 100.0F);
         poseStack.scale(60.0F, 60.0F, 60.0F);
         Quaternionf rotation = new Quaternionf().rotateY((float)Math.toRadians(45.0));
         poseStack.mulPose(rotation);
         this.renderEntity(guiGraphics, selectedEntry.familiar, 0.0F, 0.0F, 0.0F);
         poseStack.popPose();
         int infoY = this.panelY + 120;
         Component nameComponent = Component.literal(selectedEntry.displayName);
         int nameWidth = this.font.width(nameComponent);
         guiGraphics.drawString(this.font, nameComponent, this.leftPanelX + (200 - nameWidth) / 2, infoY, 16777215);
         int boxX = this.leftPanelX + 40;
         int boxY = infoY + 25;
         int boxWidth = 120;
         int boxHeight = 60;
         guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, -1430208320);
         guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + 1, -8355712);
         guiGraphics.fill(boxX, boxY + boxHeight - 1, boxX + boxWidth, boxY + boxHeight, -8355712);
         guiGraphics.fill(boxX, boxY, boxX + 1, boxY + boxHeight, -8355712);
         guiGraphics.fill(boxX + boxWidth - 1, boxY, boxX + boxWidth, boxY + boxHeight, -8355712);
         int halfWidth = boxWidth / 2;
         int halfHeight = boxHeight / 2;
         Component healthComponent = Component.literal(String.format("%.0f", selectedEntry.health));
         int healthTextWidth = this.font.width(healthComponent);
         int healthTotalWidth = 11 + healthTextWidth;
         int healthStartX = boxX + (halfWidth - healthTotalWidth) / 2;
         int healthY = boxY + (halfHeight - 9) / 2;
         this.drawHeartIcon(guiGraphics, healthStartX, healthY);
         guiGraphics.drawString(this.font, healthComponent, healthStartX + 11, healthY, 16733525);
         Component armorComponent = Component.literal(String.valueOf(selectedEntry.armor));
         int armorTextWidth = this.font.width(armorComponent);
         int armorTotalWidth = 11 + armorTextWidth;
         int armorStartX = boxX + halfWidth + (halfWidth - armorTotalWidth) / 2;
         int armorY = boxY + (halfHeight - 9) / 2;
         this.drawArmorIcon(guiGraphics, armorStartX, armorY);
         guiGraphics.drawString(this.font, armorComponent, armorStartX + 11, armorY, 11184810);
         Component blockComponent = Component.literal(selectedEntry.canBlock ? "1" : "0");
         int blockTextWidth = this.font.width(blockComponent);
         int blockTotalWidth = 18 + blockTextWidth;
         int blockStartX = boxX + (halfWidth - blockTotalWidth) / 2;
         int blockY = boxY + halfHeight + (halfHeight - 16) / 2;
         ItemStack shield = new ItemStack(Items.SHIELD);
         guiGraphics.renderItem(shield, blockStartX, blockY);
         int blockColor = selectedEntry.canBlock ? 5635925 : 16733525;
         guiGraphics.drawString(this.font, blockComponent, blockStartX + 18, blockY + 4, blockColor);
         Component enragedComponent = Component.literal(String.valueOf(selectedEntry.enraged));
         int enragedTextWidth = this.font.width(enragedComponent);
         int enragedTotalWidth = 18 + enragedTextWidth;
         int enragedStartX = boxX + halfWidth + (halfWidth - enragedTotalWidth) / 2;
         int enragedY = boxY + halfHeight + (halfHeight - 16) / 2;
         this.drawItemIcon(guiGraphics, RED_MUSHROOM, enragedStartX, enragedY - 5);
         guiGraphics.drawString(this.font, enragedComponent, enragedStartX + 18, enragedY + 4, 16733695);
      }
   }

   private UUID getCurrentSummonedFamiliarId() {
      if (this.minecraft != null && this.minecraft.player != null) {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)this.minecraft.player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         return familiarData.getCurrentSummonedFamiliarId();
      } else {
         return null;
      }
   }

   private void renderFamiliarList(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      PoseStack poseStack = guiGraphics.pose();
      int scissorX = this.rightPanelX;
      int scissorY = this.panelY;
      int scissorWidth = 200;
      int scissorHeight = 300;
      this.enableScissor(scissorX, scissorY, scissorX + scissorWidth, scissorY + scissorHeight);
      int currentY = this.panelY - this.scrollOffset;

      for (int i = 0; i < this.familiarEntries.size(); i++) {
         FamiliarSelectionScreen.FamiliarEntry entry = this.familiarEntries.get(i);
         if (currentY + 80 > this.panelY && currentY < this.panelY + 300) {
            this.renderFamiliarItem(guiGraphics, entry, this.rightPanelX, currentY, mouseX, mouseY, partialTick);
         }

         currentY += 80;
      }

      this.disableScissor();
   }

   private void renderFamiliarItem(
      GuiGraphics guiGraphics, FamiliarSelectionScreen.FamiliarEntry entry, int x, int y, int mouseX, int mouseY, float partialTick
   ) {
      PoseStack poseStack = guiGraphics.pose();
      boolean isSelected = entry.id.equals(this.selectedFamiliarId);
      boolean isHovered = mouseX >= x && mouseX < x + 200 && mouseY >= y && mouseY < y + 80;
      if (isSelected) {
         guiGraphics.fill(x, y, x + 200, y + 80, 1140915968);
      } else if (isHovered) {
         guiGraphics.fill(x, y, x + 200, y + 80, 1157627903);
      }

      poseStack.pushPose();
      poseStack.translate(x + 40, y + 50, 50.0F);
      poseStack.scale(30.0F, 30.0F, 30.0F);
      Quaternionf rotation = new Quaternionf().rotateY((float)Math.toRadians(45.0));
      poseStack.mulPose(rotation);
      this.renderEntity(guiGraphics, entry.familiar, 0.0F, 0.0F, 0.0F);
      poseStack.popPose();
      Component nameComponent = Component.literal(entry.displayName);
      guiGraphics.drawString(this.font, nameComponent, x + 85, y + 20, 16777215);
      this.drawHeartIcon(guiGraphics, x + 85, y + 35);
      Component healthComponent = Component.literal(String.format("%.0f", entry.health));
      guiGraphics.drawString(this.font, healthComponent, x + 85 + 12, y + 35, 16733525);
      this.drawArmorIcon(guiGraphics, x + 85, y + 49);
      Component armorComponent = Component.literal(String.valueOf(entry.armor));
      guiGraphics.drawString(this.font, armorComponent, x + 85 + 12, y + 50, 11184810);
      if (this.isFamiliarSummoned(entry.id)) {
         Component summonedIndicator = Component.literal("★");
         guiGraphics.drawString(this.font, summonedIndicator, x + 200 - 20, y + 10, 16777045);
      }
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

   private FamiliarSelectionScreen.FamiliarEntry getSelectedEntry() {
      return this.selectedFamiliarId == null
         ? null
         : this.familiarEntries.stream().filter(entry -> entry.id.equals(this.selectedFamiliarId)).findFirst().orElse(null);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && mouseX >= this.rightPanelX && mouseX < this.rightPanelX + 200 && mouseY >= this.panelY && mouseY < this.panelY + 300) {
         int relativeY = (int)(mouseY - this.panelY + this.scrollOffset);
         int itemIndex = relativeY / 80;
         if (itemIndex >= 0 && itemIndex < this.familiarEntries.size()) {
            FamiliarSelectionScreen.FamiliarEntry selected = this.familiarEntries.get(itemIndex);
            this.selectedFamiliarId = selected.id;
            PacketDistributor.sendToServer(new SelectFamiliarPacket(selected.id), new CustomPacketPayload[0]);
            this.updateReleaseButtonVisibility();
            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (mouseX >= this.rightPanelX && mouseX < this.rightPanelX + 200 && mouseY >= this.panelY && mouseY < this.panelY + 300) {
         this.scrollOffset -= (int)(scrollY * 20.0);
         this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, this.maxScroll));
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

   private static class ConfirmReleaseScreen extends Screen {
      private final Screen parent;
      private final Component message;
      private final Consumer<Boolean> callback;

      protected ConfirmReleaseScreen(Screen parent, Component title, Component message, Consumer<Boolean> callback) {
         super(title);
         this.parent = parent;
         this.message = message;
         this.callback = callback;
      }

      protected void init() {
         super.init();
         int buttonWidth = 60;
         int buttonHeight = 20;
         int spacing = 10;
         int totalWidth = buttonWidth * 2 + spacing;
         int startX = (this.width - totalWidth) / 2;
         int buttonY = this.height / 2 + 20;
         this.addRenderableWidget(Button.builder(Component.translatable("gui.yes"), button -> {
            this.callback.accept(true);
            this.minecraft.setScreen(this.parent);
         }).bounds(startX, buttonY, buttonWidth, buttonHeight).build());
         this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> {
            this.callback.accept(false);
            this.minecraft.setScreen(this.parent);
         }).bounds(startX + buttonWidth + spacing, buttonY, buttonWidth, buttonHeight).build());
      }

      public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
         this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
         Component titleComponent = this.getTitle();
         int titleWidth = this.font.width(titleComponent);
         guiGraphics.drawString(this.font, titleComponent, (this.width - titleWidth) / 2, this.height / 2 - 40, 16777215);
         int messageWidth = this.font.width(this.message);
         guiGraphics.drawString(this.font, this.message, (this.width - messageWidth) / 2, this.height / 2 - 10, 16777215);
         super.render(guiGraphics, mouseX, mouseY, partialTick);
      }

      public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
         guiGraphics.fill(0, 0, this.width, this.height, -2013265920);
         int boxWidth = 300;
         int boxHeight = 120;
         int boxX = (this.width - boxWidth) / 2;
         int boxY = (this.height - boxHeight) / 2;
         guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, -870178270);
         guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + 1, -1);
         guiGraphics.fill(boxX, boxY + boxHeight - 1, boxX + boxWidth, boxY + boxHeight, -1);
         guiGraphics.fill(boxX, boxY, boxX + 1, boxY + boxHeight, -1);
         guiGraphics.fill(boxX + boxWidth - 1, boxY, boxX + boxWidth, boxY + boxHeight, -1);
      }

      public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
         if (keyCode == 256) {
            this.callback.accept(false);
            this.minecraft.setScreen(this.parent);
            return true;
         } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
         }
      }

      public boolean isPauseScreen() {
         return false;
      }
   }

   private static class FamiliarEntry {
      final UUID id;
      final AbstractSpellCastingPet familiar;
      final String displayName;
      final float health;
      final int armor;
      final int enraged;
      final boolean canBlock;

      FamiliarEntry(UUID id, AbstractSpellCastingPet familiar, String displayName, float health, int armor, int enraged, boolean canBlock) {
         this.id = id;
         this.familiar = familiar;
         this.displayName = displayName;
         this.health = health;
         this.armor = armor;
         this.enraged = enraged;
         this.canBlock = canBlock;
      }
   }
}
