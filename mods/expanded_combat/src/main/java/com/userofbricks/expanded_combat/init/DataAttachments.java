package com.userofbricks.expanded_combat.init;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class DataAttachments {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(Keys.ATTACHMENT_TYPES, "expanded_combat");
   public static final Supplier<AttachmentType<Integer>> STOLEN_HEALTH = ATTACHMENT_TYPES.register(
      "stolen_health", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<Integer>> ARROW_SLOT = ATTACHMENT_TYPES.register(
      "arrow_slot", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<Integer>> WEAPON_BLOCK_COUNT = ATTACHMENT_TYPES.register(
      "block_count", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<Integer>> TIME_SINCE_WEAPON_BLOCK = ATTACHMENT_TYPES.register(
      "time_since_block", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<Integer>> ADDED_HEALTH = ATTACHMENT_TYPES.register(
      "added_health", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build()
   );
}
