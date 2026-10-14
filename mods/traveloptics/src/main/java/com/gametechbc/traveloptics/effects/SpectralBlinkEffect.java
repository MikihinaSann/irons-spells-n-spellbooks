package com.gametechbc.traveloptics.effects;

import com.gametechbc.traveloptics.api.utils.CurioUtils;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOItems;
import com.gametechbc.traveloptics.init.TOSounds;
import com.gametechbc.traveloptics.util.TOTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class SpectralBlinkEffect extends MobEffect {
   public SpectralBlinkEffect() {
      super(MobEffectCategory.BENEFICIAL, 16777215);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity != null) {
         Level world = entity.level();
         MobEffectInstance effectInstance = entity.getEffect(TOEffects.SPECTRAL_BLINK);
         if (effectInstance != null) {
            int duration = effectInstance.getDuration();
            int level = amplifier + 1;
            double range = 1.0 + level;
            if (duration == 60) {
               world.playSound(null, entity.blockPosition(), (SoundEvent)TOSounds.SPECTRAL_BLINK_CHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            if (duration > 60) {
               entity.removeEffect(TOEffects.SPECTRAL_BLINK);
               entity.addEffect(new MobEffectInstance(TOEffects.SPECTRAL_BLINK, 60, amplifier, false, true));
            }

            for (Entity nearbyEntity : world.getEntities(entity, entity.getBoundingBox().inflate(range))) {
               if (nearbyEntity instanceof LivingEntity livingEntity && nearbyEntity != entity) {
                  livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false));
               }
            }

            if (duration == 1) {
               this.handleBlinkLogic(entity, amplifier, range, world);
            }
         }
      }

      return true;
   }

   private void handleBlinkLogic(LivingEntity entity, int amplifier, double range, Level world) {
      boolean hasSpectralShift = CurioUtils.getWearingCurio(entity, (Item)TOItems.AMULET_OF_SPECTRAL_SHIFT.get());
      if (entity.isCrouching() && hasSpectralShift) {
         this.teleportEntityToPlayer(entity, range, world);
      } else {
         this.teleportPlayerToEntity(entity, range, world);
      }
   }

   private void teleportEntityToPlayer(LivingEntity entity, double range, Level world) {
      Vec3 startVec = entity.getEyePosition(1.0F);
      Vec3 viewVec = entity.getLookAngle();
      Vec3 endVec = startVec.add(viewVec.scale(range));
      AABB boundingBox = entity.getBoundingBox().expandTowards(viewVec.scale(range)).inflate(1.0);
      EntityHitResult entityHitResult = this.getForgivingEntityHitResult(world, entity, startVec, endVec, boundingBox);
      if (entityHitResult != null && entityHitResult.getType() == Type.ENTITY) {
         Entity hitEntity = entityHitResult.getEntity();
         if (hitEntity.getType().is(TOTags.SPECTRAL_SHIFT_BLACKLIST)) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.literal(hitEntity.getName().getString() + " cannot be teleported!"), true);
            }

            return;
         }

         Vec3 targetPos = entity.position();
         hitEntity.teleportTo(targetPos.x, targetPos.y, targetPos.z);
         world.playSound(null, entity.blockPosition(), (SoundEvent)TOSounds.SPECTRAL_BLINK_SUCCESS.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.literal("Teleported " + hitEntity.getName().getString() + " to you!"), true);
         }
      } else {
         this.playFailedBlinkPostEffects(world, entity);
      }
   }

   private void teleportPlayerToEntity(LivingEntity entity, double range, Level world) {
      Vec3 startVec = entity.getEyePosition(1.0F);
      Vec3 viewVec = entity.getLookAngle();
      Vec3 endVec = startVec.add(viewVec.scale(range));
      AABB boundingBox = entity.getBoundingBox().expandTowards(viewVec.scale(range)).inflate(1.0);
      EntityHitResult entityHitResult = this.getForgivingEntityHitResult(world, entity, startVec, endVec, boundingBox);
      if (entityHitResult != null && entityHitResult.getType() == Type.ENTITY) {
         Entity hitEntity = entityHitResult.getEntity();
         if (hitEntity.getType().is(TOTags.SPECTRAL_BLINK_BLACKLIST)) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.literal(hitEntity.getName().getString() + " cannot be teleported to!"), true);
            }

            return;
         }

         Vec3 targetPos = hitEntity.position();
         entity.teleportTo(targetPos.x, targetPos.y, targetPos.z);
         world.playSound(null, entity.blockPosition(), (SoundEvent)TOSounds.SPECTRAL_BLINK_SUCCESS.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("effect.traveloptics.spectral_blink.success", new Object[]{hitEntity.getName()}), true);
         }
      } else {
         this.playFailedBlinkPostEffects(world, entity);
      }
   }

   private void playFailedBlinkPostEffects(Level world, Entity entity) {
      world.playSound(null, entity.blockPosition(), (SoundEvent)TOSounds.SPECTRAL_BLINK_FAILED.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
      if (entity instanceof Player player) {
         player.displayClientMessage(Component.translatable("effect.traveloptics.spectral_blink.missed").withStyle(ChatFormatting.RED), true);
      }
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   private EntityHitResult getForgivingEntityHitResult(Level world, LivingEntity entity, Vec3 startVec, Vec3 endVec, AABB boundingBox) {
      EntityHitResult entityHitResult = null;
      double closestDistance = Double.MAX_VALUE;
      double forgivenessRadius = 0.5;

      for (Entity targetEntity : world.getEntities(entity, boundingBox, e -> !e.isSpectator() && e.isPickable())) {
         AABB entityBoundingBox = targetEntity.getBoundingBox().inflate(targetEntity.getPickRadius() + forgivenessRadius);
         if (entityBoundingBox.clip(startVec, endVec).isPresent()) {
            double distance = startVec.distanceTo(targetEntity.getBoundingBox().getCenter());
            if (distance < closestDistance) {
               closestDistance = distance;
               entityHitResult = new EntityHitResult(targetEntity);
            }
         }
      }

      return entityHitResult;
   }
}
