package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Scylla.Scylla_Ceraunus_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Scylla.Scylla_Entity;
import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.lang.reflect.Field;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SpellAnchorEntity extends Scylla_Ceraunus_Entity {
   private static EntityDataAccessor<Integer> PARENT_CONTROLLER_ID_ACCESSOR;
   private Entity homingTarget;
   private boolean isReturning = false;
   private AbstractSpell damageSpell;

   public SpellAnchorEntity(EntityType<? extends Scylla_Ceraunus_Entity> type, Level level) {
      super(type, level);
      this.pickup = Pickup.DISALLOWED;
   }

   public SpellAnchorEntity(EntityType<? extends Scylla_Ceraunus_Entity> type, Level level, LivingEntity shooter) {
      this(type, level);
      this.setOwner(shooter);
      this.setControllerUUID(shooter.getUUID());
      this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
      if (PARENT_CONTROLLER_ID_ACCESSOR != null) {
         this.entityData.set(PARENT_CONTROLLER_ID_ACCESSOR, shooter.getId());
      }
   }

   public void setHomingTarget(Entity target) {
      this.homingTarget = target;
   }

   public void setDamageSpell(AbstractSpell damageSpell) {
      if (damageSpell != null) {
         this.damageSpell = damageSpell;
      }
   }

   protected void onHitEntity(EntityHitResult result) {
      Entity target = result.getEntity();
      Entity controller = this.getController();
      Entity attacker = (Entity)(controller != null ? controller : this);
      AbstractSpell spell = this.damageSpell != null ? this.damageSpell : (AbstractSpell)ModSpells.TIDAL_GRASP.get();
      SpellDamageSource damageSource = spell.getDamageSource(this, attacker);
      boolean damaged = DamageSources.applyDamage(target, (float)this.getBaseDamage(), damageSource);
      if (target.getType() != EntityType.ENDERMAN) {
         if (damaged && target instanceof LivingEntity livingTarget) {
            this.doKnockback(livingTarget, damageSource);
            this.doPostHurtEffects(livingTarget);
         }

         if (!this.getHookMode() && this.getPassengers().isEmpty() && !this.level().isClientSide) {
            target.startRiding(this);
         }
      }
   }

   public void tick() {
      Entity controller = this.getController();
      if (controller != null && this.distanceToSqr(controller) > 4096.0) {
         this.discard();
      } else {
         if (!this.isReturning && !this.inGround && this.homingTarget != null && this.homingTarget.isAlive()) {
            Vec3 targetPos = this.homingTarget.getBoundingBox().getCenter();
            Vec3 dir = targetPos.subtract(this.position()).normalize();
            double speed = this.getDeltaMovement().length();
            if (speed < 1.0) {
               speed = 1.5;
            }

            this.setDeltaMovement(dir.scale(speed));
            double horizontalDist = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
            this.setYRot((float)(Math.atan2(dir.x, dir.z) * 180.0F / (float)Math.PI));
            this.setXRot((float)(Math.atan2(dir.y, horizontalDist) * 180.0F / (float)Math.PI));
            if (this.getBoundingBox().intersects(this.homingTarget.getBoundingBox().inflate(0.5))) {
               if (this.getHookMode()) {
                  this.setGrab(true);
                  this.startRiding(this.homingTarget, true);
                  this.homingTarget = null;
               } else {
                  this.homingTarget.startRiding(this, true);
                  this.isReturning = true;
                  this.homingTarget = null;
               }
            }
         }

         if (!this.getHookMode() && this.isReturning && controller != null) {
            this.setNoPhysics(true);
            Vec3 vec3 = controller.getEyePosition().subtract(this.position());
            this.setDeltaMovement(vec3.normalize().scale(1.5));
            if (this.distanceToSqr(controller) < 4.0) {
               if (this.getFirstPassenger() instanceof LivingEntity passenger) {
                  passenger.stopRiding();
                  passenger.teleportTo(controller.getX(), controller.getY(), controller.getZ());
                  passenger.resetFallDistance();
                  passenger.setDeltaMovement(Vec3.ZERO);
                  passenger.hurtMarked = true;
               }

               this.discard();
               return;
            }
         }

         super.tick();
         if (!this.level().isClientSide && controller != null && PARENT_CONTROLLER_ID_ACCESSOR != null) {
            this.entityData.set(PARENT_CONTROLLER_ID_ACCESSOR, controller.getId());
         }

         if (controller instanceof LivingEntity && !(controller instanceof Scylla_Entity)) {
            if (this.getHookMode()) {
               if (this.getGrab()) {
                  if (!controller.isRemoved() && controller.isAlive()) {
                     if (controller.getBoundingBox().intersects(this.getBoundingBox().inflate(1.0))) {
                        this.discard();
                        return;
                     }

                     Vec3 diff = this.position().subtract(controller.position());
                     Vec3 motion = diff.normalize().scale(1.5);
                     controller.setDeltaMovement(motion);
                     controller.resetFallDistance();
                     controller.hurtMarked = true;
                  } else {
                     this.discard();
                  }
               }
            } else if (this.getGrab()) {
               if (!controller.isRemoved() && controller.isAlive()) {
                  this.setNoPhysics(true);
                  Vec3 vec3 = controller.getEyePosition().subtract(this.position());
                  this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015 * 3.0, this.getZ());
                  this.setYRot(this.getYrotOld());
                  if (this.distanceToSqr(controller) < 3.0) {
                     this.discard();
                     return;
                  }

                  double d0 = 0.3;
                  this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d0)));
               } else {
                  this.discard();
               }
            }
         }
      }
   }

   protected double getDefaultGravity() {
      return 0.0;
   }

   protected void positionRider(Entity passenger, MoveFunction callback) {
      if (this.hasPassenger(passenger)) {
         double yOffset = this.getBbHeight() / 2.0 - passenger.getBbHeight() / 2.0;
         callback.accept(passenger, this.getX(), this.getY() + yOffset, this.getZ());
      }
   }

   protected boolean tryPickup(Player player) {
      return false;
   }

   static {
      try {
         Field f = Scylla_Ceraunus_Entity.class.getDeclaredField("CONTROLLER_ID");
         f.setAccessible(true);
         PARENT_CONTROLLER_ID_ACCESSOR = (EntityDataAccessor<Integer>)f.get(null);
      } catch (Exception var1) {
         var1.printStackTrace();
      }
   }
}
