package com.chadate.spellelemental.element.attachment.data;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class UnifiedElementAttachmentDataRegistry {
   @SubscribeEvent
   public static void onAddReloadListeners(AddReloadListenerEvent event) {
      event.addListener(new UnifiedElementAttachmentDataLoader());
   }
}
