package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.gaoler;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GaolerModel extends DefaultedEntityGeoModel<GaolerEntity> {
   public GaolerModel() {
      super(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "gaoler"));
   }

   public ResourceLocation getModelResource(GaolerEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/gaoler.geo.json");
   }

   public ResourceLocation getTextureResource(GaolerEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/gaoler/gaoler.png");
   }

   public ResourceLocation getAnimationResource(GaolerEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/gaoler.animation.json");
   }

   public void setCustomAnimations(GaolerEntity animatable, long instanceId, AnimationState<GaolerEntity> animationState) {
      super.setCustomAnimations(animatable, instanceId, animationState);
   }
}
