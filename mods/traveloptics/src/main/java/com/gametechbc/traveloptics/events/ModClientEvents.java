package com.gametechbc.traveloptics.events;

import com.gametechbc.traveloptics.effects.AstralSenseEffect;
import com.gametechbc.traveloptics.effects.AstralSenseTreasureEffect;
import net.minecraft.client.renderer.FogRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(
   modid = "traveloptics",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ModClientEvents {
   public ModClientEvents() {
      super();
   }

   @SubscribeEvent
   public static void clientSetup(FMLClientSetupEvent e) {
      e.enqueueWork(() -> {
         FogRenderer.MOB_EFFECT_FOG.add(new AstralSenseEffect.AstralSenseFogFunction());
         FogRenderer.MOB_EFFECT_FOG.add(new AstralSenseTreasureEffect.AstralSenseFogFunction());
      });
   }
}
