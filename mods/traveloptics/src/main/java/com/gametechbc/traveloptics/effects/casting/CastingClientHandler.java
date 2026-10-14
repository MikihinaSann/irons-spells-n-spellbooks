package com.gametechbc.traveloptics.effects.casting;

import com.gametechbc.traveloptics.init.TOEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

@EventBusSubscriber(
   modid = "traveloptics",
   value = {Dist.CLIENT}
)
public class CastingClientHandler {
   public CastingClientHandler() {
      super();
   }

   @SubscribeEvent
   public static void onFovModifierEvent(ComputeFovModifierEvent event) {
      Player entity = event.getPlayer();
      if (entity.hasEffect(TOEffects.CASTING_EFFECT)) {
         event.setNewFovModifier(1.0F);
      }
   }
}
