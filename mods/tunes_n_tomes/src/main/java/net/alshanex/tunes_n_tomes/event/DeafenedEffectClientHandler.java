package net.alshanex.tunes_n_tomes.event;

import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

@EventBusSubscriber(
   modid = "tunes_n_tomes",
   bus = Bus.GAME,
   value = {Dist.CLIENT}
)
public class DeafenedEffectClientHandler {
   @SubscribeEvent
   public static void onCameraSetup(ComputeCameraAngles event) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         if (player.hasEffect(TEffectRegistry.DEAFENED)) {
            float time = (float)(player.tickCount + event.getPartialTick());
            float sway = (float)Math.sin(time * 0.1F) * 1.5F;
            float pitch = (float)Math.sin(time * 0.07F) * 0.8F;
            event.setRoll(event.getRoll() + sway);
            event.setPitch(event.getPitch() + pitch);
         }
      }
   }

   @SubscribeEvent
   public static void onSoundPlay(PlaySoundEvent event) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         if (player.hasEffect(TEffectRegistry.DEAFENED)) {
            event.setSound(null);
         }
      }
   }
}
