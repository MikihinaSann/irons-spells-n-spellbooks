package com.gametechbc.traveloptics.effects;

import com.gametechbc.traveloptics.api.utils.CurioUtils;
import com.gametechbc.traveloptics.entity.extended.ExtendedFlareBombEntity;
import com.gametechbc.traveloptics.entity.extended.ExtendedIgnisFireballEntity;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOEntities;
import com.gametechbc.traveloptics.init.TOItems;
import com.github.L_Ender.cataclysm.entity.projectile.Flare_Bomb_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.ShockwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public class MeteorStormEffect extends MobEffect {
   private int ticksSinceLastSummon = 0;
   private double outerRadius = 25.0;

   public MeteorStormEffect() {
      super(MobEffectCategory.BENEFICIAL, 0);
   }

   public void setOuterRadius(double radius) {
      this.outerRadius = radius;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
         this.ticksSinceLastSummon++;
         if (this.ticksSinceLastSummon >= 15) {
            if (CurioUtils.getWearingCurio(entity, (Item)TOItems.FIRESTORM_RING.get())) {
               this.summonFlareBombs(serverLevel, entity);
            } else {
               this.summonProjectiles(serverLevel, entity);
            }

            this.ticksSinceLastSummon = 0;
         }
      }

      super.applyEffectTick(entity, amplifier);
      return true;
   }

   private void summonProjectiles(ServerLevel serverLevel, LivingEntity target) {
      RandomSource random = target.getRandom();
      double innerRadius = 8.0;
      List<Entity> nearbyEntities = serverLevel.getEntities(
         target,
         target.getBoundingBox().inflate(this.outerRadius),
         entity -> entity instanceof LivingEntity targetEntity && !this.isTeammate(target, targetEntity) && !this.isTamedCreature(target, targetEntity)
      );
      Collections.shuffle(nearbyEntities, new Random(random.nextLong()));
      int numEntitiesToTarget = Math.min(nearbyEntities.size(), 2);
      MobEffectInstance effect = target.getEffect(TOEffects.METEOR_STORM_EFFECT);
      int amplifier = effect != null ? effect.getAmplifier() : 0;

      for (int i = 0; i < 3; i++) {
         Vec3 spawnPosition;
         if (i < numEntitiesToTarget) {
            Entity targetEntity = nearbyEntities.get(i);
            Vec3 motion = targetEntity.getDeltaMovement();
            double predictionTime = 1.0;
            Vec3 predictedPosition = targetEntity.position().add(motion.scale(predictionTime));
            spawnPosition = new Vec3(predictedPosition.x, predictedPosition.y + 30.0, predictedPosition.z);
            MagicManager.spawnParticles(
               serverLevel,
               new ShockwaveParticleOptions(((SchoolType)SchoolRegistry.FIRE.get()).getTargetingColor(), -1.5F, true),
               predictedPosition.x,
               predictedPosition.y,
               predictedPosition.z,
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
         } else {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = innerRadius + random.nextDouble() * (this.outerRadius - innerRadius);
            double xOffset = Math.cos(angle) * distance;
            double zOffset = Math.sin(angle) * distance;
            double yOffset = 30.0;
            spawnPosition = new Vec3(target.getX() + xOffset, target.getY() + yOffset, target.getZ() + zOffset);
         }

         int adjustedAmplifier = amplifier;
         if (CurioUtils.getWearingCurio(target, (Item)TOItems.AZURE_IGNITION_BRACELET.get())) {
            adjustedAmplifier = amplifier + 1;
         }

         ExtendedIgnisFireballEntity fireball = new ExtendedIgnisFireballEntity(
            (EntityType<? extends ExtendedIgnisFireballEntity>)TOEntities.EXTENDED_IGNIS_FIREBALL.get(), serverLevel
         );
         fireball.setPos(spawnPosition);
         fireball.shoot(0.0, -1.0, 0.0, 2.5F, 0.0F);
         fireball.setSoul(target.getHealth() < target.getMaxHealth() * 0.5F || CurioUtils.getWearingCurio(target, (Item)TOItems.AZURE_IGNITION_BRACELET.get()));
         fireball.setCustomDamage(adjustedAmplifier);
         serverLevel.addFreshEntity(fireball);
         MagicManager.spawnParticles(
            serverLevel, (ParticleOptions)ModParticle.TRAP_FLAME.get(), spawnPosition.x, spawnPosition.y, spawnPosition.z, 1, 0.0, -1.0, 0.0, 0.0, true
         );
         MagicManager.spawnParticles(serverLevel, ParticleHelper.SUNBEAM, spawnPosition.x, spawnPosition.y, spawnPosition.z, 1, 1.0, 1.0, 1.0, 1.0, false);
         MagicManager.spawnParticles(serverLevel, ParticleHelper.SUNBEAM, spawnPosition.x, spawnPosition.y, spawnPosition.z, 1, 1.0, 1.0, 1.0, 1.0, true);
      }
   }

   private void summonFlareBombs(ServerLevel serverLevel, LivingEntity entity) {
      MobEffectInstance effect = entity.getEffect(TOEffects.METEOR_STORM_EFFECT);
      int amplifier = effect != null ? effect.getAmplifier() : 0;
      RandomSource random = entity.getRandom();
      double innerRadius = 4.0;

      for (int i = 0; i < 3; i++) {
         double angle = random.nextDouble() * Math.PI * 2.0;
         double distance = innerRadius + random.nextDouble() * (this.outerRadius - innerRadius);
         double xOffset = Math.cos(angle) * distance;
         double zOffset = Math.sin(angle) * distance;
         double yOffset = 30.0;
         Vec3 spawnPosition = new Vec3(entity.getX() + xOffset, entity.getY() + yOffset, entity.getZ() + zOffset);
         int adjustedAmplifier = amplifier;
         if (CurioUtils.getWearingCurio(entity, (Item)TOItems.AZURE_IGNITION_BRACELET.get())) {
            adjustedAmplifier = amplifier + 1;
         }

         ExtendedFlareBombEntity flareBomb = new ExtendedFlareBombEntity(
            (EntityType<? extends Flare_Bomb_Entity>)TOEntities.EXTENDED_FLARE_BOMB.get(), serverLevel
         );
         flareBomb.setPos(spawnPosition);
         flareBomb.setDeltaMovement(0.0, -1.0, 0.0);
         flareBomb.setFlameJetDamage(adjustedAmplifier);
         flareBomb.setOwner(entity);
         serverLevel.addFreshEntity(flareBomb);
         MagicManager.spawnParticles(
            serverLevel, (ParticleOptions)ModParticle.TRAP_FLAME.get(), spawnPosition.x, spawnPosition.y, spawnPosition.z, 1, 0.0, -1.0, 0.0, 0.0, true
         );
         MagicManager.spawnParticles(serverLevel, ParticleHelper.SUNBEAM, spawnPosition.x, spawnPosition.y, spawnPosition.z, 1, 1.0, 1.0, 1.0, 1.0, false);
         MagicManager.spawnParticles(serverLevel, ParticleHelper.SUNBEAM, spawnPosition.x, spawnPosition.y, spawnPosition.z, 1, 1.0, 1.0, 1.0, 1.0, true);
      }
   }

   private boolean isTeammate(LivingEntity caster, LivingEntity target) {
      return caster.isAlliedTo(target);
   }

   private boolean isTamedCreature(LivingEntity caster, LivingEntity target) {
      return !(target instanceof TamableAnimal tamedAnimal) ? false : tamedAnimal.isTame() && tamedAnimal.getOwner() == caster;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
