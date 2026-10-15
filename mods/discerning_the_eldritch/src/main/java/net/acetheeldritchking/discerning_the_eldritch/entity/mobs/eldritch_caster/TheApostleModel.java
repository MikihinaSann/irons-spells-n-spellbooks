package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.eldritch_caster;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class TheApostleModel extends DefaultedEntityGeoModel<TheApostleEntity> {
   public TheApostleModel() {
      super(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "apostle"));
   }

   public ResourceLocation getModelResource(TheApostleEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/apostle.geo.json");
   }

   public ResourceLocation getTextureResource(TheApostleEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/apostle/apostle.png");
   }

   public ResourceLocation getAnimationResource(TheApostleEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/apostle.animation.json");
   }

   public void setCustomAnimations(TheApostleEntity animatable, long instanceId, AnimationState<TheApostleEntity> animationState) {
      super.setCustomAnimations(animatable, instanceId, animationState);
   }
}
