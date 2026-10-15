package com.somake.somakespells.spells.fire;

import com.somake.somakespells.entity.ElementalBlazeEntity;
import com.somake.somakespells.event.PedestalRitualHandler;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.ritual.RitualElementalMode;
import com.somake.somakespells.util.FireElementContext;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class RitualFlameSpell extends AbstractSpell {
   private static final double CAST_RANGE = 32.0;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "ritual_flame");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(2.0)
      .build();

   public RitualFlameSpell() {
      this.baseManaCost = 8;
      this.manaCostPerLevel = 0;
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.BLAZE_SHOOT);
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel && caster instanceof ServerPlayer serverPlayer) {
         RitualElementalMode activationMode = this.getActivationMode(caster);
         EntityHitResult entityHitResult = this.raycastBlazeEntity(level, caster);
         if (!(entityHitResult != null && entityHitResult.getEntity() instanceof Blaze blaze)) {
            BlockHitResult hitResult = this.raycastBlock(level, caster);
            if (hitResult.getType() != Type.BLOCK) {
               this.showMessage(serverPlayer, "ui.somakespells.ritual_flame.invalid_target");
               return false;
            } else {
               RitualElementalMode mode = PedestalRitualHandler.getAltarRitualMode(serverLevel, hitResult.getBlockPos());
               if (mode != RitualElementalMode.SOUL_FIRE && mode == RitualElementalMode.INFERNAL_FIRE) {
               }
               return switch (PedestalRitualHandler.validateRitualActivation(serverLevel, hitResult.getBlockPos(), activationMode)) {
                  case READY -> true;
                  case ALREADY_ACTIVE -> {
                     this.showMessage(serverPlayer, "ui.somakespells.ritual_flame.already_active");
                     yield false;
                  }
                  case WRONG_ACTIVATION_MODE -> {
                     this.showWrongModeMessage(serverPlayer, mode);
                     yield false;
                  }
                  case NO_RECIPE -> {
                     this.showMessage(serverPlayer, "ui.somakespells.ritual_flame.no_recipe");
                     yield false;
                  }
                  default -> {
                     this.showMessage(serverPlayer, "ui.somakespells.ritual_flame.invalid_target");
                     yield false;
                  }
               };
            }
         } else if (this.canTransformBlaze(blaze, activationMode)) {
            return true;
         } else {
            this.showMessage(serverPlayer, "ui.somakespells.ritual_flame.invalid_target");
            return false;
         }
      } else {
         return true;
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         RitualElementalMode activationMode = this.getActivationMode(caster);
         EntityHitResult entityHitResult = this.raycastBlazeEntity(level, caster);
         if (entityHitResult != null && entityHitResult.getEntity() instanceof Blaze blaze) {
            if (this.transformBlaze(serverLevel, blaze, activationMode, caster)) {
               super.onCast(level, spellLevel, caster, castSource, playerMagicData);
            } else if (caster instanceof ServerPlayer serverPlayer) {
               this.showMessage(serverPlayer, "ui.somakespells.ritual_flame.invalid_target");
            }

            return;
         }

         BlockHitResult hitResult = this.raycastBlock(level, caster);
         if (hitResult.getType() == Type.BLOCK) {
            RitualElementalMode mode = PedestalRitualHandler.getAltarRitualMode(serverLevel, hitResult.getBlockPos());
            if (mode != RitualElementalMode.SOUL_FIRE && mode == RitualElementalMode.INFERNAL_FIRE) {
            }

            PedestalRitualHandler.RitualActivationResult result = PedestalRitualHandler.tryStartRitual(serverLevel, hitResult.getBlockPos(), activationMode);
            if (caster instanceof ServerPlayer serverPlayer && result != PedestalRitualHandler.RitualActivationResult.STARTED) {
               switch (result) {
                  case ALREADY_ACTIVE:
                     this.showMessage(serverPlayer, "ui.somakespells.ritual_flame.already_active");
                     break;
                  case WRONG_ACTIVATION_MODE:
                     this.showWrongModeMessage(serverPlayer, mode);
                     break;
                  case NO_RECIPE:
                     this.showMessage(serverPlayer, "ui.somakespells.ritual_flame.no_recipe");
                     break;
                  default:
                     this.showMessage(serverPlayer, "ui.somakespells.ritual_flame.invalid_target");
               }
            }
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private BlockHitResult raycastBlock(Level level, LivingEntity caster) {
      Vec3 start = caster.getEyePosition();
      Vec3 end = start.add(caster.getLookAngle().scale(32.0));
      return level.clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, caster));
   }

   private EntityHitResult raycastBlazeEntity(Level level, LivingEntity caster) {
      Vec3 start = caster.getEyePosition();
      Vec3 end = start.add(caster.getLookAngle().scale(32.0));
      AABB bounds = new AABB(start, end).inflate(1.0);
      return ProjectileUtil.getEntityHitResult(level, caster, start, end, bounds, this::isRitualBlazeTarget);
   }

   private boolean isRitualBlazeTarget(Entity entity) {
      return entity instanceof Blaze && entity.isAlive() && !entity.isSpectator();
   }

   private boolean canTransformBlaze(Blaze blaze, RitualElementalMode activationMode) {
      if (activationMode == RitualElementalMode.SOUL_FIRE) {
         return blaze.getType() == EntityType.BLAZE;
      } else {
         return activationMode == RitualElementalMode.INFERNAL_FIRE ? blaze.getType() == ModEntityTypes.SOUL_BLAZE.get() : false;
      }
   }

   private boolean transformBlaze(ServerLevel level, Blaze blaze, RitualElementalMode activationMode, LivingEntity caster) {
      if (activationMode == RitualElementalMode.SOUL_FIRE && blaze.getType() == EntityType.BLAZE) {
         ElementalBlazeEntity.transformToSoulBlaze(level, blaze);
         this.emitTransformationEffects(level, blaze.position(), activationMode);
         return true;
      } else if (activationMode == RitualElementalMode.INFERNAL_FIRE && blaze instanceof ElementalBlazeEntity.SoulBlazeEntity soulBlaze) {
         ElementalBlazeEntity.transformToInfernalBlaze(level, soulBlaze);
         this.emitTransformationEffects(level, blaze.position(), activationMode);
         return true;
      } else {
         return false;
      }
   }

   private void emitTransformationEffects(ServerLevel level, Vec3 position, RitualElementalMode mode) {
      switch (mode) {
         case SOUL_FIRE:
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, position.x, position.y + 0.9, position.z, 40, 0.35, 0.45, 0.35, 0.01);
            level.sendParticles((SimpleParticleType)ModParticles.SOUL_FIRE.get(), position.x, position.y + 0.9, position.z, 20, 0.25, 0.35, 0.25, 0.0);
            level.playSound(null, position.x, position.y, position.z, SoundEvents.SOUL_ESCAPE, SoundSource.HOSTILE, 1.0F, 0.95F);
            break;
         case INFERNAL_FIRE:
            level.sendParticles(
               ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.SOUL_FIRE.get()),
               position.x,
               position.y + 0.9,
               position.z,
               40,
               0.35,
               0.45,
               0.35,
               0.01
            );
            level.sendParticles(
               ModParticles.infernalFireLavaOr((SimpleParticleType)ModParticles.SOUL_FIRE_LAVA.get()),
               position.x,
               position.y + 0.9,
               position.z,
               16,
               0.2,
               0.2,
               0.2,
               0.0
            );
            level.playSound(null, position.x, position.y, position.z, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.2F, 0.75F);
      }
   }

   private void showMessage(ServerPlayer player, String translationKey) {
      player.displayClientMessage(Component.translatable(translationKey).withStyle(ChatFormatting.RED), true);
   }

   private void showWrongModeMessage(ServerPlayer player, RitualElementalMode altarMode) {
      switch (altarMode) {
         case SOUL_FIRE:
            this.showMessage(player, "ui.somakespells.ritual_flame.requires_soul_fire");
            break;
         case INFERNAL_FIRE:
            this.showMessage(player, "ui.somakespells.ritual_flame.requires_infernal_fire");
            break;
         default:
            this.showMessage(player, "ui.somakespells.ritual_flame.invalid_target");
      }
   }

   private RitualElementalMode getActivationMode(LivingEntity caster) {
      return FireElementContext.resolveForSpell(caster, this).mode();
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }
}
