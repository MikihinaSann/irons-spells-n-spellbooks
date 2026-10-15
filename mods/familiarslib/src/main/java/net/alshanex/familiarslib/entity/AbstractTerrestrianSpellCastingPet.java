package net.alshanex.familiarslib.entity;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractTerrestrianSpellCastingPet extends AbstractSpellCastingPet {
   protected AbstractTerrestrianSpellCastingPet(EntityType<? extends AbstractSpellCastingPet> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.moveControl = this.createMoveControl();
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(10, new AbstractTerrestrianSpellCastingPet.MovementAwareRandomStrollGoal(this, 1.0, 60));
   }

   protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
   }

   @Override
   protected void defineSynchedData(Builder pBuilder) {
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

   public boolean isDrinkingPotion() {
      return false;
   }

   public void startDrinkingPotion() {
   }

   @Override
   public boolean shouldAlwaysAnimateLegs() {
      return true;
   }

   protected class MovementAwareRandomStrollGoal extends Goal {
      public static final int DEFAULT_INTERVAL = 120;
      protected final PathfinderMob mob;
      protected double wantedX;
      protected double wantedY;
      protected double wantedZ;
      protected final double speedModifier;
      protected int interval;
      protected boolean forceTrigger;
      private final boolean checkNoActionTime;

      public MovementAwareRandomStrollGoal(PathfinderMob mob, double speedModifier) {
         this(mob, speedModifier, 120);
      }

      public MovementAwareRandomStrollGoal(PathfinderMob mob, double speedModifier, int interval) {
         this(mob, speedModifier, interval, true);
      }

      public MovementAwareRandomStrollGoal(PathfinderMob mob, double speedModifier, int interval, boolean checkNoActionTime) {
         this.mob = mob;
         this.speedModifier = speedModifier;
         this.interval = interval;
         this.checkNoActionTime = checkNoActionTime;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (AbstractTerrestrianSpellCastingPet.this.movementDisabled) {
            return false;
         } else if (this.mob.hasControllingPassenger()) {
            return false;
         } else {
            if (!this.forceTrigger) {
               if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
                  return false;
               }

               if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                  return false;
               }
            }

            Vec3 vec3 = this.getPosition();
            if (vec3 == null) {
               return false;
            } else {
               this.wantedX = vec3.x;
               this.wantedY = vec3.y;
               this.wantedZ = vec3.z;
               this.forceTrigger = false;
               return true;
            }
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         return DefaultRandomPos.getPos(this.mob, 10, 7);
      }

      public boolean canContinueToUse() {
         return !this.mob.getNavigation().isDone() && !this.mob.hasControllingPassenger();
      }

      public void start() {
         this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
      }

      public void stop() {
         this.mob.getNavigation().stop();
         super.stop();
      }

      public void trigger() {
         this.forceTrigger = true;
      }

      public void setInterval(int newchance) {
         this.interval = newchance;
      }
   }
}
