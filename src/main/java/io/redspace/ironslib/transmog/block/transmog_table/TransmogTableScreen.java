package io.redspace.ironslib.transmog.block.transmog_table;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.internal.mixin.PlayerAccessor;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.transmog.TransmogHolder;
import io.redspace.ironslib.transmog.TransmogItemData;
import io.redspace.ironslib.transmog.client.ITransmogPreview;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransmogTableScreen extends AbstractContainerScreen<TransmogTableMenu> {
   public static final GameProfile TRANSMOG_PREVIEW = new GameProfile(UUID.fromString("db3ebb97-ab61-484d-ba69-001dc920a330"), "[Transmog Preview]");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_lib", "textures/gui/transmog_table.png");
   private static final ResourceLocation INDICATOR_SPRITE = ResourceLocation.fromNamespaceAndPath("irons_lib", "transmog_table/place_transmog_indicator");
   private static final int TRANSMOG_WINDOW_X = 118;
   private static final int TRANSMOG_WINDOW_Y = 6;
   private static final int TRANSMOG_WINDOW_WIDTH = 72;
   private static final int TRANSMOG_WINDOW_HEIGHT = 108;
   private static final int TRANSMOG_OPTION_HEIGHT = 36;
   private static final int TRANSMOG_OPTION_WIDTH = 24;
   private static final int PREVIEW_WINDOW_X = 60;
   private static final int PREVIEW_WINDOW_Y = 24;
   private static final int PREVIEW_WINDOW_WIDTH = 52;
   private static final int PREVIEW_WINDOW_HEIGHT = 84;
   private static final Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, 0.0F, (float) Math.PI);
   private List<TransmogTableScreen.TransmogOption> transmogOptions = new ArrayList<>();
   private Button transmogForgeButton;
   private LivingEntity armorStandPreview;
   private LivingEntity playerPreview;
   private int scrollOffset;
   private boolean isScrollbarHeld;
   private boolean shouldShowPlacementHint;
   private int dyeColor = -1;
   @Nullable
   ColorPickerScreen popupScreen;
   protected static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
      IronsLib.id("gui/sprites/transmog_table/transmog_button_selected"),
      IronsLib.id("gui/sprites/transmog_table/transmog_button_disabled"),
      IronsLib.id("gui/sprites/transmog_table/transmog_button_highlighted")
   );

   public TransmogTableScreen(TransmogTableMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.initPreviewEntities();
      ((TransmogTableMenu)(Object) this.menu).armorSlotsChangedCallback = this::onArmorSlotsChanged;
      ((TransmogTableMenu)(Object) this.menu).transmogSelectionChangedCallback = this::onSelectedTransmogChanged;
      this.imageWidth = 204;
      this.imageHeight = 200;
      this.shouldShowPlacementHint = true;
   }

   protected void init() {
      super.init();
      if (Minecraft.getInstance().player != null) {
         PatreonPermissions permissions = PatreonData.getPatreonPermissions(Minecraft.getInstance().player);
         Button.Builder forgeBuilder = Button.builder(Component.empty(), button -> {
               int id = ((TransmogTableMenu)(Object) this.menu).packTransmogRequest(this.dyeColor);
               if (((TransmogTableMenu)(Object) this.menu).clickMenuButton(Minecraft.getInstance().player, id)) {
                  Minecraft.getInstance().gameMode.handleInventoryButtonClick(((TransmogTableMenu)(Object) this.menu).containerId, id);
               }
            })
            .bounds(31 + this.leftPos, this.topPos + 77, 20, 20);
         this.transmogForgeButton = new Button(forgeBuilder.x, forgeBuilder.y, forgeBuilder.width, forgeBuilder.height, forgeBuilder.message, forgeBuilder.onPress, forgeBuilder.createNarration) {
                     protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                        ResourceLocation sprite = IronsLib.id("transmog_table/transmog_button");
                        guiGraphics.blitSprite(
                           TransmogTableScreen.BUTTON_SPRITES.get(this.active, this.isHoveredOrFocused()),
                           this.getX(),
                           this.getY(),
                           this.getWidth(),
                           this.getHeight()
                        );
                     }

                     public void playDownSound(SoundManager handler) {
                        handler.play(
                           SimpleSoundInstance.forUI(
                              (SoundEvent)IronsLibRegistries.SoundRegistry.TRANSMOG_TABLE_FORGE.get(), (float)(0.9 + Math.random() * 0.25), 0.75F
                           )
                        );
                     }
                  };
         this.transmogOptions = new ArrayList<>();

         for (int i = 0; i < ((TransmogTableMenu)(Object) this.menu).transmogActions.size(); i++) {
            TransmogTableMenu.TransmogAction action = ((TransmogTableMenu)(Object) this.menu).transmogActions.get(i);
            int optionsPerRow = 3;
            int x = this.leftPos + 118 + i % optionsPerRow * 24;
            int y = this.topPos + 6 + i / optionsPerRow * 36;
            this.transmogOptions
               .add(
                  new TransmogTableScreen.TransmogOption(
                     Button.builder(
                           Component.empty(),
                           button -> {
                              if (((TransmogTableMenu)(Object) this.menu)
                                 .clickMenuButton(Minecraft.getInstance().player, ((TransmogTableScreen.TransmogOption)button).index)) {
                                 Minecraft.getInstance()
                                    .gameMode
                                    .handleInventoryButtonClick(((TransmogTableMenu)(Object) this.menu).containerId, ((TransmogTableScreen.TransmogOption)button).index);
                              }
                           }
                        )
                        .bounds(x, y, 24, 36),
                     action,
                     i,
                     action.remove() || permissions.canUse(action.holder())
                  )
               );
         }

         this.onSelectedTransmogChanged();
         this.setScrollOffset(this.scrollOffset);
         if (this.popupScreen != null) {
            this.popupScreen.init();
            this.popupScreen.setPos(this.leftPos - this.popupScreen.imageWidth, this.topPos + 18);
         }
      }
   }

   protected void initPreviewEntities() {
      this.armorStandPreview = new RemotePlayer(Minecraft.getInstance().level, TRANSMOG_PREVIEW);
      this.armorStandPreview.yBodyRot = 210.0F;
      this.armorStandPreview.yHeadRot = this.armorStandPreview.yBodyRot;
      this.armorStandPreview.yHeadRotO = this.armorStandPreview.yBodyRot;
      this.armorStandPreview.setInvisible(true);
      ((ITransmogPreview)(Object) this.armorStandPreview).irons_lib$setTransmogPreview(true);
      this.playerPreview = new RemotePlayer(Minecraft.getInstance().level, Minecraft.getInstance().getGameProfile());
      ((ITransmogPreview)(Object) this.playerPreview).irons_lib$setTransmogPreview(true);
      EntityDataAccessor<Byte> key = PlayerAccessor.getDataPlayerModeCustomisation();
      this.playerPreview.getEntityData().set(key, (Byte)Minecraft.getInstance().player.getEntityData().get(key));
   }

   public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.render(guiGraphics, mouseX, mouseY, partialTick);
      this.renderTooltip(guiGraphics, mouseX, mouseY);

      for (TransmogTableScreen.TransmogOption option : this.transmogOptions) {
         if (option.visible) {
            this.resetArmorstandPreview();
            option.setupArmorPreview(Minecraft.getInstance().player, this.armorStandPreview);
            option.renderArmorPreview(guiGraphics, mouseX, mouseY, partialTick, this.armorStandPreview, null);
         }
      }

      this.transmogForgeButton.render(guiGraphics, mouseX, mouseY, partialTick);
      int x = this.leftPos + 60;
      int y = this.topPos + 24;
      InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x, y, x + 52, y + 84, 34, 0.1875F, (float)mouseX, (float)mouseY, this.playerPreview);
      if (this.popupScreen != null) {
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(0.0F, 0.0F, 500.0F);
         this.popupScreen.render(guiGraphics, mouseX, mouseY, partialTick);
         guiGraphics.pose().popPose();
      }

      if (((TransmogTableMenu)(Object) this.menu).selectedTransmogIndex >= 0) {
         if (!((TransmogTableMenu)(Object) this.menu).transmogSlot.hasItem() && this.shouldShowPlacementHint) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose()
               .translate(
                  (float)(this.leftPos + ((TransmogTableMenu)(Object) this.menu).transmogSlot.x + 8),
                  (float)(this.topPos + ((TransmogTableMenu)(Object) this.menu).transmogSlot.y + 8),
                  0.0F
               );
            float f = 1.5F + Mth.sin(((float)this.minecraft.player.tickCount + partialTick) * 0.25F) * 0.35F;
            guiGraphics.pose().scale(f, f, 1.0F);
            guiGraphics.blitSprite(INDICATOR_SPRITE, -8, -8, 16, 16);
            guiGraphics.pose().popPose();
         } else {
            this.shouldShowPlacementHint = false;
         }
      }
   }

   protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
      for (TransmogTableScreen.TransmogOption button : this.transmogOptions) {
         if (button.isHovered() && button.isActive()) {
            guiGraphics.renderTooltip(this.font, button.getTooltip(Minecraft.getInstance().player), Optional.empty(), x, y);
            return;
         }
      }

      super.renderTooltip(guiGraphics, x, y);
   }

   protected void renderBg(GuiGraphics guiHelper, float partialTick, int mouseX, int mouseY) {
      guiHelper.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      guiHelper.blitSprite(IronsLib.id("transmog_table/scroller"), this.getScrollBarX(), this.getScrollBarY(), 6, 27);
   }

   protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
      super.renderSlot(guiGraphics, slot);
      if (slot instanceof TransmogArmorSlot armorSlot) {
         ItemStack itemstack = armorSlot.getItem();
         if (itemstack.isEmpty() && slot.isActive()) {
            guiGraphics.blitSprite(armorSlot.getEmptyIcon(), slot.x, slot.y, 16, 16);
         }
      }
   }

   protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
   }

   private void openColorPicker() {
      this.popupScreen = new ColorPickerScreen(this::closeColorPicker, this::pickColor, this.dyeColor);
      this.popupScreen.init(this.minecraft, this.width, this.height);
      this.popupScreen.init();
      this.popupScreen.setPos(this.leftPos - this.popupScreen.imageWidth, this.topPos + 18);
   }

   private void closeColorPicker() {
      this.popupScreen = null;
   }

   private void pickColor(int color) {
      this.dyeColor = color;
      this.setupPlayerPreview();
   }

   private int getScrollBarX() {
      return this.leftPos + 118 + 72 + 2;
   }

   private int getScrollBarY() {
      return this.topPos + 6 + (int)((float)this.scrollOffset / (float)this.getMaxScroll() * 81.0F);
   }

   public void updateTransmogButtonStatus() {
      this.transmogForgeButton.active = ((TransmogTableMenu)(Object) this.menu).transmogSlot.hasItem()
         && ((TransmogTableMenu)(Object) this.menu).getSelectedTransmogAction() != null
         && ((TransmogTableMenu)(Object) this.menu)
            .getSelectedTransmogAction()
            .canPerform(((TransmogTableMenu)(Object) this.menu).transmogSlot.getItem(), PatreonData.getPatreonPermissions(Minecraft.getInstance().player));
   }

   public void onSelectedTransmogChanged() {
      this.dyeColor = -1;
      TransmogTableMenu.TransmogAction transmog = ((TransmogTableMenu)(Object) this.menu).getSelectedTransmogAction();
      this.closeColorPicker();
      if (transmog != null && transmog.holder() != null) {
         this.dyeColor = transmog.holder().dyeConfig().defaultColor();
         if (transmog.holder().dyeConfig().dyeable()) {
            ItemStack currentItem = ((TransmogTableMenu)(Object) this.menu).transmogSlot.getItem();
            if (!currentItem.isEmpty() && TransmogItemData.has(currentItem)) {
               TransmogItemData currentItemData = TransmogItemData.get(currentItem);
               if (transmog.holder() == currentItemData.transmog()) {
                  this.dyeColor = currentItemData.dyeColor();
               }
            }

            this.openColorPicker();
         }
      }

      this.updateTransmogButtonStatus();
      this.setupPlayerPreview();
   }

   private int getMaxScroll() {
      int optionsPerRow = 3;
      int rowsRequired = (int)Math.ceil((double)this.transmogOptions.size() / (double)optionsPerRow);
      return Math.max(0, rowsRequired - 3);
   }

   private void setScrollOffset(int scrollOffset) {
      this.scrollOffset = scrollOffset;
      int optionsPerRow = 3;
      int minIndex = scrollOffset * optionsPerRow;
      int optionsPerColumn = 3;
      int maxIndex = minIndex + optionsPerRow * optionsPerColumn;

      for (int i = 0; i < this.transmogOptions.size(); i++) {
         TransmogTableScreen.TransmogOption option = this.transmogOptions.get(i);
         option.setY(option.originalY - scrollOffset * 36);
         if (i >= minIndex && i < maxIndex) {
            option.active = true;
            option.visible = true;
         } else {
            option.active = false;
            option.visible = false;
         }
      }
   }

   public void resetArmorstandPreview() {
      this.armorStandPreview.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
      this.armorStandPreview.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
      this.armorStandPreview.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
      this.armorStandPreview.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
   }

   public void onArmorSlotsChanged() {
      this.setupPlayerPreview();
      this.updateTransmogButtonStatus();
   }

   public void setupPlayerPreview() {
      Player actualPlayer = Minecraft.getInstance().player;
      this.playerPreview.setItemSlot(EquipmentSlot.HEAD, actualPlayer.getItemBySlot(EquipmentSlot.HEAD));
      this.playerPreview.setItemSlot(EquipmentSlot.CHEST, actualPlayer.getItemBySlot(EquipmentSlot.CHEST));
      this.playerPreview.setItemSlot(EquipmentSlot.LEGS, actualPlayer.getItemBySlot(EquipmentSlot.LEGS));
      this.playerPreview.setItemSlot(EquipmentSlot.FEET, actualPlayer.getItemBySlot(EquipmentSlot.FEET));
      if (!((TransmogTableMenu)(Object) this.menu).transmogContainer.isEmpty()
         && ((TransmogTableMenu)(Object) this.menu).transmogContainer.getItem(0).getItem() instanceof Equipable equipable
         && equipable.getEquipmentSlot().getType() == Type.HUMANOID_ARMOR) {
         ItemStack transmogPreview = ((TransmogTableMenu)(Object) this.menu).transmogContainer.getItem(0).copy();
         TransmogTableMenu.TransmogAction selectedTransmog = ((TransmogTableMenu)(Object) this.menu).getSelectedTransmogAction();
         if (selectedTransmog != null) {
            if (selectedTransmog.remove()) {
               TransmogItemData.remove(transmogPreview);
            } else if (selectedTransmog.holder().supportsSlot(equipable.getEquipmentSlot())) {
               TransmogItemData.set(transmogPreview, new TransmogItemData(selectedTransmog.holder(), this.dyeColor));
            }
         }

         this.playerPreview.setItemSlot(equipable.getEquipmentSlot(), transmogPreview);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.popupScreen != null && this.popupScreen.mouseClicked(mouseX, mouseY, button)) {
         return true;
      } else if (this.transmogForgeButton.mouseClicked(mouseX, mouseY, button)) {
         return true;
      } else {
         for (TransmogTableScreen.TransmogOption option : this.transmogOptions) {
            if (option.mouseClicked(mouseX, mouseY, button)) {
               return true;
            }
         }

         if (mouseX >= (double)this.getScrollBarX()
            && mouseX < (double)(this.getScrollBarX() + 6)
            && mouseY >= (double)this.getScrollBarY()
            && mouseY < (double)(this.getScrollBarY() + 27)) {
            this.isScrollbarHeld = true;
            return true;
         } else {
            this.isScrollbarHeld = false;
            return super.mouseClicked(mouseX, mouseY, button);
         }
      }
   }

   public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
      int maxScroll = this.getMaxScroll();
      int newScroll = Math.clamp((long)(this.scrollOffset - (int)pScrollY), 0, maxScroll);
      if (newScroll != this.scrollOffset) {
         this.setScrollOffset(newScroll);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      if (this.popupScreen != null && this.popupScreen.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) {
         return true;
      } else {
         int max = this.getMaxScroll();
         if (this.isScrollbarHeld) {
            int scrollZoneMin = this.topPos + 6;
            int scrollZoneMax = scrollZoneMin + 108;
            float scrollOffs = ((float)pMouseY - (float)scrollZoneMin - 7.5F) / ((float)(scrollZoneMax - scrollZoneMin) - 15.0F);
            scrollOffs = Mth.clamp(scrollOffs, 0.0F, 1.0F);
            int i = Math.max((int)((double)(scrollOffs * (float)max) + 0.5), 0);
            if (i != this.scrollOffset) {
               this.setScrollOffset(i);
            }

            return true;
         } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
         }
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.popupScreen != null && this.popupScreen.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
   }

   public boolean charTyped(char codePoint, int modifiers) {
      return this.popupScreen != null && this.popupScreen.charTyped(codePoint, modifiers) ? true : super.charTyped(codePoint, modifiers);
   }

   class TransmogOption extends Button {
      final TransmogTableMenu.TransmogAction action;
      final int index;
      final boolean unlocked;
      final ItemStack[] previewItems;
      final int originalY;
      final int originalX;

      private static ItemStack createStack(Item item, TransmogHolder holder) {
         ItemStack stack = new ItemStack(item);
         TransmogItemData.set(stack, new TransmogItemData(holder));
         return stack;
      }

      TransmogOption(Builder builder, TransmogTableMenu.TransmogAction action, int index, boolean unlocked) {
         super(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration);
         this.action = action;
         this.index = index;
         this.unlocked = unlocked;
         this.previewItems = this.action.remove()
            ? null
            : new ItemStack[]{
               createStack(Items.IRON_BOOTS, this.action.holder()),
               createStack(Items.IRON_LEGGINGS, this.action.holder()),
               createStack(Items.IRON_CHESTPLATE, this.action.holder()),
               createStack(Items.IRON_HELMET, this.action.holder())
            };
         this.originalY = this.getY();
         this.originalX = this.getX();
      }

      protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
         boolean hovered = this.isHoveredOrFocused();
         boolean selected = ((TransmogTableMenu)TransmogTableScreen.this.menu).selectedTransmogIndex == this.index;
         ResourceLocation frameSprite = IronsLib.id("transmog_table/transmog_option");
         if (selected) {
            frameSprite = frameSprite.withSuffix("_selected");
         } else {
            if (!this.unlocked) {
               frameSprite = frameSprite.withSuffix("_disabled");
            }

            if (hovered) {
               frameSprite = frameSprite.withSuffix("_highlighted");
            }
         }

         if (!frameSprite.getPath().endsWith("option")) {
            frameSprite = frameSprite.withPrefix("gui/sprites/");
         }

         guiGraphics.blitSprite(frameSprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
         if (this.action.remove()) {
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.75F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            guiGraphics.blitSprite(IronsLib.id("transmog_table/remove_transmog_overlay"), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         } else if (this.action.holder().dyeConfig().dyeable()) {
            guiGraphics.blitSprite(IronsLib.id("transmog_table/dyeable"), this.getX() + this.getWidth() - 12, this.getY() + this.getHeight() - 12, 11, 11);
         }

         if (!this.unlocked) {
            guiGraphics.blitSprite(IronsLib.id("transmog_table/lock"), this.getX() + this.getWidth() / 2 - 5, this.getY() + this.getHeight() / 2 - 7, 10, 14);
         }

         guiGraphics.pose().popPose();
      }

      protected void setupArmorPreview(Player player, LivingEntity armorStand) {
         for (EquipmentSlot slot : TransmogTableMenu.HUMANOID_ARMOR_SLOTS) {
            ItemStack previewStack;
            if (this.action.remove()) {
               previewStack = player.getItemBySlot(slot).copy();
               if (((TransmogTableMenu)TransmogTableScreen.this.menu).transmogSlot.getItem().getItem() instanceof Equipable equipable
                  && equipable.getEquipmentSlot() == slot) {
                  previewStack = ((TransmogTableMenu)TransmogTableScreen.this.menu).transmogSlot.getItem().copy();
               }

               TransmogItemData.remove(previewStack);
            } else {
               previewStack = this.action.holder().supportsSlot(slot) ? this.previewItems[slot.getIndex()] : ItemStack.EMPTY;
            }

            armorStand.setItemSlot(slot, previewStack);
         }
      }

      protected void renderArmorPreview(
         GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, LivingEntity armorStand, @Nullable EquipmentSlot equipmentSlot
      ) {
         float scale = (float)this.getWidth() / 16.0F * 9.5F;
         guiGraphics.pose().pushPose();
         InventoryScreen.renderEntityInInventory(
            guiGraphics,
            (float)this.getX() + (float)this.getWidth() / 2.0F,
            (float)this.getY() + (float)this.getHeight() / 2.0F,
            scale,
            new Vector3f(0.0F, 0.97F, 0.0F),
            TransmogTableScreen.ARMOR_STAND_ANGLE,
            null,
            armorStand
         );
         guiGraphics.pose().popPose();
         this.render(guiGraphics, mouseX, mouseY, partialTick);
      }

      public List<Component> getTooltip(LocalPlayer player) {
         List<Component> list = new ArrayList<>();
         if (this.action.remove()) {
            list.add(Component.translatable("tooltip.irons_lib.transmog_option.remove"));
         } else {
            TransmogHolder holder = this.action.holder();
            boolean canUse = PatreonData.getPatreonPermissions(player).canUse(holder);
            list.add(Component.translatable("tooltip.irons_lib.transmog_option.title").withStyle(ChatFormatting.LIGHT_PURPLE));
            list.add(Component.translatable(holder.descriptionId()).withStyle(ChatFormatting.ITALIC));
            list.add(Component.empty());
            list.add(
               Component.translatable(
                     "tooltip.irons_lib.transmog_option.requirement",
                     new Object[]{Component.translatable(holder.requiredPermission().getDescriptionId()).withStyle(ChatFormatting.GOLD)}
                  )
                  .withStyle(canUse ? ChatFormatting.GREEN : ChatFormatting.RED)
            );
            list.add(Component.empty());
            MutableComponent supportedSlotsList;
            if (this.action.holder().supportedSlots() == TransmogHolder.ALL_SLOTS) {
               supportedSlotsList = Component.translatable("tooltip.irons_lib.transmog_option.supported_slots.all");
            } else {
               supportedSlotsList = Component.empty();
               Set<EquipmentSlot> slots = this.action.holder().supportedSlots();
               int s = slots.size();
               int i = 0;

               for (EquipmentSlot slot : slots) {
                  supportedSlotsList.append(Component.translatable(String.format("tooltip.irons_lib.transmog_option.supported_slots.%s", slot.getName())));
                  if (++i < s) {
                     supportedSlotsList.append(", ");
                  }
               }
            }

            list.add(
               Component.translatable("tooltip.irons_lib.transmog_option.supported_slots", new Object[]{supportedSlotsList}).withStyle(ChatFormatting.GRAY)
            );
         }

         return list;
      }
   }
}
