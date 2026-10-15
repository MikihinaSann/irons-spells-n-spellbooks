package net.alshanex.tunes_n_tomes.util;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.alshanex.tunes_n_tomes.TunesNTomesConfig;
import net.alshanex.tunes_n_tomes.data.PlayerSoundMarkerData;
import net.alshanex.tunes_n_tomes.entity.segno_marker.SegnoMarkerEntity;
import net.alshanex.tunes_n_tomes.network.SyncSoundMarkerDataPacket;
import net.alshanex.tunes_n_tomes.registry.TAttachmentRegistry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class SoundMarkerManager {
   private static final Map<UUID, String> activeCastTracker = new ConcurrentHashMap<>();

   public static void onSoundSpellCast(ServerPlayer player, String spellId, int spellLevel) {
      UUID playerUUID = player.getUUID();
      AbstractSpell spell = SpellRegistry.getSpell(spellId);
      if (spell != null && spell.getCastType() == CastType.CONTINUOUS) {
         String tracked = activeCastTracker.get(playerUUID);
         if (spellId.equals(tracked)) {
            return;
         }

         activeCastTracker.put(playerUUID, spellId);
      }

      ServerLevel level = (ServerLevel)player.level();
      PlayerSoundMarkerData markerData = (PlayerSoundMarkerData)player.getData(TAttachmentRegistry.SOUND_MARKER_DATA.get());
      boolean shouldPlaceMarker = shouldPlaceMarker(player);
      SegnoMarkerEntity newMarker = null;
      if (shouldPlaceMarker && !isSpellNotMarkable(spellId)) {
         newMarker = new SegnoMarkerEntity(level, player);
         newMarker.setPos(player.getX(), player.getY(), player.getZ());
         level.addFreshEntity(newMarker);
         int maxMarkers = (Integer)TunesNTomesConfig.MAX_SOUND_MARKERS.get();
         UUID removedId = markerData.addMarker(newMarker.getUUID(), maxMarkers);
         if (removedId != null) {
            removeMarkerEntity(level, removedId);
         }

         syncMarkerData(player);
      }

      if (spell != null && ArmorSetUtils.hasCompleteSoundSet(player) && !HelperMethods.isSpellInTag(spell, ModTags.RESONANCE_BLACKLIST)) {
         UUID ownerTargetUUID = null;
         MagicData ownerMagicData = MagicData.getPlayerMagicData(player);
         if (ownerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
            ownerTargetUUID = targetData.getTargetUUID();
         }

         for (UUID markerId : markerData.getMarkerIds()) {
            if (newMarker == null || !markerId.equals(newMarker.getUUID())) {
               Entity entity = level.getEntity(findEntityByUUID(level, markerId));
               if (entity == null) {
                  for (Entity e : level.getAllEntities()) {
                     if (e instanceof SegnoMarkerEntity marker && marker.getUUID().equals(markerId)) {
                        int delayTicks = calculateResonanceDelay(player, marker);
                        marker.scheduleResonance(spell, spellLevel, delayTicks, ownerTargetUUID);
                        break;
                     }
                  }
               } else if (entity instanceof SegnoMarkerEntity marker) {
                  int delayTicks = calculateResonanceDelay(player, marker);
                  marker.scheduleResonance(spell, spellLevel, delayTicks, ownerTargetUUID);
               }
            }
         }
      }
   }

   public static void onPlayerStopCasting(ServerPlayer player) {
      activeCastTracker.remove(player.getUUID());
   }

   private static boolean shouldPlaceMarker(ServerPlayer player) {
      boolean shiftRequired = (Boolean)TunesNTomesConfig.MARKER_WITH_SHIFT.get();
      boolean isShifting = player.isShiftKeyDown();
      return shiftRequired == isShifting;
   }

   public static boolean isSpellNotMarkable(String spellId) {
      AbstractSpell spell = SpellRegistry.getSpell(spellId);
      return HelperMethods.isSpellInTag(spell, ModTags.NOT_PLACE_MARKER);
   }

   public static void removeMarkerEntity(ServerLevel level, UUID markerUUID) {
      for (Entity entity : level.getAllEntities()) {
         if (entity instanceof SegnoMarkerEntity marker && marker.getUUID().equals(markerUUID)) {
            marker.discard();
            return;
         }
      }
   }

   public static void onMarkerExpired(ServerPlayer player, UUID markerId) {
      PlayerSoundMarkerData data = (PlayerSoundMarkerData)player.getData(TAttachmentRegistry.SOUND_MARKER_DATA.get());
      data.removeMarker(markerId);
      syncMarkerData(player);
   }

   public static void syncMarkerData(ServerPlayer player) {
      PlayerSoundMarkerData data = (PlayerSoundMarkerData)player.getData(TAttachmentRegistry.SOUND_MARKER_DATA.get());
      int maxMarkers = (Integer)TunesNTomesConfig.MAX_SOUND_MARKERS.get();
      PacketDistributor.sendToPlayer(player, new SyncSoundMarkerDataPacket(data.getMarkerCount(), maxMarkers), new CustomPacketPayload[0]);
   }

   public static void cleanupPlayerMarkers(ServerPlayer player) {
      PlayerSoundMarkerData data = (PlayerSoundMarkerData)player.getData(TAttachmentRegistry.SOUND_MARKER_DATA.get());
      ServerLevel level = (ServerLevel)player.level();

      for (UUID markerId : data.getMarkerIds()) {
         removeMarkerEntity(level, markerId);
      }

      data.clear();
      activeCastTracker.remove(player.getUUID());
   }

   private static int calculateResonanceDelay(ServerPlayer player, SegnoMarkerEntity marker) {
      double distance = player.position().distanceTo(marker.position());
      int ticksPer5Blocks = (Integer)TunesNTomesConfig.RESONANCE_TICKS_PER_5_BLOCKS.get();
      return (int)(distance / 5.0) * ticksPer5Blocks;
   }

   private static int findEntityByUUID(ServerLevel level, UUID uuid) {
      return 0;
   }

   @Nullable
   public static Vec3 getClosestMarkerPosition(ServerPlayer player) {
      PlayerSoundMarkerData markerData = (PlayerSoundMarkerData)player.getData(TAttachmentRegistry.SOUND_MARKER_DATA.get());
      ServerLevel level = (ServerLevel)player.level();
      if (markerData.getMarkerCount() == 0) {
         return null;
      } else {
         SegnoMarkerEntity closest = null;
         double closestDistSqr = Double.MAX_VALUE;

         for (UUID markerId : markerData.getMarkerIds()) {
            for (Entity entity : level.getAllEntities()) {
               if (entity instanceof SegnoMarkerEntity marker && marker.getUUID().equals(markerId)) {
                  double distSqr = player.distanceToSqr(marker);
                  if (distSqr < closestDistSqr) {
                     closestDistSqr = distSqr;
                     closest = marker;
                  }
                  break;
               }
            }
         }

         return closest != null ? closest.position() : null;
      }
   }
}
