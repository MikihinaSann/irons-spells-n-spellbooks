package com.somake.somakespells.event;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class GrimoireCastAnimationHandler {
   @SubscribeEvent
   public static void onSpellPreCast(SpellPreCastEvent event) {
   }
}
