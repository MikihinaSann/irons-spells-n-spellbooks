package net.alshanex.familiarslib.util.familiars;

import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarBedBlockEntity;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

public class BedCleanupHelper {
   public static void performSafetyCheck(AbstractFamiliarBedBlockEntity bedEntity, BlockPos bedPos, ServerLevel level) {
      if (bedEntity.isBedTaken()) {
         AABB searchArea = new AABB(bedPos).inflate(10.0);
         boolean familiarFoundUsingThisBed = !level.getEntitiesOfClass(AbstractSpellCastingPet.class, searchArea, familiar -> {
            if (familiar != null && familiar.isAlive()) {
               boolean hasActiveBedGoal = familiar.getGoalSelector().getAvailableGoals().stream().anyMatch(goal -> {
                  if (goal.getGoal() instanceof FamiliarGoals.FindAndUsePetBedGoal bedGoal && goal.isRunning()) {
                     BlockPos targetPos = bedGoal.getTargetBedPos();
                     boolean hasClaimed = bedGoal.hasClaimedBed();
                     return targetPos != null && targetPos.equals(bedPos) && hasClaimed;
                  } else {
                     return false;
                  }
               });
               if (hasActiveBedGoal) {
                  return true;
               } else {
                  if (familiar.getIsSitting()) {
                     double distance = familiar.position().distanceTo(bedEntity.getSleepPosition());
                     if (distance <= 2.0) {
                        return true;
                     }
                  }

                  return false;
               }
            } else {
               return false;
            }
         }).isEmpty();
         if (!familiarFoundUsingThisBed) {
            FamiliarsLib.LOGGER.debug("Safety check: Unclaiming abandoned bed at {}", bedPos);
            bedEntity.setBedTaken(false);
         }
      }
   }
}
