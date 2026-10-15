package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.entity.effect.Wave_Entity;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.aqua.TsunamiSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BigWaveEntity extends Wave_Entity {
   public BigWaveEntity(EntityType<? extends Wave_Entity> type, Level level) {
      super(type, level);
   }

   public BigWaveEntity(Level level, LivingEntity shooter, int life, float damage) {
      this((EntityType<? extends Wave_Entity>)ModEntityTypes.BIG_WAVE.get(), level);
      this.setOwner(shooter);
      this.setMaxTicks(life);
      this.setDamage(damage);
      this.setLifespan(0);
   }

   public float maxUpStep() {
      return 4.0F;
   }

   protected void attackEntities(double strength, double x, double z) {
      AABB bashBox = this.getBoundingBox().inflate(0.01F);
      LivingEntity owner = this.getOwner();
      DamageSource source = ((TsunamiSpell)ModSpells.TSUNAMI.get()).getDamageSource(this, (Entity)(owner != null ? owner : this));

      for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
         if (owner == null || !owner.equals(target) && !owner.isAlliedTo(target) && !target.isAlliedTo(owner)) {
            boolean damaged = DamageSources.applyDamage(target, this.getDamage(), source);
            if (damaged) {
               target.extinguishFire();
            }

            if (damaged) {
               target.addEffect(new MobEffectInstance(ModMobEffects.WET, 100, 0, false, false, true));
            }

            target.hasImpulse = true;

            Vec3 motion;
            for (motion = target.getDeltaMovement(); x * x + z * z < 1.0E-5F; z = (Math.random() - Math.random()) * 0.01) {
               x = (Math.random() - Math.random()) * 0.01;
            }

            double playerSize = 1.08;
            double entitySize = target.getBbWidth() * target.getBbHeight();
            double scale = playerSize / Math.max(0.1, entitySize);
            double knockbackScale = Math.min(scale, 1.5);
            double adjustedStrength = strength * knockbackScale;
            Vec3 knockback = new Vec3(x, 0.0, z).normalize().scale(adjustedStrength);
            target.setDeltaMovement(
               motion.x / 2.0 - knockback.x, target.onGround() ? Math.min(0.5, motion.y / 2.0 + strength) : motion.y, motion.z / 2.0 - knockback.z
            );
         }
      }
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         float f = Math.max(1.0F - this.getLifespan() / (1.0F * this.getMaxTicks()), 0.0F);
         Vec3 boostVec = new Vec3(0.0, 0.0, f * f * 0.2F).yRot((float)Math.toRadians(-this.getYRot()));
         this.setDeltaMovement(this.getDeltaMovement().add(boostVec));
         Entity ownerEntity = this.getOwner();
         LivingEntity owner = ownerEntity instanceof LivingEntity livingOwner ? livingOwner : null;

         for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.75))) {
            if (target != owner && (owner == null || !target.isAlliedTo(owner))) {
               target.addEffect(new MobEffectInstance(ModMobEffects.WET, 100, 0, false, false, true));
            }
         }
      }
   }
}
