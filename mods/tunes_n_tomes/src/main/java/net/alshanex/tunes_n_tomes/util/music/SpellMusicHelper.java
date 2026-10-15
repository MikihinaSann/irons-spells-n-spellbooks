package net.alshanex.tunes_n_tomes.util.music;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SpellMusicHelper {
   public static void playMusic(Level level, LivingEntity entity, SpellMusicState state, SoundEvent sound, int duration) {
      if (!level.isClientSide && !state.isMusicPlaying()) {
         state.setPlayingSound(sound);
         state.setMusicPlaying(true);
         state.setMusicTimer(duration);
         level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundSource.MUSIC, 1.0F, 1.0F);
      }
   }

   public static void stopMusic(Level level, SpellMusicState state) {
      if (!level.isClientSide && level.getServer() != null && state.getPlayingSound() != null) {
         Objects.requireNonNull(level.getServer())
            .getPlayerList()
            .getPlayers()
            .forEach(player -> player.connection.send(new ClientboundStopSoundPacket(state.getPlayingSound().getLocation(), SoundSource.MUSIC)));
         state.setPlayingSound(null);
         state.setMusicPlaying(false);
      }
   }

   public static void stopMinecraftAmbientMusic(Level level) {
      if (!level.isClientSide && level.getServer() != null) {
         for (ServerPlayer player : Objects.requireNonNull(level.getServer()).getPlayerList().getPlayers()) {
            player.connection.send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.game"), SoundSource.MUSIC));
            player.connection.send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.creative"), SoundSource.MUSIC));
            player.connection.send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.menu"), SoundSource.MUSIC));
            player.connection
               .send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.overworld.day"), SoundSource.MUSIC));
            player.connection
               .send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.overworld.night"), SoundSource.MUSIC));
            player.connection
               .send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.overworld.hills"), SoundSource.MUSIC));
            player.connection
               .send(new ClientboundStopSoundPacket(ResourceLocation.fromNamespaceAndPath("minecraft", "music.overworld.water"), SoundSource.MUSIC));
         }
      }
   }

   public static void tickMusicLoop(Level level, LivingEntity entity, SpellMusicState state, SoundEvent sound, int duration) {
      if (state.isMusicPlaying()) {
         stopMinecraftAmbientMusic(level);
         state.tickTimer();
         if (state.getMusicTimer() <= 0) {
            stopMusic(level, state);
            playMusic(level, entity, state, sound, duration);
         }
      } else {
         playMusic(level, entity, state, sound, duration);
      }
   }

   public static void cleanup(Level level, UUID casterUUID, ConcurrentHashMap<UUID, SpellMusicState> stateMap) {
      SpellMusicState state = stateMap.remove(casterUUID);
      if (state != null) {
         if (state.isMusicPlaying()) {
            stopMusic(level, state);
         }

         if (state.getTrackedEntity() != null && state.getTrackedEntity().isAlive()) {
            state.getTrackedEntity().discard();
         }
      }
   }
}
