package com.somake.somakespells.mixin;

import com.somake.somakespells.config.EffectBalanceConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   targets = {"net.mcreator.borninchaosv.procedures.StonePersistenceKazhdyiTikVoVriemiaEffiektaProcedure"},
   remap = false
)
public abstract class StonePersistenceProcedureMixin {
   @Redirect(
      method = {"execute(Lnet/minecraft/world/entity/Entity;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
      )
   )
   private static boolean somake$blockStonePersistenceResistance(LivingEntity entity, MobEffectInstance effect) {
      return EffectBalanceConfig.MODIFY_BORN_IN_CHAOS_STONE_PERSISTENCE.get() ? false : entity.addEffect(effect);
   }
}
