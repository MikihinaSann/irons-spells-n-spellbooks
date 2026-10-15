package net.alshanex.familiarslib.util.familiars;

import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.block.AbstractFamiliarBedBlock;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarBedBlockEntity;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class FamiliarBedHelper {
   public static void snapToExactBedPosition(AbstractSpellCastingPet familiar) {
      BlockPos petPos = familiar.blockPosition();

      for (int x = -1; x <= 1; x++) {
         for (int y = -1; y <= 0; y++) {
            for (int z = -1; z <= 1; z++) {
               BlockPos checkPos = petPos.offset(x, y, z);
               if (familiar.level().getBlockState(checkPos).getBlock() instanceof AbstractFamiliarBedBlock
                  && familiar.level().getBlockEntity(checkPos) instanceof AbstractFamiliarBedBlockEntity petBed) {
                  Vec3 exactPos = petBed.getSleepPosition();
                  double distance = familiar.position().distanceTo(exactPos);
                  if (distance <= 3.0) {
                     familiar.setPos(exactPos.x, exactPos.y, exactPos.z);
                     if (!familiar.level().isClientSide) {
                        FamiliarsLib.LOGGER.debug("Snapped pet " + familiar.getUUID() + " to bed position: " + exactPos);
                     }

                     return;
                  }
               }
            }
         }
      }
   }
}
