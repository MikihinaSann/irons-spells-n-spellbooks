package com.somake.somakespells.spells.aqua;

import com.somake.somakespells.entity.ControlledWaterEntity;
import com.somake.somakespells.registries.ModSchools;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class WaterControlSpell extends AbstractSpell {
   public static final double CONTROL_DISTANCE = 4.5;
   private static final int BASE_DURATION_TICKS = 120;
   private static final int DURATION_PER_LEVEL_TICKS = 20;
   private static final double BLOCK_PUSHBACK = 0.6;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "water_control");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(ModSchools.AQUA.getId())
      .setMaxLevel(5)
      .setCooldownSeconds(12.0)
      .build();

   public WaterControlSpell() {
      this.baseManaCost = 35;
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 7;
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
      return Optional.of(SoundEvents.WATER_AMBIENT);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      return true;
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(ParticleTypes.SPLASH, caster.getX(), caster.getY() + caster.getEyeHeight() * 0.75, caster.getZ(), 3, 0.1, 0.12, 0.1, 0.01);
         serverLevel.sendParticles(
            ParticleTypes.FALLING_WATER, caster.getX(), caster.getY() + caster.getEyeHeight() * 0.8, caster.getZ(), 4, 0.12, 0.12, 0.12, 0.0
         );
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!level.isClientSide) {
         ControlledWaterEntity existingWater = this.findControlledWater(level, caster);
         if (existingWater != null) {
            existingWater.discard();
         }

         ControlledWaterEntity controlledWater = new ControlledWaterEntity(level, caster);
         controlledWater.setLifetime(this.getDurationTicks(spellLevel));
         controlledWater.setDamage(this.getImpactDamage(spellLevel, caster));
         Vec3 targetPosition = this.computeControlPosition(level, caster);
         controlledWater.followTarget(targetPosition, caster);
         level.addFreshEntity(controlledWater);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getImpactDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(4.5, 1)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDurationTicks(spellLevel), 1)})
      );
   }

   private ControlledWaterEntity findControlledWater(Level level, LivingEntity caster) {
      for (ControlledWaterEntity water : level.getEntitiesOfClass(ControlledWaterEntity.class, caster.getBoundingBox().inflate(24.0))) {
         if (water.isOwnedBy(caster)) {
            return water;
         }
      }

      return null;
   }

   private int getDurationTicks(int spellLevel) {
      return 120 + (spellLevel - 1) * 20;
   }

   private float getImpactDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   private Vec3 computeControlPosition(Level level, LivingEntity caster) {
      Vec3 eyePosition = caster.getEyePosition();
      Vec3 look = caster.getLookAngle().normalize();
      Vec3 maxTarget = eyePosition.add(look.scale(4.5));
      HitResult hitResult = Utils.raycastForBlock(level, eyePosition, maxTarget, Fluid.NONE);
      return hitResult.getType() == Type.BLOCK && hitResult.getLocation().distanceToSqr(eyePosition) < 20.25
         ? hitResult.getLocation().subtract(look.scale(0.6))
         : maxTarget;
   }
}
