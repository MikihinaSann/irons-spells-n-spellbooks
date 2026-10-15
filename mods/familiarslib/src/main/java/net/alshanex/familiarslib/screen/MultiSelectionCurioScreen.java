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
import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.data.PlayerFamiliarData;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractMultiSelectionCurio;
import net.alshanex.familiarslib.network.UpdateMultiSelectionCurioPacket;
import net.alshanex.familiarslib.registry.AttachmentRegistry;
import net.alshanex.familiarslib.util.CurioUtils;
import net.alshanex.familiarslib.util.consumables.ConsumableUtils;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MultiSelectionCurioScreen extends Screen {
   private static final int CELL_SIZE = 120;
   private static final int GRID_COLS = 3;
   private static final int GRID_ROWS = 2;
   private static final int PANEL_WIDTH = 400;
   private static final int PANEL_HEIGHT = 280;
   private static final int SCROLL_SPEED = 20;
   private final List<MultiSelectionCurioScreen.FamiliarGridEntry> familiarEntries = new ArrayList<>();
   private final Set<UUID> selectedFamiliars = new HashSet<>();
   private int scrollOffset = 0;
   private int maxScroll = 0;
   private float animationTime = 0.0F;
   private int gridStartX;
   private int gridStartY;
   private static final ResourceLocation HEART_CONTAINER = ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/heart/container.png");
   private static final ResourceLocation HEART_FULL = ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/heart/full.png");
   private static final ResourceLocation ARMOR_FULL = ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/armor_full.png");
   private static final ResourceLocation RED_MUSHROOM = ResourceLocation.withDefaultNamespace("textures/block/red_mushroom.png");

   public MultiSelectionCurioScreen() {
      super(Component.translatable("screen.familiarslib.multi_selection_curio"));
   }

   protected void init() {
      super.init();
      this.loadFamiliarData();
      this.loadSelectedFamiliars();
      this.gridStartX = (this.width - 400) / 2 + 20;
      int availableHeight = this.height - 55;
      this.gridStartY = 40 + (availableHeight - 280) / 2;
      this.gridStartY = Math.max(40, this.gridStartY);
      this.calculateMaxScroll();
   }

   private void loadFamiliarData() {
      this.familiarEntries.clear();
      if (this.minecraft != null && this.minecraft.player != null) {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)this.minecraft.player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
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
               this.familiarEntries.add(new MultiSelectionCurioScreen.FamiliarGridEntry(id, familiar, displayName, displayHealth, armor, enraged, canBlock));
               float maxHealthWithModifiers = ConsumableUtils.calculateMaxHealthWithModifiers(consumableData, baseMaxHealth);
               FamiliarsLib.LOGGER
                  .debug(
                     "Multi Selection curio: Loaded familiar {} - Health: {}/{}, Armor: {}, Enraged: {}, Blocking: {}",
                     new Object[]{id, displayHealth, maxHealthWithModifiers, armor, enraged, canBlock}
                  );
            }
         }
      }
   }

   private void loadSelectedFamiliars() {
      this.selectedFamiliars.clear();
      if (this.minecraft != null && this.minecraft.player != null) {
         Set<UUID> equippedFamiliars = CurioUtils.getSelectedFamiliarsFromEquipped(this.minecraft.player);
         if (!equippedFamiliars.isEmpty()) {
            this.selectedFamiliars.addAll(equippedFamiliars);
         } else {
            ItemStack heldItem = this.minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!(heldItem.getItem() instanceof AbstractMultiSelectionCurio)) {
               heldItem = this.minecraft.player.getItemInHand(InteractionHand.OFF_HAND);
            }

            if (heldItem.getItem() instanceof AbstractMultiSelectionCurio) {
               this.selectedFamiliars.addAll(AbstractMultiSelectionCurio.getSelectedFamiliars(heldItem));
            }
         }
      }
   }

   private void calculateMaxScroll() {
      int totalRows = (int)Math.ceil(this.familiarEntries.size() / 3.0);
      int visibleRows = 2;
      if (totalRows > visibleRows) {
         this.maxScroll = (totalRows - visibleRows) * 120;
      } else {
         this.maxScroll = 0;
      }

      FamiliarsLib.LOGGER
         .debug(
            "Total familiars: {}, Total rows: {}, Visible rows: {}, Max scroll: {}",
            new Object[]{this.familiarEntries.size(), totalRows, visibleRows, this.maxScroll}
         );
   }

   private void drawHeartIcon(GuiGraphics guiGraphics, int x, int y) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
      Component title = Component.translatable("screen.familiarslib.multi_selection_curio");
      int titleWidth = this.font.width(title);
      guiGraphics.drawString(this.font, title, (this.width - titleWidth) / 2, 10, 16777215);
      Component counter = Component.translatable("screen.familiarslib.multi_selection_curio.selected_count", new Object[]{this.selectedFamiliars.size(), 10});
      int counterWidth = this.font.width(counter);
      guiGraphics.drawString(this.font, counter, (this.width - counterWidth) / 2, 25, 16777045);
      this.renderFamiliarGrid(guiGraphics, mouseX, mouseY, partialTick);
      super.render(guiGraphics, mouseX, mouseY, partialTick);
   }

   private void renderFamiliarGrid(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      int scissorX = this.gridStartX;
      int scissorY = this.gridStartY;
      int scissorWidth = 360;
      int scissorHeight = 260;
      this.enableScissor(scissorX, scissorY, scissorX + scissorWidth, scissorY + scissorHeight);
      int currentIndex = 0;

      for (MultiSelectionCurioScreen.FamiliarGridEntry entry : this.familiarEntries) {
         int col = currentIndex % 3;
         int row = currentIndex / 3;
         int cellX = this.gridStartX + col * 120;
         int cellY = this.gridStartY + row * 120 - this.scrollOffset;
         if (cellY + 120 > this.gridStartY - 10 && cellY < this.gridStartY + scissorHeight + 10) {
            this.renderFamiliarCell(guiGraphics, entry, cellX, cellY, mouseX, mouseY, partialTick);
         }

         currentIndex++;
      }

      this.disableScissor();
   }

   private void renderFamiliarCell(
      GuiGraphics guiGraphics, MultiSelectionCurioScreen.FamiliarGridEntry entry, int x, int y, int mouseX, int mouseY, float partialTick
   ) {
      PoseStack poseStack = guiGraphics.pose();
      boolean isSelected = this.selectedFamiliars.contains(entry.id);
      boolean cellVisible = y + 120 > this.gridStartY && y < this.gridStartY + 240;
      boolean isHovered = cellVisible && mouseX >= x + 5 && mouseX < x + 120 - 5 && mouseY >= y + 5 && mouseY < y + 120 - 5;
      int backgroundColor = 1140850688;
      if (isSelected) {
         backgroundColor = 1140915968;
      } else if (isHovered) {
         backgroundColor = 1157627903;
      }

      guiGraphics.fill(x + 5, y + 5, x + 120 - 5, y + 120 - 5, backgroundColor);
      int borderColor = isSelected ? -16711936 : (isHovered ? -1 : -10066330);
      guiGraphics.renderOutline(x + 5, y + 5, 110, 110, borderColor);
      poseStack.pushPose();
      poseStack.translate(x + 60, y + 45, 50.0F);
      poseStack.scale(25.0F, 25.0F, 25.0F);
      Quaternionf rotation = new Quaternionf().rotateY((float)Math.toRadians(45.0));
      poseStack.mulPose(rotation);
      this.renderEntity(guiGraphics, entry.familiar, 0.0F, 0.0F, 0.0F);
      poseStack.popPose();
      String displayName = entry.displayName;
      if (this.font.width(displayName) > 100) {
         displayName = this.font.plainSubstrByWidth(displayName, 95) + "...";
      }

      int nameWidth = this.font.width(displayName);
      guiGraphics.drawString(this.font, displayName, x + (120 - nameWidth) / 2, y + 55, 16777215);
      this.renderAttributesInQuadrants(guiGraphics, entry, x, y + 68);
      if (isSelected) {
         Component checkmark = Component.literal("✓");
         guiGraphics.drawString(this.font, checkmark, x + 120 - 20, y + 10, 65280);
      }
   }

   private void renderAttributesInQuadrants(GuiGraphics guiGraphics, MultiSelectionCurioScreen.FamiliarGridEntry entry, int x, int y) {
      int boxX = x + 20;
      int boxWidth = 80;
      int boxHeight = 40;
      guiGraphics.fill(boxX, y, boxX + boxWidth, y + boxHeight, -1430208320);
      guiGraphics.fill(boxX, y, boxX + boxWidth, y + 1, -8355712);
      guiGraphics.fill(boxX, y + boxHeight - 1, boxX + boxWidth, y + boxHeight, -8355712);
      guiGraphics.fill(boxX, y, boxX + 1, y + boxHeight, -8355712);
      guiGraphics.fill(boxX + boxWidth - 1, y, boxX + boxWidth, y + boxHeight, -8355712);
      int halfWidth = boxWidth / 2;
      int halfHeight = boxHeight / 2;
      Component healthComponent = Component.literal(String.format("%.0f", entry.health));
      int healthTextWidth = this.font.width(healthComponent);
      int healthTotalWidth = 11 + healthTextWidth;
      int healthStartX = boxX + (halfWidth - healthTotalWidth) / 2;
      int healthY = y + (halfHeight - 9) / 2;
      this.drawHeartIcon(guiGraphics, healthStartX, healthY);
      guiGraphics.drawString(this.font, healthComponent, healthStartX + 11, healthY, 16733525);
      Component armorComponent = Component.literal(String.valueOf(entry.armor));
      int armorTextWidth = this.font.width(armorComponent);
      int armorTotalWidth = 11 + armorTextWidth;
      int armorStartX = boxX + halfWidth + (halfWidth - armorTotalWidth) / 2;
      int armorY = y + (halfHeight - 9) / 2;
      this.drawArmorIcon(guiGraphics, armorStartX, armorY);
      guiGraphics.drawString(this.font, armorComponent, armorStartX + 11, armorY, 11184810);
      Component blockComponent = Component.literal(entry.canBlock ? "1" : "0");
      int blockTextWidth = this.font.width(blockComponent);
      int blockTotalWidth = 18 + blockTextWidth;
      int blockStartX = boxX + (halfWidth - blockTotalWidth) / 2;
      int blockY = y + halfHeight + (halfHeight - 16) / 2;
      ItemStack shield = new ItemStack(Items.SHIELD);
      guiGraphics.renderItem(shield, blockStartX, blockY);
      int blockColor = entry.canBlock ? 5635925 : 16733525;
      guiGraphics.drawString(this.font, blockComponent, blockStartX + 18, blockY + 4, blockColor);
      Component enragedComponent = Component.literal(String.valueOf(entry.enraged));
      int enragedTextWidth = this.font.width(enragedComponent);
      int enragedTotalWidth = 18 + enragedTextWidth;
      int enragedStartX = boxX + halfWidth + (halfWidth - enragedTotalWidth) / 2;
      int enragedY = y + halfHeight + (halfHeight - 16) / 2;
      this.drawItemIcon(guiGraphics, RED_MUSHROOM, enragedStartX, enragedY - 5);
      guiGraphics.drawString(this.font, enragedComponent, enragedStartX + 18, enragedY + 4, 16733695);
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
         int currentIndex = 0;

         for (MultiSelectionCurioScreen.FamiliarGridEntry entry : this.familiarEntries) {
            int col = currentIndex % 3;
            int row = currentIndex / 3;
            int cellX = this.gridStartX + col * 120;
            int cellY = this.gridStartY + row * 120 - this.scrollOffset;
            boolean cellVisible = cellY + 120 > this.gridStartY && cellY < this.gridStartY + 240;
            boolean mouseInCell = mouseX >= cellX + 5 && mouseX < cellX + 120 - 5 && mouseY >= cellY + 5 && mouseY < cellY + 120 - 5;
            if (cellVisible && mouseInCell) {
               this.toggleFamiliarSelection(entry.id);
               return true;
            }

            currentIndex++;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   private void toggleFamiliarSelection(UUID familiarId) {
      if (this.selectedFamiliars.contains(familiarId)) {
         this.selectedFamiliars.remove(familiarId);
      } else if (this.selectedFamiliars.size() < 10) {
         this.selectedFamiliars.add(familiarId);
      }

      PacketDistributor.sendToServer(new UpdateMultiSelectionCurioPacket(new HashSet<>(this.selectedFamiliars)), new CustomPacketPayload[0]);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (this.maxScroll > 0) {
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

   private static class FamiliarGridEntry {
      final UUID id;
      final AbstractSpellCastingPet familiar;
      final String displayName;
      final float health;
      final int armor;
      final int enraged;
      final boolean canBlock;

      FamiliarGridEntry(UUID id, AbstractSpellCastingPet familiar, String displayName, float health, int armor, int enraged, boolean canBlock) {
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
