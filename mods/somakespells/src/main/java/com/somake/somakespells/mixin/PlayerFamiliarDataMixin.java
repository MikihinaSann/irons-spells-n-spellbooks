package com.somake.somakespells.mixin;

import com.somake.somakespells.config.Config;
import net.alshanex.familiarslib.data.FamiliarRoster;
import net.alshanex.familiarslib.data.FamiliarSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({FamiliarRoster.class})
public abstract class PlayerFamiliarDataMixin {
   @Shadow(
      remap = false
   )
   private FamiliarSavedData store;

   @Overwrite(
      remap = false
   )
   public boolean canTameMoreFamiliars() {
      return this.store.size() < (Integer)Config.FAMILIAR_LIMIT.get();
   }

   @Overwrite(
      remap = false
   )
   public int getRemainingFamiliarSlots() {
      return Math.max(0, (Integer)Config.FAMILIAR_LIMIT.get() - this.store.size());
   }

   @Overwrite(
      remap = false
   )
   public boolean isAtMaxCapacity() {
      return this.store.size() >= (Integer)Config.FAMILIAR_LIMIT.get();
   }
}
