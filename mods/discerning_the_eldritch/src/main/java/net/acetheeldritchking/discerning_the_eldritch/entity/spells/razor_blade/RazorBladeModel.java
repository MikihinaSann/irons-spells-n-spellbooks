package net.acetheeldritchking.discerning_the_eldritch.entity.spells.razor_blade;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RazorBladeModel extends GeoModel<RazorBlade> {
   public ResourceLocation getModelResource(RazorBlade razorBlade) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/razor_blade.geo.json");
   }

   public ResourceLocation getTextureResource(RazorBlade razorBlade) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/razor_blade/razor_blade.png");
   }

   public ResourceLocation getAnimationResource(RazorBlade razorBlade) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/razor_blade.animation.json");
   }
}
