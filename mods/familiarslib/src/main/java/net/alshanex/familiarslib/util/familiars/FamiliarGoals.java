package net.alshanex.familiarslib.util.familiars;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.SummonedSkeleton;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.block.AbstractFamiliarBedBlock;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarBedBlockEntity;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarStorageBlockEntity;
import net.alshanex.familiarslib.entity.AbstractFlyingSpellCastingPet;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.registry.FParticleRegistry;
import net.alshanex.familiarslib.util.CylinderParticleManager;
import net.alshanex.familiarslib.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FamiliarGoals {
   public static class AlliedFamiliarDefenseGoal extends TargetGoal {
      private final AbstractSpellCastingPet familiar;
      private LivingEntity targetMob;
      private int timestamp;
      private static final double HELP_RANGE = 16.0;

      public AlliedFamiliarDefenseGoal(AbstractSpellCastingPet familiar) {
         super(familiar, false);
         this.familiar = familiar;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      public boolean canUse() {
         if (this.familiar.getTarget() == null && !this.familiar.getIsInHouse()) {
            if (this.familiar.getIsSitting()) {
               return false;
            } else if (this.familiar.getMovementDisabled()) {
               return false;
            } else if (!this.familiar.canExecuteGoalsInHouse()) {
               return false;
            } else {
               AABB searchArea = this.familiar.getBoundingBox().inflate(16.0);
               List<AbstractSpellCastingPet> nearbyFamiliars = this.familiar
                  .level()
                  .getEntitiesOfClass(
                     AbstractSpellCastingPet.class,
                     searchArea,
                     alliedFamiliarx -> alliedFamiliarx != this.familiar
                        && this.familiar.isAlliedTo(alliedFamiliarx)
                        && alliedFamiliarx.getTarget() != null
                        && alliedFamiliarx.getTarget().isAlive()
                        && !alliedFamiliarx.getIsInHouse()
                        && !alliedFamiliarx.getIsSitting()
                        && this.canHelpAgainst(alliedFamiliarx.getTarget())
                  );
               AbstractSpellCastingPet closestAlliedFamiliar = null;
               double closestDistance = Double.MAX_VALUE;

               for (AbstractSpellCastingPet alliedFamiliar : nearbyFamiliars) {
                  double distance = this.familiar.distanceToSqr(alliedFamiliar);
                  if (distance < closestDistance) {
                     closestDistance = distance;
                     closestAlliedFamiliar = alliedFamiliar;
                  }
               }

               if (closestAlliedFamiliar != null && closestAlliedFamiliar.getTarget() != null) {
                  this.targetMob = closestAlliedFamiliar.getTarget();
                  return true;
               } else {
                  return false;
               }
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         if (this.familiar.getIsSitting() || this.familiar.getIsInHouse()) {
            return false;
         } else if (this.familiar.getMovementDisabled()) {
            return false;
         } else if (!this.familiar.canExecuteGoalsInHouse()) {
            return false;
         } else if (this.targetMob == null || !this.targetMob.isAlive()) {
            return false;
         } else if (!this.canHelpAgainst(this.targetMob)) {
            return false;
         } else if (this.familiar.distanceToSqr(this.targetMob) > 512.0) {
            return false;
         } else {
            AABB searchArea = this.familiar.getBoundingBox().inflate(24.0);
            List<AbstractSpellCastingPet> alliedFamiliars = this.familiar
               .level()
               .getEntitiesOfClass(
                  AbstractSpellCastingPet.class,
                  searchArea,
                  alliedFamiliar -> alliedFamiliar != this.familiar && this.familiar.isAlliedTo(alliedFamiliar) && alliedFamiliar.getTarget() == this.targetMob
               );
            return !alliedFamiliars.isEmpty();
         }
      }

      public void start() {
         this.familiar.setTarget(this.targetMob);
         this.timestamp = this.familiar.tickCount;
         super.start();
      }

      public void stop() {
         this.familiar.setTarget(null);
         this.targetMob = null;
         super.stop();
      }

      private boolean canHelpAgainst(LivingEntity target) {
         if (this.familiar.isAlliedTo(target)) {
            return false;
         } else if (!this.familiar.hasLineOfSight(target)) {
            return false;
         } else {
            TargetingConditions conditions = TargetingConditions.forCombat()
               .range(16.0)
               .selector(entity -> entity instanceof LivingEntity && !this.familiar.isAlliedTo(entity) && entity.isAlive());
            return conditions.test(this.familiar, target);
         }
      }
   }

   public static class ApplyRandomEffectGoal extends Goal {
      private final AbstractSpellCastingPet entity;
      private final Supplier<LivingEntity> targetEntity;
      private final List<MobEffectInstance> effectHolders;
      private final int interval;
      private int tickCounter;

      public ApplyRandomEffectGoal(AbstractSpellCastingPet entity, Supplier<LivingEntity> targetEntity, List<MobEffectInstance> effectHolders, int interval) {
         this.entity = entity;
         this.targetEntity = targetEntity;
         this.effectHolders = effectHolders;
         this.interval = interval;
         this.tickCounter = 0;
         this.setFlags(EnumSet.of(Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity owner = this.targetEntity.get();
         if (owner == null) {
            return false;
         } else if (!this.entity.canExecuteGoalsInHouse()) {
            return false;
         } else {
            return this.horizontalDistanceSqr(owner, this.entity.position()) > 400.0F ? false : ++this.tickCounter >= this.interval;
         }
      }

      private float horizontalDistanceSqr(LivingEntity livingEntity, Vec3 vec3) {
         double dx = livingEntity.getX() - vec3.x;
         double dz = livingEntity.getZ() - vec3.z;
         return (float)(dx * dx + dz * dz);
      }

      public void start() {
         this.tickCounter = 0;
         this.entity.triggerAnim("interact_controller", "interact");
         LivingEntity owner = this.targetEntity.get();
         int randomIndex = ThreadLocalRandom.current().nextInt(this.effectHolders.size());
         this.entity.triggerAnim("interact_controller", "interact");
         owner.addEffect(this.effectHolders.get(randomIndex));
      }

      public boolean canContinueToUse() {
         return !this.entity.canExecuteGoalsInHouse() ? false : this.canUse();
      }
   }

   public static class CasualLookAtPlayerGoal extends Goal {
      private final AbstractSpellCastingPet familiar;
      private final Class<? extends LivingEntity> lookAtType;
      private final float lookDistance;
      private LivingEntity lookAt;
      private int lookTime;

      public CasualLookAtPlayerGoal(AbstractSpellCastingPet familiar, Class<? extends LivingEntity> lookAtType, float lookDistance) {
         this.familiar = familiar;
         this.lookAtType = lookAtType;
         this.lookDistance = lookDistance;
         this.setFlags(EnumSet.of(Flag.LOOK));
      }

      public boolean canUse() {
         if (!this.familiar.getIsInHouse()) {
            return false;
         } else if (this.familiar.getRandom().nextInt(80) != 0) {
            return false;
         } else {
            this.lookAt = this.familiar
               .level()
               .getNearestEntity(
                  this.lookAtType,
                  TargetingConditions.forNonCombat().range(this.lookDistance),
                  this.familiar,
                  this.familiar.getX(),
                  this.familiar.getEyeY(),
                  this.familiar.getZ(),
                  this.familiar.getBoundingBox().inflate(this.lookDistance, 3.0, this.lookDistance)
               );
            return this.lookAt != null;
         }
      }

      public boolean canContinueToUse() {
         if (!this.familiar.getIsInHouse()) {
            return false;
         } else if (!this.lookAt.isAlive()) {
            return false;
         } else {
            return this.familiar.distanceToSqr(this.lookAt) > this.lookDistance * this.lookDistance ? false : this.lookTime > 0;
         }
      }

      public void start() {
         this.lookTime = this.adjustedTickDelay(40 + this.familiar.getRandom().nextInt(40));
      }

      public void stop() {
         this.lookAt = null;
      }

      public void tick() {
         if (this.lookAt != null) {
            this.familiar.getLookControl().setLookAt(this.lookAt.getX(), this.lookAt.getEyeY(), this.lookAt.getZ());
         }

         this.lookTime--;
      }
   }

   public static class CasualRandomLookGoal extends Goal {
      private final AbstractSpellCastingPet familiar;
      private double relX;
      private double relZ;
      private int lookTime;

      public CasualRandomLookGoal(AbstractSpellCastingPet familiar) {
         this.familiar = familiar;
         this.setFlags(EnumSet.of(Flag.LOOK));
      }

      public boolean canUse() {
         return !this.familiar.getIsInHouse() ? false : this.familiar.getRandom().nextFloat() < 0.02F;
      }

      public boolean canContinueToUse() {
         return this.familiar.getIsInHouse() && this.lookTime >= 0;
      }

      public void start() {
         double angle = Math.random() * 2.0 * Math.PI;
         this.relX = Math.cos(angle);
         this.relZ = Math.sin(angle);
         this.lookTime = 20 + this.familiar.getRandom().nextInt(20);
      }

      public void tick() {
         this.lookTime--;
         this.familiar.getLookControl().setLookAt(this.familiar.getX() + this.relX, this.familiar.getEyeY(), this.familiar.getZ() + this.relZ);
      }
   }

   public static class DisableSculkShriekerGoal extends Goal {
      private final AbstractSpellCastingPet controllerEntity;
      private final Supplier<LivingEntity> targetEntity;
      private final int radius;

      public DisableSculkShriekerGoal(AbstractSpellCastingPet controllerEntity, Supplier<LivingEntity> targetEntity, int radius) {
         this.controllerEntity = controllerEntity;
         this.targetEntity = targetEntity;
         this.radius = radius;
         this.setFlags(EnumSet.noneOf(Flag.class));
      }

      public boolean canUse() {
         LivingEntity owner = this.targetEntity.get();
         if (owner == null) {
            return false;
         } else {
            return !this.controllerEntity.canExecuteGoalsInHouse() ? false : this.controllerEntity.isAlive() && owner.isAlive();
         }
      }

      public void tick() {
         LivingEntity owner = this.targetEntity.get();
         BlockPos controllerPos = owner.blockPosition();

         for (int x = -this.radius; x <= this.radius; x++) {
            for (int y = -this.radius; y <= this.radius; y++) {
               for (int z = -this.radius; z <= this.radius; z++) {
                  BlockPos currentPos = controllerPos.offset(x, y, z);
                  BlockState blockState = this.controllerEntity.level().getBlockState(currentPos);
                  if (blockState.is(Blocks.SCULK_SHRIEKER) && (Boolean)blockState.getValue(SculkShriekerBlock.SHRIEKING)) {
                     this.controllerEntity.triggerAnim("interact_controller", "interact");
                     CylinderParticleManager.spawnParticlesAtBlockPos(
                        owner.level(),
                        currentPos.getCenter(),
                        50,
                        (ParticleOptions)ParticleRegistry.FIREFLY_PARTICLE.get(),
                        CylinderParticleManager.ParticleDirection.UPWARD,
                        0.5,
                        0.0,
                        0.0
                     );
                     this.controllerEntity.level().setBlock(currentPos, (BlockState)blockState.setValue(SculkShriekerBlock.SHRIEKING, false), 3);
                  }
               }
            }
         }
      }

      public boolean canContinueToUse() {
         return this.canUse() && this.controllerEntity.canExecuteGoalsInHouse();
      }
   }

   public static class FamiliarHurtByTargetGoal extends HurtByTargetGoal {
      private final AbstractSpellCastingPet familiar;
      private static final double ALERT_RANGE = 20.0;

      public FamiliarHurtByTargetGoal(AbstractSpellCastingPet familiar) {
         super(familiar, new Class[0]);
         this.familiar = familiar;
      }

      public void start() {
         super.start();
         if (this.familiar.getLastHurtByMob() != null) {
            this.alertAlliedFamiliars(this.familiar.getLastHurtByMob());
         }
      }

      private void alertAlliedFamiliars(LivingEntity attacker) {
         if (attacker != null && attacker.isAlive()) {
            if (!this.familiar.getIsInHouse()) {
               AABB searchArea = this.familiar.getBoundingBox().inflate(20.0);

               for (AbstractSpellCastingPet alliedFamiliar : this.familiar
                  .level()
                  .getEntitiesOfClass(
                     AbstractSpellCastingPet.class,
                     searchArea,
                     alliedFamiliarx -> alliedFamiliarx != this.familiar
                        && this.familiar.isAlliedTo(alliedFamiliarx)
                        && !alliedFamiliarx.getIsInHouse()
                        && !alliedFamiliarx.getIsSitting()
                        && alliedFamiliarx.canExecuteGoalsInHouse()
                        && (alliedFamiliarx.getTarget() == null || alliedFamiliarx.getTarget() == attacker)
                  )) {
                  if (!alliedFamiliar.isAlliedTo(attacker) && alliedFamiliar.hasLineOfSight(attacker) && alliedFamiliar.getTarget() == null) {
                     alliedFamiliar.setTarget(attacker);
                     if (alliedFamiliar.getLastHurtByMob() == null) {
                        alliedFamiliar.setLastHurtByMob(attacker);
                     }
                  }
               }
            }
         }
      }
   }

   public static class FindAndUsePetBedGoal extends Goal {
      private final AbstractSpellCastingPet pet;
      private final double searchRadius;
      private BlockPos targetBedPos;
      private Vec3 exactSleepPosition;
      private int cooldownTicks;
      private static final int SEARCH_COOLDOWN = 60;
      private boolean hasSnappedToBed = false;
      private boolean hasClaimedBed = false;
      private int bedRegenTimer = 0;
      private boolean wasPlayingSleepAnimation = false;

      public FindAndUsePetBedGoal(AbstractSpellCastingPet pet, double searchRadius) {
         this.pet = pet;
         this.searchRadius = searchRadius;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (!this.pet.level().isClientSide && this.pet.tickCount % 100 == 0) {
            FamiliarsLib.LOGGER
               .debug(
                  "FindAndUsePetBedGoal check - Pet: "
                     + this.pet.getUUID()
                     + ", Sitting: "
                     + this.pet.getIsSitting()
                     + ", Health: "
                     + this.pet.getHealth()
                     + "/"
                     + this.pet.getMaxHealth()
                     + ", Cooldown: "
                     + this.cooldownTicks
               );
         }

         if (this.pet.getIsSitting()) {
            return false;
         } else if (this.cooldownTicks > 0) {
            this.cooldownTicks--;
            return false;
         } else if (this.pet.getHealth() >= this.pet.getMaxHealth()) {
            this.cooldownTicks = 60;
            return false;
         } else {
            BlockPos foundBed = this.findAvailableBed();
            if (foundBed != null) {
               this.targetBedPos = foundBed;
               if (this.pet.level().getBlockEntity(this.targetBedPos) instanceof AbstractFamiliarBedBlockEntity petBed) {
                  if (!petBed.isBedTaken()) {
                     petBed.setBedTaken(true);
                     this.hasClaimedBed = true;
                     FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " immediately claimed bed at " + this.targetBedPos);
                     this.exactSleepPosition = petBed.getSleepPosition();
                     this.hasSnappedToBed = false;
                     FamiliarsLib.LOGGER
                        .debug(
                           "Pet " + this.pet.getUUID() + " found available bed at " + this.targetBedPos + ", exact sleep position: " + this.exactSleepPosition
                        );
                     return true;
                  }

                  FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " failed to claim bed at " + this.targetBedPos + " - already taken");
               }
            }

            this.cooldownTicks = 60;
            return false;
         }
      }

      public boolean canContinueToUse() {
         if (this.pet.getHealth() >= this.pet.getMaxHealth()) {
            return false;
         } else {
            return this.pet.getIsSitting() && this.hasSnappedToBed && this.targetBedPos != null
               ? this.isValidBed(this.targetBedPos)
               : !this.pet.getIsSitting() && this.targetBedPos != null && this.exactSleepPosition != null && this.isValidBed(this.targetBedPos);
         }
      }

      public void start() {
         if (this.exactSleepPosition != null) {
            FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " starting to move to exact sleep position: " + this.exactSleepPosition);
            this.pet.getNavigation().moveTo(this.exactSleepPosition.x, this.exactSleepPosition.y, this.exactSleepPosition.z, 1.0);
         }

         this.hasSnappedToBed = false;
         this.bedRegenTimer = 0;
         this.wasPlayingSleepAnimation = false;
      }

      public void tick() {
         if (this.targetBedPos != null && this.exactSleepPosition != null) {
            Vec3 petPos = this.pet.position();
            double distanceToSleepPos = petPos.distanceTo(this.exactSleepPosition);
            if (distanceToSleepPos <= 1.5 && !this.hasSnappedToBed && this.hasClaimedBed) {
               if (!this.pet.level().isClientSide) {
                  FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " within snap range, teleporting to exact position");
                  this.pet.getNavigation().stop();
                  if (this.pet instanceof AbstractFlyingSpellCastingPet) {
                     this.pet.setDeltaMovement(Vec3.ZERO);
                     this.pet.getMoveControl().setWantedPosition(this.exactSleepPosition.x, this.exactSleepPosition.y, this.exactSleepPosition.z, 0.0);
                  } else {
                     this.pet.setDeltaMovement(Vec3.ZERO);
                  }

                  this.pet.setPos(this.exactSleepPosition.x, this.exactSleepPosition.y, this.exactSleepPosition.z);
                  this.hasSnappedToBed = true;

                  try {
                     this.pet.setSitting(true);
                  } catch (Exception var6) {
                     FamiliarsLib.LOGGER.error("Error setting pet to sitting: ", var6);
                  }
               }
            } else if (this.hasSnappedToBed && this.pet.getIsSitting()) {
               if (this.pet instanceof AbstractFlyingSpellCastingPet) {
                  double currentDistance = this.pet.position().distanceTo(this.exactSleepPosition);
                  if (currentDistance > 0.5) {
                     this.pet.setPos(this.exactSleepPosition.x, this.exactSleepPosition.y, this.exactSleepPosition.z);
                     this.pet.setDeltaMovement(Vec3.ZERO);
                  }
               }

               this.handleSleepAndRegeneration();
            } else {
               if (!this.hasSnappedToBed && (this.pet.getNavigation().isDone() || distanceToSleepPos > 3.0)) {
                  this.pet.getNavigation().moveTo(this.exactSleepPosition.x, this.exactSleepPosition.y, this.exactSleepPosition.z, 1.2);
               }
            }
         }
      }

      public void stop() {
         try {
            if (this.pet.getIsSitting()) {
               this.pet.setSitting(false);
               FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " stopped sitting when bed goal ended");
            }

            if (this.hasSnappedToBed && this.targetBedPos != null && this.exactSleepPosition != null) {
               Vec3 exitPosition = new Vec3(this.exactSleepPosition.x, this.exactSleepPosition.y + 1.0, this.exactSleepPosition.z);
               BlockPos exitBlockPos = BlockPos.containing(exitPosition);
               boolean isSafeToExit = this.pet.level().getBlockState(exitBlockPos).isAir() || this.pet.level().getBlockState(exitBlockPos).canBeReplaced();
               if (isSafeToExit) {
                  this.pet.setPos(exitPosition.x, exitPosition.y, exitPosition.z);
                  FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " moved to exit position: " + exitPosition);
               } else {
                  Vec3 safeExitPosition = this.findSafeExitPosition(this.exactSleepPosition);
                  if (safeExitPosition != null) {
                     this.pet.setPos(safeExitPosition.x, safeExitPosition.y, safeExitPosition.z);
                     FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " moved to safe exit position: " + safeExitPosition);
                  }
               }
            }

            if (this.hasClaimedBed && this.targetBedPos != null) {
               this.releaseBed();
            }

            this.resetBedState();
            this.pet.getNavigation().stop();
            this.cooldownTicks = 60;
            this.bedRegenTimer = 0;
            this.wasPlayingSleepAnimation = false;
         } catch (Exception var5) {
            FamiliarsLib.LOGGER.error("Error in FindAndUsePetBedGoal.stop(): ", var5);
            this.forceCleanup();
         }
      }

      private Vec3 findSafeExitPosition(Vec3 bedPosition) {
         double[][] offsets = new double[][]{
            {0.0, 1.0, 0.0},
            {1.0, 1.0, 0.0},
            {-1.0, 1.0, 0.0},
            {0.0, 1.0, 1.0},
            {0.0, 1.0, -1.0},
            {1.0, 0.0, 0.0},
            {-1.0, 0.0, 0.0},
            {0.0, 0.0, 1.0},
            {0.0, 0.0, -1.0},
            {0.0, 2.0, 0.0}
         };

         for (double[] offset : offsets) {
            Vec3 testPosition = bedPosition.add(offset[0], offset[1], offset[2]);
            BlockPos testBlockPos = BlockPos.containing(testPosition);
            boolean isAir = this.pet.level().getBlockState(testBlockPos).isAir() || this.pet.level().getBlockState(testBlockPos).canBeReplaced();
            boolean hasGroundNearby = this.hasGroundNearby(testBlockPos);
            if (isAir && hasGroundNearby) {
               return testPosition;
            }
         }

         return null;
      }

      private boolean hasGroundNearby(BlockPos pos) {
         for (int y = 0; y <= 3; y++) {
            BlockPos groundCheck = pos.offset(0, -y, 0);
            if (this.pet.level().getBlockState(groundCheck).isSolid()) {
               return true;
            }
         }

         return false;
      }

      private void resetBedState() {
         this.targetBedPos = null;
         this.exactSleepPosition = null;
         this.hasSnappedToBed = false;
         this.hasClaimedBed = false;
      }

      private void forceCleanup() {
         try {
            if (this.targetBedPos != null
               && this.pet.level().getBlockEntity(this.targetBedPos) instanceof AbstractFamiliarBedBlockEntity petBed
               && petBed.isBedTaken()) {
               petBed.setBedTaken(false);
               FamiliarsLib.LOGGER.debug("Force-released bed at {} for pet {}", this.targetBedPos, this.pet.getUUID());
            }
         } catch (Exception var6) {
            FamiliarsLib.LOGGER.error("Error in force cleanup: ", var6);
         } finally {
            this.resetBedState();
         }
      }

      public BlockPos getTargetBedPos() {
         return this.targetBedPos;
      }

      public boolean hasClaimedBed() {
         return this.hasClaimedBed;
      }

      public boolean isActuallyRunning() {
         return this.targetBedPos != null && this.hasClaimedBed;
      }

      private BlockPos findAvailableBed() {
         BlockPos petPos = this.pet.blockPosition();
         FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " searching for available bed around " + petPos);
         int searchRange = (int)this.searchRadius;

         for (int x = -searchRange; x <= searchRange; x++) {
            for (int y = -2; y <= 2; y++) {
               for (int z = -searchRange; z <= searchRange; z++) {
                  BlockPos checkPos = petPos.offset(x, y, z);
                  double distance = petPos.distSqr(checkPos);
                  if (!(distance > this.searchRadius * this.searchRadius)
                     && this.pet.level().getBlockState(checkPos).getBlock() instanceof AbstractFamiliarBedBlock
                     && this.pet.level().getBlockEntity(checkPos) instanceof AbstractFamiliarBedBlockEntity petBed
                     && !petBed.isBedTaken()) {
                     FamiliarsLib.LOGGER.debug("Found available bed at " + checkPos);
                     return checkPos;
                  }
               }
            }
         }

         FamiliarsLib.LOGGER.debug("No available bed found for pet " + this.pet.getUUID());
         return null;
      }

      private boolean isValidBed(BlockPos pos) {
         try {
            if (!(this.pet.level().getBlockState(pos).getBlock() instanceof AbstractFamiliarBedBlock)) {
               return false;
            } else {
               BlockEntity be = this.pet.level().getBlockEntity(pos);
               return be instanceof AbstractFamiliarBedBlockEntity;
            }
         } catch (Exception var3) {
            FamiliarsLib.LOGGER.error("Error checking if bed is valid: ", var3);
            return false;
         }
      }

      private void releaseBed() {
         if (this.targetBedPos != null) {
            if (this.pet.level().getBlockEntity(this.targetBedPos) instanceof AbstractFamiliarBedBlockEntity petBed) {
               petBed.setBedTaken(false);
               this.hasClaimedBed = false;
               FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " released bed at " + this.targetBedPos);
            }
         }
      }

      private void handleSleepAndRegeneration() {
         boolean isOnValidBed = this.isOnCompatibleBed();
         boolean shouldPlaySleepAnimation = this.pet.getIsSitting() && isOnValidBed;
         if (shouldPlaySleepAnimation) {
            if (!this.wasPlayingSleepAnimation) {
               if (!this.pet.level().isClientSide) {
                  FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " starting sleep animation on compatible bed");
               }

               this.wasPlayingSleepAnimation = true;
            }

            if (!this.pet.level().isClientSide) {
               this.bedRegenTimer++;
               if (this.bedRegenTimer >= 20) {
                  if (this.pet.getHealth() < this.pet.getMaxHealth()) {
                     this.pet.heal(1.0F);
                     FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " healed to " + this.pet.getHealth() + "/" + this.pet.getMaxHealth());

                     try {
                        CylinderParticleManager.spawnParticlesAtBlockPos(
                           this.pet.level(),
                           this.pet.position(),
                           1,
                           (ParticleOptions)FParticleRegistry.SLEEP_PARTICLE.get(),
                           CylinderParticleManager.ParticleDirection.UPWARD,
                           0.1,
                           0.0,
                           0.8
                        );
                     } catch (Exception var4) {
                        FamiliarsLib.LOGGER.error("Error spawning sleep particles: ", var4);
                     }
                  } else {
                     FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " fully healed, goal will stop");
                  }

                  this.bedRegenTimer = 0;
               }
            }
         } else {
            if (this.wasPlayingSleepAnimation) {
               if (!this.pet.level().isClientSide) {
                  FamiliarsLib.LOGGER.debug("Pet " + this.pet.getUUID() + " stopping sleep animation - not on valid bed or not sitting");
               }

               this.wasPlayingSleepAnimation = false;
            }

            if (this.bedRegenTimer > 0) {
               this.bedRegenTimer = 0;
            }
         }
      }

      private boolean isOnCompatibleBed() {
         if (this.targetBedPos == null) {
            return false;
         } else {
            BlockPos petPos = this.pet.blockPosition();

            for (int x = -1; x <= 1; x++) {
               for (int y = -1; y <= 0; y++) {
                  for (int z = -1; z <= 1; z++) {
                     BlockPos checkPos = petPos.offset(x, y, z);
                     if (checkPos.equals(this.targetBedPos)
                        && this.pet.level().getBlockState(checkPos).getBlock() instanceof AbstractFamiliarBedBlock
                        && this.pet.level().getBlockEntity(checkPos) instanceof AbstractFamiliarBedBlockEntity petBed) {
                        return petBed.isPositionCorrectForSleeping(this.pet.position());
                     }
                  }
               }
            }

            return false;
         }
      }
   }

   public static class FlyingWizardAttackGoal extends WizardAttackGoal {
      protected float preferredHeight = 4.0F;
      protected float optimalCombatRange = 12.0F;
      protected float minCombatRange = 6.0F;
      protected float maxCombatRange = 20.0F;
      protected FamiliarGoals.FlyingWizardAttackGoal.MovementPattern currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.ORBIT;
      protected int patternTicks = 0;
      protected int patternDuration = 100;
      protected double orbitAngle = 0.0;
      protected boolean orbitClockwise = true;

      public FlyingWizardAttackGoal(IMagicEntity abstractSpellCastingMob, double speedModifier, int attackInterval) {
         this(abstractSpellCastingMob, speedModifier, attackInterval, attackInterval);
      }

      public FlyingWizardAttackGoal(IMagicEntity abstractSpellCastingMob, double speedModifier, int attackIntervalMin, int attackIntervalMax) {
         super(abstractSpellCastingMob, speedModifier, attackIntervalMin, attackIntervalMax);
         this.isFlying = true;
         this.allowFleeing = false;
      }

      public FamiliarGoals.FlyingWizardAttackGoal setPreferredHeight(float height) {
         this.preferredHeight = height;
         return this;
      }

      public FamiliarGoals.FlyingWizardAttackGoal setCombatRanges(float min, float optimal, float max) {
         this.minCombatRange = min;
         this.optimalCombatRange = optimal;
         this.maxCombatRange = max;
         this.spellcastingRange = max;
         this.spellcastingRangeSqr = max * max;
         return this;
      }

      public void start() {
         super.start();
         this.orbitAngle = this.mob.getRandom().nextDouble() * Math.PI * 2.0;
         this.orbitClockwise = this.mob.getRandom().nextBoolean();
         this.selectNewPattern();
      }

      public void stop() {
         super.stop();
         this.currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.HOVER;
         this.patternTicks = 0;
      }

      protected void doMovement(double distanceSquared) {
         if (this.target != null) {
            this.mob.lookAt(this.target, 30.0F, 30.0F);
            this.patternTicks++;
            if (this.patternTicks >= this.patternDuration) {
               this.selectNewPattern();
            }

            double distance = Math.sqrt(distanceSquared);
            this.executeMovement(distance);
         }
      }

      protected void selectNewPattern() {
         this.patternTicks = 0;
         if (this.target == null) {
            this.currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.HOVER;
            this.patternDuration = 60;
         } else {
            double distance = this.mob.distanceTo(this.target);
            float healthPercent = this.mob.getHealth() / this.mob.getMaxHealth();
            boolean isCasting = this.spellCastingMob.isCasting();
            boolean isDrinking = this.spellCastingMob.isDrinkingPotion();
            if (!isCasting && !isDrinking) {
               float roll = this.mob.getRandom().nextFloat();
               if (!this.hasLineOfSight) {
                  this.currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.APPROACH;
                  this.patternDuration = 80;
               } else if (distance > this.maxCombatRange) {
                  this.currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.APPROACH;
                  this.patternDuration = 60 + this.mob.getRandom().nextInt(40);
               } else if (distance < this.minCombatRange) {
                  this.currentPattern = roll < 0.6F
                     ? FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.RETREAT
                     : FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.ASCEND;
                  this.patternDuration = 40 + this.mob.getRandom().nextInt(30);
               } else if (healthPercent < 0.3F && roll < 0.4F) {
                  this.currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.RETREAT;
                  this.patternDuration = 60;
               } else if (roll < 0.5F) {
                  this.currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.ORBIT;
                  this.patternDuration = 80 + this.mob.getRandom().nextInt(60);
                  if (this.mob.getRandom().nextFloat() < 0.3F) {
                     this.orbitClockwise = !this.orbitClockwise;
                  }
               } else if (roll < 0.7F) {
                  this.currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.STRAFE;
                  this.patternDuration = 40 + this.mob.getRandom().nextInt(30);
               } else if (roll < 0.85F) {
                  double heightDiff = this.mob.getY() - this.target.getY();
                  if (heightDiff < this.preferredHeight) {
                     this.currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.ASCEND;
                  } else {
                     this.currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.DIVE;
                  }

                  this.patternDuration = 30 + this.mob.getRandom().nextInt(20);
               } else {
                  this.currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.HOVER;
                  this.patternDuration = 30 + this.mob.getRandom().nextInt(30);
               }
            } else {
               if (distance < this.minCombatRange) {
                  this.currentPattern = FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.RETREAT;
                  this.patternDuration = 40;
               } else {
                  this.currentPattern = this.mob.getRandom().nextFloat() < 0.7F
                     ? FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.HOVER
                     : FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.ORBIT;
                  this.patternDuration = 60 + this.mob.getRandom().nextInt(40);
               }
            }
         }
      }

      protected void executeMovement(double distance) {
         double speed = this.getFlightSpeed();
         Vec3 targetPos = this.target.position();
         Vec3 mobPos = this.mob.position();
         switch (this.currentPattern) {
            case ORBIT:
               this.executeOrbit(targetPos, mobPos, speed);
               break;
            case STRAFE:
               this.executeStrafe(targetPos, mobPos, speed);
               break;
            case APPROACH:
               this.executeApproach(targetPos, mobPos, speed);
               break;
            case RETREAT:
               this.executeRetreat(targetPos, mobPos, speed);
               break;
            case DIVE:
               this.executeDive(targetPos, mobPos, speed);
               break;
            case ASCEND:
               this.executeAscend(targetPos, mobPos, speed);
               break;
            case HOVER:
               this.executeHover(targetPos, mobPos);
         }
      }

      protected double getFlightSpeed() {
         double baseSpeed = this.speedModifier;
         if (this.spellCastingMob.isCasting()) {
            return baseSpeed * 0.5;
         } else {
            return this.spellCastingMob.isDrinkingPotion() ? baseSpeed * 0.3 : baseSpeed;
         }
      }

      protected void executeOrbit(Vec3 targetPos, Vec3 mobPos, double speed) {
         double orbitRadius = this.optimalCombatRange;
         double orbitSpeed = 0.03 * (this.orbitClockwise ? 1 : -1);
         this.orbitAngle += orbitSpeed;
         double targetX = targetPos.x + Math.cos(this.orbitAngle) * orbitRadius;
         double targetZ = targetPos.z + Math.sin(this.orbitAngle) * orbitRadius;
         double targetY = targetPos.y + this.preferredHeight + Math.sin(this.orbitAngle * 2.0) * 1.5;
         if (this.mob.getRandom().nextInt(20) == 0) {
            targetX += (this.mob.getRandom().nextDouble() - 0.5) * 2.0;
            targetZ += (this.mob.getRandom().nextDouble() - 0.5) * 2.0;
         }

         this.mob.getMoveControl().setWantedPosition(targetX, targetY, targetZ, speed);
      }

      protected void executeStrafe(Vec3 targetPos, Vec3 mobPos, double speed) {
         Vec3 toTarget = targetPos.subtract(mobPos).normalize();
         Vec3 strafeDir = new Vec3(-toTarget.z, 0.0, toTarget.x);
         if (!this.orbitClockwise) {
            strafeDir = strafeDir.scale(-1.0);
         }

         double currentDist = mobPos.distanceTo(targetPos);
         Vec3 adjustedDir = strafeDir;
         if (currentDist < this.minCombatRange) {
            adjustedDir = strafeDir.subtract(toTarget.scale(0.5));
         } else if (currentDist > this.maxCombatRange) {
            adjustedDir = strafeDir.add(toTarget.scale(0.5));
         }

         adjustedDir = adjustedDir.normalize();
         double targetX = mobPos.x + adjustedDir.x * 6.0;
         double targetZ = mobPos.z + adjustedDir.z * 6.0;
         double targetY = targetPos.y + this.preferredHeight;
         this.mob.getMoveControl().setWantedPosition(targetX, targetY, targetZ, speed * 1.2);
      }

      protected void executeApproach(Vec3 targetPos, Vec3 mobPos, double speed) {
         Vec3 toTarget = targetPos.subtract(mobPos);
         double dist = toTarget.horizontalDistance();
         Vec3 direction = toTarget.normalize();
         double desiredDist = this.optimalCombatRange * 0.8;
         double targetX;
         double targetZ;
         if (dist > desiredDist) {
            targetX = targetPos.x - direction.x * desiredDist;
            targetZ = targetPos.z - direction.z * desiredDist;
         } else {
            targetX = mobPos.x;
            targetZ = mobPos.z;
         }

         double targetY = targetPos.y + this.preferredHeight;
         this.mob.getMoveControl().setWantedPosition(targetX, targetY, targetZ, speed * 1.3);
      }

      protected void executeRetreat(Vec3 targetPos, Vec3 mobPos, double speed) {
         Vec3 awayFromTarget = mobPos.subtract(targetPos).normalize();
         double retreatDist = 8.0;
         double targetX = mobPos.x + awayFromTarget.x * retreatDist;
         double targetZ = mobPos.z + awayFromTarget.z * retreatDist;
         double targetY = mobPos.y + 2.0;
         double sideOffset = (this.mob.getRandom().nextDouble() - 0.5) * 4.0;
         targetX += -awayFromTarget.z * sideOffset;
         targetZ += awayFromTarget.x * sideOffset;
         this.mob.getMoveControl().setWantedPosition(targetX, targetY, targetZ, speed * 1.4);
      }

      protected void executeDive(Vec3 targetPos, Vec3 mobPos, double speed) {
         Vec3 toTarget = targetPos.subtract(mobPos).normalize();
         double targetX = targetPos.x - toTarget.x * this.optimalCombatRange * 0.7;
         double targetZ = targetPos.z - toTarget.z * this.optimalCombatRange * 0.7;
         double targetY = targetPos.y + 2.0;
         this.mob.getMoveControl().setWantedPosition(targetX, targetY, targetZ, speed * 1.5);
      }

      protected void executeAscend(Vec3 targetPos, Vec3 mobPos, double speed) {
         double horizontalDist = mobPos.subtract(targetPos).horizontalDistance();
         double targetX = mobPos.x;
         double targetZ = mobPos.z;
         if (horizontalDist < this.optimalCombatRange) {
            Vec3 away = mobPos.subtract(targetPos).normalize();
            targetX = mobPos.x + away.x * 4.0;
            targetZ = mobPos.z + away.z * 4.0;
         }

         double targetY = mobPos.y + 6.0;
         this.mob.getMoveControl().setWantedPosition(targetX, targetY, targetZ, speed);
      }

      protected void executeHover(Vec3 targetPos, Vec3 mobPos) {
         double currentDist = mobPos.distanceTo(targetPos);
         double targetX = mobPos.x;
         double targetZ = mobPos.z;
         double targetY = targetPos.y + this.preferredHeight;
         if (currentDist < this.minCombatRange - 1.0F) {
            Vec3 away = mobPos.subtract(targetPos).normalize();
            targetX = mobPos.x + away.x * 2.0;
            targetZ = mobPos.z + away.z * 2.0;
         } else if (currentDist > this.maxCombatRange + 2.0F) {
            Vec3 toward = targetPos.subtract(mobPos).normalize();
            targetX = mobPos.x + toward.x * 2.0;
            targetZ = mobPos.z + toward.z * 2.0;
         }

         targetY += Math.sin(this.mob.tickCount * 0.08) * 0.3;
         this.mob.getMoveControl().setWantedPosition(targetX, targetY, targetZ, 0.5);
      }

      public float getStrafeMultiplier() {
         return 0.0F;
      }

      protected void doSpellAction() {
         super.doSpellAction();
         if (this.spellCastingMob.isCasting() || this.spellCastingMob.isDrinkingPotion()) {
            this.currentPattern = this.mob.getRandom().nextFloat() < 0.7F
               ? FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.HOVER
               : FamiliarGoals.FlyingWizardAttackGoal.MovementPattern.ORBIT;
            this.patternTicks = 0;
            this.patternDuration = 80;
         }
      }

      public static enum MovementPattern {
         ORBIT,
         STRAFE,
         APPROACH,
         RETREAT,
         DIVE,
         ASCEND,
         HOVER;
      }
   }

   public static class MeleeSpellCastWarlockAttackGoal<T extends PathfinderMob & IAnimatedAttacker & IMagicEntity> extends GenericAnimatedWarlockAttackGoal<T> {
      @Nullable
      private AbstractSpell gapCloserSpell;
      private int gapCloserSpellLevel;
      private double gapCloserDistance = 3.0;
      private int spellCooldown = 0;
      private static final int SPELL_COOLDOWN_TICKS = 100;
      private boolean hasAttemptedGapClose = false;
      @Nullable
      private LivingEntity lastTarget = null;

      public MeleeSpellCastWarlockAttackGoal(T mob, float speedModifier, int attackIntervalMin, int attackIntervalMax) {
         super(mob, speedModifier, attackIntervalMin, attackIntervalMax);
      }

      public FamiliarGoals.MeleeSpellCastWarlockAttackGoal<T> setGapCloserSpell(@Nullable AbstractSpell spell, int level) {
         this.gapCloserSpell = spell;
         this.gapCloserSpellLevel = level;
         return this;
      }

      public FamiliarGoals.MeleeSpellCastWarlockAttackGoal<T> setGapCloserDistance(double distance) {
         this.gapCloserDistance = distance;
         return this;
      }

      public void start() {
         super.start();
         this.hasAttemptedGapClose = false;
      }

      public void stop() {
         super.stop();
         this.hasAttemptedGapClose = false;
      }

      public void tick() {
         super.tick();
         LivingEntity currentTarget = this.mob.getTarget();
         if (currentTarget != this.lastTarget) {
            this.hasAttemptedGapClose = false;
            this.lastTarget = currentTarget;
         }

         if (this.spellCooldown > 0) {
            this.spellCooldown--;
         }

         if (this.shouldCastGapCloserSpell()) {
            this.castGapCloserSpell();
         }
      }

      private boolean shouldCastGapCloserSpell() {
         if (this.gapCloserSpell == null) {
            return false;
         } else if (this.spellCooldown > 0) {
            return false;
         } else if (this.hasAttemptedGapClose) {
            return false;
         } else {
            LivingEntity target = this.mob.getTarget();
            if (target == null) {
               return false;
            } else {
               MagicData magicData = MagicData.getPlayerMagicData(this.mob);
               if (magicData.isCasting()) {
                  return false;
               } else if (this.isMeleeing()) {
                  return false;
               } else {
                  double distanceToTarget = this.mob.distanceTo(target);
                  return !(distanceToTarget < this.gapCloserDistance);
               }
            }
         }
      }

      private void castGapCloserSpell() {
         if (this.gapCloserSpell != null && this.mob.getTarget() != null) {
            MagicData magicData = MagicData.getPlayerMagicData(this.mob);
            int spellLevel = this.gapCloserSpellLevel;
            ((IMagicEntity)this.mob).initiateCastSpell(this.gapCloserSpell, spellLevel);
            this.hasAttemptedGapClose = true;
            this.spellCooldown = 100;
         }
      }

      @Nullable
      public AbstractSpell getGapCloserSpell() {
         return this.gapCloserSpell;
      }

      public double getGapCloserDistance() {
         return this.gapCloserDistance;
      }

      public int getSpellCooldown() {
         return this.spellCooldown;
      }

      public boolean hasAttemptedGapClose() {
         return this.hasAttemptedGapClose;
      }
   }

   public static class MovementAwareFollowOwnerGoal extends Goal {
      private final AbstractSpellCastingPet pet;
      @Nullable
      private LivingEntity owner;
      private Supplier<LivingEntity> ownerGetter;
      private final double speedModifier;
      private final PathNavigation navigation;
      private int timeToRecalcPath;
      private final float stopDistance;
      private final float startDistance;
      private float oldWaterCost;
      private float teleportDistance;
      private boolean canFly;

      public MovementAwareFollowOwnerGoal(
         AbstractSpellCastingPet pet,
         Supplier<LivingEntity> ownerGetter,
         double speedModifier,
         float startDistance,
         float stopDistance,
         boolean canFly,
         float teleportDistance
      ) {
         this.pet = pet;
         this.ownerGetter = ownerGetter;
         this.speedModifier = speedModifier;
         this.navigation = pet.getNavigation();
         this.startDistance = startDistance;
         this.stopDistance = stopDistance;
         this.teleportDistance = teleportDistance;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
         this.canFly = canFly;
      }

      public boolean canUse() {
         if (this.pet.getMovementDisabled()) {
            return false;
         } else {
            LivingEntity livingentity = this.ownerGetter.get();
            if (livingentity == null) {
               return false;
            } else if (this.pet.distanceToSqr(livingentity) < this.startDistance * this.startDistance) {
               return false;
            } else if (this.pet.getIsInHouse()) {
               return false;
            } else {
               this.owner = livingentity;
               return true;
            }
         }
      }

      public boolean canContinueToUse() {
         if (this.pet.getMovementDisabled()) {
            return false;
         } else {
            return this.navigation.isDone() ? false : !(this.pet.distanceToSqr(this.owner) <= this.stopDistance * this.stopDistance);
         }
      }

      public void start() {
         this.timeToRecalcPath = 0;
         this.oldWaterCost = this.pet.getPathfindingMalus(PathType.WATER);
         this.pet.setPathfindingMalus(PathType.WATER, 0.0F);
      }

      public void stop() {
         this.owner = null;
         this.navigation.stop();
         this.pet.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
      }

      public void tick() {
         boolean flag = this.shouldTryTeleportToOwner();
         if (!flag) {
            this.pet.getLookControl().setLookAt(this.owner, 10.0F, this.pet.getMaxHeadXRot());
         }

         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (flag) {
               this.tryToTeleportToOwner();
            } else {
               this.navigation.moveTo(this.owner, this.speedModifier);
            }
         }
      }

      public void tryToTeleportToOwner() {
         LivingEntity livingentity = this.ownerGetter.get();
         if (livingentity != null) {
            this.teleportToAroundBlockPos(livingentity.blockPosition());
         }
      }

      public boolean shouldTryTeleportToOwner() {
         LivingEntity livingentity = this.ownerGetter.get();
         return livingentity != null && this.pet.distanceToSqr(livingentity) >= this.teleportDistance * this.teleportDistance;
      }

      private void teleportToAroundBlockPos(BlockPos pPos) {
         for (int i = 0; i < 10; i++) {
            int j = this.pet.getRandom().nextIntBetweenInclusive(-3, 3);
            int k = this.pet.getRandom().nextIntBetweenInclusive(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
               int l = this.pet.getRandom().nextIntBetweenInclusive(-1, 1);
               if (this.maybeTeleportTo(pPos.getX() + j, pPos.getY() + l, pPos.getZ() + k)) {
                  return;
               }
            }
         }
      }

      private boolean maybeTeleportTo(int pX, int pY, int pZ) {
         if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) {
            return false;
         } else {
            this.pet.moveTo(pX + 0.5, pY, pZ + 0.5, this.pet.getYRot(), this.pet.getXRot());
            this.navigation.stop();
            return true;
         }
      }

      private boolean canTeleportTo(BlockPos pPos) {
         PathType pathtype = WalkNodeEvaluator.getPathTypeStatic(this.pet, pPos);
         if (pathtype != PathType.WALKABLE) {
            return false;
         } else {
            BlockState blockstate = this.pet.level().getBlockState(pPos.below());
            if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
               return false;
            } else {
               BlockPos blockpos = pPos.subtract(this.pet.blockPosition());
               return this.pet.level().noCollision(this.pet, this.pet.getBoundingBox().move(blockpos));
            }
         }
      }
   }

   public static class MovementAwareLookAtPlayerGoal extends Goal {
      public static final float DEFAULT_PROBABILITY = 0.02F;
      private final AbstractSpellCastingPet pet;
      @Nullable
      protected Entity lookAt;
      protected final float lookDistance;
      private int lookTime;
      protected final float probability;
      private final boolean onlyHorizontal;
      protected final Class<? extends LivingEntity> lookAtType;
      protected final TargetingConditions lookAtContext;

      public MovementAwareLookAtPlayerGoal(AbstractSpellCastingPet pet, Class<? extends LivingEntity> lookAtType, float lookDistance) {
         this(pet, lookAtType, lookDistance, 0.02F);
      }

      public MovementAwareLookAtPlayerGoal(AbstractSpellCastingPet pet, Class<? extends LivingEntity> lookAtType, float lookDistance, float probability) {
         this(pet, lookAtType, lookDistance, probability, false);
      }

      public MovementAwareLookAtPlayerGoal(
         AbstractSpellCastingPet pet, Class<? extends LivingEntity> lookAtType, float lookDistance, float probability, boolean onlyHorizontal
      ) {
         this.pet = pet;
         this.lookAtType = lookAtType;
         this.lookDistance = lookDistance;
         this.probability = probability;
         this.onlyHorizontal = onlyHorizontal;
         this.setFlags(EnumSet.of(Flag.LOOK));
         if (lookAtType == Player.class) {
            this.lookAtContext = TargetingConditions.forNonCombat().range(lookDistance).selector(p_25531_ -> EntitySelector.notRiding(pet).test(p_25531_));
         } else {
            this.lookAtContext = TargetingConditions.forNonCombat().range(lookDistance);
         }
      }

      public boolean canUse() {
         if (this.pet.getMovementDisabled()) {
            return false;
         } else if (this.pet.getRandom().nextFloat() >= this.probability) {
            return false;
         } else {
            if (this.pet.getTarget() != null) {
               this.lookAt = this.pet.getTarget();
            }

            if (this.lookAtType == Player.class) {
               this.lookAt = this.pet.level().getNearestPlayer(this.lookAtContext, this.pet, this.pet.getX(), this.pet.getEyeY(), this.pet.getZ());
            } else {
               this.lookAt = this.pet
                  .level()
                  .getNearestEntity(
                     this.pet
                        .level()
                        .getEntitiesOfClass(this.lookAtType, this.pet.getBoundingBox().inflate(this.lookDistance, 3.0, this.lookDistance), p_148124_ -> true),
                     this.lookAtContext,
                     this.pet,
                     this.pet.getX(),
                     this.pet.getEyeY(),
                     this.pet.getZ()
                  );
            }

            return this.lookAt != null;
         }
      }

      public boolean canContinueToUse() {
         if (this.pet.getMovementDisabled()) {
            return false;
         } else if (!this.lookAt.isAlive()) {
            return false;
         } else {
            return this.pet.distanceToSqr(this.lookAt) > this.lookDistance * this.lookDistance ? false : this.lookTime > 0;
         }
      }

      public void start() {
         this.lookTime = this.adjustedTickDelay(40 + this.pet.getRandom().nextInt(40));
      }

      public void stop() {
         this.lookAt = null;
      }

      public void tick() {
         if (this.lookAt.isAlive()) {
            double d0 = this.onlyHorizontal ? this.pet.getEyeY() : this.lookAt.getEyeY();
            this.pet.getLookControl().setLookAt(this.lookAt.getX(), d0, this.lookAt.getZ());
            this.lookTime--;
         }
      }
   }

   public static class MovementAwareRandomLookAroundGoal extends Goal {
      private final AbstractSpellCastingPet pet;
      private double relX;
      private double relZ;
      private int lookTime;

      public MovementAwareRandomLookAroundGoal(AbstractSpellCastingPet pet) {
         this.pet = pet;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return this.pet.getMovementDisabled() ? false : this.pet.getRandom().nextFloat() < 0.02F;
      }

      public boolean canContinueToUse() {
         return this.pet.getMovementDisabled() ? false : this.lookTime >= 0;
      }

      public void start() {
         double d0 = (Math.PI * 2) * this.pet.getRandom().nextDouble();
         this.relX = Math.cos(d0);
         this.relZ = Math.sin(d0);
         this.lookTime = 20 + this.pet.getRandom().nextInt(20);
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         this.lookTime--;
         this.pet.getLookControl().setLookAt(this.pet.getX() + this.relX, this.pet.getEyeY(), this.pet.getZ() + this.relZ);
      }
   }

   public static class StayNearHouseGoal extends Goal {
      private final AbstractSpellCastingPet familiar;
      private final BlockPos housePos;

      public StayNearHouseGoal(AbstractSpellCastingPet familiar, BlockPos housePos) {
         this.familiar = familiar;
         this.housePos = housePos;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      private double getMaxDistance() {
         if (this.familiar.level() == null) {
            return 20.0;
         } else {
            return this.familiar.level().getBlockEntity(this.housePos) instanceof AbstractFamiliarStorageBlockEntity storageEntity
               ? storageEntity.getMaxDistance()
               : 20.0;
         }
      }

      public boolean canUse() {
         if (!this.familiar.getIsInHouse()) {
            return false;
         } else {
            BlockEntity blockEntity = this.familiar.level().getBlockEntity(this.housePos);
            if (!(blockEntity instanceof AbstractFamiliarStorageBlockEntity)) {
               return false;
            } else {
               double maxDistance = this.getMaxDistance();
               double distance = this.familiar.position().distanceTo(Vec3.atCenterOf(this.housePos));
               return distance > maxDistance;
            }
         }
      }

      public boolean canContinueToUse() {
         if (!this.familiar.getIsInHouse()) {
            return false;
         } else {
            double maxDistance = this.getMaxDistance();
            double distance = this.familiar.position().distanceTo(Vec3.atCenterOf(this.housePos));
            return distance > maxDistance * 0.8 && this.familiar.getNavigation().isInProgress();
         }
      }

      public void start() {
         Vec3 houseCenter = Vec3.atCenterOf(this.housePos);
         Vec3 currentPos = this.familiar.position();
         Vec3 direction = houseCenter.subtract(currentPos).normalize();
         double offsetX = (this.familiar.getRandom().nextDouble() - 0.5) * 6.0;
         double offsetZ = (this.familiar.getRandom().nextDouble() - 0.5) * 6.0;
         Vec3 targetPos = currentPos.add(direction.scale(8.0)).add(offsetX, 0.0, offsetZ);
         PathNavigation navigation = this.familiar.getNavigation();
         navigation.moveTo(targetPos.x, targetPos.y, targetPos.z, 1.2);
      }
   }

   public static class StealItemsWhenNotWatchedGoal extends Goal {
      private final AbstractSpellCastingPet mob;
      private final double searchRadius;
      private ItemEntity targetItem;
      private int stealingTime;
      private final int STEALING_DURATION = 20;

      public StealItemsWhenNotWatchedGoal(AbstractSpellCastingPet mob, double searchRadius) {
         this.mob = mob;
         this.searchRadius = searchRadius;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (this.mob.getIsSitting()) {
            return false;
         } else if (this.mob.getMovementDisabled()) {
            return false;
         } else {
            List<ItemEntity> nearbyItems = this.mob
               .level()
               .getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(this.searchRadius), this::isDesirableItem);
            if (nearbyItems.isEmpty()) {
               return false;
            } else if (this.isAnyPlayerWatching()) {
               return false;
            } else {
               this.targetItem = nearbyItems.get(0);
               double closestDistance = this.mob.distanceToSqr(this.targetItem);

               for (int i = 1; i < nearbyItems.size(); i++) {
                  ItemEntity item = nearbyItems.get(i);
                  double distance = this.mob.distanceToSqr(item);
                  if (distance < closestDistance) {
                     this.targetItem = item;
                     closestDistance = distance;
                  }
               }

               return true;
            }
         }
      }

      public void start() {
         this.stealingTime = 0;
      }

      public boolean canContinueToUse() {
         return this.targetItem != null && this.targetItem.isAlive() && !this.isAnyPlayerWatching() && this.stealingTime < 20;
      }

      public void tick() {
         this.mob.getLookControl().setLookAt(this.targetItem.getX(), this.targetItem.getY(), this.targetItem.getZ());
         this.stealingTime++;
         if (this.stealingTime >= 20) {
            this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.5F, 1.0F);
            this.targetItem.discard();
            this.targetItem = null;
         }
      }

      public void stop() {
         this.targetItem = null;
         this.stealingTime = 0;
      }

      private boolean isDesirableItem(ItemEntity itemEntity) {
         return itemEntity != null && itemEntity.isAlive() && itemEntity.getItem().is(ModTags.ILLUSIONIST_STEALS);
      }

      private boolean isAnyPlayerWatching() {
         for (Player player : this.mob
            .level()
            .getEntitiesOfClass(
               Player.class,
               new AABB(
                  this.mob.getX() - 16.0,
                  this.mob.getY() - 16.0,
                  this.mob.getZ() - 16.0,
                  this.mob.getX() + 16.0,
                  this.mob.getY() + 16.0,
                  this.mob.getZ() + 16.0
               )
            )) {
            if (player.isAlive() && !player.isSpectator() && this.isInPlayerFieldOfView(player)) {
               return true;
            }
         }

         return false;
      }

      private boolean isInPlayerFieldOfView(Player player) {
         Vec3 playerViewVector = player.getViewVector(1.0F).normalize();
         Vec3 playerToMobVector = new Vec3(this.mob.getX() - player.getX(), this.mob.getEyeY() - player.getEyeY(), this.mob.getZ() - player.getZ());
         double distanceSquared = playerToMobVector.lengthSqr();
         playerToMobVector = playerToMobVector.normalize();
         if (distanceSquared < 4.0) {
            return true;
         } else {
            double dotProduct = playerViewVector.dot(playerToMobVector);
            return dotProduct > -0.1 && player.hasLineOfSight(this.mob);
         }
      }
   }

   public static class TargetAttackerOfPlayersGoal extends TargetGoal {
      private final AbstractSpellCastingPet pet;
      private final Supplier<LivingEntity> owner;
      private LivingEntity target;

      public TargetAttackerOfPlayersGoal(AbstractSpellCastingPet pet, Supplier<LivingEntity> owner) {
         super(pet, true);
         this.pet = pet;
         this.owner = owner;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      public boolean canUse() {
         LivingEntity getOwner = this.owner.get();
         if (getOwner == null) {
            return false;
         } else if (this.pet.getMovementDisabled()) {
            return false;
         } else if (!this.pet.canExecuteGoalsInHouse()) {
            return false;
         } else {
            Level level = this.pet.level();
            List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(
               LivingEntity.class, this.pet.getBoundingBox().inflate(15.0), entity -> entity instanceof Mob mob && mob.getTarget() == getOwner
            );
            if (!nearbyEntities.isEmpty()) {
               this.target = nearbyEntities.getFirst();
               return true;
            } else {
               return false;
            }
         }
      }

      public void start() {
         this.pet.setTarget(this.target);
         this.pet.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, this.target, 200L);
         super.start();
      }
   }

   public static class TeleportToOwnerGoal extends Goal {
      private final AbstractSpellCastingPet mob;
      @javax.annotation.Nullable
      private LivingEntity owner;
      private Supplier<LivingEntity> ownerGetter;
      private float teleportDistance;
      private int teleportDelay;
      private Vec3 lastOwnerPosition = Vec3.ZERO;
      private int ticksSinceLastCheck = 0;
      private static final int CHECK_INTERVAL = 10;
      private static final double INSTANT_TELEPORT_THRESHOLD = 20.0;
      private static final int SAFE_POSITION_SEARCH_RADIUS = 16;

      public TeleportToOwnerGoal(AbstractSpellCastingPet pTamable, Supplier<LivingEntity> ownerGetter, float teleportDistance) {
         this.mob = pTamable;
         this.ownerGetter = ownerGetter;
         this.teleportDistance = teleportDistance;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public void start() {
         this.teleportDelay = 0;
      }

      public boolean canUse() {
         LivingEntity livingentity = this.ownerGetter.get();
         if (livingentity == null) {
            return false;
         } else if (this.hasOwnerTeleportedInstantly(livingentity)) {
            this.owner = livingentity;
            return true;
         } else if (this.mob.distanceToSqr(livingentity) < this.teleportDistance * this.teleportDistance) {
            return false;
         } else if (this.mob.getIsSitting()) {
            return false;
         } else if (this.mob.getIsInHouse()) {
            return false;
         } else {
            this.owner = livingentity;
            return true;
         }
      }

      public boolean canContinueToUse() {
         LivingEntity livingentity = this.ownerGetter.get();
         if (livingentity == null) {
            return false;
         } else if (this.teleportDelay > 0) {
            return false;
         } else {
            return !this.hasOwnerTeleportedInstantly(livingentity) && !this.shouldTryTeleportToOwner() ? this.canUse() : true;
         }
      }

      public void tick() {
         LivingEntity livingentity = this.ownerGetter.get();
         if (livingentity != null) {
            this.updateOwnerPositionTracking(livingentity);
            boolean needsTeleport = this.shouldTryTeleportToOwner() || this.hasOwnerTeleportedInstantly(livingentity);
            if (!needsTeleport) {
               this.mob.getLookControl().setLookAt(this.owner, 10.0F, this.mob.getMaxHeadXRot());
            } else if (!this.tryToTeleportToOwnerSafely()) {
               this.teleportDelay = 10;
               this.mob.getLookControl().setLookAt(this.owner, 10.0F, this.mob.getMaxHeadXRot());
            }
         }
      }

      private boolean hasOwnerTeleportedInstantly(LivingEntity owner) {
         if (this.mob.getIsInHouse()) {
            return false;
         } else {
            this.ticksSinceLastCheck++;
            if (this.ticksSinceLastCheck >= 10) {
               Vec3 currentOwnerPos = owner.position();
               if (this.lastOwnerPosition.equals(Vec3.ZERO)) {
                  this.lastOwnerPosition = currentOwnerPos;
                  this.ticksSinceLastCheck = 0;
                  return false;
               } else {
                  double distanceMoved = this.lastOwnerPosition.distanceTo(currentOwnerPos);
                  boolean hasTeleported = distanceMoved > 20.0;
                  this.lastOwnerPosition = currentOwnerPos;
                  this.ticksSinceLastCheck = 0;
                  return hasTeleported;
               }
            } else {
               return false;
            }
         }
      }

      private void updateOwnerPositionTracking(LivingEntity owner) {
         if (this.ticksSinceLastCheck == 0) {
            this.lastOwnerPosition = owner.position();
         }
      }

      public boolean tryToTeleportToOwnerSafely() {
         LivingEntity livingentity = this.ownerGetter.get();
         if (livingentity != null) {
            BlockPos safePos = this.findSafeTeleportPosition(livingentity.blockPosition(), this.mob.level());
            if (safePos != null && !safePos.equals(livingentity.blockPosition())) {
               this.mob.moveTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5, this.mob.getYRot(), this.mob.getXRot());
               return true;
            }
         }

         return false;
      }

      private BlockPos findSafeTeleportPosition(BlockPos ownerPos, Level level) {
         BlockPos nearbyPos = this.findSafeTeleportPositionAtLevel(ownerPos, level, 4);
         if (nearbyPos != null) {
            return nearbyPos;
         } else {
            for (int yOffset = -1; yOffset >= -32; yOffset--) {
               BlockPos lowerPos = ownerPos.offset(0, yOffset, 0);
               if (level.getBlockState(lowerPos.below()).isSolid()) {
                  BlockPos safePos = this.findSafeTeleportPositionAtLevel(lowerPos, level, 4);
                  if (safePos != null) {
                     return safePos;
                  }
               }
            }

            return this.findSafeTeleportPositionExtended(ownerPos, level);
         }
      }

      private BlockPos findSafeTeleportPositionAtLevel(BlockPos centerPos, Level level, int radius) {
         for (int i = 0; i < 10; i++) {
            int j = this.mob.getRandom().nextIntBetweenInclusive(-radius, radius);
            int k = this.mob.getRandom().nextIntBetweenInclusive(-radius, radius);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
               BlockPos testPos = new BlockPos(centerPos.getX() + j, centerPos.getY(), centerPos.getZ() + k);
               if (this.canTeleportTo(testPos)) {
                  return testPos;
               }
            }
         }

         for (int r = 1; r <= radius; r++) {
            for (int angle = 0; angle < 8; angle++) {
               double radians = (Math.PI * 2) * angle / 8.0;
               int x = centerPos.getX() + (int)(Math.cos(radians) * r);
               int z = centerPos.getZ() + (int)(Math.sin(radians) * r);
               BlockPos testPos = new BlockPos(x, centerPos.getY(), z);
               if (this.canTeleportTo(testPos)) {
                  return testPos;
               }
            }
         }

         return null;
      }

      private BlockPos findSafeTeleportPositionExtended(BlockPos centerPos, Level level) {
         for (int radius = 1; radius <= 16; radius++) {
            for (int angle = 0; angle < 16; angle++) {
               double radians = (Math.PI * 2) * angle / 16.0;
               int x = centerPos.getX() + (int)(Math.cos(radians) * radius);
               int z = centerPos.getZ() + (int)(Math.sin(radians) * radius);

               for (int yOffset = 10; yOffset >= -10; yOffset--) {
                  BlockPos testPos = new BlockPos(x, centerPos.getY() + yOffset, z);
                  if (this.canTeleportTo(testPos)) {
                     return testPos;
                  }
               }
            }
         }

         return null;
      }

      public boolean shouldTryTeleportToOwner() {
         LivingEntity livingentity = this.ownerGetter.get();
         if (livingentity == null) {
            return false;
         } else {
            return this.mob.getIsInHouse() ? false : this.mob.distanceToSqr(livingentity) >= this.teleportDistance * this.teleportDistance;
         }
      }

      private boolean canTeleportTo(BlockPos pPos) {
         Level level = this.mob.level();
         if (!level.isLoaded(pPos)) {
            return false;
         } else if (level.getBlockState(pPos).isAir() && level.getBlockState(pPos.above()).isAir()) {
            BlockState blockstate = level.getBlockState(pPos.below());
            if (blockstate.isSolid() && !(blockstate.getBlock() instanceof LeavesBlock)) {
               PathType pathtype = WalkNodeEvaluator.getPathTypeStatic(this.mob, pPos);
               if (pathtype != PathType.WALKABLE) {
                  return false;
               } else {
                  BlockPos blockpos = pPos.subtract(this.mob.blockPosition());
                  return level.noCollision(this.mob, this.mob.getBoundingBox().move(blockpos));
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static class ThunderstrikeGoal extends Goal {
      private final AbstractSpellCastingPet pet;
      private Creeper creeper;

      public ThunderstrikeGoal(AbstractSpellCastingPet pet) {
         this.pet = pet;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (this.creeper != null) {
            return false;
         } else if (this.pet.getMovementDisabled()) {
            return false;
         } else if (!this.pet.canExecuteGoalsInHouse()) {
            return false;
         } else {
            this.creeper = this.pet
               .level()
               .getEntitiesOfClass(Creeper.class, this.pet.getBoundingBox().inflate(15.0), creeper -> !creeper.isPowered())
               .stream()
               .min(Comparator.comparingDouble(this.pet::distanceTo))
               .orElse(null);
            return this.creeper != null;
         }
      }

      public boolean canContinueToUse() {
         return this.creeper != null && this.creeper.isAlive() && !this.creeper.isPowered() && this.pet.canExecuteGoalsInHouse();
      }

      public void start() {
         this.pet.getNavigation().moveTo(this.creeper, 1.0);
      }

      public void tick() {
         if (this.creeper != null) {
            if (this.pet.distanceTo(this.creeper) < 8.0) {
               this.pet.getLookControl().setLookAt(this.creeper, 10.0F, 10.0F);
               if (!this.creeper.isPowered() && this.pet.level instanceof ServerLevel serverLevel) {
                  this.pet.triggerAnim("interact_controller", "interact");
                  LightningBolt lightningBolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(this.pet.level);
                  lightningBolt.setVisualOnly(true);
                  lightningBolt.setDamage(0.0F);
                  lightningBolt.setPos(this.creeper.getX(), this.creeper.getY(), this.creeper.getZ());
                  this.pet.level.addFreshEntity(lightningBolt);
                  this.creeper.thunderHit(serverLevel, lightningBolt);
               }
            } else {
               this.pet.getNavigation().moveTo(this.creeper, 1.0);
            }
         }
      }

      public void stop() {
         this.creeper = null;
      }
   }

   public static class WanderAroundHouseGoal extends Goal {
      private final AbstractSpellCastingPet familiar;
      private final BlockPos housePos;
      private final double speedModifier;
      private Vec3 targetPos;
      private int cooldown = 0;

      public WanderAroundHouseGoal(AbstractSpellCastingPet familiar, BlockPos housePos, double speedModifier) {
         this.familiar = familiar;
         this.housePos = housePos;
         this.speedModifier = speedModifier;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      private double getWanderRadius() {
         if (this.familiar.level() == null) {
            return 15.0;
         } else {
            return this.familiar.level().getBlockEntity(this.housePos) instanceof AbstractFamiliarStorageBlockEntity storageEntity
               ? storageEntity.getMaxDistance()
               : 15.0;
         }
      }

      public boolean canUse() {
         if (!this.familiar.getIsInHouse()) {
            return false;
         } else {
            BlockEntity blockEntity = this.familiar.level().getBlockEntity(this.housePos);
            if (!(blockEntity instanceof AbstractFamiliarStorageBlockEntity)) {
               return false;
            } else if (this.cooldown > 0) {
               this.cooldown--;
               return false;
            } else {
               return !this.familiar.getNavigation().isInProgress() && this.familiar.getRandom().nextInt(60) == 0;
            }
         }
      }

      public boolean canContinueToUse() {
         if (!this.familiar.getIsInHouse()) {
            return false;
         } else {
            double wanderRadius = this.getWanderRadius();
            double distanceToHouse = this.familiar.position().distanceTo(Vec3.atCenterOf(this.housePos));
            return distanceToHouse > wanderRadius + 5.0 ? false : this.familiar.getNavigation().isInProgress() && this.targetPos != null;
         }
      }

      public void start() {
         Vec3 houseCenter = Vec3.atCenterOf(this.housePos);
         Vec3 currentPos = this.familiar.position();
         double wanderRadius = this.getWanderRadius();

         for (int attempt = 0; attempt < 10; attempt++) {
            double angle = this.familiar.getRandom().nextDouble() * 2.0 * Math.PI;
            double distance = 3.0 + this.familiar.getRandom().nextDouble() * (wanderRadius - 3.0);
            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;
            this.targetPos = houseCenter.add(offsetX, 0.0, offsetZ);
            if (currentPos.distanceTo(this.targetPos) > 2.0) {
               break;
            }
         }

         if (this.targetPos != null) {
            this.targetPos = this.findGroundPosition(this.targetPos);
            PathNavigation navigation = this.familiar.getNavigation();
            boolean success = navigation.moveTo(this.targetPos.x, this.targetPos.y, this.targetPos.z, this.speedModifier);
            if (!success) {
               Vec3 closerPos = houseCenter.add((this.targetPos.x - houseCenter.x) * 0.5, 0.0, (this.targetPos.z - houseCenter.z) * 0.5);
               closerPos = this.findGroundPosition(closerPos);
               navigation.moveTo(closerPos.x, closerPos.y, closerPos.z, this.speedModifier);
            }
         }

         this.cooldown = 40 + this.familiar.getRandom().nextInt(40);
      }

      public void stop() {
         this.targetPos = null;
         this.familiar.getNavigation().stop();
      }

      private Vec3 findGroundPosition(Vec3 pos) {
         BlockPos blockPos = BlockPos.containing(pos);

         for (int y = 0; y <= 5; y++) {
            BlockPos checkPos = blockPos.offset(0, -y, 0);
            if (this.familiar.level().getBlockState(checkPos).isSolid() && this.familiar.level().getBlockState(checkPos.above()).isAir()) {
               return Vec3.atBottomCenterOf(checkPos.above());
            }
         }

         for (int yx = 1; yx <= 3; yx++) {
            BlockPos checkPos = blockPos.offset(0, yx, 0);
            if (this.familiar.level().getBlockState(checkPos).isSolid() && this.familiar.level().getBlockState(checkPos.above()).isAir()) {
               return Vec3.atBottomCenterOf(checkPos.above());
            }
         }

         return pos;
      }
   }

   public static class WitherifyGoal extends Goal {
      private final AbstractSpellCastingPet pet;
      private Skeleton skeleton;

      public WitherifyGoal(AbstractSpellCastingPet pet) {
         this.pet = pet;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (this.skeleton != null) {
            return false;
         } else if (this.pet.getMovementDisabled()) {
            return false;
         } else if (!this.pet.canExecuteGoalsInHouse()) {
            return false;
         } else {
            this.skeleton = this.pet
               .level()
               .getEntitiesOfClass(Skeleton.class, this.pet.getBoundingBox().inflate(15.0))
               .stream()
               .filter(entity -> !(entity instanceof SummonedSkeleton))
               .min(Comparator.comparingDouble(this.pet::distanceTo))
               .orElse(null);
            return this.skeleton != null;
         }
      }

      public boolean canContinueToUse() {
         return this.skeleton != null && this.skeleton.isAlive() && this.pet.canExecuteGoalsInHouse();
      }

      public void start() {
         this.pet.getNavigation().moveTo(this.skeleton, 1.0);
      }

      public void tick() {
         if (this.skeleton != null) {
            if (this.pet.distanceTo(this.skeleton) < 3.0) {
               this.pet.getLookControl().setLookAt(this.skeleton, 10.0F, 10.0F);
               this.pet.triggerAnim("interact_controller", "interact");
               WitherSkeleton witherSkeleton = new WitherSkeleton(EntityType.WITHER_SKELETON, this.pet.level());
               witherSkeleton.setPos(this.skeleton.getX(), this.skeleton.getY(), this.skeleton.getZ());
               witherSkeleton.finalizeSpawn(
                  (ServerLevel)this.pet.level(), this.pet.level.getCurrentDifficultyAt(this.skeleton.getOnPos()), MobSpawnType.MOB_SUMMONED, null
               );
               this.pet.level().addFreshEntity(witherSkeleton);
               MagicManager.spawnParticles(
                  this.pet.level,
                  new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.ELDRITCH.get()).getTargetingColor(), 2.0F),
                  this.skeleton.getX(),
                  this.skeleton.getY() + 0.165F,
                  this.skeleton.getZ(),
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  true
               );
               this.skeleton.remove(RemovalReason.DISCARDED);
            } else {
               this.pet.getNavigation().moveTo(this.skeleton, 1.0);
            }
         }
      }

      public void stop() {
         this.skeleton = null;
      }
   }
}
