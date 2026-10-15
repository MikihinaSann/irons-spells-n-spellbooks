package com.somake.somakespells.spells.aqua;

import com.github.L_Ender.cataclysm.entity.projectile.Lightning_Spear_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Water_Spear_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.somake.somakespells.entity.SpellLightningSpearEntity;
import com.somake.somakespells.entity.SpellWaterSpearEntity;
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
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThunderCloudSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "thunder_cloud");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(ModSchools.AQUA.getId())
      .setMaxLevel(10)
      .setCooldownSeconds(30.0)
      .build();

   public ThunderCloudSpell() {
      this.baseManaCost = 50;
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 3;
      this.spellPowerPerLevel = 1;
      this.castTime = 100;
   }

   public CastType getCastType() {
      return CastType.CONTINUOUS;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.WEATHER_RAIN);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.LIGHTNING_BOLT_THUNDER);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      return true;
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      HitResult result = Utils.raycastForBlock(level, caster.getEyePosition(), caster.getEyePosition().add(caster.getLookAngle().scale(32.0)), Fluid.NONE);
      Vec3 targetPos = result.getLocation();
      double cloudHeight = 8.0;
      Vec3 cloudCenter = targetPos.add(0.0, cloudHeight, 0.0);
      if (level instanceof ServerLevel serverLevel) {
         for (int i = 0; i < 4; i++) {
            double x = cloudCenter.x + (level.random.nextDouble() - 0.5) * 8.0;
            double y = cloudCenter.y + (level.random.nextDouble() - 0.5) * 2.0;
            double z = cloudCenter.z + (level.random.nextDouble() - 0.5) * 8.0;
            serverLevel.sendParticles((ParticleOptions)ModParticle.RAIN_CLOUD.get(), x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
         }

         for (int i = 0; i < 20; i++) {
            double x = cloudCenter.x + (level.random.nextDouble() - 0.5) * 6.0;
            double y = cloudCenter.y - 0.5;
            double z = cloudCenter.z + (level.random.nextDouble() - 0.5) * 6.0;
            serverLevel.sendParticles(ParticleTypes.FALLING_WATER, x, y, z, 1, 0.0, -1.0, 0.0, 0.0);
         }

         if (playerMagicData.getCastDurationRemaining() % 10 == 0) {
            float rainRadius = 4.0F;

            for (Entity target : level.getEntities(
               caster,
               new AABB(targetPos.x - rainRadius, targetPos.y, targetPos.z - rainRadius, targetPos.x + rainRadius, targetPos.y + 10.0, targetPos.z + rainRadius)
            )) {
               if (target instanceof LivingEntity livingTarget && target != caster) {
                  livingTarget.addEffect(new MobEffectInstance(ModMobEffects.WET, 100, 0, false, false, true));
               }
            }
         }

         if (playerMagicData.getCastDurationRemaining() % 5 == 0) {
            double spawnX = cloudCenter.x + (level.random.nextDouble() - 0.5) * 6.0;
            double spawnY = cloudCenter.y;
            double spawnZ = cloudCenter.z + (level.random.nextDouble() - 0.5) * 6.0;
            Vec3 spawnPos = new Vec3(spawnX, spawnY, spawnZ);
            double groundTargetX = targetPos.x + (level.random.nextDouble() - 0.5) * 4.0;
            double groundTargetZ = targetPos.z + (level.random.nextDouble() - 0.5) * 4.0;
            Vec3 groundTarget = new Vec3(groundTargetX, targetPos.y, groundTargetZ);
            Vec3 dir = groundTarget.subtract(spawnPos).normalize();
            float damage = this.getDamage(spellLevel, caster);
            float yRot = (float)(Mth.atan2(dir.z, dir.x) * (180.0 / Math.PI)) + 90.0F;
            float xRot = (float)(-(Mth.atan2(dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z)) * (180.0 / Math.PI)));
            if (level.random.nextBoolean()) {
               SpellWaterSpearEntity spear = new SpellWaterSpearEntity(
                  (EntityType<? extends Water_Spear_Entity>)ModEntityTypes.SPELL_WATER_SPEAR.get(), caster, spawnX, spawnY, spawnZ, dir, damage, level
               );
               spear.setDamageSpell(this);
               level.addFreshEntity(spear);
            } else {
               SpellLightningSpearEntity spear = new SpellLightningSpearEntity(
                  (EntityType<? extends Lightning_Spear_Entity>)ModEntityTypes.SPELL_LIGHTNING_SPEAR.get(), caster, spawnX, spawnY, spawnZ, dir, damage, level
               );
               spear.setDamageSpell(this);
               spear.setAreaDamage(damage * 0.5F);
               spear.setAreaRadius(2.0F);
               level.addFreshEntity(spear);
            }
         }
      }
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
