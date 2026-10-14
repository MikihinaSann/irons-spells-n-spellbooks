package com.gametechbc.traveloptics.effects.reversal;

import com.gametechbc.traveloptics.entity.spells.reversal.ReversalEntity;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOSounds;
import com.gametechbc.traveloptics.util.TODamageTypes;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ReversalEffect extends MagicMobEffect {
   private float damageMultiplier = 1.0F;

   public ReversalEffect() {
      super(MobEffectCategory.BENEFICIAL, 16711680);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide) {
         MobEffectInstance effectInstance = entity.getEffect(TOEffects.REVERSAL);
         if (effectInstance != null) {
            int duration = effectInstance.getDuration();
            if (duration > 8) {
               entity.removeEffect(TOEffects.REVERSAL);
               entity.addEffect(new MobEffectInstance(TOEffects.REVERSAL, 8, amplifier, false, true));
            }

            if (duration == 2) {
               float storedDamage = ReversalEffectHandler.getStoredDamage(entity);
               if (storedDamage > 0.0F && effectInstance instanceof ReversalEffectInstance reversalEffectInstance) {
                  this.performRaycastAndApplyDamage(entity, storedDamage * reversalEffectInstance.getDamageMultiplier());
                  ReversalEffectHandler.clearStoredDamage(entity);
               }
            }
         }
      }

      return true;
   }

   public void onEffectRemoved(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide) {
         Projectile lastProjectile = ReversalEffectHandler.getLastProjectileHit(entity);
         if (entity instanceof Player player && lastProjectile != null) {
            this.reflectProjectile(player, lastProjectile);
            ReversalEffectHandler.clearLastProjectileHit(entity);
            this.playSound(player, (SoundEvent)TOSounds.REVERSAL_TRIGGER.get());
         }
      }

      super.onEffectRemoved(entity, amplifier);
   }

   private void reflectProjectile(Player player, Projectile originalProjectile) {
      Level level = player.level();
      Vec3 lookDirection = player.getLookAngle();
      if (originalProjectile.getType().create(level) instanceof Projectile projectile) {
         projectile.setPos(player.getEyePosition(1.0F));
         projectile.shoot(lookDirection.x, lookDirection.y, lookDirection.z, 1.5F, 0.0F);
         level.addFreshEntity(projectile);
         ReversalEntity reversal = new ReversalEntity(level, false);
         reversal.moveTo(player.position());
         reversal.setYRot(player.getYRot());
         reversal.setXRot(player.getXRot());
         level.addFreshEntity(reversal);
         player.getActiveEffects()
            .stream()
            .<Holder>map(MobEffectInstance::getEffect)
            .filter(holder -> ((MobEffect)holder.value()).getCategory() == MobEffectCategory.HARMFUL)
            .forEach(player::removeEffect);
         player.displayClientMessage(Component.translatable("effect.gametechbcs_spellbooks.reversal.projectile.feedback"), true);
      }
   }

   private void performRaycastAndApplyDamage(LivingEntity entity, float adjustedDamage) {
      Player player = (Player)entity;
      Vec3 start = player.getEyePosition(1.0F);
      Vec3 end = start.add(player.getLookAngle().scale(5.0));
      Level level = player.level();
      EntityHitResult entityHitResult = this.raycastForEntities(level, player, start, end, targetEntityx -> targetEntityx != player && targetEntityx.isAlive());
      this.playSound(player, (SoundEvent)TOSounds.REVERSAL_TRIGGER.get());
      if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity targetEntity) {
         Holder<DamageType> damageTypeHolder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(TODamageTypes.REVERSAL);
         DamageSource damageSource = new DamageSource(damageTypeHolder);
         targetEntity.hurt(damageSource, adjustedDamage);
         Vec3 targetPosition = targetEntity.position().add(0.0, 0.8, 0.0);
         ReversalEntity reversal = new ReversalEntity(level, false);
         reversal.moveTo(targetPosition);
         reversal.setYRot(player.getYRot());
         reversal.setXRot(player.getXRot());
         level.addFreshEntity(reversal);
         player.getActiveEffects()
            .stream()
            .<Holder>map(MobEffectInstance::getEffect)
            .filter(holder -> ((MobEffect)holder.value()).getCategory() == MobEffectCategory.HARMFUL)
            .forEach(player::removeEffect);
         player.displayClientMessage(Component.literal("Reversed " + String.format("%.0f", adjustedDamage) + " damage!"), true);
      }
   }

   private EntityHitResult raycastForEntities(Level level, Player player, Vec3 start, Vec3 end, Predicate<LivingEntity> predicate) {
      double distance = 5.0;
      Vec3 direction = end.subtract(start).normalize();
      Vec3 rayEnd = start.add(direction.scale(distance));
      double raycastInflation = 4.0;
      EntityHitResult entityHitResult = null;
      double closestDistance = distance;

      for (LivingEntity entity : level.getEntitiesOfClass(
         LivingEntity.class, player.getBoundingBox().expandTowards(direction.scale(distance)).inflate(raycastInflation)
      )) {
         if (predicate.test(entity)) {
            Vec3 entityPos = entity.position();
            double distanceToEntity = start.distanceTo(entityPos);
            if (distanceToEntity < closestDistance) {
               closestDistance = distanceToEntity;
               entityHitResult = new EntityHitResult(entity);
            }
         }
      }

      return entityHitResult;
   }

   private void playSound(Entity entity, SoundEvent sound) {
      entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public void setDamageMultiplier(float multiplier) {
      this.damageMultiplier = multiplier;
   }
}
