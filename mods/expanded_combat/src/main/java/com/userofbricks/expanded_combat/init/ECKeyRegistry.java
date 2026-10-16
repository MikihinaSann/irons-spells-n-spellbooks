package com.userofbricks.expanded_combat.init;

import com.userofbricks.expanded_combat.datagen.LangStrings;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class ECKeyRegistry {
   public static KeyMapping cycleQuiverRight = new KeyMapping(LangStrings.CYCLE_QUIVER_RIGHT, 88, LangStrings.KEY_CATEGORY);
   public static KeyMapping cycleQuiverLeft = new KeyMapping(LangStrings.CYCLE_QUIVER_LEFT, 90, LangStrings.KEY_CATEGORY);

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public static void registerKeys(RegisterKeyMappingsEvent evt) {
      evt.register(cycleQuiverLeft);
      evt.register(cycleQuiverRight);
   }
}
