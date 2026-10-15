package net.alshanex.tunes_n_tomes;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(
   value = "tunes_n_tomes",
   dist = {Dist.CLIENT}
)
@EventBusSubscriber(
   modid = "tunes_n_tomes",
   value = {Dist.CLIENT}
)
public class TunesNTomesModClient {
   public TunesNTomesModClient() {
   }

   @SubscribeEvent
   static void onClientSetup(FMLClientSetupEvent event) {
   }
}
