package net.alshanex.familiarslib.util.familiars;

import java.util.UUID;
import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarStorageBlockEntity;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.minecraft.server.level.ServerLevel;

public class FamiliarDeathStorageHandler {
   public static void notifyFamiliarDeath(AbstractSpellCastingPet familiar) {
      if (!familiar.level().isClientSide) {
         UUID familiarId = familiar.getUUID();
         ServerLevel level = (ServerLevel)familiar.level();
         FamiliarsLib.LOGGER.debug("Notifying storage blocks about death of familiar {}", familiarId);
         if (familiar.getIsInHouse()
            && familiar.housePosition != null
            && level.getBlockEntity(familiar.housePosition) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
            storageEntity.handleFamiliarDeath(familiarId);
            FamiliarsLib.LOGGER.debug("Notified house at {} about familiar death", familiar.housePosition);
         }
      }
   }
}
