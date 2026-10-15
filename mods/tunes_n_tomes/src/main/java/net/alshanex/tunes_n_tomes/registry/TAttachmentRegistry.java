package net.alshanex.tunes_n_tomes.registry;

import java.util.function.Supplier;
import net.alshanex.tunes_n_tomes.data.PlayerSoundMarkerData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class TAttachmentRegistry {
   private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "tunes_n_tomes");
   public static final Supplier<AttachmentType<PlayerSoundMarkerData>> SOUND_MARKER_DATA = ATTACHMENT_TYPES.register(
      "sound_marker_data", () -> AttachmentType.serializable(PlayerSoundMarkerData::new).build()
   );

   public static void register(IEventBus eventBus) {
      ATTACHMENT_TYPES.register(eventBus);
   }
}
