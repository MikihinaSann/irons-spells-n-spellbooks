package com.somake.somakespells.spells.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.WingedRuinControllerEntity;
import com.somake.somakespells.util.RedSoulDurationHelper;
import com.somake.somakespells.util.RedSoulHelper;
import com.somake.somakespells.util.RedSoulVisuals;
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
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class WingedRuinSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "winged_ruin");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(MagicFromTheEastCompat.spiritSchoolOrEvocation())
      .setMaxLevel(8)
      .setCooldownSeconds(30.0)
      .build();

   public WingedRuinSpell() {
      this.baseManaCost = 60;
      this.manaCostPerLevel = 8;
      this.baseSpellPower = 11;
      this.spellPowerPerLevel = 2;
      this.castTime = 20;
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

   public Optional<SoundEvent> getCastStartSound() {
      return MagicFromTheEastCompat.getSpiritCastSound();
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      if (!RedSoulHelper.isActive(caster)) {
         if (caster instanceof ServerPlayer player) {
            player.displayClientMessage(
               Component.literal("[Red Soul] ")
                  .withStyle(new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD})
                  .append(Component.translatable("message.somakespells.winged_ruin.requires_red_soul").withStyle(ChatFormatting.RED)),
               true
            );
         }

         return false;
      } else {
         return caster.onGround()
            && !caster.isPassenger()
            && !caster.isFallFlying()
            && this.findDestination(level, caster, this.getRange(spellLevel)).isPresent();
      }
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getImpactDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getRange(spellLevel), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{Utils.stringTruncation(4.0, 0)})
      );
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity caster, @Nullable MagicData playerMagicData) {
      if (caster instanceof ServerPlayer player && !RedSoulDurationHelper.consumeTime(player, 400, 20)) {
         player.displayClientMessage(
            Component.literal("[Red Soul] ")
               .withStyle(new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD})
               .append(Component.translatable("message.somakespells.winged_ruin.insufficient_time").withStyle(ChatFormatting.RED)),
            true
         );
         if (playerMagicData != null) {
            playerMagicData.resetCastingState();
         }
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         RedSoulVisuals.spawnSpiritCastPulse(serverLevel, caster);
         this.findDestination(level, caster, this.getRange(spellLevel))
            .ifPresent(
               destination -> serverLevel.addFreshEntity(
                  new WingedRuinControllerEntity(
                     serverLevel,
                     caster,
                     destination,
                     this.getImpactDamage(spellLevel, caster),
                     this.getRuptureDamage(spellLevel, caster),
                     this.getImpactRadius(spellLevel)
                  )
               )
            );
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private Optional<Vec3> findDestination(Level level, LivingEntity caster, float range) {
      Vec3 eye = caster.getEyePosition();
      Vec3 end = eye.add(caster.getLookAngle().scale(range));
      BlockHitResult sight = level.clip(new ClipContext(eye, end, Block.COLLIDER, Fluid.NONE, caster));
      Vec3 horizontal = sight.getType() == Type.MISS ? end : sight.getLocation();
      BlockHitResult ground = level.clip(new ClipContext(horizontal.add(0.0, 8.0, 0.0), horizontal.add(0.0, -40.0, 0.0), Block.COLLIDER, Fluid.NONE, caster));
      if (ground.getType() == Type.MISS) {
         return Optional.empty();
      } else {
         Vec3 destination = ground.getLocation().add(0.0, 0.02, 0.0);
         return level.noCollision(caster, caster.getBoundingBox().move(destination.subtract(caster.position()))) ? Optional.of(destination) : Optional.empty();
      }
   }

   private float getRange(int level) {
      return 14.0F + level;
   }

   private float getImpactRadius(int level) {
      return 3.5F + level * 0.15F;
   }

   private float getImpactDamage(int level, LivingEntity caster) {
      return this.getSpellPower(level, caster) * 0.95F + 5.0F;
   }

   private float getRuptureDamage(int level, LivingEntity caster) {
      return this.getSpellPower(level, caster) * 0.28F + 1.0F;
   }
}
