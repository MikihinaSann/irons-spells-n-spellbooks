package com.somake.somakespells.block.entity;

import com.somake.somakespells.ritual.RitualElementalMode;
import java.util.List;
import net.minecraft.core.BlockPos;

public interface SoulFireAltarState {
   RitualElementalMode somake$getRitualMode();

   int somake$getRitualCharges();

   List<BlockPos> somake$getRitualPedestals();

   void somake$activateRitualMode(RitualElementalMode var1, List<BlockPos> var2, int var3);

   void somake$clearRitualMode();

   default boolean somake$isModeActive(RitualElementalMode mode) {
      return this.somake$getRitualMode() == mode && this.somake$getRitualCharges() > 0 && !this.somake$getRitualPedestals().isEmpty();
   }

   default boolean somake$isSoulFireActive() {
      return this.somake$isModeActive(RitualElementalMode.SOUL_FIRE);
   }

   default boolean somake$isInfernalFireActive() {
      return this.somake$isModeActive(RitualElementalMode.INFERNAL_FIRE);
   }

   default int somake$getSoulFireCharges() {
      return this.somake$getRitualCharges();
   }

   default List<BlockPos> somake$getSoulFirePedestals() {
      return this.somake$getRitualPedestals();
   }

   default void somake$activateSoulFire(List<BlockPos> pedestalPositions, int charges) {
      this.somake$activateRitualMode(RitualElementalMode.SOUL_FIRE, pedestalPositions, charges);
   }

   default void somake$clearSoulFire() {
      this.somake$clearRitualMode();
   }
}
