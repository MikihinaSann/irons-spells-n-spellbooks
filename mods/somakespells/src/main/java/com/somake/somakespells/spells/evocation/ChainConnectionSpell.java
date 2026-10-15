package com.somake.somakespells.spells.evocation;

import com.somake.somakespells.capabilities.ConnectionKind;
import com.somake.somakespells.capabilities.ConnectionPendingCastData;
import com.somake.somakespells.event.ConnectionEventHandler;
import com.somake.somakespells.registries.ModAttachments;
import com.somake.somakespells.registries.ModSchools;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ChainConnectionSpell extends AbstractSpell {
   private static final int RECAST_WINDOW_TICKS = 100;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "chain_connection");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(ModSchools.AQUA.getId())
      .setMaxLevel(5)
      .setCooldownSeconds(32.0)
      .build();

   public ChainConnectionSpell() {
      this.baseManaCost = 50;
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 0;
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
         Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getMaxDistance(spellLevel), 1)})
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
      return Optional.of(SoundEvents.CHAIN_PLACE);
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
               if (pendingCastData.isActive(level.getGameTime()) && pendingCastData.getKind() == ConnectionKind.SHACKLED) {
                  UUID firstTargetUuid = pendingCastData.getFirstTargetUuid();
                  LivingEntity firstTarget = this.getLivingTarget(serverLevel, firstTargetUuid);
                  pendingCastData.clear();
                  if (firstTarget != null && firstTarget.isAlive() && firstTarget != targetEntity) {
                     ConnectionEventHandler.createLink(
                        serverLevel, caster, firstTarget, targetEntity, spellLevel, this.getDuration(spellLevel), ConnectionKind.SHACKLED
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
      pendingCastData.set(targetEntity.getUUID(), spellLevel, level.getGameTime() + 100L, ConnectionKind.SHACKLED);
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, targetEntity), 100, castSource, null), playerMagicData);
      }

      level.sendParticles(
         ParticleTypes.ENCHANT, targetEntity.getX(), targetEntity.getY() + targetEntity.getBbHeight() * 0.5F, targetEntity.getZ(), 10, 0.25, 0.35, 0.25, 0.05
      );
      level.sendParticles(
         ParticleTypes.CRIT, targetEntity.getX(), targetEntity.getY() + targetEntity.getBbHeight() * 0.5F, targetEntity.getZ(), 4, 0.2, 0.2, 0.2, 0.02
      );
   }

   private LivingEntity getLivingTarget(ServerLevel level, UUID uuid) {
      if (uuid == null) {
         return null;
      } else {
         return level.getEntity(uuid) instanceof LivingEntity livingEntity ? livingEntity : null;
      }
   }

   public int getDuration(int spellLevel) {
      return 160 + spellLevel * 40;
   }

   public float getMaxDistance(int spellLevel) {
      return 4.5F + spellLevel * 0.5F;
   }
}
