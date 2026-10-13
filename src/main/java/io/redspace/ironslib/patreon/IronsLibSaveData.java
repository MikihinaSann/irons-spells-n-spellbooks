package io.redspace.ironslib.patreon;

import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.patreon.data.ChronicleEntry;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.patreon.data.ProfileResult;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

public class IronsLibSaveData extends SavedData {
   private static final String DATA_KEY = "ServerId";
   private static final String CACHE_TIMESTAMP_KEY = "PatreonCacheTime";
   private static final String CACHE_CHRONICLE_KEY = "PatreonChronicle";
   private static final String CACHE_PROFILES_KEY = "PatreonProfiles";
   private static final String CHRONICLE_DISPLAY_NAME_KEY = "profile";
   private static final String CHRONICLE_PACKED_KEY = "data";
   private static final String PROFILE_USERNAME_KEY = "profile";
   private static final String PROFILE_UUID_KEY = "uuid";
   private static final String PROFILE_DATA_KEY = "data";
   public static final String SAVED_DATA_ID = "irons_lib_data";
   public static final String LEGACY_SAVED_DATA_ID = "irons_patreon_lib_data";
   private final UUID serverId;
   private long patreonCacheTimestamp;
   private List<ChronicleEntry> patreonChronicle;
   private List<ProfileResult> patreonProfiles;

   private IronsLibSaveData(UUID serverId) {
      this.serverId = serverId;
      this.patreonCacheTimestamp = 0L;
      this.patreonChronicle = new ArrayList<>();
      this.patreonProfiles = new ArrayList<>();
   }

   public static Factory<IronsLibSaveData> factory() {
      return new Factory<>(() -> new IronsLibSaveData(UUID.randomUUID()), (tag, provider) -> load(tag, provider), null);
   }

   private static IronsLibSaveData load(CompoundTag tag, Provider registries) {
      UUID id = tag.hasUUID("ServerId") ? tag.getUUID("ServerId") : UUID.randomUUID();
      IronsLibSaveData data = new IronsLibSaveData(id);
      data.patreonCacheTimestamp = tag.getLong("PatreonCacheTime");
      data.patreonChronicle = readChronicleList(tag.getList("PatreonChronicle", 10));
      data.patreonProfiles = readProfileList(tag.getList("PatreonProfiles", 10));
      return data;
   }

   @NotNull
   public CompoundTag save(CompoundTag tag, @NotNull Provider registries) {
      tag.putUUID("ServerId", this.serverId);
      tag.putLong("PatreonCacheTime", this.patreonCacheTimestamp);
      tag.put("PatreonChronicle", writeChronicleList(this.patreonChronicle));
      tag.put("PatreonProfiles", writeProfileList(this.patreonProfiles));
      return tag;
   }

   public UUID getServerId() {
      return this.serverId;
   }

   public long getPatreonCacheTimestamp() {
      return this.patreonCacheTimestamp;
   }

   public List<ChronicleEntry> getCachedPatreonChronicle() {
      return this.patreonChronicle;
   }

   public List<ProfileResult> getCachedPatreonProfiles() {
      return this.patreonProfiles;
   }

   public void setPatreonCache(long timestamp, List<ChronicleEntry> chronicle, List<ProfileResult> profiles) {
      this.patreonCacheTimestamp = timestamp;
      this.patreonChronicle = chronicle != null ? new ArrayList<>(chronicle) : new ArrayList<>();
      this.patreonProfiles = profiles != null ? new ArrayList<>(profiles) : new ArrayList<>();
      this.setDirty();
   }

   @Nullable
   public static IronsLibSaveData get(MinecraftServer server) {
      ServerLevel level = server.getLevel(Level.OVERWORLD);
      if (level == null) {
         return null;
      } else {
         DimensionDataStorage storage = level.getDataStorage();
         Factory<IronsLibSaveData> factory = factory();
         IronsLibSaveData data = (IronsLibSaveData)storage.get(factory, "irons_lib_data");
         if (data != null) {
            return data;
         } else {
            IronsLibSaveData legacy = handleMigration(storage, factory);
            return legacy != null ? legacy : (IronsLibSaveData)storage.computeIfAbsent(factory, "irons_lib_data");
         }
      }
   }

   @org.jetbrains.annotations.Nullable
   private static IronsLibSaveData handleMigration(DimensionDataStorage storage, Factory<IronsLibSaveData> factory) {
      IronsLibSaveData legacy = (IronsLibSaveData)storage.get(factory, "irons_patreon_lib_data");
      if (legacy != null) {
         storage.set("irons_lib_data", legacy);
         legacy.setDirty();
         storage.set("irons_patreon_lib_data", new SavedData() {
            public CompoundTag save(CompoundTag tag, Provider registries) {
               return new CompoundTag();
            }

            public void save(File file, Provider registries) {
               try {
                  if (file.exists() && !file.delete()) {
                     file.deleteOnExit();
                  }
               } catch (Exception var4) {
                  IronsLib.LOGGER.error("Failed to cleanup file {}", file.getName());
               }
            }
         });
         return legacy;
      } else {
         return null;
      }
   }

   @Nullable
   public static UUID getOrCreateServerId(MinecraftServer server) {
      IronsLibSaveData data = get(server);
      return data != null ? data.getServerId() : null;
   }

   private static byte[] xorName(String s) {
      if (s == null) {
         return new byte[0];
      } else {
         byte[] bytes = s.getBytes(StandardCharsets.UTF_8);

         for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)(~bytes[i]);
         }

         return bytes;
      }
   }

   private static String unxorName(byte[] bytes) {
      if (bytes != null && bytes.length != 0) {
         byte[] copy = (byte[])bytes.clone();

         for (int i = 0; i < copy.length; i++) {
            copy[i] = (byte)(~copy[i]);
         }

         return new String(copy, StandardCharsets.UTF_8);
      } else {
         return "";
      }
   }

   private static ListTag writeChronicleList(List<ChronicleEntry> list) {
      ListTag tag = new ListTag();

      for (ChronicleEntry e : list) {
         CompoundTag c = new CompoundTag();
         c.putByteArray("profile", xorName(e.displayName()));
         c.putShort("data", e.data());
         tag.add(c);
      }

      return tag;
   }

   private static List<ChronicleEntry> readChronicleList(ListTag tag) {
      List<ChronicleEntry> list = new ArrayList<>(tag.size());

      for (int i = 0; i < tag.size(); i++) {
         try {
            CompoundTag c = tag.getCompound(i);
            String displayName = unxorName(c.getByteArray("profile"));
            short packed = c.getShort("data");
            list.add(new ChronicleEntry(displayName, packed));
         } catch (Exception var6) {
            IronsLib.LOGGER.error("Failed to read server data chronicle entry: {}", var6.getMessage());
         }
      }

      return list;
   }

   private static ListTag writeProfileList(List<ProfileResult> profiles) {
      ListTag list = new ListTag();

      for (ProfileResult profile : profiles) {
         if (profile.uuid() != null) {
            CompoundTag tag = new CompoundTag();
            tag.putByteArray("profile", xorName(profile.username()));
            tag.putUUID("uuid", profile.uuid());
            tag.putShort("data", (short)profile.permissions().ordinal());
            list.add(tag);
         }
      }

      return list;
   }

   private static List<ProfileResult> readProfileList(ListTag profiles) {
      List<ProfileResult> list = new ArrayList<>(profiles.size());

      for (int i = 0; i < profiles.size(); i++) {
         try {
            CompoundTag tag = profiles.getCompound(i);
            if (tag.hasUUID("uuid")) {
               String username = unxorName(tag.getByteArray("profile"));
               UUID uuid = tag.getUUID("uuid");
               int ord = tag.getShort("data");
               if (ord >= 0 && ord < PatreonPermissions.values().length) {
                  PatreonPermissions perm = PatreonPermissions.values()[ord];
                  list.add(new ProfileResult(username, uuid, perm));
               }
            }
         } catch (Exception var8) {
            IronsLib.LOGGER.error("Failed to parse server data profile entry: {}", var8.getMessage());
         }
      }

      return list;
   }
}
