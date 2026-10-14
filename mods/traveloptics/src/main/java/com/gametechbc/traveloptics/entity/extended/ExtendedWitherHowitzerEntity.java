package com.gametechbc.traveloptics.entity.extended;

import com.github.L_Ender.cataclysm.entity.projectile.Wither_Howitzer_Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class ExtendedWitherHowitzerEntity extends Wither_Howitzer_Entity {
   private float aoeEntityDamage;

   public ExtendedWitherHowitzerEntity(EntityType<? extends Wither_Howitzer_Entity> type, Level world) {
      super(type, world);
      this.aoeEntityDamage = 1.0F;
   }

   public ExtendedWitherHowitzerEntity(
      EntityType<? extends Wither_Howitzer_Entity> type, Level world, LivingEntity thrower, float entityHitDamage, float aoeEntityDamage
   ) {
      super(type, world, thrower);
      this.aoeEntityDamage = aoeEntityDamage;
   }

   protected void onHit(HitResult hitResult) {
      super.onHit(hitResult);
      if (!this.level().isClientSide) {
         this.level().explode(this, this.getX(), this.getY(), this.getZ(), this.getRadius(), false, ExplosionInteraction.NONE);
         this.applyAoeDamage(this.getX(), this.getY(), this.getZ(), this.getAoeEntityDamage(), this.getRadius());
         this.discard();
      }
   }

   private void applyAoeDamage(double x, double y, double z, float damage, float radius) {
      for (LivingEntity entity : this.level()
         .getEntitiesOfClass(LivingEntity.class, new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius))) {
         if (this.distanceToSqr(entity) <= radius * radius) {
            entity.hurt(this.damageSources().magic(), damage);
         }
      }
   }

   public void setAoeEntityDamage(float aoeEntityDamage) {
      this.aoeEntityDamage = aoeEntityDamage;
   }

   public float getAoeEntityDamage() {
      return this.aoeEntityDamage;
   }
}
