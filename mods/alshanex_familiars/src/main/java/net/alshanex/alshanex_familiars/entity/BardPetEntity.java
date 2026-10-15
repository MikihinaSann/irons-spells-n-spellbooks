package net.alshanex.alshanex_familiars.entity;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.alshanex.alshanex_familiars.registry.AFSoundRegistry;
import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.alshanex_familiars.registry.ParticleRegistry;
import net.alshanex.alshanex_familiars.registry.PetSpellRegistry;
import net.alshanex.alshanex_familiars.util.AlshanexFamiliarsTags;
import net.alshanex.familiarslib.advancements.SimpleAdvancementTrigger;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.registry.CriteriaTriggersRegistry;
import net.alshanex.familiarslib.registry.FSchoolRegistry;
import net.alshanex.familiarslib.util.CylinderParticleManager;
import net.alshanex.familiarslib.util.HelperMethods;
import net.alshanex.familiarslib.util.CylinderParticleManager.ParticleDirection;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class BardPetEntity extends AbstractTerrestrianSpellCastingPet {
   public static final ResourceLocation textureResource = new ResourceLocation("alshanex_familiars", "textures/entity/bard_pet.png");
   SoundEvent playingSound = null;
   private boolean musicPlaying = false;
   private int musicTimer = 0;
   private static int MUSIC_DURATION = 4800;

   public BardPetEntity(EntityType<? extends AbstractTerrestrianSpellCastingPet> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.initializeAttackGoal(2);
   }

   protected WizardAttackGoal createAttackGoal(float min, float max) {
      List<AbstractSpell> attackSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.BARD_ATTACK_SPELLS);
      List<AbstractSpell> defenseSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.BARD_DEFENSE_SPELLS);
      List<AbstractSpell> movementSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.BARD_MOVEMENT_SPELLS);
      List<AbstractSpell> supportSpells = HelperMethods.getSpellsFromTag(AlshanexFamiliarsTags.BARD_SUPPORT_SPELLS);
      return new WizardAttackGoal(this, 1.25, 40, 50)
         .setSpells(attackSpells, defenseSpells, movementSpells, supportSpells)
         .setSingleUseSpell(PetSpellRegistry.ANGEL_SPELL.get(), 1400, 1800, 2, 4)
         .setAllowFleeing(true)
         .setSpellQuality(min, max);
   }

   protected Item getValidTrinket() {
      return (Item)ItemRegistry.BARD_TRINKET.get();
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder pBuilder) {
      super.defineSynchedData(pBuilder);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
   }

   public void onAddedToLevel() {
      super.onAddedToLevel();
      if (this.getSummoner() != null) {
         MagicManager.spawnParticles(
            this.level,
            new BlastwaveParticleOptions(((SchoolType)FSchoolRegistry.SOUND.get()).getTargetingColor(), 2.0F),
            this.getX(),
            this.getY() + 0.165F,
            this.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
      }
   }

   protected void triggerAdvancement(ServerPlayer player) {
      ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_BARD_TRIGGER.get()).trigger(player);
   }

   public void tick() {
      super.tick();
      if (this.getSummoner() != null) {
         if (this.musicPlaying) {
            if (this.getTarget() != null) {
               this.stopMusic();
               this.musicPlaying = false;
               MUSIC_DURATION = 0;
               this.musicTimer = 0;
            }

            if (this.musicTimer % 20 == 0 && this.getTarget() == null) {
               CylinderParticleManager.spawnParticlesAtBlockPos(
                  this.level, this.position(), 1, (ParticleOptions)ParticleRegistry.MUSICAL_NOTE_PARTICLE.get(), ParticleDirection.UPWARD, 0.1, 0.0, 0.8
               );
            }

            this.stopMinecraftAmbientMusic();
            if (this.musicTimer > 0) {
               this.musicTimer--;
            } else {
               this.playMusic();
            }
         }

         if (!this.musicPlaying && this.getTarget() == null) {
            this.playMusic();
         }
      }
   }

   public void onRemovedFromLevel() {
      if (this.musicPlaying) {
         this.stopMusic();
         this.musicPlaying = false;
         MUSIC_DURATION = 0;
         this.musicTimer = 0;
      }

      super.onRemovedFromLevel();
   }

   private void playMusic() {
      if (!this.level().isClientSide && !this.musicPlaying && !this.isDeadOrDying()) {
         Random random = new Random();
         int randomNumber = random.nextInt(10) + 1;
         if (randomNumber == 1) {
            this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_1.get();
            this.musicPlaying = true;
            MUSIC_DURATION = 2700;
            this.musicTimer = MUSIC_DURATION;
         }

         if (randomNumber == 2) {
            this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_2.get();
            this.musicPlaying = true;
            MUSIC_DURATION = 5000;
            this.musicTimer = MUSIC_DURATION;
         }

         if (randomNumber == 3) {
            this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_3.get();
            this.musicPlaying = true;
            MUSIC_DURATION = 3400;
            this.musicTimer = MUSIC_DURATION;
         }

         if (randomNumber == 4) {
            this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_4.get();
            this.musicPlaying = true;
            MUSIC_DURATION = 2800;
            this.musicTimer = MUSIC_DURATION;
         }

         if (randomNumber == 5) {
            this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_5.get();
            this.musicPlaying = true;
            MUSIC_DURATION = 4300;
            this.musicTimer = MUSIC_DURATION;
         }

         if (randomNumber == 6) {
            this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_6.get();
            this.musicPlaying = true;
            MUSIC_DURATION = 3600;
            this.musicTimer = MUSIC_DURATION;
         }

         if (randomNumber == 7) {
            this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_7.get();
            this.musicPlaying = true;
            MUSIC_DURATION = 1700;
            this.musicTimer = MUSIC_DURATION;
         }

         if (randomNumber == 8) {
            this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_8.get();
            this.musicPlaying = true;
            MUSIC_DURATION = 1300;
            this.musicTimer = MUSIC_DURATION;
         }

         if (randomNumber == 9) {
            this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_9.get();
            this.musicPlaying = true;
            MUSIC_DURATION = 4000;
            this.musicTimer = MUSIC_DURATION;
         }

         if (randomNumber == 10) {
            this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_10.get();
            this.musicPlaying = true;
            MUSIC_DURATION = 2900;
            this.musicTimer = MUSIC_DURATION;
         }

         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), this.playingSound, SoundSource.MUSIC, 1.0F, 1.0F);
      }
   }

   private void stopMusic() {
      if (!this.level().isClientSide && this.level().getServer() != null && this.playingSound != null) {
         Objects.requireNonNull(this.level().getServer())
            .getPlayerList()
            .getPlayers()
            .forEach(player -> player.connection.send(new ClientboundStopSoundPacket(this.playingSound.getLocation(), SoundSource.MUSIC)));
         this.playingSound = null;
      }
   }

   private void stopMinecraftAmbientMusic() {
      if (!this.level().isClientSide && this.level().getServer() != null) {
         for (ServerPlayer player : Objects.requireNonNull(this.level().getServer()).getPlayerList().getPlayers()) {
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
}
