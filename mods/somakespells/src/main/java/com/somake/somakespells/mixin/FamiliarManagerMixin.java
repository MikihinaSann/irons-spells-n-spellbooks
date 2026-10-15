package com.somake.somakespells.mixin;

import com.somake.somakespells.config.Config;
import net.alshanex.familiarslib.data.FamiliarRoster;
import net.alshanex.familiarslib.util.familiars.FamiliarManager;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({FamiliarManager.class})
public abstract class FamiliarManagerMixin {
   @Overwrite(
      remap = false
   )
   public static String getFamiliarCapacityInfo(ServerPlayer player) {
      FamiliarRoster familiarData = FamiliarRoster.of(player);
      return familiarData.getFamiliarCount() + "/" + Config.FAMILIAR_LIMIT.get();
   }
}
