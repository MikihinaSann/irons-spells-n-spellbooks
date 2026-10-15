package com.somake.somakespells.spells.aqua;

import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.somake.somakespells.entity.WaterSplashEntity;
import com.somake.somakespells.particle.CustomStormParticleOptions;
import com.somake.somakespells.particle.OrbitingWaterParticleOptions;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
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
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class StormAuraSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "storm_aura");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(ModSchools.AQUA.getId())
      .setMaxLevel(5)
      .setCooldownSeconds(18.0)
      .build();

   public StormAuraSpell() {
      this.baseManaCost = 50;
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 24;
      this.spellPowerPerLevel = 4;
      this.castTime = 100;
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
      return Optional.of(SoundEvents.BEACON_AMBIENT);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      float radius = 7.0F;

      for (Entity target : level.getEntities(caster, caster.getBoundingBox().inflate(radius))) {
         if (target instanceof LivingEntity && target != caster && !target.isAlliedTo(caster)) {
            Vec3 dir = caster.position().subtract(target.position()).normalize();
            target.setDeltaMovement(target.getDeltaMovement().add(dir.scale(0.15)));
            target.hurtMarked = true;
         }
      }

      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            new CustomStormParticleOptions(0.2F, 0.8F, 1.0F, 1.0F, 2.0F, caster.getId(), 0.0F),
            caster.getX(),
            caster.getY() + 1.0,
            caster.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
         serverLevel.sendParticles(
            new CustomStormParticleOptions(0.1F, 0.5F, 0.9F, 2.0F, 1.0F, caster.getId(), 0.0F),
            caster.getX(),
            caster.getY() + 1.0,
            caster.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
         serverLevel.sendParticles(
            new OrbitingWaterParticleOptions(1.0F, 1.0F, 1.0F, 0.5F, 0.5F, caster.getId(), 0.0F),
            caster.getX(),
            caster.getY() + 1.0,
            caster.getZ(),
            3,
            0.0,
            0.0,
            0.0,
            0.0
         );
         serverLevel.sendParticles(
            new OrbitingWaterParticleOptions(1.0F, 1.0F, 1.0F, 1.0F, 2.0F, caster.getId(), 0.0F),
            caster.getX(),
            caster.getY() + 1.0,
            caster.getZ(),
            3,
            0.0,
            0.0,
            0.0,
            0.0
         );
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      float radius = 7.0F;
      float damage = this.getDamage(spellLevel, caster);

      for (Entity target : level.getEntities(caster, caster.getBoundingBox().inflate(radius))) {
         if (target instanceof LivingEntity livingTarget && target != caster && !target.isAlliedTo(caster) && target.distanceToSqr(caster) < radius * radius) {
            if (DamageSources.applyDamage(livingTarget, damage, this.getDamageSource(caster))) {
               livingTarget.addEffect(new MobEffectInstance(ModMobEffects.WET, 100, 0, false, false, true));
            }

            Vec3 dir = target.position().subtract(caster.position()).normalize();
            target.push(dir.x * 2.5, 0.5, dir.z * 2.5);
         }
      }

      if (level instanceof ServerLevel serverLevel) {
         ScreenShake_Entity.ScreenShake(serverLevel, caster.position(), 20.0F, 0.2F, 0, 20);
         serverLevel.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.PLAYERS, 2.0F, 0.8F);
         WaterSplashEntity splash = new WaterSplashEntity((EntityType<?>)ModEntityTypes.WATER_SPLASH.get(), level);
         splash.moveTo(caster.position());
         splash.setYRot(caster.getYRot());
         splash.setAnimationState(1);
         level.addFreshEntity(splash);
         serverLevel.sendParticles(
            new CustomStormParticleOptions(0.2F, 0.8F, 1.0F, 1.0F, 2.0F, caster.getId(), 0.5F),
            caster.getX(),
            caster.getY() + 1.0,
            caster.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
         serverLevel.sendParticles(
            new CustomStormParticleOptions(0.1F, 0.5F, 0.9F, 2.0F, 1.0F, caster.getId(), 0.5F),
            caster.getX(),
            caster.getY() + 1.0,
            caster.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
         serverLevel.sendParticles(
            new OrbitingWaterParticleOptions(1.0F, 1.0F, 1.0F, 1.0F, 2.0F, caster.getId(), 0.5F),
            caster.getX(),
            caster.getY() + 1.0,
            caster.getZ(),
            8,
            0.0,
            0.0,
            0.0,
            0.0
         );
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(7.0, 1)})
      );
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
