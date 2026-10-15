package net.acetheeldritchking.cataclysm_spellbooks.registries;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class CSAttachmentRegistry {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(
      NeoForgeRegistries.ATTACHMENT_TYPES, "cataclysm_spellbooks"
   );
   public static final Supplier<AttachmentType<Integer>> FORGONE_RAGE = ATTACHMENT_TYPES.register(
      "forgone_rage", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
   );
   public static final Supplier<AttachmentType<Integer>> KINGS_WRATH = ATTACHMENT_TYPES.register(
      "kings_wrath", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
   );

   public static void register(IEventBus eventBus) {
      ATTACHMENT_TYPES.register(eventBus);
   }
}
