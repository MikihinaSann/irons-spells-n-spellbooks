package com.somake.somakespells.client;

import com.somake.somakespells.registries.ModMobEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;

public final class RedSoulHudOverlay {
   public static final Layer INSTANCE = RedSoulHudOverlay::render;
   private static final int BAR_WIDTH = 122;
   private static final int BAR_HEIGHT = 7;

   private RedSoulHudOverlay() {
   }

   private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
      Minecraft minecraft = Minecraft.getInstance();
      if (ModMobEffects.RED_SOUL != null && !minecraft.options.hideGui && minecraft.player != null && !minecraft.player.isSpectator()) {
         MobEffectInstance effect = minecraft.player.getEffect(ModMobEffects.RED_SOUL);
         if (effect != null && !effect.isInfiniteDuration()) {
            int duration = Math.max(0, effect.getDuration());
            float progress = Mth.clamp(duration / 7200.0F, 0.0F, 1.0F);
            int x = graphics.guiWidth() - 122 - 12;
            int y = graphics.guiHeight() - 25;
            int fill = Math.round(120.0F * progress);
            graphics.fill(x - 1, y - 1, x + 122 + 1, y + 7 + 1, -1610612736);
            graphics.fill(x, y, x + 122, y + 7, -802552567);
            if (fill > 0) {
               graphics.fill(x + 1, y + 1, x + 1 + fill, y + 7 - 1, -1760715);
               graphics.fill(x + 1, y + 1, x + 1 + fill, y + 2, -34685);
            }
         }
      }
   }
}
