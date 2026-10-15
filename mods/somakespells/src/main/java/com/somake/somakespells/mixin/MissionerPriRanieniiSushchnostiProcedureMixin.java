package com.somake.somakespells.mixin;

import com.somake.somakespells.config.Config;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(
   targets = {"net.mcreator.borninchaosv.procedures.MissionerPriRanieniiSushchnostiProcedure"},
   remap = false
)
public abstract class MissionerPriRanieniiSushchnostiProcedureMixin {
   @Redirect(
      method = {"execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/storage/LevelData;setRaining(Z)V",
         remap = true
      ),
      remap = false,
      require = 0,
      expect = 1
   )
   private static void somake$blockMissionaryRainStart(LevelData levelData, boolean raining) {
      if ((Boolean)Config.ENABLE_MISSIONARY_RAIN.get() || !raining) {
         levelData.setRaining(raining);
      }
   }
}
