package net.acetheeldritchking.aces_spell_utils.entity.mobs;

import io.redspace.ironsspellbooks.api.network.IClientEventEntity;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

public abstract class GenericBossEntity extends AbstractSpellCastingMob implements Enemy, IClientEventEntity {
   public static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(GenericBossEntity.class, EntityDataSerializers.INT);
   public boolean hasCustomMusic;
   public boolean changeMusicOnPhaseChange;
   public boolean hasTransitionPhase;
   public int usePhaseAsTransition;
   public int usePhaseForMusicChange;

   public GenericBossEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public SoundEvent getBossMusic() {
      return null;
   }

   public boolean hasCustomMusic() {
      return this.hasCustomMusic;
   }

   public boolean changeMusicOnPhaseChange() {
      return this.changeMusicOnPhaseChange;
   }

   public boolean hasTransitionPhase() {
      return this.hasTransitionPhase;
   }

   public int usePhaseAsTransition() {
      return this.usePhaseAsTransition;
   }

   public int usePhaseForMusicChange() {
      return this.usePhaseForMusicChange;
   }

   public SoundEvent getTransitionMusic() {
      return null;
   }

   public SoundEvent getOtherPhaseMusic() {
      return null;
   }

   public void handleClientEvent(byte b) {
   }

   public void setPhase(int phase) {
      this.entityData.set(PHASE, phase);
   }

   public void setPhase(GenericBossEntity.Phase phase) {
      this.setPhase(phase.value);
   }

   public int getPhase() {
      return (Integer)this.entityData.get(PHASE);
   }

   public boolean isPhase(GenericBossEntity.Phase phase) {
      return phase.value == this.getPhase();
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setPhase(pCompound.getInt("phase"));
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("phase", this.getPhase());
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(PHASE, 0);
   }

   public static enum Phase {
      FirstPhase(0),
      SecondPhase(1),
      ThirdPhase(2),
      FourthPhase(3),
      FifthPhase(4),
      SixthPhase(5),
      SeventhPhase(6),
      EighthPhase(7),
      NinethPhase(8),
      TenthPhase(9),
      EleventhPhase(10),
      TwelfthPhase(11),
      TransitionPhase(12);

      public final int value;

      private Phase(int value) {
         this.value = value;
      }
   }
}
