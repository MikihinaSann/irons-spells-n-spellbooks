package net.alshanex.familiarslib.registry;

import java.util.function.Supplier;
import net.alshanex.familiarslib.data.PlayerFamiliarData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AttachmentRegistry {
   private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "familiarslib");
   public static final Supplier<AttachmentType<PlayerFamiliarData>> PLAYER_FAMILIAR_DATA = ATTACHMENT_TYPES.register(
      "player_familiar_data", () -> AttachmentType.serializable(PlayerFamiliarData::new).build()
   );

   public static void register(IEventBus eventBus) {
      ATTACHMENT_TYPES.register(eventBus);
   }
}
