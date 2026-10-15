package net.hazen.hazennstuff.Spells.Schools.Radiance;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell.TeleportData;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Datagen.HnSTags;
import net.hazen.hazennstuff.Registries.HnSDamageTypes;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.hazen.hazennstuff.Registries.HnSParticleHelper;
import net.hazen.hazennstuff.Registries.HnSSchoolRegistry;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.hazen.hazennstuff.Spells.AbstractSpells.AbstractTaggedSpell;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class PrismaticShiftSpell extends AbstractTaggedSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "prismatic_shift");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(HnSSchoolRegistry.RADIANCE_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(1.5)
      .build();
   private boolean hasHarmonious;

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getDistance(spellLevel, caster), 1)}));
   }

   public PrismaticShiftSpell() {
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 4;
      this.baseManaCost = 40;
      this.manaCostPerLevel = 10;
      this.castTime = 0;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
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
      return Optional.of((SoundEvent)HnSSounds.TERRARIA_CAST.get());
   }

   public void onClientPreCast(Level level, int spellLevel, LivingEntity entity, InteractionHand hand, @Nullable MagicData playerMagicData) {
      super.onClientPreCast(level, spellLevel, entity, hand, playerMagicData);
      int particleCount = 40;
      double radius = 0.5;
      double speed = 0.15;
      Vec3 center = entity.position().add(0.0, entity.getBbHeight() * 0.1, 0.0);

      for (int i = 0; i < particleCount; i++) {
         double angle = (Math.PI * 2) * i / particleCount;
         double xOffset = Math.cos(angle) * radius;
         double zOffset = Math.sin(angle) * radius;
         double xMotion = Math.cos(angle) * speed;
         double zMotion = Math.sin(angle) * speed;
         level.addParticle(HnSParticleHelper.ROD_OF_DISCORD_PARTICLE, center.x + xOffset, center.y, center.z + zOffset, xMotion, 0.15, zMotion);
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 dest = null;
      TeleportData teleportData = (TeleportData)playerMagicData.getAdditionalCastData();
      if (teleportData != null) {
         Vec3 potentialTarget = teleportData.getTeleportTargetPosition();
         if (potentialTarget != null) {
            dest = potentialTarget;
            Utils.handleSpellTeleport(this, entity, potentialTarget);
         }
      } else {
         HitResult hitResult = Utils.raycastForEntity(level, entity, this.getDistance(spellLevel, entity), true);
         if (entity.isPassenger()) {
            entity.stopRiding();
         }

         if (hitResult.getType() == Type.ENTITY && ((EntityHitResult)hitResult).getEntity() instanceof LivingEntity target) {
            for (int j = 0; j < 8; j++) {
               dest = target.position().subtract(new Vec3(0.0, 0.0, 1.5).yRot(-(target.getYRot() + j * 45) * (float) (Math.PI / 180.0)));
               if (level.getBlockState(BlockPos.containing(dest).above()).isAir()) {
                  break;
               }
            }

            Utils.handleSpellTeleport(this, entity, dest.add(0.0, 1.0, 0.0));
            entity.lookAt(Anchor.EYES, target.getEyePosition().subtract(0.0, 0.15, 0.0));
         } else {
            dest = TeleportSpell.findTeleportLocation(level, entity, this.getDistance(spellLevel, entity));
            Utils.handleSpellTeleport(this, entity, dest);
         }
      }

      this.chaoticTeleportHurt(entity);
      this.chaosState(spellLevel, entity);
      this.spawnArrivalParticles(level, dest);
      entity.resetFallDistance();
      level.playSound((Player)null, dest.x, dest.y, dest.z, this.getCastFinishSound().get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public void chaoticTeleportHurt(LivingEntity entity) {
      boolean hasChaosState = entity.hasEffect(HnSEffects.CHAOS_STATE);
      if (entity instanceof ServerPlayer player && !player.level().isClientSide() && hasChaosState) {
         float percentDamage = 0.3F;
         float maxHealth = entity.getMaxHealth();
         float damage = maxHealth * percentDamage;
         damage = Math.max(1.0F, damage);
         DamageSource damageSource = new DamageSource(DamageSources.getHolderFromResource(entity, HnSDamageTypes.RADIANCE_MAGIC));
         entity.hurt(damageSource, damage);
         player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.25F, 1.0F);
      }
   }

   public void chaosState(int spellLevel, LivingEntity entity) {
      this.hasHarmonious = this.hasTaggedItem(entity, HnSTags.HARMONIOUS_EQUIPMENT);
      if (this.hasHarmonious) {
         if (entity instanceof ServerPlayer serverPlayer && !serverPlayer.level().isClientSide() && this.hasHarmonious) {
            entity.addEffect(
               new MobEffectInstance(
                  HnSEffects.HARMONIZED, (int)(this.getSpellPower(spellLevel, entity) * 20.0F), (int)(spellLevel * 0.5) - 1, false, false, true
               )
            );
         }
      } else {
         entity.addEffect(new MobEffectInstance(HnSEffects.CHAOS_STATE, 120, 1, false, false, true));
      }
   }

   private void spawnArrivalParticles(Level level, Vec3 dest) {
      if (!level.isClientSide()) {
         MagicManager.spawnParticles(
            level,
            new BlastwaveParticleOptions(HnSSpellRegistries.PRISMATIC_SHIFT.get().getSchoolType().getTargetingColor(), 1.25F),
            dest.x,
            dest.y,
            dest.z,
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
         int particleCount = 40;
         double radius = 1.2;
         double speed = 0.25;

         for (int i = 0; i < particleCount; i++) {
            double angle = (Math.PI * 2) * i / particleCount;
            double xOffset = Math.cos(angle) * radius;
            double zOffset = Math.sin(angle) * radius;
            double spawnX = dest.x + xOffset;
            double spawnY = dest.y + 0.2;
            double spawnZ = dest.z + zOffset;
            double xMotion = -Math.cos(angle) * speed;
            double zMotion = -Math.sin(angle) * speed;
            MagicManager.spawnParticles(level, HnSParticleHelper.ROD_OF_DISCORD_PARTICLE, spawnX, spawnY, spawnZ, 1, xMotion, 0.05, zMotion, 0.0, true);
         }
      }
   }

   private float getDistance(int spellLevel, LivingEntity sourceEntity) {
      return (float)(Utils.softCapFormula(this.getEntityPowerMultiplier(sourceEntity)) * this.getSpellPower(spellLevel, (Entity)null));
   }

   public AnimationHolder getCastStartAnimation() {
      return AnimationHolder.none();
   }
}
