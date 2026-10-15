package net.acetheeldritchking.cataclysm_spellbooks.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSPotionEffectRegistry;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({MouseHandler.class})
public class MouseMixin {
   @WrapOperation(
      method = {"turnPlayer"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
      )}
   )
   private void cataclysmSpellbooks$playerModifyTurnMovement(LocalPlayer player, double yRot, double xRot, Operation<Void> original) {
      if (player.hasEffect(CSPotionEffectRegistry.INCAPACITATED_EFFECT)) {
         original.call(new Object[]{player, 0.0, 0.0});
      } else {
         original.call(new Object[]{player, yRot, xRot});
      }
   }
}
