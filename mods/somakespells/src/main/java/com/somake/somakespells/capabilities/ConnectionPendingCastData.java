package com.somake.somakespells.capabilities;

import java.util.UUID;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class ConnectionPendingCastData implements INBTSerializable<CompoundTag> {
   private UUID firstTargetUuid;
   private int spellLevel;
   private long expiresAt;
   private ConnectionKind kind;

   public void set(UUID firstTargetUuid, int spellLevel, long expiresAt, ConnectionKind kind) {
      this.firstTargetUuid = firstTargetUuid;
      this.spellLevel = spellLevel;
      this.expiresAt = expiresAt;
      this.kind = kind;
   }

   public boolean hasPendingTarget() {
      return this.firstTargetUuid != null && this.kind != null;
   }

   public boolean isActive(long gameTime) {
      return this.hasPendingTarget() && this.expiresAt > gameTime;
   }

   public UUID getFirstTargetUuid() {
      return this.firstTargetUuid;
   }

   public int getSpellLevel() {
      return this.spellLevel;
   }

   public long getExpiresAt() {
      return this.expiresAt;
   }

   public ConnectionKind getKind() {
      return this.kind;
   }

   public void clear() {
      this.firstTargetUuid = null;
      this.spellLevel = 0;
      this.expiresAt = 0L;
      this.kind = null;
   }

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      if (this.firstTargetUuid != null) {
         tag.putUUID("firstTarget", this.firstTargetUuid);
      }

      if (this.kind != null) {
         tag.putString("kind", this.kind.name());
      }

      tag.putInt("spellLevel", this.spellLevel);
      tag.putLong("expiresAt", this.expiresAt);
      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag tag) {
      this.firstTargetUuid = tag.hasUUID("firstTarget") ? tag.getUUID("firstTarget") : null;
      this.kind = tag.contains("kind") ? ConnectionKind.valueOf(tag.getString("kind")) : null;
      this.spellLevel = tag.getInt("spellLevel");
      this.expiresAt = tag.getLong("expiresAt");
   }
}
