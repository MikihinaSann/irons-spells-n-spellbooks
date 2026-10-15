package net.alshanex.alshanex_familiars.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.alshanex.alshanex_familiars.registry.AFSoundRegistry;
import net.alshanex.alshanex_familiars.registry.EffectRegistry;
import net.alshanex.alshanex_familiars.registry.ParticleRegistry;
import net.alshanex.familiarslib.registry.FSchoolRegistry;
import net.alshanex.familiarslib.util.CylinderParticleManager;
import net.alshanex.familiarslib.util.HelperMethods;
import net.alshanex.familiarslib.util.CylinderParticleManager.ParticleDirection;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class LullabySpell extends AbstractSpell {
   SoundEvent playingSound = null;
   private boolean musicPlaying = false;
   private int musicTimer = 0;
   private static final int MUSIC_DURATION = 200;
   private final ResourceLocation spellId = new ResourceLocation("alshanex_familiars", "lullaby");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(FSchoolRegistry.SOUND_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(40.0)
      .build();

   public LullabySpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 15;
      this.spellPowerPerLevel = 1;
      this.castTime = 300;
      this.baseManaCost = 7;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(), 1)}));
   }

   public boolean allowCrafting() {
      return false;
   }

   public CastType getCastType() {
      return CastType.CONTINUOUS;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CONTINUOUS_OVERHEAD;
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (this.musicPlaying) {
         this.stopMinecraftAmbientMusic(world);
         if (this.musicTimer > 0) {
            this.musicTimer--;
         } else {
            this.playMusic(world, entity);
         }
      }

      if (!this.musicPlaying) {
         this.playMusic(world, entity);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 20 == 0) {
         this.applySleepy(level, entity);
      }

      if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 5 == 0) {
         boolean isBlackParticle = Utils.random.nextDouble() <= 0.5;
         if (isBlackParticle) {
            CylinderParticleManager.spawnParticlesAtBlockPos(
               level,
               entity.position(),
               1,
               (ParticleOptions)ParticleRegistry.BLACK_NOTE_PARTICLE.get(),
               ParticleDirection.UPWARD,
               0.1,
               0.2,
               entity.getBbHeight()
            );
         } else {
            CylinderParticleManager.spawnParticlesAtBlockPos(
               level,
               entity.position(),
               1,
               (ParticleOptions)ParticleRegistry.MUSICAL_NOTE_PARTICLE.get(),
               ParticleDirection.UPWARD,
               0.1,
               0.2,
               entity.getBbHeight()
            );
         }
      }
   }

   private double getRadius() {
      return 15.0;
   }

   public void applySleepy(Level world, LivingEntity entity) {
      double radius = this.getRadius();
      double radiusSqr = radius * radius;
      world.getEntitiesOfClass(
            LivingEntity.class,
            new AABB(entity.position().subtract(radius, radius, radius), entity.position().add(radius, radius, radius)),
            livingEntity -> HelperMethods.horizontalDistanceSqr(livingEntity, entity.position()) < radiusSqr
               && !livingEntity.is(entity)
               && !entity.isAlliedTo(livingEntity)
               && !livingEntity.hasEffect(EffectRegistry.SLEEPY)
         )
         .forEach(livingEntity -> livingEntity.addEffect(new MobEffectInstance(EffectRegistry.SLEEPY, 100, 0, false, false, true)));
   }

   public void onServerCastComplete(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
      if (this.musicPlaying) {
         this.stopMusic(level);
         this.musicPlaying = false;
         this.musicTimer = 0;
      }

      super.onServerCastComplete(level, spellLevel, entity, playerMagicData, cancelled);
   }

   private void stopMinecraftAmbientMusic(Level level) {
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

   private void playMusic(Level level, LivingEntity entity) {
      if (!level.isClientSide && !this.musicPlaying) {
         this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_1.get();
         this.musicPlaying = true;
         this.musicTimer = 200;
         level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), this.playingSound, SoundSource.MUSIC, 1.0F, 1.0F);
      }
   }

   private void stopMusic(Level level) {
      if (!level.isClientSide && level.getServer() != null && this.playingSound != null) {
         Objects.requireNonNull(level.getServer())
            .getPlayerList()
            .getPlayers()
            .forEach(player -> player.connection.send(new ClientboundStopSoundPacket(this.playingSound.getLocation(), SoundSource.MUSIC)));
         this.playingSound = null;
      }
   }
}
