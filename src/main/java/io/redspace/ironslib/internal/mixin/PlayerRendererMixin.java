package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.kinetic_weapon.KineticWeapon;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerRenderer.class})
public class PlayerRendererMixin {
   @Inject(
      method = {"getArmPose"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void irons_lib$kineticPoseWinsOverUseAnimation(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<ArmPose> cir) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (KineticWeapon.has(itemStack) && player.getUsedItemHand() == hand && player.getUseItemRemainingTicks() > 0) {
         ArmPose pose = IClientItemExtensions.of(itemStack).getArmPose(player, hand, itemStack);
         cir.setReturnValue(pose != null ? pose : ArmPose.ITEM);
      }
   }
}
