package net.alshanex.tunes_n_tomes.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;

@EventBusSubscriber(
   modid = "tunes_n_tomes",
   bus = Bus.GAME,
   value = {Dist.CLIENT}
)
public class SleepyBlurEvents {
   @SubscribeEvent
   public static void onClientTick(Post event) {
      SleepyBlurHandler.onClientTick();
   }

   @SubscribeEvent
   public static void onRenderLevel(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_LEVEL) {
         SleepyBlurHandler.processBlur();
      }
   }
}
