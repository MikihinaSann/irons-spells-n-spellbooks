package com.gametechbc.traveloptics.effects;

import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.util.TODamageTypes;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VortexPunchEffect extends MobEffect {
   public VortexPunchEffect() {
      super(MobEffectCategory.BENEFICIAL, 10423267);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity != null) {
         Level level = entity.level();
         MobEffectInstance effectInstance = entity.getEffect(TOEffects.VORTEX_PUNCH);
         if (effectInstance != null) {
            int duration = effectInstance.getDuration();
            double range = 30.0;
            if (duration == 9) {
               Entity nearestVortex = this.findNearestVortex(entity, level, range);
               if (nearestVortex != null) {
                  Vec3 targetPos = nearestVortex.position();
                  Vec3 offset = entity.getLookAngle().scale(-2.8);
                  Vec3 teleportPos = targetPos.add(offset);
                  entity.teleportTo(teleportPos.x, teleportPos.y + 0.5, teleportPos.z);
               } else if (entity instanceof Player player) {
                  player.displayClientMessage(Component.translatable("effect.traveloptics.void_vortex.warning").withStyle(ChatFormatting.RED), true);
               }
            }

            if (duration == 2 && !level.isClientSide()) {
               Entity nearestVortex = this.findNearestVortex(entity, level, 5.0);
               if (nearestVortex != null) {
                  for (LivingEntity nearbyEntity : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(3.0))) {
                     if (nearbyEntity != entity) {
                        nearbyEntity.hurt(this.damageSource(entity), amplifier);
                        Vec3 currentVelocity = nearbyEntity.getDeltaMovement();
                        Vec3 knockUpVelocity = new Vec3(currentVelocity.x, 1.0, currentVelocity.z);
                        nearbyEntity.setDeltaMovement(knockUpVelocity);
                        nearbyEntity.hasImpulse = true;
                        MagicManager.spawnParticles(
                           level, ParticleHelper.UNSTABLE_ENDER, nearbyEntity.getX(), nearbyEntity.getY(), nearbyEntity.getZ(), 30, 0.0, 0.5, 0.0, 0.3, false
                        );
                        MagicManager.spawnParticles(
                           level, ParticleTypes.POOF, nearbyEntity.getX(), nearbyEntity.getY(), nearbyEntity.getZ(), 20, 0.0, 0.5, 0.0, 0.3, false
                        );
                     }
                  }
               }
            }

            if (duration == 2) {
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)ModSounds.ENDER_GUARDIAN_FIST.get(), SoundSource.PLAYERS, 1.0F, 1.0F
               );
               ScreenShake_Entity.ScreenShake(level, entity.position(), 15.0F, 0.02F, 10, 20);
            }
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   private Entity findNearestVortex(LivingEntity entity, Level level, double range) {
      AABB searchArea = entity.getBoundingBox().inflate(range);

      for (Entity nearbyEntity : level.getEntities(entity, searchArea)) {
         if (nearbyEntity.getType() == ModEntities.VOID_VORTEX.get()) {
            return nearbyEntity;
         }
      }

      return null;
   }

   private DamageSource damageSource(LivingEntity entity) {
      Holder<DamageType> damageTypeHolder = entity.level()
         .registryAccess()
         .registryOrThrow(Registries.DAMAGE_TYPE)
         .getHolderOrThrow(TODamageTypes.VOID_UPPERCUT);
      return new DamageSource(damageTypeHolder);
   }
}
