package com.somake.somakespells.spells.blood;

import com.somake.somakespells.config.Config;
import com.somake.somakespells.entity.WitheredRoseVortexEntity;
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
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WitheredRoseVortexSpell extends AbstractSpell {
   private static final double RANGE = 28.0;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "withered_rose_vortex");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(24.0)
      .build();

   public WitheredRoseVortexSpell() {
      this.baseManaCost = 70;
      this.manaCostPerLevel = 16;
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 3;
      this.castTime = 28;
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

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CHARGED_CAST;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.HEARTSTOP_CAST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.WITHER_SPAWN);
   }

   public boolean allowLooting() {
      return !(Boolean)Config.DISABLE_WITHERED_ROSE_SPELLS_CRAFTING_AND_LOOTING.get();
   }

   public boolean allowCrafting() {
      return !(Boolean)Config.DISABLE_WITHERED_ROSE_SPELLS_CRAFTING_AND_LOOTING.get();
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamagePerPulse(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.duration", new Object[]{Utils.stringTruncation(this.getLifetimeTicks(spellLevel) / 20.0F, 1)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getWitherDuration(spellLevel), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      HitResult hit = Utils.raycastForEntity(
         level, caster, caster.getEyePosition(), caster.getEyePosition().add(caster.getLookAngle().scale(28.0)), true, 0.0F, entity -> false
      );
      Vec3 target = hit.getLocation();
      double groundY = this.findGround(level, target);
      WitheredRoseVortexEntity vortex = new WitheredRoseVortexEntity(
         level,
         caster,
         this.getDamagePerPulse(spellLevel, caster),
         this.getRadius(spellLevel, caster),
         this.getLifetimeTicks(spellLevel),
         this.getWitherDuration(spellLevel),
         this.getWitherAmplifier(spellLevel)
      );
      vortex.setPos(target.x, groundY + 0.04, target.z);
      level.addFreshEntity(vortex);
      level.playSound(null, target.x, groundY, target.z, SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 1.15F, 0.65F);
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private double findGround(Level level, Vec3 target) {
      int x = Mth.floor(target.x);
      int z = Mth.floor(target.z);
      int startY = Mth.floor(target.y) + 2;
      int minY = Math.max(level.getMinBuildHeight() + 1, startY - 12);

      for (int y = startY; y >= minY; y--) {
         BlockState state = level.getBlockState(new BlockPos(x, y, z));
         if (!state.getCollisionShape(level, new BlockPos(x, y, z)).isEmpty()) {
            return y + 1.0;
         }
      }

      return target.y;
   }

   private float getDamagePerPulse(int spellLevel, LivingEntity caster) {
      return 2.0F + this.getSpellPower(spellLevel, caster) * 0.34F;
   }

   private float getRadius(int spellLevel, LivingEntity caster) {
      float bloodPower = caster == null ? 1.0F : (float)this.getSchoolType().getPowerFor(caster);
      return Mth.clamp(3.25F + spellLevel * 0.48F + Math.max(0.0F, bloodPower - 1.0F) * 0.65F, 4.0F, 7.0F);
   }

   private int getLifetimeTicks(int spellLevel) {
      return 120 + (Mth.clamp(spellLevel, 1, 5) - 1) * 20;
   }

   private int getWitherDuration(int spellLevel) {
      return 50 + Mth.clamp(spellLevel, 1, 5) * 10;
   }

   private int getWitherAmplifier(int spellLevel) {
      return spellLevel >= 5 ? 1 : 0;
   }
}
