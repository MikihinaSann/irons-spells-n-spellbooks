package net.alshanex.tunes_n_tomes.event;

import java.io.IOException;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SleepyBlurHandler {
   private static final float CYCLE_TICKS = 80.0F;
   private static final float MAX_BLUR_RADIUS = 15.0F;
   private static final float MIN_BLUR_RADIUS = 0.0F;
   private static PostChain blurEffect = null;
   private static boolean wasActive = false;
   private static int lastWidth = -1;
   private static int lastHeight = -1;

   public static void onClientTick() {
      Minecraft mc = Minecraft.getInstance();
      Player player = mc.player;
      if (player != null && mc.level != null) {
         MobEffectInstance sleepyEffect = player.getEffect(TEffectRegistry.SLEEPY);
         boolean shouldBeActive = sleepyEffect != null;
         if (shouldBeActive) {
            if (blurEffect == null) {
               loadBlurShader(mc);
            }

            if (blurEffect != null) {
               int width = mc.getWindow().getWidth();
               int height = mc.getWindow().getHeight();
               if (width != lastWidth || height != lastHeight) {
                  blurEffect.resize(width, height);
                  lastWidth = width;
                  lastHeight = height;
               }

               float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(true);
               float ticksAlive = player.tickCount + partialTick;
               float cycleProgress = ticksAlive % 80.0F / 80.0F;
               float wave = (Mth.cos(cycleProgress * (float) (Math.PI * 2)) + 1.0F) / 2.0F;
               int amplifier = sleepyEffect.getAmplifier();
               float minRadius = 0.0F + Math.min(amplifier * 2.0F, 6.0F);
               float radius = Mth.lerp(wave, minRadius, 15.0F);
               blurEffect.setUniform("Radius", radius);
            }

            wasActive = true;
         } else if (wasActive) {
            shutdown();
         }
      } else {
         if (wasActive) {
            shutdown();
         }
      }
   }

   public static void processBlur() {
      if (blurEffect != null && wasActive) {
         Minecraft mc = Minecraft.getInstance();
         blurEffect.process(mc.getTimer().getGameTimeDeltaPartialTick(true));
         mc.getMainRenderTarget().bindWrite(false);
      }
   }

   public static boolean isActive() {
      return wasActive && blurEffect != null;
   }

   private static void loadBlurShader(Minecraft mc) {
      try {
         blurEffect = new PostChain(
            mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), ResourceLocation.withDefaultNamespace("shaders/post/blur.json")
         );
         blurEffect.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
         lastWidth = mc.getWindow().getWidth();
         lastHeight = mc.getWindow().getHeight();
      } catch (IOException var2) {
         blurEffect = null;
      }
   }

   public static void shutdown() {
      if (blurEffect != null) {
         blurEffect.close();
         blurEffect = null;
      }

      wasActive = false;
      lastWidth = -1;
      lastHeight = -1;
   }
}
