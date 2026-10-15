package net.alshanex.alshanex_familiars.mixin;

import net.alshanex.alshanex_familiars.entity.misc.FrozenEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public class FrozenLivingEntityMixin {
   @Inject(
      method = {"getDimensions"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void modifyDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
      if (((Object)this) instanceof FrozenEntity frozenEntity) {
         cir.setReturnValue(frozenEntity.getFrozenEntitySize());
      }
   }
}
