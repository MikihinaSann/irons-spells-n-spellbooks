package net.hazen.hazennstuff.Item.Armor.AzureLib.ArbitriumRobes;

import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.easing.AzEasingTypeRegistry;
import mod.azure.azurelib.common.animation.impl.AzItemAnimator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ArbitriumAnimator extends AzItemAnimator {
   private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
      "hazennstuff", "animations/armor/arbitrium_robes_armor.animation.json"
   );

   public void registerControllers(AzAnimationControllerContainer animationControllerContainer) {
      animationControllerContainer.add(
         AzAnimationController.builder(this, "base_controller").setEasingType(AzEasingTypeRegistry.getOrNull("linear")).setTransitionLength(6).build(),
         new AzAnimationController[0]
      );
   }

   @NotNull
   public ResourceLocation getAnimationLocation(ItemStack animatable) {
      return ANIMATIONS;
   }
}
