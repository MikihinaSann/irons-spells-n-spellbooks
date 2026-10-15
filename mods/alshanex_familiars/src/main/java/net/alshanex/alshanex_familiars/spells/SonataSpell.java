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
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.alshanex.alshanex_familiars.entity.sound.BlackNoteEntity;
import net.alshanex.alshanex_familiars.entity.sound.DefaultNoteEntity;
import net.alshanex.alshanex_familiars.registry.AFSoundRegistry;
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
import net.minecraft.world.phys.Vec3;

public class SonataSpell extends AbstractSpell {
   SoundEvent playingSound = null;
   private boolean musicPlaying = false;
   private int musicTimer = 0;
   private static final int MUSIC_DURATION = 200;
   private final ResourceLocation spellId = new ResourceLocation("alshanex_familiars", "sonata");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(FSchoolRegistry.SOUND_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(40.0)
      .build();

   public SonataSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 15;
      this.spellPowerPerLevel = 1;
      this.castTime = 300;
      this.baseManaCost = 5;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel), 1)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{this.getCount(spellLevel, caster)})
      );
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
         this.shootNotes(level, spellLevel, entity);
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

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.4F;
   }

   private double getRadius(int spellLevel) {
      return 10 + spellLevel * 2;
   }

   private int getCount(int spellLevel, LivingEntity entity) {
      return 3 + spellLevel;
   }

   public void shootNotes(Level world, int spellLevel, LivingEntity entity) {
      int count = this.getCount(spellLevel, entity);
      double radius = this.getRadius(spellLevel);
      double radiusSqr = radius * radius;
      world.getEntitiesOfClass(
            LivingEntity.class,
            new AABB(entity.position().subtract(radius, radius, radius), entity.position().add(radius, radius, radius)),
            livingEntity -> HelperMethods.horizontalDistanceSqr(livingEntity, entity.position()) < radiusSqr
               && !entity.isAlliedTo(livingEntity)
               && !livingEntity.hasEffect(MobEffectRegistry.GUIDING_BOLT)
         )
         .forEach(livingEntity -> livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.GUIDING_BOLT, 100, 0, false, false, true)));

      for (int i = 0; i < count; i++) {
         double angle = Math.toRadians(360 / count * i);
         double x = Math.cos(angle);
         double z = Math.sin(angle);
         Vec3 direction = new Vec3(x, 0.0, z).normalize().scale(1.5);
         float xRot = (float)Math.toDegrees(-Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));
         float yRot = (float)Math.toDegrees(Math.atan2(direction.x, direction.z));
         boolean isBlackNote = Utils.random.nextDouble() < 0.3;
         if (isBlackNote) {
            BlackNoteEntity blackNote = new BlackNoteEntity(world, entity);
            blackNote.setPos(entity.getX(), entity.getEyeY() - 0.5, entity.getZ());
            blackNote.setRotation(xRot, yRot);
            blackNote.setDamage(this.getDamage(spellLevel, entity));
            blackNote.shoot(new Vec3(x * 1.5, 0.0, z * 1.5));
            world.addFreshEntity(blackNote);
         } else {
            DefaultNoteEntity defaultNote = new DefaultNoteEntity(world, entity);
            defaultNote.setPos(entity.getX(), entity.getEyeY() - 0.5, entity.getZ());
            defaultNote.setRotation(xRot, yRot);
            defaultNote.setDamage(this.getDamage(spellLevel, entity));
            defaultNote.shoot(new Vec3(x * 1.5, 0.0, z * 1.5));
            world.addFreshEntity(defaultNote);
         }
      }
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
         this.playingSound = (SoundEvent)AFSoundRegistry.BARD_SONG_2.get();
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
