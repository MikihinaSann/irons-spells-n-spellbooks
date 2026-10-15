package com.somake.somakespells.spells.blood;

import com.somake.somakespells.capabilities.BloodmarkRecastData;
import com.somake.somakespells.entity.BloodmarkMarkerEntity;
import com.somake.somakespells.entity.BloodmarkZoneEntity;
import com.somake.somakespells.registries.ModAttachments;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;

public class BloodmarkSpell extends AbstractSpell {
   private static final int RECAST_WINDOW_TICKS = 140;
   private static final double MAX_MARK_DISTANCE = 128.0;
   private static final double MAX_MARK_DISTANCE_SQR = 16384.0;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "bloodmark");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(18.0)
      .build();

   public BloodmarkSpell() {
      this.baseManaCost = 18;
      this.manaCostPerLevel = 4;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 0;
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

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      if (!(caster instanceof ServerPlayer serverPlayer && level instanceof ServerLevel)) {
         return true;
      } else if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         return true;
      } else {
         BloodmarkRecastData data = (BloodmarkRecastData)serverPlayer.getData((AttachmentType)ModAttachments.BLOODMARK_RECAST.get());
         if (data.getPointCount() > 0 && !data.hasAllPoints()) {
            Vec3 point = this.getMarkedPoint(level, caster);
            if (this.isWithinMarkDistance(data, point)) {
               return true;
            } else {
               this.showTooFarMessage(serverPlayer);
               return false;
            }
         } else {
            return true;
         }
      }
   }

   public int getRecastCount(int spellLevel, LivingEntity entity) {
      return 5;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.AMETHYST_BLOCK_CHIME);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM);
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (caster instanceof ServerPlayer serverPlayer && level instanceof ServerLevel serverLevel) {
         BloodmarkRecastData data = (BloodmarkRecastData)serverPlayer.getData((AttachmentType)ModAttachments.BLOODMARK_RECAST.get());
         if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
            data.clear();
            data.setExpiresAt(serverLevel.getGameTime() + 140L);
            playerMagicData.getPlayerRecasts()
               .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, caster), 140, castSource, null), playerMagicData);
            Vec3 point = this.getMarkedPoint(level, caster);
            data.addPoint(point);
            serverLevel.addFreshEntity(
               new BloodmarkMarkerEntity(serverLevel, point.x, point.y, point.z, Math.max(40, (int)(data.getExpiresAt() - serverLevel.getGameTime())))
            );
            serverPlayer.displayClientMessage(Component.literal("Bloodmark point 1/4 marked"), true);
            super.onCast(level, spellLevel, caster, castSource, playerMagicData);
         } else {
            RecastInstance recastInstance = playerMagicData.getPlayerRecasts().getRecastInstance(this.getSpellId());
            if (recastInstance == null) {
               data.clear();
               super.onCast(level, spellLevel, caster, castSource, playerMagicData);
            } else {
               data.setExpiresAt(serverLevel.getGameTime() + 140L);
               if (!data.hasAllPoints()) {
                  Vec3 point = this.getMarkedPoint(level, caster);
                  if (!this.isWithinMarkDistance(data, point)) {
                     this.showTooFarMessage(serverPlayer);
                     return;
                  }

                  data.addPoint(point);
                  serverLevel.addFreshEntity(
                     new BloodmarkMarkerEntity(serverLevel, point.x, point.y, point.z, Math.max(40, (int)(data.getExpiresAt() - serverLevel.getGameTime())))
                  );
                  serverPlayer.displayClientMessage(Component.literal("Bloodmark point " + data.getPointCount() + "/4 marked"), true);
               } else {
                  List<Vec3> points = data.getPoints();
                  int duration = this.getZoneDuration(spellLevel, caster);
                  float damagePerPulse = this.getZoneDamage(spellLevel, caster);
                  serverLevel.addFreshEntity(new BloodmarkZoneEntity(serverLevel, caster, points, duration, damagePerPulse));
                  serverPlayer.displayClientMessage(Component.literal("Bloodmark connected"), true);
                  data.clear();
               }

               super.onCast(level, spellLevel, caster, castSource, playerMagicData);
            }
         }
      } else {
         super.onCast(level, spellLevel, caster, castSource, playerMagicData);
      }
   }

   private Vec3 getMarkedPoint(Level level, LivingEntity caster) {
      BlockPos groundPos = BlockPos.containing(caster.getX(), caster.getBoundingBox().minY - 0.2, caster.getZ());
      return new Vec3(groundPos.getX() + 0.5, groundPos.getY() + 1.02, groundPos.getZ() + 0.5);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getZoneDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getZoneDuration(spellLevel, caster), 1)})
      );
   }

   private int getZoneDuration(int spellLevel, LivingEntity caster) {
      float spellPower = caster != null ? this.getSpellPower(spellLevel, caster) : 0.0F;
      float bloodPower = caster != null ? (float)this.getSchoolType().getPowerFor(caster) : 1.0F;
      return Mth.clamp(160 + spellLevel * 20 + Math.round(spellPower * 5.0F + Math.max(0.0F, bloodPower - 1.0F) * 120.0F), 180, 420);
   }

   private float getZoneDamage(int spellLevel, LivingEntity caster) {
      float spellPower = caster != null ? this.getSpellPower(spellLevel, caster) : 0.0F;
      float bloodPower = caster != null ? (float)this.getSchoolType().getPowerFor(caster) : 1.0F;
      return 2.5F + spellLevel * 1.5F + spellPower * 0.35F + Math.max(0.0F, bloodPower - 1.0F) * 4.0F;
   }

   private boolean isWithinMarkDistance(BloodmarkRecastData data, Vec3 point) {
      List<Vec3> points = data.getPoints();
      if (points.isEmpty()) {
         return true;
      } else {
         Vec3 previousPoint = points.get(points.size() - 1);
         return point.subtract(previousPoint).horizontalDistanceSqr() <= 16384.0;
      }
   }

   private void showTooFarMessage(ServerPlayer serverPlayer) {
      serverPlayer.displayClientMessage(
         Component.literal("Bloodmark point is too far from the previous mark (max 128 blocks)").withStyle(ChatFormatting.RED), true
      );
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }
}
