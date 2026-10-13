package io.redspace.ironslib.internal.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.redspace.ironslib.kinetic_weapon.UseEffects;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LocalPlayer.class})
public class LocalPlayerMixin {
   @ModifyExpressionValue(
      method = {"aiStep"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
      )}
   )
   private boolean irons_lib$chargeIsNotASlowUse(boolean isUsingItem) {
      return isUsingItem && !UseEffects.useIsUnrestricted((LocalPlayer)(Object) this);
   }

   @ModifyExpressionValue(
      method = {"canStartSprinting"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
      )}
   )
   private boolean irons_lib$chargeDoesNotRefuseSprint(boolean isUsingItem) {
      return isUsingItem && !UseEffects.useIsUnrestricted((LocalPlayer)(Object) this);
   }
}
