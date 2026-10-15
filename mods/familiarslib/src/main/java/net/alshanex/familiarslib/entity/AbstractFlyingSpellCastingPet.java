package net.alshanex.familiarslib.entity;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractFlyingSpellCastingPet extends AbstractSpellCastingPet {
   private int knockbackCooldown = 0;
   private Vec3 smoothedVelocity = Vec3.ZERO;
   private float smoothedPitch = 0.0F;
   private float smoothedYaw = 0.0F;
   private int combatMovementTicks = 0;
   private Vec3 currentOrbitCenter = null;
   private double orbitAngle = 0.0;
   private boolean orbitClockwise = true;

   protected AbstractFlyingSpellCastingPet(EntityType<? extends AbstractSpellCastingPet> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.moveControl = new AbstractFlyingSpellCastingPet.DynamicFlyingMoveControl(this);
      this.lookControl = new AbstractFlyingSpellCastingPet.SmoothFlyingLookControl(this);
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(10, new AbstractFlyingSpellCastingPet.FlyingRandomStrollGoal(this, 1.0));
   }

   protected PathNavigation createNavigation(Level level) {
      FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
      navigation.setCanOpenDoors(false);
      navigation.setCanFloat(true);
      navigation.setCanPassDoors(true);
      return navigation;
   }

   public void travel(Vec3 travelVector) {
      if (this.isControlledByLocalInstance()) {
         if (this.isInWater()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
         } else if (this.isInLava()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
         } else {
            LivingEntity target = this.getTarget();
            boolean inCombat = target != null && target.isAlive();
            float friction = inCombat ? 0.94F : 0.91F;
            float speed = this.getSpeed() * (inCombat ? 1.15F : 1.0F);
            this.moveRelative(speed, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(friction));
         }
      }

      this.calculateEntityAnimation(false);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.knockbackCooldown > 0) {
         this.knockbackCooldown--;
      }

      LivingEntity target = this.getTarget();
      if (target != null && target.isAlive()) {
         this.combatMovementTicks++;
         if (this.currentOrbitCenter == null || this.combatMovementTicks % 20 == 0) {
            this.currentOrbitCenter = target.position();
         }

         if (this.getRandom().nextInt(200) == 0) {
            this.orbitClockwise = !this.orbitClockwise;
         }
      } else {
         this.combatMovementTicks = 0;
         this.currentOrbitCenter = null;
      }

      this.updateFlightVisuals();
   }

   private void updateFlightVisuals() {
      Vec3 movement = this.getDeltaMovement();
      float targetPitch = 0.0F;
      if (movement.lengthSqr() > 0.001) {
         targetPitch = (float)Math.toDegrees(Math.atan2(-movement.y, movement.horizontalDistance()));
         targetPitch = Mth.clamp(targetPitch, -45.0F, 45.0F);
      }

      this.smoothedPitch = Mth.lerp(0.1F, this.smoothedPitch, targetPitch);
   }

   public void knockback(double strength, double x, double z) {
      super.knockback(strength * 0.6, x, z);
      this.knockbackCooldown = 15;
      Vec3 current = this.getDeltaMovement();
      this.setDeltaMovement(current.x, current.y + strength * 0.3, current.z);
   }

   public boolean isInKnockback() {
      return this.knockbackCooldown > 0;
   }

   public float getSmoothedPitch() {
      return this.smoothedPitch;
   }

   public int getCombatMovementTicks() {
      return this.combatMovementTicks;
   }

   public Vec3 getCurrentOrbitCenter() {
      return this.currentOrbitCenter;
   }

   public double getOrbitAngle() {
      return this.orbitAngle;
   }

   public void setOrbitAngle(double angle) {
      this.orbitAngle = angle;
   }

   public boolean isOrbitClockwise() {
      return this.orbitClockwise;
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.FLYING_SPEED, 0.35F)
         .add(Attributes.MOVEMENT_SPEED, 0.3);
   }

   @Override
   public boolean shouldAlwaysAnimateLegs() {
      return false;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
   }

   public boolean isDrinkingPotion() {
      return false;
   }

   public void startDrinkingPotion() {
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder pBuilder) {
      super.defineSynchedData(pBuilder);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
   }

   public static class DynamicFlyingMoveControl extends FlyingMoveControl {
      private final AbstractFlyingSpellCastingPet entity;
      private Vec3 velocitySmoothing = Vec3.ZERO;
      private int idleHoverTicks = 0;
      private double hoverOffset = 0.0;

      public DynamicFlyingMoveControl(AbstractFlyingSpellCastingPet entity) {
         super(entity, 20, true);
         this.entity = entity;
      }

      public void tick() {
         if (this.entity.getIsSitting()) {
            this.entity.setDeltaMovement(Vec3.ZERO);
            this.velocitySmoothing = Vec3.ZERO;
         } else if (this.entity.isInKnockback()) {
            Vec3 current = this.entity.getDeltaMovement();
            this.entity.setDeltaMovement(current.scale(0.92));
            this.velocitySmoothing = current;
         } else {
            LivingEntity target = this.entity.getTarget();
            boolean inCombat = target != null && target.isAlive();
            boolean isCasting = this.entity.isCasting();
            if (this.operation == Operation.MOVE_TO) {
               this.idleHoverTicks = 0;
               Vec3 targetPos = new Vec3(this.wantedX, this.wantedY, this.wantedZ);
               Vec3 currentPos = this.entity.position();
               Vec3 toTarget = targetPos.subtract(currentPos);
               double distance = toTarget.length();
               if (distance < 0.3) {
                  this.operation = Operation.WAIT;
                  this.entity.setDeltaMovement(this.entity.getDeltaMovement().scale(0.5));
                  this.velocitySmoothing = this.entity.getDeltaMovement();
                  return;
               }

               Vec3 direction = toTarget.normalize();
               double baseSpeed = this.entity.getAttributeValue(Attributes.FLYING_SPEED);
               double desiredSpeed;
               if (isCasting) {
                  desiredSpeed = baseSpeed * this.speedModifier * 0.4;
               } else if (inCombat) {
                  desiredSpeed = baseSpeed * this.speedModifier * 1.3;
               } else {
                  desiredSpeed = baseSpeed * this.speedModifier;
               }

               desiredSpeed = Math.min(desiredSpeed, distance * 0.5);
               Vec3 desiredVelocity = direction.scale(desiredSpeed);
               if (!inCombat && !isCasting) {
                  double wobble = Math.sin(this.entity.tickCount * 0.1) * 0.02;
                  desiredVelocity = desiredVelocity.add(0.0, wobble, 0.0);
               }

               double smoothFactor = inCombat ? 0.25 : (isCasting ? 0.08 : 0.15);
               this.velocitySmoothing = this.velocitySmoothing.lerp(desiredVelocity, smoothFactor);
               this.entity.setDeltaMovement(this.velocitySmoothing);
               this.updateRotation(direction, inCombat, target);
            } else {
               this.idleHoverTicks++;
               this.hoverOffset = Math.sin(this.idleHoverTicks * 0.05) * 0.015;
               Vec3 hoverVelocity = new Vec3(0.0, this.hoverOffset, 0.0);
               if (this.idleHoverTicks % 60 == 0) {
                  hoverVelocity = hoverVelocity.add(
                     (this.entity.getRandom().nextDouble() - 0.5) * 0.02, 0.0, (this.entity.getRandom().nextDouble() - 0.5) * 0.02
                  );
               }

               if (inCombat && !isCasting) {
                  Vec3 toTargetx = target.position().add(0.0, 2.0, 0.0).subtract(this.entity.position());
                  if (toTargetx.lengthSqr() > 4.0) {
                     hoverVelocity = hoverVelocity.add(toTargetx.normalize().scale(0.02));
                  }

                  this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
               }

               this.velocitySmoothing = this.velocitySmoothing.scale(0.85).add(hoverVelocity.scale(0.15));
               this.entity.setDeltaMovement(this.velocitySmoothing);
            }
         }
      }

      private void updateRotation(Vec3 movementDirection, boolean inCombat, LivingEntity target) {
         float targetYaw;
         if (inCombat && target != null && this.entity.hasLineOfSight(target)) {
            Vec3 toTarget = target.position().subtract(this.entity.position());
            float combatYaw = (float)(Math.atan2(toTarget.z, toTarget.x) * (180.0 / Math.PI)) - 90.0F;
            float movementYaw = (float)(Math.atan2(movementDirection.z, movementDirection.x) * (180.0 / Math.PI)) - 90.0F;
            float blendFactor = this.entity.isCasting() ? 0.9F : 0.6F;
            float yawDiff = Mth.wrapDegrees(combatYaw - movementYaw);
            targetYaw = movementYaw + yawDiff * blendFactor;
         } else {
            targetYaw = (float)(Math.atan2(movementDirection.z, movementDirection.x) * (180.0 / Math.PI)) - 90.0F;
         }

         float rotSpeed = inCombat ? 12.0F : 6.0F;
         this.entity.setYRot(this.rotlerp(this.entity.getYRot(), targetYaw, rotSpeed));
      }
   }

   protected static class FlyingRandomStrollGoal extends Goal {
      private final AbstractFlyingSpellCastingPet mob;
      private final double speedModifier;
      private int nextStartTime;
      private Vec3 targetPos;

      public FlyingRandomStrollGoal(AbstractFlyingSpellCastingPet mob, double speedModifier) {
         this.mob = mob;
         this.speedModifier = speedModifier;
         this.nextStartTime = 0;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (this.mob.getIsSitting() || this.mob.movementDisabled || this.mob.hasControllingPassenger()) {
            return false;
         } else if (this.mob.getTarget() != null && this.mob.getTarget().isAlive()) {
            return false;
         } else if (--this.nextStartTime > 0) {
            return false;
         } else {
            this.nextStartTime = 80 + this.mob.getRandom().nextInt(100);
            Vec3 pos = this.getRandomPosition();
            if (pos == null) {
               return false;
            } else {
               this.targetPos = pos;
               return true;
            }
         }
      }

      public boolean canContinueToUse() {
         if (this.mob.getIsSitting() || this.mob.movementDisabled || this.mob.hasControllingPassenger()) {
            return false;
         } else if (this.mob.getTarget() != null && this.mob.getTarget().isAlive()) {
            return false;
         } else {
            return this.targetPos == null ? false : this.mob.position().distanceToSqr(this.targetPos) > 2.0;
         }
      }

      public void start() {
         if (this.targetPos != null && !this.mob.getIsSitting()) {
            this.mob.getMoveControl().setWantedPosition(this.targetPos.x, this.targetPos.y, this.targetPos.z, this.speedModifier);
         }
      }

      public void stop() {
         this.targetPos = null;
      }

      @Nullable
      private Vec3 getRandomPosition() {
         Vec3 viewVector = this.mob.getViewVector(0.0F);
         Vec3 hoverPos = HoverRandomPos.getPos(this.mob, 10, 8, viewVector.x, viewVector.z, (float) (Math.PI / 2), 4, 2);
         return hoverPos != null ? hoverPos : AirAndWaterRandomPos.getPos(this.mob, 10, 6, -2, viewVector.x, viewVector.z, (float) (Math.PI / 2));
      }
   }

   public static class SmoothFlyingLookControl extends LookControl {
      private final AbstractFlyingSpellCastingPet entity;
      private float currentYawVelocity = 0.0F;
      private float currentPitchVelocity = 0.0F;

      public SmoothFlyingLookControl(AbstractFlyingSpellCastingPet entity) {
         super(entity);
         this.entity = entity;
      }

      public void tick() {
         if (!this.entity.getIsSitting()) {
            LivingEntity target = this.entity.getTarget();
            boolean inCombat = target != null && target.isAlive();
            if (this.lookAtCooldown > 0) {
               this.lookAtCooldown--;
               this.getYRotD().ifPresent(targetYaw -> {
                  float yawDiff = Mth.wrapDegrees(targetYaw - this.entity.yHeadRot);
                  float springStrength = inCombat ? 0.15F : 0.08F;
                  float damping = 0.7F;
                  this.currentYawVelocity = this.currentYawVelocity * damping + yawDiff * springStrength;
                  this.currentYawVelocity = Mth.clamp(this.currentYawVelocity, -this.yMaxRotSpeed, this.yMaxRotSpeed);
                  this.entity.yHeadRot = this.entity.yHeadRot + this.currentYawVelocity;
               });
               this.getXRotD().ifPresent(targetPitch -> {
                  float pitchDiff = targetPitch - this.entity.getXRot();
                  float springStrength = inCombat ? 0.12F : 0.06F;
                  float damping = 0.7F;
                  this.currentPitchVelocity = this.currentPitchVelocity * damping + pitchDiff * springStrength;
                  this.currentPitchVelocity = Mth.clamp(this.currentPitchVelocity, -this.xMaxRotAngle, this.xMaxRotAngle);
                  this.entity.setXRot(this.entity.getXRot() + this.currentPitchVelocity);
               });
            } else {
               this.currentYawVelocity *= 0.8F;
               this.currentPitchVelocity *= 0.8F;
               if (Math.abs(this.entity.getXRot()) > 1.0F) {
                  this.entity.setXRot(Mth.lerp(0.05F, this.entity.getXRot(), 0.0F));
               }

               float yawDiff = Mth.wrapDegrees(this.entity.yBodyRot - this.entity.yHeadRot);
               this.entity.yHeadRot += yawDiff * 0.1F;
            }

            float wrappedDifference = Mth.wrapDegrees(this.entity.yBodyRot - this.entity.yHeadRot);
            float maxHeadTurn = inCombat ? 90.0F : 75.0F;
            if (wrappedDifference < -maxHeadTurn) {
               this.entity.yHeadRot = this.entity.yBodyRot + maxHeadTurn;
            } else if (wrappedDifference > maxHeadTurn) {
               this.entity.yHeadRot = this.entity.yBodyRot - maxHeadTurn;
            }
         }
      }
   }
}
