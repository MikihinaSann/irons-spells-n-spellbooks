package com.snackpirate.aeromancy;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.snackpirate.aeromancy.spells.AASpells;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class AAOverlay implements Layer {
   private static float ticks;
   public static AAOverlay instance = new AAOverlay();

   public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
      ticks = ticks + deltaTracker.getGameTimeDeltaTicks();
      if (!Minecraft.getInstance().options.hideGui && (Minecraft.getInstance().player == null || !Minecraft.getInstance().player.isSpectator())) {
         int screenWidth = guiGraphics.guiWidth();
         int screenHeight = guiGraphics.guiHeight();
         Player player = Minecraft.getInstance().player;
         if (player != null) {
            if (player.hasEffect(AASpells.MobEffects.BREATHLESS)) {
               renderOverlay(guiGraphics, Aeromancy.id("textures/entity/wind_shield_layer.png"), 0.35F, 0.35F, 0.35F, 0.1F, screenWidth, screenHeight);
            }
         }
      }
   }

   private static void renderOverlay(GuiGraphics gui, ResourceLocation texture, float r, float g, float b, float a, int screenWidth, int screenHeight) {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.ONE, DestFactor.ONE, SourceFactor.ONE, DestFactor.ONE);
      gui.setColor(r, g, b, a);
      gui.blit(texture, 0, 0, -90, 80.0F * ticks, ticks, screenWidth, screenHeight, 2 * screenWidth, 2 * screenHeight);
      gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
   }
}
