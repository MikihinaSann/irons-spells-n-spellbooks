package com.somake.somakespells.registries;

import com.somake.somakespells.capabilities.BloodmarkRecastData;
import com.somake.somakespells.capabilities.ConnectionLinkData;
import com.somake.somakespells.capabilities.ConnectionPendingCastData;
import com.somake.somakespells.capabilities.ConnectionTransferQueueData;
import com.somake.somakespells.capabilities.FireOrbsRecastData;
import com.somake.somakespells.capabilities.PlayerSpellMastery;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "somakespells");
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerSpellMastery>> PLAYER_SPELL_MASTERY = ATTACHMENT_TYPES.register(
      "spell_mastery", () -> AttachmentType.serializable(PlayerSpellMastery::new).copyOnDeath().build()
   );
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<ConnectionLinkData>> CONNECTION_LINK_DATA = ATTACHMENT_TYPES.register(
      "connection_link_data", () -> AttachmentType.serializable(ConnectionLinkData::new).build()
   );
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<ConnectionPendingCastData>> CONNECTION_PENDING_CAST = ATTACHMENT_TYPES.register(
      "connection_pending_cast", () -> AttachmentType.serializable(ConnectionPendingCastData::new).build()
   );
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<ConnectionTransferQueueData>> CONNECTION_TRANSFER_QUEUE = ATTACHMENT_TYPES.register(
      "connection_transfer_queue", () -> AttachmentType.serializable(ConnectionTransferQueueData::new).build()
   );
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<BloodmarkRecastData>> BLOODMARK_RECAST = ATTACHMENT_TYPES.register(
      "bloodmark_recast", () -> AttachmentType.serializable(BloodmarkRecastData::new).build()
   );
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<FireOrbsRecastData>> FIRE_ORBS_RECAST = ATTACHMENT_TYPES.register(
      "fire_orbs_recast", () -> AttachmentType.serializable(FireOrbsRecastData::new).build()
   );

   public static void register(IEventBus eventBus) {
      ATTACHMENT_TYPES.register(eventBus);
   }
}
