package io.redspace.ironslib.internal.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironslib.kinetic_weapon.KineticAnimation;
import io.redspace.ironslib.kinetic_weapon.client.KineticAnimations;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemInHandLayer.class})
public class ItemInHandLayerMixin {
   @Inject(
      method = {"renderArmWithItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
      )}
   )
   private void irons_lib$kineticThirdPersonItem(
      LivingEntity entity,
      ItemStack itemStack,
      ItemDisplayContext displayContext,
      HumanoidArm arm,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int lightCoords,
      CallbackInfo ci
   ) {
      KineticAnimation animation = KineticAnimations.activeFor(entity, arm, itemStack);
      if (animation != null) {
         float partialTick = KineticAnimations.partialTick();
         animation.thirdPersonItem(poseStack, entity, arm, KineticAnimations.ticksUsingItem(entity, partialTick), partialTick, itemStack);
      }
   }
}
