package net.alshanex.tunes_n_tomes.util.music;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

public class SpellMusicState {
   private boolean musicPlaying = false;
   private int musicTimer = 0;
   @Nullable
   private SoundEvent playingSound = null;
   @Nullable
   private Entity trackedEntity = null;

   public boolean isMusicPlaying() {
      return this.musicPlaying;
   }

   public void setMusicPlaying(boolean playing) {
      this.musicPlaying = playing;
   }

   public int getMusicTimer() {
      return this.musicTimer;
   }

   public void setMusicTimer(int timer) {
      this.musicTimer = timer;
   }

   public void tickTimer() {
      if (this.musicTimer > 0) {
         this.musicTimer--;
      }
   }

   @Nullable
   public SoundEvent getPlayingSound() {
      return this.playingSound;
   }

   public void setPlayingSound(@Nullable SoundEvent sound) {
      this.playingSound = sound;
   }

   @Nullable
   public Entity getTrackedEntity() {
      return this.trackedEntity;
   }

   public void setTrackedEntity(@Nullable Entity entity) {
      this.trackedEntity = entity;
   }
}
