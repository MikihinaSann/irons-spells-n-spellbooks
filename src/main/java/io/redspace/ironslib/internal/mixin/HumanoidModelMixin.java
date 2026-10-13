package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.kinetic_weapon.KineticAnimation;
import io.redspace.ironslib.kinetic_weapon.client.KineticAnimations;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HumanoidModel.class})
public class HumanoidModelMixin {
   @Inject(
      method = {"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/world/entity/LivingEntity;F)V"
      )}
   )
   private void irons_lib$kineticThirdPersonArmPose(
      LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci
   ) {
      ItemStack itemStack = entity.getUseItem();
      HumanoidArm arm = KineticAnimations.armFor(entity, entity.getUsedItemHand());
      KineticAnimation animation = KineticAnimations.activeFor(entity, arm, itemStack);
      if (animation != null) {
         float ticksUsingItem = KineticAnimations.ticksUsingItem(entity, KineticAnimations.partialTick());
         animation.thirdPersonArmPose((HumanoidModel<?>)(Object) this, entity, arm, ticksUsingItem, itemStack);
      }
   }
}
