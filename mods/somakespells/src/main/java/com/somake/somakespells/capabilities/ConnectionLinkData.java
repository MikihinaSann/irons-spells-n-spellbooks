package com.somake.somakespells.capabilities;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class ConnectionLinkData implements INBTSerializable<CompoundTag> {
   private final EnumMap<ConnectionKind, ConnectionLinkData.LinkEntry> links = new EnumMap<>(ConnectionKind.class);

   public void set(UUID partnerUuid, UUID ownerUuid, int spellLevel, long expiresAt, ConnectionKind kind) {
      if (kind != null && partnerUuid != null) {
         this.links.put(kind, new ConnectionLinkData.LinkEntry(partnerUuid, ownerUuid, spellLevel, expiresAt, kind));
      } else {
         if (kind != null) {
            this.clear(kind);
         }
      }
   }

   public boolean hasLink() {
      return !this.links.isEmpty();
   }

   public boolean hasLink(ConnectionKind kind) {
      return kind != null && this.links.containsKey(kind);
   }

   public boolean isActive(long gameTime) {
      for (ConnectionLinkData.LinkEntry link : this.links.values()) {
         if (link.isActive(gameTime)) {
            return true;
         }
      }

      return false;
   }

   public boolean isActive(ConnectionKind kind, long gameTime) {
      ConnectionLinkData.LinkEntry link = this.getLink(kind);
      return link != null && link.isActive(gameTime);
   }

   public ConnectionLinkData.LinkEntry getLink(ConnectionKind kind) {
      return kind == null ? null : this.links.get(kind);
   }

   public List<ConnectionLinkData.LinkEntry> getLinks() {
      return List.copyOf(this.links.values());
   }

   public List<ConnectionKind> getKinds() {
      return List.copyOf(this.links.keySet());
   }

   public UUID getPartnerUuid() {
      ConnectionLinkData.LinkEntry link = this.getPrimaryLink();
      return link != null ? link.getPartnerUuid() : null;
   }

   public UUID getOwnerUuid() {
      ConnectionLinkData.LinkEntry link = this.getPrimaryLink();
      return link != null ? link.getOwnerUuid() : null;
   }

   public int getSpellLevel() {
      ConnectionLinkData.LinkEntry link = this.getPrimaryLink();
      return link != null ? link.getSpellLevel() : 0;
   }

   public long getExpiresAt() {
      ConnectionLinkData.LinkEntry link = this.getPrimaryLink();
      return link != null ? link.getExpiresAt() : 0L;
   }

   public ConnectionKind getKind() {
      ConnectionLinkData.LinkEntry link = this.getPrimaryLink();
      return link != null ? link.getKind() : null;
   }

   public void clear(ConnectionKind kind) {
      if (kind != null) {
         this.links.remove(kind);
      }
   }

   public void clear() {
      this.links.clear();
   }

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      ListTag listTag = new ListTag();

      for (ConnectionLinkData.LinkEntry link : this.links.values()) {
         listTag.add(link.serialize());
      }

      tag.put("entries", listTag);
      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag tag) {
      this.links.clear();
      if (tag.contains("entries", 9)) {
         ListTag listTag = tag.getList("entries", 10);

         for (int i = 0; i < listTag.size(); i++) {
            ConnectionLinkData.LinkEntry link = ConnectionLinkData.LinkEntry.deserialize(listTag.getCompound(i));
            if (link != null) {
               this.links.put(link.getKind(), link);
            }
         }
      } else {
         if (tag.contains("kind") && tag.hasUUID("partner")) {
            ConnectionKind legacyKind = ConnectionKind.valueOf(tag.getString("kind"));
            this.links
               .put(
                  legacyKind,
                  new ConnectionLinkData.LinkEntry(
                     tag.getUUID("partner"), tag.hasUUID("owner") ? tag.getUUID("owner") : null, tag.getInt("spellLevel"), tag.getLong("expiresAt"), legacyKind
                  )
               );
         }
      }
   }

   private ConnectionLinkData.LinkEntry getPrimaryLink() {
      return this.links.isEmpty() ? null : new ArrayList<>(this.links.values()).getFirst();
   }

   public static class LinkEntry {
      private final UUID partnerUuid;
      private final UUID ownerUuid;
      private final int spellLevel;
      private final long expiresAt;
      private final ConnectionKind kind;

      public LinkEntry(UUID partnerUuid, UUID ownerUuid, int spellLevel, long expiresAt, ConnectionKind kind) {
         this.partnerUuid = partnerUuid;
         this.ownerUuid = ownerUuid;
         this.spellLevel = spellLevel;
         this.expiresAt = expiresAt;
         this.kind = kind;
      }

      public UUID getPartnerUuid() {
         return this.partnerUuid;
      }

      public UUID getOwnerUuid() {
         return this.ownerUuid;
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

      public boolean isActive(long gameTime) {
         return this.expiresAt > gameTime;
      }

      private CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.putUUID("partner", this.partnerUuid);
         if (this.ownerUuid != null) {
            tag.putUUID("owner", this.ownerUuid);
         }

         tag.putInt("spellLevel", this.spellLevel);
         tag.putLong("expiresAt", this.expiresAt);
         tag.putString("kind", this.kind.name());
         return tag;
      }

      private static ConnectionLinkData.LinkEntry deserialize(CompoundTag tag) {
         if (tag.hasUUID("partner") && tag.contains("kind")) {
            ConnectionKind kind = ConnectionKind.valueOf(tag.getString("kind"));
            return new ConnectionLinkData.LinkEntry(
               tag.getUUID("partner"), tag.hasUUID("owner") ? tag.getUUID("owner") : null, tag.getInt("spellLevel"), tag.getLong("expiresAt"), kind
            );
         } else {
            return null;
         }
      }
   }
}
