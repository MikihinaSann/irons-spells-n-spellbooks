package com.snackpirate.aeromancy.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class AADataAttachments {
   private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(Keys.ATTACHMENT_TYPES, "aero_additions");
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<AeromancySpellData>> AEROMANCY_DATA = ATTACHMENT_TYPES.register(
      "aeromancy_magic_data",
      () -> AttachmentType.builder(holder -> holder instanceof ServerPlayer serverPlayer ? new AeromancySpellData(serverPlayer) : new AeromancySpellData())
         .serialize(new AeromancyDataProvider())
         .build()
   );

   public static void register(IEventBus eventBus) {
      ATTACHMENT_TYPES.register(eventBus);
   }
}
