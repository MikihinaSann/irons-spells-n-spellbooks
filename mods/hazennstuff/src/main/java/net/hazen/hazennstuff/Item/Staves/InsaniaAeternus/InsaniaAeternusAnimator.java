package net.hazen.hazennstuff.Item.Staves.InsaniaAeternus;

import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzItemAnimator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InsaniaAeternusAnimator extends AzItemAnimator {
   private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
      "hazennstuff", "animations/item/staves/insania_aeternus.animation.json"
   );

   public void registerControllers(AzAnimationControllerContainer<ItemStack> animationControllerContainer) {
      animationControllerContainer.add(AzAnimationController.builder(this, "base_controller").build(), new AzAnimationController[0]);
   }

   @NotNull
   public ResourceLocation getAnimationLocation(ItemStack animatable) {
      return ANIMATIONS;
   }
}
