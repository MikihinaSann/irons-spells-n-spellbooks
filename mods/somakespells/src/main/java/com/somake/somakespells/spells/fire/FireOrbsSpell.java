package com.somake.somakespells.spells.fire;

import com.somake.somakespells.capabilities.FireOrbsRecastData;
import com.somake.somakespells.entity.SoulfireOrbEntity;
import com.somake.somakespells.registries.ModAttachments;
import com.somake.somakespells.util.FireElementContext;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;

public class FireOrbsSpell extends AbstractSpell {
   private static final int PROJECTILE_COUNT = 5;
   private static final int STORAGE_DURATION_TICKS = 600;
   private static final double TARGET_RADIUS = 3.0;
   private static final double TARGET_HEIGHT = 15.0;
   private static final int FINAL_HOLD_TICKS = 8;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "fire_orbs");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(10.0)
      .build();

   public FireOrbsSpell() {
      this.manaCostPerLevel = 5;
      this.baseManaCost = 30;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 2;
      this.castTime = 5;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public int getRecastCount(int spellLevel, LivingEntity entity) {
      return 5;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.BLAZE_SHOOT);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CHARGED_CAST;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{5})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel && caster instanceof ServerPlayer serverPlayer) {
         FireOrbsRecastData recastData = (FireOrbsRecastData)serverPlayer.getData((AttachmentType)ModAttachments.FIRE_ORBS_RECAST.get());
         cleanupExpiredSession(serverLevel, recastData);
         normalizeTrackedOrbs(serverLevel, recastData);
         if (recastData.hasLockedTarget()) {
            super.onCast(level, spellLevel, caster, castSource, playerMagicData);
         } else if (recastData.getTotalOrbCount() >= 5) {
            super.onCast(level, spellLevel, caster, castSource, playerMagicData);
         } else {
            recastData.setExpiresAt(serverLevel.getGameTime() + 600L);
            if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
               playerMagicData.getPlayerRecasts()
                  .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, caster), 600, castSource, null), playerMagicData);
            }

            this.summonFrontOrb(serverLevel, caster, spellLevel, recastData);
            super.onCast(level, spellLevel, caster, castSource, playerMagicData);
         }
      } else {
         super.onCast(level, spellLevel, caster, castSource, playerMagicData);
      }
   }

   public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
   }

   public static void handleBasicAttack(ServerPlayer player) {
      if (player.level() instanceof ServerLevel serverLevel) {
         FireOrbsRecastData var11 = (FireOrbsRecastData)player.getData((AttachmentType)ModAttachments.FIRE_ORBS_RECAST.get());
         cleanupExpiredSession(serverLevel, var11);
         normalizeTrackedOrbs(serverLevel, var11);
         if (var11.hasFrontOrbs()) {
            var11.setExpiresAt(serverLevel.getGameTime() + 600L);
            if (!var11.hasLockedTarget()) {
               var11.setLockedTargetPos(getTargetPosition(serverLevel, player));
            }

            Vec3 targetPos = var11.getLockedTargetPos();
            int totalOrbCount = var11.getTotalOrbCount();
            int skyIndex = var11.getSkyOrbCount();
            SoulfireOrbEntity orbToLaunch = null;
            UUID launchedOrbId = null;

            while (var11.hasFrontOrbs() && orbToLaunch == null) {
               UUID candidateId = var11.popFrontOrb();
               if (candidateId == null) {
                  break;
               }

               if (serverLevel.getEntity(candidateId) instanceof SoulfireOrbEntity orb && orb.isAlive()) {
                  orbToLaunch = orb;
                  launchedOrbId = candidateId;
               }
            }

            if (orbToLaunch != null && launchedOrbId != null) {
               var11.addSkyOrb(launchedOrbId);
               orbToLaunch.transferToSky(getSkySpawnPosition(targetPos, skyIndex, totalOrbCount));
               reindexFrontOrbs(serverLevel, var11);
               if (!var11.hasFrontOrbs()) {
                  releaseSkyOrbs(serverLevel, var11);
                  var11.clear();
               }
            } else {
               normalizeTrackedOrbs(serverLevel, var11);
               if (!var11.hasFrontOrbs() && var11.getSkyOrbCount() > 0) {
                  releaseSkyOrbs(serverLevel, var11);
                  var11.clear();
               }
            }
         }
      }
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster) * 0.75F;
      return caster == null ? power : FireElementContext.resolveForSpell(caster, this).applySupportedDamage(power, caster);
   }

   private void summonFrontOrb(ServerLevel level, LivingEntity caster, int spellLevel, FireOrbsRecastData recastData) {
      SoulfireOrbEntity orb = this.createConfiguredOrb(level, caster, spellLevel);
      orb.prepareFrontOrbit(recastData.getFrontOrbCount());
      level.addFreshEntity(orb);
      recastData.addFrontOrb(orb.getUUID());
      orb.playSummonSound();
      orb.playSummonEffects();
   }

   private SoulfireOrbEntity createConfiguredOrb(ServerLevel level, LivingEntity caster, int spellLevel) {
      FireElementContext context = FireElementContext.resolveForSpell(caster, this);
      SoulfireOrbEntity orb = new SoulfireOrbEntity(level, caster);
      orb.setDamage(this.getDamage(spellLevel, caster));
      orb.setIsSoulFire(context.isSoulFire());
      orb.setIsInfernalFire(context.isInfernalFire());
      return orb;
   }

   private static Vec3 getTargetPosition(Level level, LivingEntity caster) {
      Vec3 start = caster.getEyePosition();
      Vec3 look = caster.getLookAngle();
      Vec3 end = start.add(look.scale(32.0));
      HitResult hitResult = level.clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, caster));
      return hitResult.getLocation();
   }

   private static Vec3 getSkySpawnPosition(Vec3 targetPos, int index, int totalOrbs) {
      int orbCount = Math.max(totalOrbs, 1);
      double angle = (Math.PI * 2) * index / orbCount;
      double xOffset = 3.0 * Math.cos(angle);
      double zOffset = 3.0 * Math.sin(angle);
      return targetPos.add(xOffset, 15.0, zOffset);
   }

   private static void cleanupExpiredSession(ServerLevel level, FireOrbsRecastData recastData) {
      if (recastData.getTotalOrbCount() > 0 && !recastData.isActive(level.getGameTime())) {
         cleanupTrackedOrbs(level, recastData);
      }
   }

   private static void cleanupTrackedOrbs(ServerLevel level, FireOrbsRecastData recastData) {
      for (UUID orbId : recastData.getFrontOrbIds()) {
         if (level.getEntity(orbId) instanceof SoulfireOrbEntity orb && orb.isAlive()) {
            orb.discard();
         }
      }

      for (UUID orbIdx : recastData.getSkyOrbIds()) {
         if (level.getEntity(orbIdx) instanceof SoulfireOrbEntity orb && orb.isAlive()) {
            orb.discard();
         }
      }

      recastData.clear();
   }

   private static void normalizeTrackedOrbs(ServerLevel level, FireOrbsRecastData recastData) {
      List<UUID> validFrontOrbs = new ArrayList<>();

      for (UUID orbId : recastData.getFrontOrbIds()) {
         if (level.getEntity(orbId) instanceof SoulfireOrbEntity orb && orb.isAlive()) {
            validFrontOrbs.add(orbId);
         }
      }

      recastData.setFrontOrbIds(validFrontOrbs);
      List<UUID> validSkyOrbs = new ArrayList<>();

      for (UUID orbIdx : recastData.getSkyOrbIds()) {
         if (level.getEntity(orbIdx) instanceof SoulfireOrbEntity orb && orb.isAlive()) {
            validSkyOrbs.add(orbIdx);
         }
      }

      recastData.setSkyOrbIds(validSkyOrbs);
      reindexFrontOrbs(level, recastData);
      if (recastData.getTotalOrbCount() <= 0) {
         recastData.clear();
      }
   }

   private static void reindexFrontOrbs(ServerLevel level, FireOrbsRecastData recastData) {
      List<UUID> frontOrbIds = recastData.getFrontOrbIds();

      for (int i = 0; i < frontOrbIds.size(); i++) {
         if (level.getEntity(frontOrbIds.get(i)) instanceof SoulfireOrbEntity orb && orb.isAlive()) {
            orb.prepareFrontOrbit(i);
         }
      }
   }

   private static void releaseSkyOrbs(ServerLevel level, FireOrbsRecastData recastData) {
      for (UUID orbId : recastData.getSkyOrbIds()) {
         if (level.getEntity(orbId) instanceof SoulfireOrbEntity orb && orb.isAlive()) {
            orb.releaseAfter(8);
         }
      }
   }
}
