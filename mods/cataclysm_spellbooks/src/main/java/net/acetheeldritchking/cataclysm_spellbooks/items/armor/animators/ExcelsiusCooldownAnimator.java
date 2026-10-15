package net.acetheeldritchking.cataclysm_spellbooks.items.armor.animators;

import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzItemAnimator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ExcelsiusCooldownAnimator extends AzItemAnimator {
   private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
      "cataclysm_spellbooks", "animations/item/excelsius_armor_cd.animation.json"
   );

   public void registerControllers(AzAnimationControllerContainer animationControllerContainer) {
      animationControllerContainer.add(AzAnimationController.builder(this, "base_controller").build(), new AzAnimationController[0]);
   }

   @NotNull
   public ResourceLocation getAnimationLocation(ItemStack animatable) {
      return ANIMATIONS;
   }
}
