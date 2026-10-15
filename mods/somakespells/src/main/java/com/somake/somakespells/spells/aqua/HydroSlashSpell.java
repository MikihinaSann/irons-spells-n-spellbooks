package com.somake.somakespells.spells.aqua;

import com.github.L_Ender.cataclysm.client.particle.Options.ScyllaSwingParticleOptions;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.somake.somakespells.entity.WaterSplashEntity;
import com.somake.somakespells.registries.ModEntityTypes;
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
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HydroSlashSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "hydro_slash");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(ModSchools.AQUA.getId())
      .setMaxLevel(10)
      .setCooldownSeconds(10.0)
      .build();

   public HydroSlashSpell() {
      this.baseManaCost = 25;
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 1;
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
      return Optional.of(SoundEvents.PLAYER_ATTACK_SWEEP);
   }

   public int getRecastCount(int spellLevel, LivingEntity entity) {
      return 3;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int comboStage = 0;
      if (playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         RecastInstance recast = playerMagicData.getPlayerRecasts().getRecastInstance(this.getSpellId());
         int remaining = recast.getRemainingRecasts();
         if (remaining == 2) {
            comboStage = 1;
         } else {
            comboStage = 2;
         }
      } else if (entity instanceof ServerPlayer) {
         playerMagicData.getPlayerRecasts().addRecast(new RecastInstance(this.getSpellId(), spellLevel, 3, 80, castSource, null), playerMagicData);
      }

      float radius = 4.0F;
      Vec3 forward = entity.getForward();
      Vec3 origin = entity.getEyePosition();

      for (Entity target : level.getEntities(entity, entity.getBoundingBox().inflate(radius))) {
         if (target instanceof LivingEntity && target != entity && !target.isAlliedTo(entity)) {
            Vec3 dir = target.position().subtract(origin).normalize();
            double dot = forward.dot(dir);
            if (dot > 0.5 && entity.distanceToSqr(target) < radius * radius) {
               float damage = this.getDamage(spellLevel, entity);
               if (comboStage == 2) {
                  damage *= 1.5F;
               }

               DamageSources.applyDamage(target, damage, this.getDamageSource(entity));
            }
         }
      }

      if (level instanceof ServerLevel serverLevel) {
         float yaw = -entity.getYRot() + 180.0F;
         float pitch = -entity.getXRot();
         float scale = 1.0F;
         if (comboStage == 0) {
            pitch += 90.0F;
         } else if (comboStage == 1) {
            pitch -= 90.0F;
            yaw += 180.0F;
         } else {
            yaw += 90.0F;
            ScreenShake_Entity.ScreenShake(level, entity.position().add(forward.scale(2.0)), 10.0F, 0.1F, 0, 10);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)ModSounds.HEAVY_SMASH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            WaterSplashEntity splash = new WaterSplashEntity((EntityType<?>)ModEntityTypes.WATER_SPLASH.get(), level);
            Vec3 spawnPos = entity.position().add(forward.scale(2.0));
            splash.moveTo(spawnPos.x, entity.getY(), spawnPos.z);
            level.addFreshEntity(splash);
         }

         double d0 = entity.getX() + forward.x * 1.5;
         double d1 = entity.getY() + entity.getBbHeight() * 0.5 + forward.y * 1.5;
         double d2 = entity.getZ() + forward.z * 1.5;
         serverLevel.sendParticles(
            new ScyllaSwingParticleOptions(scale, (float)Math.toRadians(yaw), (float)Math.toRadians(pitch)), d0, d1, d2, 1, 0.0, 0.0, 0.0, 0.0
         );
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) + Utils.getWeaponDamage(entity);
   }

   private String getDamageText(int spellLevel, LivingEntity entity) {
      if (entity != null) {
         float weaponDamage = Utils.getWeaponDamage(entity);
         String plus = "";
         if (weaponDamage > 0.0F) {
            plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
         }

         String damage = Utils.stringTruncation(this.getDamage(spellLevel, entity), 1);
         return damage + plus;
      } else {
         return this.getSpellPower(spellLevel, entity) + "";
      }
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}));
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ONE_HANDED_HORIZONTAL_SWING_ANIMATION;
   }

   public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker);
   }
}
