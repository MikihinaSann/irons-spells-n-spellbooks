package com.somake.somakespells.spells.blood;

import com.somake.somakespells.capabilities.ConnectionKind;
import com.somake.somakespells.capabilities.ConnectionPendingCastData;
import com.somake.somakespells.event.ConnectionEventHandler;
import com.somake.somakespells.registries.ModAttachments;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
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
import net.minecraft.world.level.Level;

public class CursedConnectionSpell extends AbstractSpell {
   private static final int RECAST_WINDOW_TICKS = 100;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "cursed_connection");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(30.0)
      .build();

   public CursedConnectionSpell() {
      this.baseManaCost = 45;
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 2;
      this.castTime = 15;
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
      return 2;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel), 1)}),
         Component.translatable("ui.irons_spellbooks.percent_damage", new Object[]{Utils.stringTruncation(this.getSharePercent(spellLevel) * 100.0F, 0)}),
         Component.translatable(
            "ui.somakespells.connection_max_distance", new Object[]{Utils.stringTruncation(ConnectionEventHandler.getConnectionMaxDistance(spellLevel), 0)}
         )
      );
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.AMETHYST_BLOCK_CHIME);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM);
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, caster, playerMagicData, this, 24, 0.35F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      ICastData iCastData = playerMagicData.getAdditionalCastData();
      if (level instanceof ServerLevel serverLevel && iCastData instanceof TargetEntityCastData targetData) {
         LivingEntity targetEntity = targetData.getTarget(serverLevel);
         if (targetEntity != null && targetEntity != caster && targetEntity.isAlive()) {
            if (caster instanceof ServerPlayer serverPlayer) {
               ConnectionPendingCastData pendingCastData = (ConnectionPendingCastData)serverPlayer.getData(ModAttachments.CONNECTION_PENDING_CAST);
               if (pendingCastData.isActive(level.getGameTime()) && pendingCastData.getKind() == ConnectionKind.CURSED) {
                  UUID firstTargetUuid = pendingCastData.getFirstTargetUuid();
                  LivingEntity firstTarget = this.getLivingTarget(serverLevel, firstTargetUuid);
                  pendingCastData.clear();
                  if (firstTarget != null && firstTarget.isAlive() && firstTarget != targetEntity) {
                     ConnectionEventHandler.createLink(
                        serverLevel, caster, firstTarget, targetEntity, spellLevel, this.getDuration(spellLevel), ConnectionKind.CURSED
                     );
                  } else {
                     this.markFirstTarget(serverLevel, playerMagicData, targetEntity, spellLevel, castSource, pendingCastData);
                  }
               } else {
                  this.markFirstTarget(serverLevel, playerMagicData, targetEntity, spellLevel, castSource, pendingCastData);
               }
            }

            super.onCast(level, spellLevel, caster, castSource, playerMagicData);
         } else {
            super.onCast(level, spellLevel, caster, castSource, playerMagicData);
         }
      } else {
         super.onCast(level, spellLevel, caster, castSource, playerMagicData);
      }
   }

   private void markFirstTarget(
      ServerLevel level, MagicData playerMagicData, LivingEntity targetEntity, int spellLevel, CastSource castSource, ConnectionPendingCastData pendingCastData
   ) {
      pendingCastData.set(targetEntity.getUUID(), spellLevel, level.getGameTime() + 100L, ConnectionKind.CURSED);
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, targetEntity), 100, castSource, null), playerMagicData);
      }

      double x = targetEntity.getX();
      double y = targetEntity.getY() + targetEntity.getBbHeight() * 0.5F;
      double z = targetEntity.getZ();
      MagicManager.spawnParticles(level, ParticleHelper.BLOOD, x, y, z, 14, 0.25, 0.35, 0.25, 0.03, true);
      level.sendParticles(new BlastwaveParticleOptions(0.82F, 0.12F, 0.16F, 0.6F), x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
   }

   private LivingEntity getLivingTarget(ServerLevel level, UUID uuid) {
      if (uuid == null) {
         return null;
      } else {
         return level.getEntity(uuid) instanceof LivingEntity livingEntity ? livingEntity : null;
      }
   }

   public int getDuration(int spellLevel) {
      return 200 + spellLevel * 40;
   }

   public float getSharePercent(int spellLevel) {
      return spellLevel * 0.1F;
   }
}
