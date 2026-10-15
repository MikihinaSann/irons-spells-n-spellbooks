package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.entity.projectile.Phantom_Arrow_Entity;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.symmetry.PhantomBarrageSpell;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PhantomBarrageProjectile extends Phantom_Arrow_Entity {
   private static final EntityDataAccessor<Integer> WAIT_TIMER = SynchedEntityData.defineId(PhantomBarrageProjectile.class, EntityDataSerializers.INT);
   private LivingEntity owner;

   public PhantomBarrageProjectile(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.setNoGravity(true);
      this.pickup = Pickup.DISALLOWED;
   }

   public PhantomBarrageProjectile(Level worldIn, LivingEntity shooter) {
      super((EntityType)ModEntityTypes.PHANTOM_BARRAGE_PROJECTILE.get(), worldIn);
      this.setOwner(shooter);
      this.owner = shooter;
      this.setNoGravity(true);
      this.pickup = Pickup.DISALLOWED;
      this.setPos(shooter.getX(), shooter.getEyeY() + 1.5, shooter.getZ());
   }

   public void setWaitTimer(int ticks) {
      this.entityData.set(WAIT_TIMER, ticks);
   }

   public int getWaitTimer() {
      return (Integer)this.entityData.get(WAIT_TIMER);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(WAIT_TIMER, 20);
   }

   public void tick() {
      if (!this.level().isClientSide && this.tickCount > 200) {
         this.discard();
      }

      int timer = this.getWaitTimer();
      if (timer > 0) {
         this.setWaitTimer(timer - 1);
         if (this.owner == null && this.getOwner() instanceof LivingEntity livingOwner) {
            this.owner = livingOwner;
         }

         if (this.owner != null) {
            Vec3 look = this.owner.getLookAngle();
            this.setDeltaMovement(look.scale(0.01));
         } else {
            this.setDeltaMovement(Vec3.ZERO);
         }
      } else if (!this.level().isClientSide && timer == 0) {
         this.setWaitTimer(-1);
         this.launch();
      }

      super.tick();
   }

   private void launch() {
      if (this.owner != null) {
         LivingEntity target = this.owner.getLastHurtMob();
         if (target == null || !target.isAlive()) {
            target = this.owner.getLastHurtByMob();
         }

         if (target != null && target.isAlive()) {
            Vec3 dir = target.getBoundingBox().getCenter().subtract(this.position()).normalize();
            this.setDeltaMovement(dir.scale(1.5));
            double d0 = dir.x;
            double d1 = dir.y;
            double d2 = dir.z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            this.setYRot((float)(Mth.atan2(d0, d2) * 180.0F / (float)Math.PI));
            this.setXRot((float)(Mth.atan2(d1, d3) * 180.0F / (float)Math.PI));
         } else {
            Vec3 forward = this.owner.getLookAngle();
            this.setDeltaMovement(forward.scale(1.5));
         }

         this.hasImpulse = true;
      }
   }

   protected void tickDespawn() {
      if (this.getWaitTimer() <= 0) {
         super.tickDespawn();
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
      this.discard();
   }

   protected boolean canHitEntity(Entity entity) {
      return this.getOwner() != null && entity.is(this.getOwner()) ? false : super.canHitEntity(entity);
   }

   protected void onHitEntity(EntityHitResult result) {
      Entity entity = result.getEntity();
      float damage = (float)this.getBaseDamage();
      Entity owner = this.getOwner();
      if (entity.hurt(((PhantomBarrageSpell)ModSpells.PHANTOM_BARRAGE.get()).getDamageSource(this, (Entity)(owner != null ? owner : this)), damage)
         && entity instanceof LivingEntity living) {
         this.doKnockback(living, ((PhantomBarrageSpell)ModSpells.PHANTOM_BARRAGE.get()).getDamageSource(this, (Entity)(owner != null ? owner : this)));
         this.doPostHurtEffects(living);
      }

      this.discard();
   }
}
