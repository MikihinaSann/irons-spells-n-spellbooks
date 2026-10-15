package com.somake.somakespells.mixin;

import com.somake.somakespells.config.EffectBalanceConfig;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   targets = {"net.mcreator.borninchaosv.procedures.TransformingEasterCakePriZaviershieniiIspolzovaniiaProcedure"},
   remap = false
)
public abstract class TransformingEasterCakeProcedureMixin {
   @Redirect(
      method = {"execute(Lnet/minecraft/world/entity/Entity;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I",
         ordinal = 1
      )
   )
   private static int somake$replaceRabbitAgilityDuration(RandomSource random, int min, int max) {
      return EffectBalanceConfig.REDUCE_TRANSFORMING_EASTER_CAKE_DURATION.get() ? Mth.nextInt(random, 1200, 2400) : Mth.nextInt(random, min, max);
   }

   @Redirect(
      method = {"execute(Lnet/minecraft/world/entity/Entity;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I",
         ordinal = 2
      )
   )
   private static int somake$replacePredatorsDesireDuration(RandomSource random, int min, int max) {
      return EffectBalanceConfig.REDUCE_TRANSFORMING_EASTER_CAKE_DURATION.get() ? Mth.nextInt(random, 1200, 2400) : Mth.nextInt(random, min, max);
   }

   @Redirect(
      method = {"execute(Lnet/minecraft/world/entity/Entity;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I",
         ordinal = 3
      )
   )
   private static int somake$replaceStonePersistenceDuration(RandomSource random, int min, int max) {
      return EffectBalanceConfig.REDUCE_TRANSFORMING_EASTER_CAKE_DURATION.get() ? Mth.nextInt(random, 1200, 2400) : Mth.nextInt(random, min, max);
   }
}
