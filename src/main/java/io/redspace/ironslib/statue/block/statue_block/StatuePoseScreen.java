package io.redspace.ironslib.statue.block.statue_block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.internal.network.PlatformPacketDistributor;
import io.redspace.ironslib.internal.network.packet.SelectStatuePosePacket;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class StatuePoseScreen extends Screen {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_lib", "textures/gui/statue_pose_screen.png");
   private static final Quaternionf PREVIEW_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, (float) (-Math.PI / 6), (float) Math.PI);
   private static final Quaternionf OPTION_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, (float) (Math.PI / 6), (float) Math.PI);
   private static final int PREVIEW_X = 8;
   private static final int PREVIEW_Y = 24;
   private static final int PREVIEW_WIDTH = 108;
   private static final int PREVIEW_HEIGHT = 162;
   private static final int OPTIONS_WINDOW_X = 122;
   private static final int OPTIONS_WINDOW_Y = 24;
   private static final int OPTIONS_WINDOW_WIDTH = 108;
   private static final int OPTIONS_WINDOW_HEIGHT = 162;
   private static final int TITLE_X = 67;
   private static final int TITLE_Y = 8;
   private static final int TITLE_WIDTH = 103;
   private static final int TITLE_HEIGHT = 10;
   private static final int POSE_OPTION_WIDTH = 36;
   private static final int POSE_OPTION_HEIGHT = 54;
   private final BlockPos pos;
   private int leftPos;
   private int topPos;
   private final int imageWidth;
   private final int imageHeight;
   private int scrollOffset;
   private boolean isScrollbarHeld;
   private boolean isDraggingPreview;
   private float previewRotationDegrees;
   private List<StatuePoseScreen.PoseOption> poseOptions;
   PlayerStatueBlockEntity previewStatue;
   StatueData statueData;
   Button flipPoseButton;

   public StatuePoseScreen(BlockPos pos, StatueData statueData) {
      super(Component.empty());
      this.pos = pos;
      this.imageWidth = 246;
      this.imageHeight = 196;
      this.statueData = statueData;
      this.setupPreviewStatue();
      this.setupFlipPoseButton();
   }

   public void init() {
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      this.poseOptions = new ArrayList<>();
      ArrayList<PlayerStatuePose> poses = new ArrayList<>(List.of(PlayerStatuePose.values()));
      poses.sort(Comparator.comparing(PlayerStatuePose::getSerializedName));
      if (this.statueData != null) {
         int selectedPose = -1;

         for (int i = 0; i < poses.size(); i++) {
            PlayerStatuePose pose = poses.get(i);
            int optionsPerRow = 3;
            int x = this.leftPos + 122 + i % optionsPerRow * 36;
            int y = this.topPos + 24 + i / optionsPerRow * 54;
            this.poseOptions.add(new StatuePoseScreen.PoseOption(Button.builder(Component.empty(), button -> {
            }).bounds(x, y, 36, 54), i, this.statueData, pose));
            if (this.statueData.pose() == pose) {
               selectedPose = i;
            }
         }

         if (selectedPose >= 0) {
            this.setScrollOffset(Math.min(selectedPose / 3, this.getMaxScroll()));
         } else {
            this.setScrollOffset(this.scrollOffset);
         }

         this.setupFlipPoseButton();
      }
   }

   private void setupFlipPoseButton() {
      Button.Builder flipBuilder = Button.builder(Component.empty(), button -> this.updatePose(this.statueData.pose(), !this.statueData.flipped()))
            .bounds(this.leftPos + 8 + 108 - 22, this.topPos + 24 + 162 - 22, 22, 22);
      this.flipPoseButton = new Button(
         flipBuilder.x, flipBuilder.y, flipBuilder.width, flipBuilder.height, flipBuilder.message, flipBuilder.onPress, flipBuilder.createNarration
      ) {
         static final WidgetSprites SPRITES = new WidgetSprites(
            IronsLib.id("statue_pose_screen/flip_pose"), IronsLib.id("statue_pose_screen/flip_pose"), IronsLib.id("statue_pose_screen/flip_pose_highlighted")
         );

         protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
         }
      };
      if (this.statueData == null) {
         this.flipPoseButton.active = false;
         this.flipPoseButton.visible = false;
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.render(guiGraphics, mouseX, mouseY, partialTick);
      if (this.previewStatue != null) {
         float scale = 3.7125F;
         Quaternionf rotation = new Quaternionf(PREVIEW_ANGLE)
            .rotateAxis(this.previewRotationDegrees * (float) (Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F));
         renderStatueInInventory(
            guiGraphics, (float)(this.leftPos + 8) + 54.0F, (float)(this.topPos + 24) + 81.0F, scale, new Vector3f(), rotation, this.previewStatue
         );
      }

      this.flipPoseButton.render(guiGraphics, mouseX, mouseY, partialTick);

      for (StatuePoseScreen.PoseOption option : this.poseOptions) {
         if (option.visible) {
            option.render(guiGraphics, mouseX, mouseY, partialTick);
            if (option.isHovered()) {
               this.setTooltipForNextRenderPass(option.getTooltipList());
            }
         }
      }
   }

   public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      guiGraphics.blitSprite(IronsLib.id("transmog_table/scroller"), this.getScrollBarX(), this.getScrollBarY(), 6, 27);
      this.renderPlayerName(guiGraphics);
   }

   private void renderPlayerName(@NotNull GuiGraphics guiGraphics) {
      Component title;
      if (this.statueData == null) {
         title = Component.translatable("block.irons_lib.player_statue.unknown_player");
      } else {
         title = Component.literal(PatreonData.profileFromUUID(this.statueData.uuid()).username());
      }

      int width = this.font.width(title.getString());
      float scale = Math.clamp(82.4F / (float)width, 0.0F, 1.0F);
      PoseStack stack = guiGraphics.pose();
      stack.pushPose();
      stack.translate((float)(this.leftPos + 67) + 51.5F, (float)(this.topPos + 8 + 10), 0.0F);
      if (scale < 1.0F) {
         stack.scale(scale, scale, scale);
      }

      guiGraphics.drawString(this.font, title, -width / 2, -9, -1);
      stack.popPose();
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.isScrollbarHeld = false;
      this.isDraggingPreview = false;
      if (this.flipPoseButton.mouseClicked(mouseX, mouseY, button)) {
         return true;
      } else {
         for (StatuePoseScreen.PoseOption option : this.poseOptions) {
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
         } else if (mouseX >= (double)(this.leftPos + 8)
            && mouseX < (double)(this.leftPos + 8 + 108)
            && mouseY >= (double)(this.topPos + 24)
            && mouseY < (double)(this.topPos + 24 + 162)) {
            this.isDraggingPreview = true;
            return true;
         } else {
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
      int max = this.getMaxScroll();
      if (this.isScrollbarHeld) {
         int scrollZoneMin = this.topPos + 24;
         int scrollZoneMax = scrollZoneMin + 162;
         float scrollOffs = ((float)pMouseY - (float)scrollZoneMin - 7.5F) / ((float)(scrollZoneMax - scrollZoneMin) - 15.0F);
         scrollOffs = Mth.clamp(scrollOffs, 0.0F, 1.0F);
         int i = Math.max((int)((double)(scrollOffs * (float)max) + 0.5), 0);
         if (i != this.scrollOffset) {
            this.setScrollOffset(i);
         }

         return true;
      } else if (this.isDraggingPreview) {
         this.previewRotationDegrees += (float)(pDragX / 108.0 * 180.0);
         return true;
      } else {
         return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      Key mouseKey = InputConstants.getKey(keyCode, scanCode);
      if (super.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (net.neoforged.neoforge.client.NeoKeyMappings.isActiveAndMatches(this.minecraft.options.keyInventory, mouseKey)) {
         this.onClose();
         return true;
      } else {
         return false;
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   private int getMaxScroll() {
      int optionsPerRow = 3;
      int rowsRequired = (int)Math.ceil((double)this.poseOptions.size() / (double)optionsPerRow);
      return Math.max(0, rowsRequired - 3);
   }

   private void setScrollOffset(int scrollOffset) {
      this.scrollOffset = scrollOffset;
      int optionsPerRow = 3;
      int minIndex = scrollOffset * optionsPerRow;
      int optionsPerColumn = 3;
      int maxIndex = minIndex + optionsPerRow * optionsPerColumn;

      for (int i = 0; i < this.poseOptions.size(); i++) {
         StatuePoseScreen.PoseOption option = this.poseOptions.get(i);
         option.setY(option.originalY - scrollOffset * 54);
         if (i >= minIndex && i < maxIndex) {
            option.active = true;
            option.visible = true;
         } else {
            option.active = false;
            option.visible = false;
         }
      }
   }

   private int getScrollBarX() {
      return this.leftPos + 122 + 108 + 2;
   }

   private int getScrollBarY() {
      return this.topPos + 24 + (int)((float)this.scrollOffset / (float)this.getMaxScroll() * 135.0F);
   }

   private void updatePose(PlayerStatuePose pose, boolean flipped) {
      StatueData current = this.statueData;
      StatueData updated = current.updatePose(pose).updateFlipped(flipped);
      if (!current.equals(updated)) {
         this.statueData = updated;
         this.setupPreviewStatue();
         PlatformPacketDistributor.sendToServer(new SelectStatuePosePacket(this.pos, this.statueData.pose(), this.statueData.flipped()));
      }
   }

   private void setupPreviewStatue() {
      this.previewStatue = PlayerStatueBlockEntity.renderable(this.statueData);
   }

   public static void renderStatueInInventory(
      GuiGraphics guiGraphics, float x, float y, float scale, Vector3f translate, Quaternionf pose, PlayerStatueBlockEntity statue
   ) {
      scale *= 16.0F;
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate((double)x, (double)y, 50.0);
      guiGraphics.pose().scale(scale, scale, -scale);
      guiGraphics.pose().translate(translate.x, translate.y + 1.0F, translate.z);
      guiGraphics.pose().mulPose(pose);
      guiGraphics.pose().translate(-0.5F, 0.0F, -0.5F);
      Lighting.setupForEntityInInventory();
      BlockEntityRenderDispatcher blockRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
      RenderSystem.runAsFancy(
         () -> {
            BlockEntityRenderer<PlayerStatueBlockEntity> blockentityrenderer = blockRenderDispatcher.getRenderer(statue);
            if (blockentityrenderer != null) {
               blockentityrenderer.render(
                  statue, DeltaTracker.ZERO.getGameTimeDeltaTicks(), guiGraphics.pose(), guiGraphics.bufferSource(), 240, OverlayTexture.NO_OVERLAY
               );
            }
         }
      );
      guiGraphics.flush();
      guiGraphics.pose().popPose();
      Lighting.setupFor3DItems();
   }

   class PoseOption extends Button {
      final int index;
      final int originalY;
      final int originalX;
      final PlayerStatueBlockEntity entity;
      final PlayerStatuePose pose;

      PoseOption(Builder builder, int index, StatueData statueData, PlayerStatuePose pose) {
         super(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration);
         this.index = index;
         this.originalY = this.getY();
         this.originalX = this.getX();
         this.entity = PlayerStatueBlockEntity.renderable(new StatueData(statueData.uuid(), pose, false));
         this.pose = pose;
      }

      public void onPress() {
         StatuePoseScreen.this.updatePose(this.pose, StatuePoseScreen.this.statueData.flipped());
      }

      protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
         boolean hovered = this.isHoveredOrFocused();
         boolean selected = this.pose == StatuePoseScreen.this.statueData.pose();
         ResourceLocation frameSprite = IronsLib.id("transmog_table/transmog_option");
         if (selected) {
            frameSprite = frameSprite.withSuffix("_selected");
         } else if (hovered) {
            frameSprite = frameSprite.withSuffix("_highlighted");
         }

         if (!frameSprite.getPath().endsWith("option")) {
            frameSprite = frameSprite.withPrefix("gui/sprites/");
         }

         guiGraphics.blitSprite(frameSprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         float scale = 1.2375001F;
         StatuePoseScreen.renderStatueInInventory(
            guiGraphics, (float)this.getX() + 18.0F, (float)this.getY() + 27.0F, scale, new Vector3f(), StatuePoseScreen.OPTION_ANGLE, this.entity
         );
      }

      public List<FormattedCharSequence> getTooltipList() {
         return Stream.of(
               Component.translatable(this.pose.descriptionId()),
               Component.translatable("block.irons_lib.player_statue.pose_guide")
                  .withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY})
            )
            .flatMap(component -> Tooltip.splitTooltip(StatuePoseScreen.this.minecraft, component).stream())
            .toList();
      }
   }
}
