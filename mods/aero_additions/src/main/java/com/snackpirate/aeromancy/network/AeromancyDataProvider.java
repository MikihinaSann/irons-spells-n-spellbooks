package com.snackpirate.aeromancy.network;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class AeromancyDataProvider implements IAttachmentSerializer<CompoundTag, AeromancySpellData> {
   public AeromancySpellData read(IAttachmentHolder holder, CompoundTag tag, Provider provider) {
      AeromancySpellData magicData = holder instanceof ServerPlayer serverPlayer ? new AeromancySpellData(serverPlayer) : new AeromancySpellData(true);
      magicData.loadNBTData(tag, provider);
      return magicData;
   }

   @Nullable
   public CompoundTag write(AeromancySpellData aeromancySpellData, Provider provider) {
      CompoundTag tag = new CompoundTag();
      aeromancySpellData.saveNBTData(tag, provider);
      return tag;
   }
}
