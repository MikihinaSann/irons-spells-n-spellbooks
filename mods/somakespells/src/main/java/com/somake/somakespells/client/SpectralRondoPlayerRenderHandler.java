package com.somake.somakespells.client;

import com.mojang.math.Axis;
import com.somake.somakespells.registries.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RenderPlayerEvent.Post;
import net.neoforged.neoforge.client.event.RenderPlayerEvent.Pre;

@EventBusSubscriber(
   modid = "somakespells",
   value = {Dist.CLIENT},
   bus = Bus.GAME
)
public final class SpectralRondoPlayerRenderHandler {
   private static final ThreadLocal<Boolean> TRANSFORMED = ThreadLocal.withInitial(() -> false);

   private SpectralRondoPlayerRenderHandler() {
   }

   @SubscribeEvent
   public static void onRenderPre(Pre event) {
      if (ModMobEffects.SPECTRAL_RONDO != null) {
         Player player = event.getEntity();
         MobEffectInstance effect = player.getEffect(ModMobEffects.SPECTRAL_RONDO);
         if (effect != null) {
            float elapsed = 22 - effect.getDuration() + event.getPartialTick();
            float spinDegrees = 0.0F;
            if (elapsed >= 1.0F && elapsed <= 21.0F) {
               float progress = Math.min(1.0F, (elapsed - 1.0F) / 20.0F);
               spinDegrees = 360.0F * smoothStep(progress);
            }

            if (spinDegrees != 0.0F) {
               event.getPoseStack().pushPose();
               event.getPoseStack().translate(0.0, 1.0, 0.0);
               event.getPoseStack().mulPose(Axis.YP.rotationDegrees(spinDegrees));
               event.getPoseStack().translate(0.0, -1.0, 0.0);
               TRANSFORMED.set(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onRenderPost(Post event) {
      if (TRANSFORMED.get()) {
         event.getPoseStack().popPose();
         TRANSFORMED.set(false);
      }
   }

   private static float smoothStep(float value) {
      return value * value * (3.0F - 2.0F * value);
   }
}
