package com.somake.somakespells.util;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.SpiritEmpowermentSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.Optional;
import net.miauczel.legendary_monsters.Particle.custom.Circle.EnumRingBehavior;
import net.miauczel.legendary_monsters.Particle.custom.Circle.RingData;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class RedSoulVisuals {
   private static final double ACTIVATION_RADIUS = 4.0;
   private static final float ACTIVATION_DAMAGE = 1.0F;
   private static final double ACTIVATION_KNOCKBACK = 0.85;

   private RedSoulVisuals() {
   }

   public static void beginActivation(LivingEntity entity) {
      if (entity.level() instanceof ServerLevel level) {
         playSound(level, entity, "ominous_windup", 1.0F, 0.9F);
         CameraShakeEntity.cameraShake(level, entity.position(), 12.0F, 0.04F, 0, 12);
         particle("ghostly_soul").ifPresent(soul -> spawnConvergingSouls(level, entity, soul, 18));
      }
   }

   public static void tickActivation(LivingEntity entity, int activationTick) {
      if (entity.level() instanceof ServerLevel level) {
         if (activationTick <= 8) {
            particle("ghostly_soul").ifPresent(soul -> spawnConvergingSouls(level, entity, soul, 3));
         } else {
            if (activationTick == 9) {
               Vec3 center = entity.position().add(0.0, entity.getBbHeight() * 0.55, 0.0);
               particle("soul_explosion_red").ifPresent(particle -> level.sendParticles(particle, center.x, center.y, center.z, 5, 0.65, 0.85, 0.65, 0.02));
               particle("red_soul_flame").ifPresent(particle -> level.sendParticles(particle, center.x, center.y, center.z, 32, 0.7, 0.95, 0.7, 0.045));
               level.sendParticles(
                  new RingData(0.0F, (float) (Math.PI / 2), 28, 1.0F, 0.02F, 0.02F, 0.9F, 42.0F, false, EnumRingBehavior.GROW),
                  entity.getX(),
                  entity.getY() + 0.08,
                  entity.getZ(),
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0
               );
               playSound(level, entity, "soul_shield_smash", 1.0F, 0.82F);
               CameraShakeEntity.cameraShake(level, entity.position(), 16.0F, 0.09F, 0, 12);
               applyActivationRepulsion(level, entity);
            }
         }
      }
   }

   public static void tickAura(LivingEntity entity, int idleTicks) {
      if (entity.level() instanceof ServerLevel level && !entity.isInvisible()) {
         int tick = entity.tickCount;
         Vec3 center = entity.position().add(0.0, entity.getBbHeight() * 0.55, 0.0);
         if (tick % 5 == 0) {
            particle("ghostly_soul_red").ifPresent(particle -> level.sendParticles(particle, center.x, center.y, center.z, 2, 0.38, 0.52, 0.38, 0.012));
         }

         if (tick % 10 == 0) {
            particle("red_soul_flame").ifPresent(particle -> level.sendParticles(particle, center.x, center.y + 0.12, center.z, 1, 0.08, 0.12, 0.08, 0.0));
         }

         double horizontalSpeed = entity.getDeltaMovement().horizontalDistanceSqr();
         if (horizontalSpeed > 0.012 && tick % 3 == 0) {
            Vec3 behind = entity.position().subtract(horizontalForward(entity).scale(0.45)).add(0.0, 0.25, 0.0);
            particle("ghostly_soul_red")
               .ifPresent(particle -> level.sendParticles(particle, behind.x, behind.y, behind.z, entity.isSprinting() ? 3 : 1, 0.22, 0.18, 0.22, 0.015));
         }

         if ((!entity.onGround() || entity.isSprinting()) && tick % 3 == 0) {
            spawnWingTrails(level, entity, 2);
         }

         if (idleTicks >= 45 && tick % 20 == 0) {
            particle("ground_soul_red")
               .ifPresent(particle -> level.sendParticles(particle, entity.getX(), entity.getY() + 2.0, entity.getZ(), 1, 0.3, 0.02, 0.3, 0.0));
            if (tick % 40 == 0) {
               level.sendParticles(
                  new RingData(0.0F, (float) (Math.PI / 2), 24, 0.75F, 0.01F, 0.02F, 0.45F, 24.0F, false, EnumRingBehavior.GROW),
                  entity.getX(),
                  entity.getY() + 0.06,
                  entity.getZ(),
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0
               );
            }
         }
      }
   }

   public static void spawnSpiritCastPulse(ServerLevel level, LivingEntity caster) {
      if (RedSoulHelper.isActive(caster) && !caster.isInvisible()) {
         Vec3 center = caster.position().add(0.0, caster.getBbHeight() * 0.62, 0.0);
         particle("red_soul_flame").ifPresent(particle -> level.sendParticles(particle, center.x, center.y, center.z, 14, 0.42, 0.55, 0.42, 0.025));
         particle("ghostly_soul_red").ifPresent(particle -> level.sendParticles(particle, center.x, center.y, center.z, 10, 0.55, 0.6, 0.55, 0.04));
         level.sendParticles(
            new RingData(0.0F, (float) (Math.PI / 2), 20, 1.0F, 0.04F, 0.04F, 0.65F, 20.0F, false, EnumRingBehavior.GROW),
            caster.getX(),
            caster.getY() + 0.08,
            caster.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
         spawnWingTrails(level, caster, 5);
         playSound(level, caster, "soul_fly", 0.42F, 1.18F);
      }
   }

   public static void deactivate(LivingEntity entity) {
      if (entity.level() instanceof ServerLevel level) {
         Vec3 var3 = entity.position().add(0.0, entity.getBbHeight() * 0.55, 0.0);
         particle("ghostly_soul_red").ifPresent(particle -> level.sendParticles(particle, var3.x, var3.y, var3.z, 24, 0.75, 0.85, 0.75, 0.055));
         particle("ghostly_soul").ifPresent(particle -> level.sendParticles(particle, var3.x, var3.y, var3.z, 14, 0.55, 0.7, 0.55, 0.035));
         level.sendParticles(
            new RingData(0.0F, (float) (Math.PI / 2), 24, 0.75F, 0.02F, 0.03F, 0.65F, 32.0F, false, EnumRingBehavior.SHRINK),
            entity.getX(),
            entity.getY() + 0.08,
            entity.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
         playSound(level, entity, "soul_fly", 0.8F, 0.62F);
      }
   }

   private static void spawnConvergingSouls(ServerLevel level, LivingEntity entity, ParticleOptions particle, int count) {
      Vec3 destination = entity.position().add(0.0, entity.getBbHeight() * 0.58, 0.0);

      for (int i = 0; i < count; i++) {
         double angle = entity.getRandom().nextDouble() * Math.PI * 2.0;
         double radius = 1.8 + entity.getRandom().nextDouble() * 1.8;
         Vec3 origin = entity.position().add(Math.cos(angle) * radius, 0.2 + entity.getRandom().nextDouble() * 2.8, Math.sin(angle) * radius);
         Vec3 velocity = destination.subtract(origin).normalize().scale(0.18);
         level.sendParticles(particle, origin.x, origin.y, origin.z, 0, velocity.x, velocity.y, velocity.z, 1.0);
      }
   }

   private static void applyActivationRepulsion(ServerLevel level, LivingEntity caster) {
      AABB area = caster.getBoundingBox().inflate(4.0);

      for (LivingEntity target : level.getEntitiesOfClass(
         LivingEntity.class, area, targetx -> targetx.isAlive() && targetx != caster && !targetx.isAlliedTo(caster) && targetx.distanceToSqr(caster) <= 16.0
      )) {
         DamageSources.applyDamage(target, 1.0F, ((SpiritEmpowermentSpell)ModSpells.SPIRIT_EMPOWERMENT.get()).getDamageSource(caster));
         Vec3 horizontal = target.position().subtract(caster.position()).multiply(1.0, 0.0, 1.0);
         if (horizontal.lengthSqr() < 1.0E-4) {
            horizontal = horizontalForward(caster).scale(-1.0);
         } else {
            horizontal = horizontal.normalize();
         }

         target.push(horizontal.x * 0.85, 0.25, horizontal.z * 0.85);
         target.hurtMarked = true;
      }
   }

   private static void spawnWingTrails(ServerLevel level, LivingEntity entity, int count) {
      Vec3 forward = horizontalForward(entity);
      Vec3 right = new Vec3(-forward.z, 0.0, forward.x);
      Vec3 base = entity.position().subtract(forward.scale(0.35)).add(0.0, entity.getBbHeight() * 0.72, 0.0);
      particle("red_soul_flame").ifPresent(particle -> {
         for (double side : new double[]{-0.72, 0.72}) {
            Vec3 point = base.add(right.scale(side));
            level.sendParticles(particle, point.x, point.y, point.z, count, 0.12, 0.28, 0.12, 0.02);
         }
      });
   }

   private static Vec3 horizontalForward(LivingEntity entity) {
      Vec3 look = entity.getLookAngle();
      Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
      return horizontal.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 0.0, 1.0) : horizontal.normalize();
   }

   private static Optional<ParticleOptions> particle(String path) {
      return BuiltInRegistries.PARTICLE_TYPE.getOptional(LegendaryMonstersCompat.id(path)).map(type -> (ParticleOptions)type);
   }

   private static void playSound(ServerLevel level, LivingEntity entity, String path, float volume, float pitch) {
      Optional<SoundEvent> sound = BuiltInRegistries.SOUND_EVENT.getOptional(LegendaryMonstersCompat.id(path));
      sound.ifPresent(event -> level.playSound(null, entity.blockPosition(), event, SoundSource.PLAYERS, volume, pitch));
   }
}
