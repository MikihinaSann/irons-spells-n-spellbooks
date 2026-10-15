package com.somake.somakespells.spells.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.RedEchoEntity;
import com.somake.somakespells.item.RedSoulLanternProgressHelper;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class CrimsonReflectionSpell extends AbstractSpell {
   private static final int RITUAL_COLOR = 10620450;
   private static final double MAX_RITUAL_MOVEMENT_SQR = 2.25;
   private static final DustParticleOptions RITUAL_DUST = new DustParticleOptions(new Vector3f(0.75F, 0.03F, 0.08F), 1.0F);
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "crimson_reflection");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(MagicFromTheEastCompat.spiritSchoolOrEvocation())
      .setMaxLevel(1)
      .setCooldownSeconds(180.0)
      .setAllowCrafting(false)
      .build();

   public CrimsonReflectionSpell() {
      this.baseManaCost = 300;
      this.manaCostPerLevel = 0;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 0;
      this.castTime = 160;
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

   public boolean allowLooting() {
      return false;
   }

   public boolean requiresLearning() {
      return false;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return MagicFromTheEastCompat.getSpiritCastSound();
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      if (caster instanceof ServerPlayer player) {
         if (!RedSoulLanternProgressHelper.hasChargedLantern(player)) {
            deny(player, "message.somakespells.crimson_reflection.requires_lantern");
            return false;
         } else {
            RedEchoEntity.DuelCheck check = RedEchoEntity.checkDuelStart(player);
            if (!check.allowed()) {
               deny(player, check.messageKey());
               return false;
            } else {
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity caster, @Nullable MagicData playerMagicData) {
      super.onServerPreCast(level, spellLevel, caster, playerMagicData);
      if (caster instanceof ServerPlayer player && playerMagicData != null) {
         TargetedAreaEntity circle = TargetedAreaEntity.createTargetAreaEntity(level, player.position(), 4.5F, 10620450);
         circle.setDuration(this.getEffectiveCastTime(spellLevel, player) + 20);
         circle.setShouldFade(true);
         playerMagicData.setAdditionalCastData(new TargetAreaCastData(player.position(), circle));
      }
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity caster, @Nullable MagicData playerMagicData) {
      if (caster instanceof ServerPlayer player && level instanceof ServerLevel serverLevel) {
         if (!RedSoulLanternProgressHelper.hasChargedLantern(player)) {
            deny(player, "message.somakespells.crimson_reflection.requires_lantern");
            Utils.serverSideCancelCast(player, false);
         } else if (playerMagicData != null
            && playerMagicData.getAdditionalCastData() instanceof TargetAreaCastData ritual
            && player.distanceToSqr(ritual.getCenter()) > 2.25) {
            deny(player, "message.somakespells.crimson_reflection.left_circle");
            Utils.serverSideCancelCast(player, false);
         } else {
            RedEchoEntity.DuelCheck check = RedEchoEntity.checkDuelStart(player);
            if (!check.allowed()) {
               deny(player, check.messageKey());
               Utils.serverSideCancelCast(player, false);
            } else {
               if (player.tickCount % 4 == 0) {
                  double angle = player.tickCount * 0.28;
                  double radius = 2.4;
                  serverLevel.sendParticles(
                     RITUAL_DUST,
                     player.getX() + Math.cos(angle) * radius,
                     player.getY() + 0.15,
                     player.getZ() + Math.sin(angle) * radius,
                     1,
                     0.0,
                     0.04,
                     0.0,
                     0.0
                  );
               }
            }
         }
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (caster instanceof ServerPlayer player && RedSoulLanternProgressHelper.hasChargedLantern(player) && RedEchoEntity.startDuel(player)) {
         RedSoulLanternProgressHelper.consumeCharge(player);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   public void onServerCastComplete(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData, boolean cancelled) {
      super.onServerCastComplete(level, spellLevel, caster, playerMagicData, cancelled);
   }

   private static void deny(ServerPlayer player, String translationKey) {
      player.displayClientMessage(Component.translatable(translationKey).withStyle(ChatFormatting.RED), true);
   }
}
