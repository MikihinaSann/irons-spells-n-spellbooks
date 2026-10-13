package io.redspace.ironslib.internal.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironslib.kinetic_weapon.KineticAnimation;
import io.redspace.ironslib.kinetic_weapon.KineticWeapon;
import io.redspace.ironslib.kinetic_weapon.client.KineticAnimations;
import io.redspace.ironslib.kinetic_weapon.client.KineticHitFeedback;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemInHandRenderer.class})
public class ItemInHandRendererMixin {
   @WrapOperation(
      method = {"renderArmWithItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;"
      )}
   )
   private UseAnim irons_lib$skipTridentPoseForKineticWeapon(ItemStack itemStack, Operation<UseAnim> original) {
      UseAnim useAnim = (UseAnim)original.call(new Object[]{itemStack});
      return useAnim == UseAnim.SPEAR && KineticWeapon.has(itemStack) ? UseAnim.NONE : useAnim;
   }

   @Inject(
      method = {"renderArmWithItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
      )}
   )
   private void irons_lib$kineticFirstPersonPose(
      AbstractClientPlayer player,
      float frameInterp,
      float xRot,
      InteractionHand hand,
      float attack,
      ItemStack itemStack,
      float inverseArmHeight,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int lightCoords,
      CallbackInfo ci
   ) {
      HumanoidArm arm = KineticAnimations.armFor(player, hand);
      KineticAnimation animation = KineticAnimations.activeFor(player, arm, itemStack);
      if (animation != null) {
         float timeHeld = (float)itemStack.getUseDuration(player) - ((float)player.getUseItemRemainingTicks() - frameInterp + 1.0F);
         animation.firstPersonUse(KineticHitFeedback.ticksSinceHit(player, frameInterp), poseStack, timeHeld, arm, itemStack);
      }
   }
}
